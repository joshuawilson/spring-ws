/*
 * Copyright 2005-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ws.soap.security.wss4j;

import java.util.Properties;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.memory.InMemoryDaoImpl;
import org.springframework.ws.context.DefaultMessageContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.security.wss4j.callback.SpringDigestPasswordValidationCallbackHandler;
import org.springframework.ws.soap.security.wss4j.callback.SpringPlainTextPasswordValidationCallbackHandler;

import org.apache.ws.security.WSConstants;
import org.junit.After;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public abstract class Wss4jMessageInterceptorSpringSecurityCallbackHandlerTestCase extends Wss4jTestCase {

    private Properties users = new Properties();

    private AuthenticationManager authenticationManager;

    @Override
    protected void onSetup() throws Exception {
        authenticationManager = createMock(AuthenticationManager.class);
        users.setProperty("Bert", "Ernie,ROLE_TEST");
    }

    @After
    public void tearDown() throws Exception {
        verify(authenticationManager);
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testValidateUsernameTokenPlainText() throws Exception {
        EndpointInterceptor interceptor = prepareInterceptor("UsernameToken", true, false);
        SoapMessage message = loadSoap11Message("usernameTokenPlainText-soap.xml");
        MessageContext messageContext = new DefaultMessageContext(message, getSoap11MessageFactory());
        interceptor.handleRequest(messageContext, null);
        assertValidateUsernameToken(message);

        // test clean up
        messageContext.getResponse();
        interceptor.handleResponse(messageContext, null);
        interceptor.afterCompletion(messageContext, null, null);
        assertNull("Authentication created", SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testValidateUsernameTokenDigest() throws Exception {
        EndpointInterceptor interceptor = prepareInterceptor("UsernameToken", true, true);
        SoapMessage message = loadSoap11Message("usernameTokenDigest-soap.xml");
        MessageContext messageContext = new DefaultMessageContext(message, getSoap11MessageFactory());
        interceptor.handleRequest(messageContext, null);
        assertValidateUsernameToken(message);

        // test clean up
        messageContext.getResponse();
        interceptor.handleResponse(messageContext, null);
        interceptor.afterCompletion(messageContext, null, null);
        assertNull("Authentication created", SecurityContextHolder.getContext().getAuthentication());
    }

    protected void assertValidateUsernameToken(SoapMessage message) throws Exception {
        Object result = getMessage(message);
        assertNotNull("No result returned", result);
        assertXpathNotExists("Security Header not removed", "/SOAP-ENV:Envelope/SOAP-ENV:Header/wsse:Security",
                getDocument(message));
        assertNotNull("No Authentication created", SecurityContextHolder.getContext().getAuthentication());
    }

    protected EndpointInterceptor prepareInterceptor(String actions, boolean validating, boolean digest)
            throws Exception {
        Wss4jSecurityInterceptor interceptor = new Wss4jSecurityInterceptor();
        if (validating) {
            interceptor.setValidationActions(actions);
        }
        else {
            interceptor.setSecurementActions(actions);
        }
        if (digest) {
            SpringDigestPasswordValidationCallbackHandler callbackHandler =
                    new SpringDigestPasswordValidationCallbackHandler();
            InMemoryDaoImpl userDetailsService = new InMemoryDaoImpl();
            userDetailsService.setUserProperties(users);
            userDetailsService.afterPropertiesSet();
            callbackHandler.setUserDetailsService(userDetailsService);
            interceptor.setSecurementPasswordType(WSConstants.PW_DIGEST);
            interceptor.setValidationCallbackHandler(callbackHandler);
            interceptor.afterPropertiesSet();
        }
        else {
            SpringPlainTextPasswordValidationCallbackHandler callbackHandler =
                    new SpringPlainTextPasswordValidationCallbackHandler();
            Authentication authResult = new TestingAuthenticationToken("Bert", "Ernie", new GrantedAuthority[0]);
            expect(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("Bert", "Ernie"))).andReturn(authResult);
            callbackHandler.setAuthenticationManager(authenticationManager);
            callbackHandler.afterPropertiesSet();
            interceptor.setSecurementPasswordType(WSConstants.PW_TEXT);
            interceptor.setValidationCallbackHandler(callbackHandler);
            interceptor.afterPropertiesSet();
        }
        replay(authenticationManager);
        return interceptor;
    }
}