# ReactorFX
[![Build Status](https://travis-ci.org/shadskii/ReactorFX.svg?branch=master)](https://travis-ci.org/shadskii/ReactorFX)
[![codecov](https://codecov.io/gh/shadskii/ReactorFX/branch/master/graph/badge.svg)](https://codecov.io/gh/shadskii/ReactorFX)


<img src="https://github.com/shadskii/ReactorFX/blob/master/ReactorFX_logo.png?raw=true" align="middle">

This lightweight convenience library allows for simple integration between [Project Reactor](https://projectreactor.io/) and 
[JavaFX](https://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm). ReactorFX provides fluent 
factories to create `Flux` for the propagation of events from JavaFX [Controls](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Node.html), 
[Collections](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/package-summary.html), and 
[Observables](https://docs.oracle.com/javase/8/javafx/api/javafx/beans/Observable.html).

## Usage
ReactorFX currently supports Java8+
```groovy
compile() //coming soon

```

## Events
In JavaFX actions from external sources are propagated through [Events.](https://docs.oracle.com/javase/8/javafx/api/javafx/event/Event.html) 
These Events can be emitted from `Node`, `Scene`, `MenuItem`, and `Window`. ReactorFX provides simple, fluent, and consistent 
factories for the creation of Fluxes from these sources. You can create Fluxes by using `FxFlux.from()` and 
passing the source and `EventType` to listen to. `FxFlux.from()` provides overloaded factories such that omitting the 
`EventType` will result in a `Flux` that listens for `ActionEvents`.
 
 ###### Events From A Control
 ```java
 Button btn = new Button("Hey I'm A Button!");
 Flux<Event> buttonEvents = FxFlux.from(btn)
                                  .subscribeOn(FxSchedulers.fxThread())
                                  .publishOn(anotherScheduler);
 ```
 ###### Events From A Scene
 ```java
 Scene scene = new Scene(new Label("Hey I'm A Label!"));
 Flux<MouseEvent> mouseEvents = FxFlux.from(scene, MouseEvent.MOUSE_CLICKED)
                                  .subscribeOn(FxSchedulers.fxThread())
                                  .publishOn(anotherScheduler);
 ``` 
 
 ###### Events From A Window
  ```java
  Flux<WindowEvent> windowEvents = FxFlux.from(primaryStage, WindowEvent.WINDOW_HIDING)
                                   .subscribeOn(FxSchedulers.fxThread())
                                   .publishOn(anotherScheduler);
  ``` 

## ObservableValue
Updates of any JavaFX `ObservableValue` can be emitted onto a `Flux` by using the factory `FxFlux.from(ObservableValue<T> observableValue)` 
which creates a `Flux` that emits the initial value of the observable followed by any subsequent changes to the Observable. Often the
initial value of an `ObservableValue` is null. The reactive streams specification disallows null values in a sequence so these 
null values are not emitted.

```java
SimpleObjectProperty<String> observable = new SimpleObjectProperty<>();
Flux<String> flux = FxFlux.from(observable); 
```

Changes from an `ObservableValue` can also be emitted as a `Change` which is a pairing of the old value and the new value. 
This `Flux` can be produced from the factory `FxFlux.fromChangesOf(ObservableValue<T> observableValue)`. 
```java
SimpleObjectProperty<String> observable = new SimpleObjectProperty<>();
Flux<Change<String>> flux = FxFlux.fromChangesOf(observable)
                                  .filter(change -> "Hello".equals(change.getOldValue()))
                                  .filter(change -> "World".equals(change.getNewValue()));
```


## JavaFX Scheduler
JavaFX controls are required to be updated on the JavaFX Application Thread. `FxSchedulers.fxThread()` is a 
[Scheduler](https://projectreactor.io/docs/core/release/api/) that provides a way to easily the 
JavaFX Application Thread. Using this scheduler makes it possible to listen to JavaFX controls using Reactive Streams.

```java
ProgressBar p1 = new ProgressBar();

Flux.interval(Duration.ofMillis(1000))
    .map(l -> l/100.0)
    .publishOn(FxSchedulers.fxThread())
    .subscribe(p1::setProgress);
```


## JavaFX Collections Support
ReactorFX also provides fluent factories for creating a `Flux` from any [JavaFX Collection](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/package-summary.html) 
by four overloaded factory methods. 
```java
from()
```
Using this factory will produce a `Flux` that emits the argument JavaFX Collection whenever it has been changed.

```java
fromAdditionsOf()
```
Using this factory produces a `Flux` that emits any element added to the argument collection after it has been added.

```java
fromRemovalsOf()
```
Using this factory produces a `Flux` that emits any element removed from the argument collection whenever it has been 
removed.

```java
fromChangesOf()
```
This factory is only provided for [ObservableArray](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableArray.html)
and it emits the changed sub-array of the argument array whenever it has been changed.

#### Collections
* [ObservableList](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableList.html)
* [ObservableMap](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableMap.html)
* [ObservableSet](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableSet.html)
* [ObservableFloatArray](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableFloatArray.html)
* [ObservableIntegerArray](https://docs.oracle.com/javase/8/javafx/api/javafx/collections/ObservableIntegerArray.html)


_Licensed under [Apache Software License 2.0](www.apache.org/licenses/LICENSE-2.0)_