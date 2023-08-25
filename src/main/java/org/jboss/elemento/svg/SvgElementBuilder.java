/*
 *  Copyright 2022 Red Hat
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
package org.jboss.elemento.svg;

import org.jboss.elemento.ElementBuilder;

/** Builder for simple SVG elements. */
public class SvgElementBuilder<E extends SVGElement> extends ElementBuilder<E, SvgElementBuilder<E>>
        implements WithSvgElement<E, SvgElementBuilder<E>> {

    public SvgElementBuilder(E element) {
        super(element);
    }

    @Override
    public SvgElementBuilder<E> that() {
        return this;
    }
}
