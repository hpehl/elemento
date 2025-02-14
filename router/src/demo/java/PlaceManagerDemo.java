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

import org.jboss.elemento.By;
import org.jboss.elemento.router.LoadData;
import org.jboss.elemento.router.LoadedData;
import org.jboss.elemento.router.Page;
import org.jboss.elemento.router.Parameter;
import org.jboss.elemento.router.Place;
import org.jboss.elemento.router.PlaceManager;
import org.jboss.elemento.router.Places;

import elemental2.dom.HTMLElement;
import elemental2.dom.Response;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

import static elemental2.dom.DomGlobal.fetch;
import static java.util.Arrays.asList;
import static org.jboss.elemento.Elements.a;
import static org.jboss.elemento.Elements.body;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.h;
import static org.jboss.elemento.Elements.p;
import static org.jboss.elemento.Elements.span;
import static org.jboss.elemento.router.Place.place;

@SuppressWarnings("unused")
public class PlaceManagerDemo {

    // @start region = placeManager
    public static class TimeLoader implements LoadData<String> {

        @Override
        public Promise<String> load(Place place, Parameter parameter) {
            String area = parameter.get("area");
            String location = parameter.get("location");
            String url = "https://worldtimeapi.org/api/timezone/" + area + "/" + location;
            return fetch(url)
                    .then(Response::json)
                    .then(json -> {
                        JsPropertyMap<String> map = Js.cast(json);
                        return Promise.resolve(map.get("datetime"));
                    });
        }
    }

    public static class TimePage implements Page {

        @Override
        public Iterable<HTMLElement> elements(Place place, Parameter parameter, LoadedData data) {
            String area = parameter.get("area");
            String location = parameter.get("location");
            String currentTime = data.get();
            return asList(
                    h(1, "Current time").element(),
                    p()
                            .add("It's ")
                            .add(span().text(currentTime))
                            .add(" in " + area + "/" + location)
                            .element());
        }
    }

    public static class HomePage implements Page {

        @Override
        public Iterable<HTMLElement> elements(Place place, Parameter parameter, LoadedData data) {
            return asList(
                    h(1, "Welcome").element(),
                    p()
                            .add("What time is it in ")
                            .add(a("/time/Europe/Berlin").text("Berlin"))
                            .add("?")
                            .element());
        }
    }

    public static class Application {

        public void entryPoint() {
            body().add(div().id("main"));

            Places places = Places.places()
                    .add(place("/"), HomePage::new)
                    .add(place("/time/:area/:location")
                            .loader(new TimeLoader()), HomePage::new);

            PlaceManager placeManager = new PlaceManager()
                    .root(By.id("main"))
                    .register(places);
            placeManager.start();
        }
    }
    // @end region = placeManager
}
