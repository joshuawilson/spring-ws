/*
 * Copyright 2005-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ws.server.endpoint.adapter.method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.core.MethodParameter;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DomMethodArgumentProcessorTest extends AbstractPayloadMethodProcessorTest {

    @Override
    protected AbstractPayloadMethodProcessor createProcessor() {
        return new DomMethodArgumentProcessor();
    }

    @Override
    protected MethodParameter[] createSupportedParameters() throws NoSuchMethodException {
        return new MethodParameter[]{
                new MethodParameter(getClass().getMethod("element", Element.class), 0)};
    }

    @Override
    protected MethodParameter[] createSupportedReturnTypes() throws NoSuchMethodException {
        return new MethodParameter[]{new MethodParameter(getClass().getMethod("element", Element.class), -1)};
    }

    @Override
    protected void testArgument(Object argument, MethodParameter parameter) {
        assertTrue("argument not a node", argument instanceof Node);
        Node node = (Node) argument;
        assertEquals("Invalid namespace", NAMESPACE_URI, node.getNamespaceURI());
        assertEquals("Invalid local name", LOCAL_NAME, node.getLocalName());
    }

    @Override
    protected Element getReturnValue(MethodParameter returnType) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        return document.createElementNS(NAMESPACE_URI, LOCAL_NAME);
    }

    @ResponsePayload
    public Element element(@RequestPayload Element element) {
        return element;
    }

}
