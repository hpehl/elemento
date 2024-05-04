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
package org.jboss.elemento.router.processor;

import java.io.IOException;
import java.util.List;

import javax.annotation.processing.Filer;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import static javax.lang.model.element.Modifier.PUBLIC;

class CdiCodeGenerator extends CodeGenerator {

    private static final String BEAN_MANAGER = "beanManager";

    void generateCode(Filer filer, String packageName, List<RouteInfo> routes) throws IOException {
        ClassName beanManagerClass = ClassName.get("org.kie.j2cl.tools.di.core", "BeanManager");
        ClassName placeClass = ClassName.get("org.jboss.elemento.router", "Place");
        ClassName placesClass = ClassName.get("org.jboss.elemento.router", "Places");

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC)
                .addParameter(beanManagerClass, BEAN_MANAGER)
                .addStatement("super()");

        for (RouteInfo route : routes) {
            String placeName = placeStatement(constructorBuilder, placeClass, route);
            constructorBuilder.addStatement("add($N, () -> $N.lookupBean($L.class).getInstance())",
                    placeName, BEAN_MANAGER, route.pageClass);
        }

        TypeSpec routesImplType = TypeSpec.classBuilder(Names.CLASS)
                .superclass(placesClass)
                .addModifiers(PUBLIC)
                .addMethod(constructorBuilder.build())
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, routesImplType)
                .build();
        javaFile.writeTo(filer);
    }
}
