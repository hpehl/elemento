/*
 *  Copyright 2023 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jboss.elemento;

import elemental2.dom.HTMLInputElement;

import static java.util.Objects.requireNonNull;

/** Builder for input elements. */
public class HTMLInputElementBuilder<E extends HTMLInputElement> implements
        ElementAttributeMethods<E, HTMLInputElementBuilder<E>>,
        ElementClassListMethods<E, HTMLInputElementBuilder<E>>,
        ElementConsumerMethods<E, HTMLInputElementBuilder<E>>,
        ElementEventMethods<E, HTMLInputElementBuilder<E>>,
        ElementHTMLMethods<E, HTMLInputElementBuilder<E>>,
        ElementIdMethods<E, HTMLInputElementBuilder<E>>,
        ElementQueryMethods<E>,
        ElementTextMethods<E, HTMLInputElementBuilder<E>>,
        HTMLElementAttributeMethods<E, HTMLInputElementBuilder<E>>,
        HTMLElementDataMethods<E, HTMLInputElementBuilder<E>>,
        HTMLElementStyleMethods<E, HTMLInputElementBuilder<E>>,
        HTMLElementVisibilityMethods<E, HTMLInputElementBuilder<E>>,
        HTMLInputElementMethods<E, HTMLInputElementBuilder<E>> {

    private final E element;

    public HTMLInputElementBuilder(E element) {
        this.element = requireNonNull(element, "element required");
    }

    @Override
    public HTMLInputElementBuilder<E> that() {
        return this;
    }

    @Override
    public E element() {
        return element;
    }
}
