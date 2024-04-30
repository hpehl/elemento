[![Verify Codebase](https://github.com/hal/elemento/actions/workflows/verify.yml/badge.svg)](https://github.com/hal/elemento/actions/workflows/verify.yml) [![Javadoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://hal.github.io/elemento/apidocs/) [![Maven Central](https://img.shields.io/maven-central/v/org.jboss.elemento/elemento-core)](https://search.maven.org/search?q=g:org.jboss.elemento%20AND%20a:elemento-core) ![GWT3/J2CL compatible](https://img.shields.io/badge/GWT3/J2CL-compatible-brightgreen.svg) [![Chat on Gitter](https://badges.gitter.im/hal/elemento.svg)](https://gitter.im/hal/elemento)

# Elemento

Elemento simplifies working with [Elemental2](https://github.com/google/elemental2). In a nutshell Elemento brings the following features to the table:

- Type safe [builders](#builder-api), [event handlers](#event-handlers) and [CSS selectors](#typesafe-css-selectors)
- [Helper methods](#goodies) to manipulate the DOM tree
- Execute [asynchronous tasks](#flow) in parallel, in sequence or as long as a certain condition is met.
- Simple, non-invasive, slash-based [router](#router) (`/a/b/c`)
- Small [logging](#logger) wrapper around `console.log` using categories, log levels, and a predefined log format.
- Ready to be used with GWT and J2CL
- Minimal dependencies
  - Elemental2 1.1.0 (`elemental2-core`, `elemental2-dom` and `elemental2-webstorage`)
  - GWT project (`org.gwtproject.event:gwt-event` and `org.gwtproject.safehtml:gwt-safehtml`)

**TOC**
* [Get Started](#get-started)
* [Builder API](#builder-api)
  * [References](#references)
* [Event Handlers](#event-handlers)
* [Typesafe CSS Selectors](#typesafe-css-selectors)
* [Custom Elements](#custom-elements)
* [Goodies](#goodies)
  * [Attach / Detach](#attach--detach)
  * [Iterators / Iterables / Streams](#iterators--iterables--streams)
* [SVG & MathML](#svg--mathml)
  * [SVG](#svg)
  * [MathML](#mathml)
* [Flow](#flow)
* [Router](#router)
* [Logger](#logger)
* [Samples](#samples)
* [Contributing](#contributing)
* [Get Help](#get-help)

# Get Started

Elemento is available in [Maven Central](https://search.maven.org/search?q=g:org.jboss.elemento%20AND%20a:elemento-core). The easiest way is to import its BOM

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.jboss.elemento</groupId>
            <artifactId>elemento-bom</artifactId>
            <version>1.4.11</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

and add a dependency to either

```xml
<dependency>
    <groupId>org.jboss.elemento</groupId>
    <artifactId>elemento-core</artifactId>
    <type>gwt-lib</type>
</dependency>
```

or

```xml
<dependency>
    <groupId>org.jboss.elemento</groupId>
    <artifactId>elemento-core</artifactId>
</dependency>
```

depending on your stack. If you're using GWT, inherit from `org.jboss.elemento.Core`:

```xml
<module>
    <inherits name="org.jboss.elemento.Core"/>
</module>
```

# Builder API

When working with GWT Elemental it is often awkward and cumbersome to create an hierarchy of elements. Even simple structures like

```html
<section class="main">
    <input class="toggle-all" type="checkbox">
    <label for="toggle-all">Mark all as complete</label>
    <ul class="todo-list">
        <li>
            <div class="view">
                <input class="toggle" type="checkbox" checked>
                <label>Taste Elemento</label>
                <button class="destroy"></button>
            </div>
            <input class="edit">
        </li>
    </ul>
</section>
```

lead to a vast amount of `Document.createElement()` and chained `Node.appendChild()` calls. With Elemento creating the above structure is as easy as

```java
import static org.jboss.elemento.Elements.*;
import static org.jboss.elemento.InputType.checkbox;
import static org.jboss.elemento.InputType.text;

HTMLElement section = section().css("main")
        .add(input(checkbox).id("toggle-all").css("toggle-all"))
        .add(label()
                .apply(l -> l.htmlFor = "toggle-all")
                .textContent("Mark all as complete"))
        .add(ul().css("todo-list")
                .add(li()
                        .add(div().css("view")
                                .add(input(checkbox)
                                        .css("toggle")
                                        .checked(true))
                                .add(label().textContent("Taste Elemento"))
                                .add(button().css("destroy")))
                        .add(input(text).css("edit"))))
        .element();
```

The class `Elements` provides convenience methods to create the most common elements. It uses a fluent API to create and append elements on the fly. Take a look at the [API documentation](https://hal.github.io/elemento/apidocs/org/jboss/elemento/Elements.html) for more details.

## References

When creating large hierarchies of elements you often need to assign an element somewhere in the tree. Use an inline assignment together with `element()` to create and assign the element in one go:

```java
import static org.jboss.elemento.Elements.*;

final HTMLElement count;
final HTMLElement footer = footer()
        .add(count = span().css("todo-count").element())
        .element();
```

# Event Handlers

Elemento provides methods to easily register event handlers. There are [constants](https://hal.github.io/elemento/apidocs/org/jboss/elemento/EventType.html) for most of the known event types.

You can either add event handlers when building the element hierarchy:

```java
import static org.jboss.elemento.Elements.*;
import static org.jboss.elemento.EventType.*;
import static org.jboss.elemento.InputType.checkbox;
import static org.jboss.elemento.InputType.text;

HTMLLIElement listItem = li()
        .add(div().css("view")
                .add(input(checkbox)
                        .css("toggle")
                        .on(change, event -> toggle()))
                .add(label()
                        .textContent("Taste Elemento")
                        .on(dblclick, event -> edit()))
                .add(button()
                        .css("destroy")
                        .on(click, event -> destroy())))
        .add(input(text)
                .css("edit")
                .on(keydown, this::keyDown)
                .on(blur, event -> blur()))
        .element();
```

or register them later using `EventType.bind()`:

```java
import org.gwtproject.event.shared.HandlerRegistration;
import static elemental2.dom.DomGlobal.alert;
import static org.jboss.elemento.EventType.bind;
import static org.jboss.elemento.EventType.click;

HandlerRegistration handler = bind(listItem, click, event -> alert("Clicked"));
```

The latter approach returns `org.gwtproject.event.shared.HandlerRegistration` which you can use to remove the handler again.

In order to make it easier to work with keyboard events, Elemento provides an [enum](https://hal.github.io/elemento/apidocs/org/jboss/elemento/Key.html) with the most common keyboard codes:

```java
import elemental2.dom.KeyboardEvent;
import static org.jboss.elemento.Key.Escape;
import static org.jboss.elemento.Key.Enter;

void keyDown(KeyboardEvent event) {
    if (Escape.match(event)) {
        ...
    } else if (Enter.match(event)) {
        ...
    }
}
```

# Typesafe CSS Selectors

Elemento provides a typesafe selector API. It can be used to express simple CSS selector like `.class` or `#id` up to complex selectors like

```css
#main [data-list-item|=foo] a[href^="http://"] > .fas.fa-check, .external[hidden]
```

This selector can be created with

```java
import org.jboss.elemento.By;
import static org.jboss.elemento.By.AttributeOperator.CONTAINS_TOKEN;
import static org.jboss.elemento.By.AttributeOperator.STARTS_WITH;

By selector = By.group(
        By.id("main")
                .desc(By.data("listItem", CONTAINS_TOKEN, "foo")
                        .desc(By.element("a").and(By.attribute("href", STARTS_WITH, "http://"))
                                .child(By.classname(new String[]{"fas", "fa-check"})))),
        By.classname("external").and(By.attribute("hidden"))
);
```
The selector can be used to find single or all HTML elements:

```java
import org.jboss.elemento.By;
import static org.jboss.elemento.By.AttributeOperator.STARTS_WITH;
import static org.jboss.elemento.Elements.a;
import static org.jboss.elemento.Elements.body;

By selector = By.element("a").and(By.attribute("href", STARTS_WITH, "http://"));
for (HTMLElement element : body().findAll(selector)) {
    a(element).css("external");
}
```

# Custom Elements

Elemento makes it easy to create custom elements. As for Elemento custom elements are a composite of HTML elements and / or other custom elements. They're ordinary classes which can hold state or register event handlers. The only requirement is to implement `IsElement<E extends HTMLElement>` and return a root element:

```java
import static org.jboss.elemento.Elements.*;

class TodoItemElement implements IsElement<HTMLElement> {

    private final HTMLElement root;
    private final HTMLInputElement toggle;
    private final HTMLElement label;
    private final HTMLInputElement summary;

    TodoItemElement(TodoItem item) {
        this.root = li().data("item", item.id)
                .add(div().css("view")
                        .add(toggle = input(checkbox).css("toggle")
                                .checked(item.completed)
                                .element())
                        .add(label = label().textContent(item.text).element())
                        .add(destroy = button().css("destroy").element()))
                .add(summary = input(text).css("edit").element())
                .element();
        this.root.classList.toggle("completed", item.completed);
    }

    @Override
    public HTMLElement element() {
        return root;
    }

    // event handlers omitted
}
```

The builder API has support for `IsElement<E extends HTMLElement>` which makes it easy to use custom elements when building the element hierarchy:

```java
import static org.jboss.elemento.Elements.ul;

TodoItemRepository repository = ...;
TodoItemElement[] itemElements = repository.items().stream()
        .map(TodoItemElement::new)
        .toArray();
ul().addAll(itemElements).element();
```

# Goodies

Besides the builder API, Elemento comes with a bunch of static helper methods that roughly fall into these categories:

1. Get notified when an element is attached to and detached from the DOM tree.
1. Iterate over elements.
1. Methods to manipulate the DOM tree (add, insert and remove elements).
1. Methods to manipulate an element.
1. Methods to generate safe IDs.

See the API documentation of [Elements](https://hal.github.io/elemento/apidocs/org/jboss/elemento/Elements.html) for more details.

## Attach / Detach

Implement `Attachable` to get notified when an element is attached to and detached from the DOM tree. The attachable interface provides a static method to easily register the callbacks to `attach(MutationRecord)` and `detach(MutationRecord)`:

```java
import elemental2.dom.MutationRecord;
import org.jboss.elemento.Attachable;
import org.jboss.elemento.IsElement;
import static elemental2.dom.DomGlobal.console;
import static org.jboss.elemento.Elements.li;

class TodoItemElement implements IsElement<HTMLElement>, Attachable {

    private final HTMLElement root;

    TodoItemElement(TodoItem item) {
        this.root = li().element();
        Attachable.register(root, this);
    }

    @Override
    public HTMLElement element() {
        return root;
    }

    @Override
    public void attach(MutationRecord mutationRecord) {
        console.log("Todo item has been attached");
    }

    @Override
    public void detach(MutationRecord mutationRecord) {
        console.log("Todo item has been detached");
    }
}
```

Elemento uses the [`MutationObserver`](https://developer.mozilla.org/docs/Web/API/MutationObserver) API to detect changes in the DOM tree and passes an [`MutationRecord`](https://developer.mozilla.org/en-US/docs/Web/API/MutationRecord) instance to the `attach(MutationRecord)` and `detach(MutationRecord)` methods. This instance contains additional information about the DOM manipulation.

## Iterators / Iterables / Streams

Elemento provides several methods to iterate over node lists, child elements or elements returned by a selector. There are methods which return `Iterator`, `Iterable` and `Stream`.

See the API documentation of [Elements](https://hal.github.io/elemento/apidocs/org/jboss/elemento/Elements.html) for more details.

# SVG & MathML

Elemento comes with basic support for SVG and MathML.

## SVG

To create SVG elements, add the following dependency to your POM:

```xml
<dependency>
    <groupId>org.jboss.elemento</groupId>
    <artifactId>elemento-svg</artifactId>
    <version>1.4.11</version>
</dependency>
```

In your GWT module inherit from `org.jboss.elemento.SVG`:

```xml
<module>
    <inherits name="org.jboss.elemento.SVG"/>
</module>
```

Finally, use the static methods in `org.jboss.elemento.svg.SVG` to create SVG elements.

## MathML

To create MathML elements, add the following dependency to your POM:

```xml
<dependency>
    <groupId>org.jboss.elemento</groupId>
    <artifactId>elemento-mathml</artifactId>
    <version>1.4.11</version>
</dependency>
```

In your GWT module inherit from `org.jboss.elemento.MathML`:

```xml
<module>
    <inherits name="org.jboss.elemento.MathML"/>
</module>
```

Finally, use the static methods in `org.jboss.elemento.mathml.MathML` to create MathML elements.

# Flow

The module `elemento-flow` provides a way to execute a list of asynchronous tasks in parallel or sequentially, or to execute a single task repeatedly as long as certain conditions are met.

See the API documentation of [Flow](https://hal.github.io/elemento/apidocs/org/jboss/elemento/flow/Flow.html) for more details.

## Parallel

```java
// datetime format is "2022-03-31T11:03:39.348365+02:00"
Task<FlowContext> currentTime = context -> fetch("https://worldtimeapi.org/api/timezone/Europe/Berlin")
        .then(Response::json)
        .then(json -> Promise.resolve(Js.<JsPropertyMap<String>>cast(json).get("datetime").substring(11, 23)))
        .then(context::resolve);
double ms = 500 + new Random().nextInt(2000);
Task<FlowContext> delay = context -> new Promise<>((res, __) -> setTimeout(___ -> res.onInvoke(context), ms));

// execute the two tasks in parallel
Flow.parallel(new FlowContext(), List.of(currentTime, delay))
        .subscribe(context -> console.log("Current time: " + context.pop("n/a")));
```

## Sequential

```java
// datetime format is "2022-03-31T11:03:39.348365+02:00"
Task<FlowContext> currentTime = context -> fetch("https://worldtimeapi.org/api/timezone/Europe/Berlin")
        .then(Response::json)
        .then(json -> Promise.resolve(Js.<JsPropertyMap<String>>cast(json).get("datetime").substring(11, 23)))
        .then(context::resolve);
double ms = 500 + new Random().nextInt(2_000);
Task<FlowContext> delay = context -> new Promise<>((res, __) -> setTimeout(___ -> res.onInvoke(context), ms));

// execute the two tasks in sequence and cancel after 1_000 ms
Flow.parallel(new FlowContext(), List.of(currentTime, delay))
        .timeout(1_000)
        .subscribe(context -> console.log("Current time: " + context.pop("n/a")));

```

## Repeated

```java
Task<FlowContext> currentTime = context -> fetch("https://worldtimeapi.org/api/timezone/Europe/Berlin")
        .then(Response::json)
        .then(json -> Promise.resolve(Js.<JsPropertyMap<String>>cast(json).get("datetime").substring(11, 23)))
        .then(context::resolve);

// fetch the current time until the milliseconds end with "0" and cancel after 5 iterations
Flow.repeat(new FlowContext(), currentTime)
        .while_(context -> !context.pop("").endsWith("0"))
        .iterations(5)
        .subscribe(context -> console.log("Current time: " + context.pop("n/a")));
```

# Router

Elemento offers a very basic router. The router is minimal invasive and built around a few simple concepts:

- `Route`: Annotation that can be used to decorate pages. An annotation processor collects all classes annotated with `@Route` and generates an implementation of `Routes`.
- `Routes`: Provides a map of places and their corresponding pages. This can be used to register all places in one go.
- `Place`: Data class that represents a place in an application. A place is identified by a route, and can have an optional title and a custom root element. If present the children of the root element are replaced by the elements of the page.
- `Page`: Simple interface that represents a collection of HTML elements. Implementations need to implement a single method: `Iterable<HTMLElement> elements()`
- `PlaceManager`: Class that keeps track of registered places, handles navigation events, and updates the DOM accordingly. The place manager can be customized using builder like methods and has a `start()` method to show the initial page.

See the API documentation of [PlaceManager](https://hal.github.io/elemento/apidocs/org/jboss/elemento/router/PlaceManager.html) for more details.

```java
@Route("/")
public class HomePage implements Page {

    @Override
    public Iterable<HTMLElement> elements() {
        return singletonList(div()
                .add(h(1, "Welcome"))
                .add(p().textContent("Hello world!"))
                .element());
    }
}

public class Application {

    public void entryPoint() {
        body().add(div().id("main"));
        new PlaceManager()
                .root(By.id("main"))
                .register(new Place("/"), HomePage::new)
                // could also be registered with
                // .register(RoutesImpl.INSTANCE.places());
                .start();
    }
}
```

# Logger

Elemento contains a small wrapper around `console.log` that uses categories, log levels, and a predefined log format.

The different log methods delegate to the corresponding methods in `console`:

1. `Logger.error(String, Object... params)` → [`console.error()`](https://developer.mozilla.org/en-US/docs/Web/API/console/error_static)
2. `Logger.warn(String, Object... params)` → [`console.warn()`](https://developer.mozilla.org/en-US/docs/Web/API/console/warn_static)
3. `Logger.info(String, Object... params)` → [`console.info()`](https://developer.mozilla.org/en-US/docs/Web/API/console/info_static)
4. `Logger.debug(String, Object... params)` → [`console.debug()`](https://developer.mozilla.org/en-US/docs/Web/API/console/debug_static)

## Get loggers

To get a logger use `Logger.getLogger(String category)`.

```java
package org.acme;

public class Foo {

    private static final Logger logger = Logger.getLogger(Foo.class.getName());
}
```

You can use an arbitrary string as category. By using a hierarchical category, you can override subcategories. [String substitutions](https://developer.mozilla.org/en-US/docs/Web/API/console#using_string_substitutions) are supported, and you can pass a variable list of parameters to the log methods.

The log level is set globally for all categories using `Logger.setLevel(Level level)`. You can override the level for one category using `Logger.setLevel(String category, Level level)`. To reset a category, use `Logger.resetLevel(String category)`. If the category contains `.`, it is interpreted hierarchically. This means that if the category `org.jboss` is overridden, this is also applied to all subcategories (unless overridden otherwise).

## Log format

The log format is predefined as

```
HH:mm:ss.SSS <level> [<category>] <message>
```

and cannot be customized. If the category is a fully qualified class name, the package names are shortened. In any case the category is trimmed, and right aligned.

## Controlling log levels from JavaScript

The logger module exports some methods with slightly adjusted signatures to JavaScript. You can use them for instance in the browser dev tools to control the global and category based log levels:

- `org.jboss.elemento.logger.Logger.setLevel(String level)` - sets the global log level
- `org.jboss.elemento.logger.Logger.setLevel(String category, String level)` - overrides the log level for one category
- `org.jboss.elemento.logger.Logger.resetLevel(String category)` - resets the log level for the category to the global log level

Please use the fully qualified name!

# Samples

Elemento comes with different sample applications to showcase and test the various modules. They're available at https://hal.github.io/elemento/samples/

# Contributing

If you want to contribute to Elemento, please follow the steps in [contribution](CONTRIBUTING.md).

# Get Help

If you need help feel free to contact us at Gitter, browse the API documentation or file an issue.

- [Gitter Channel](https://gitter.im/hal/elemento)
- [API documentation](https://hal.github.io/elemento/apidocs/)
- [Issues](https://github.com/hal/elemento/issues)
