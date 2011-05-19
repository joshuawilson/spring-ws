/*
 * Copyright 2006 the original author or authors.
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

package org.springframework.ws.pox.dom;

import org.springframework.ws.pox.PoxMessageException;

/**
 * Specific subclass of <code>PoxMessageException</code> for DOM Plain Old XML messages.
 *
 * @author Arjen Poutsma
 * @since 1.0.0
 */
public class DomPoxMessageException extends PoxMessageException {

    public DomPoxMessageException(String msg) {
        super(msg);
    }

    public DomPoxMessageException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
