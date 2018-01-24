# (FX)cellent Reactor
[![Build Status](https://travis-ci.org/shadskii/FXcellent-Reactor.svg?branch=master)](https://travis-ci.org/shadskii/FXcellent-Reactor)
[![codecov](https://codecov.io/gh/shadskii/FXcellent-Reactor/branch/master/graph/badge.svg)](https://codecov.io/gh/shadskii/FXcellent-Reactor)

This lightweight library allows for better integration between Project Reactor and JavaFX. `FxFlux` minimizes the effort required for using Project Reactor for JavaFX event handling.

## Example Usage

```java
private Button btn;

Flux<Event> buttonEvents = FxFlux.from(btn)
                                 .subscribeOn(FxSchedulers.platform())
                                 .publishOn(anotherScheduler);
```

#### Events
In JavaFX actions from external sources are propagated through [Events.](https://docs.oracle.com/javase/8/javafx/api/javafx/event/Event.html) 
These Events can be emitted from `Node`, `Scene`, and `Window`. FXcellent Reactor provides provides simple and fluent factories for the creation 
of Fluxes from these sources. You can create Fluxes from these by using `FxFlux.from()` and passing the source and `EventType`
 to listen to. `FxFlux.from()` provides overloaded factories so that omitting the `EventType` will result in a `Flux` that 
 listens for `ActionEvents`.
###### Node
```java
FxFlux.from(Node source)
```
```java
FxFlux.from(Node source, EventType<T> eventType)
```
###### Scene
```java
FxFlux.from(Scene source)
```
```java
FxFlux.from(Scene source, EventType<T> eventType)
```
###### Window
```java
FxFlux.from(Window source)
```
```java
FxFlux.from(Window source, EventType<T> eventType)
```

#### ObservableValue
Updates of any JavaFx `ObservableValue` can be emitted onto a `Flux` by using the factory `FxFlux.from(ObservableValue<T> observableValue)` 
which creates a `Flux` that emits the initial value of the observable followed by any subsequent changes to the Observable. Often the
initial value of an `ObservableValue` is null. The reactive streams specification disallows null values in a sequence so these 
null values are not emitted.

```java
SimpleObjectProperty<String> observable = new SimpleObjectProperty<>();
Flux<String> flux = FxFlux.from(observable); 
```

<br />

#### JavaFX Collections Support

###### ObservableList
```java
fromList(ObservableList<T> source)
```

```java
fromListAdditions(ObservableList<T> source)
```

```java
fromListRemovals(ObservableList<T> source)
```

###### ObservableMap
```java
fromMap(ObservableMap<T,V> source)
```

```java
fromMapAdditions(ObservableMap<T,V> source)
```

```java
fromMapRemovals(ObservableMap<T,V> source)
```

###### ObservableSet
```java
fromSet(ObservableSet<T> source)
```

```java
fromSetAdditions(ObservableSet<T> source)
```

```java
fromSetRemovals(ObservableSet<T> source)
```

###### ObservableArray
```java
fromIntegerArray(ObservableIntegerArray<T> source)
```

```java
fromFloatArray(ObservableFloatArray<T> source)
```

```java
fromArrayChanges(ObservableIntegerArray<T> source)
```
```java
fromArrayChanges(ObservableFloatArray<T> source)
```

<br />

#### JavaFX Scheduler
JavaFX controls are required to be updated on the JavaFX Event Dispatch Thread. `FxSchedulers.platform()` is a 
[Scheduler](https://projectreactor.io/docs/core/release/api/) that provides a way to easily Schedule tasks on the 
JavaFX Thread. Using this scheduler makes it possible to JavaFX controls using Reactive Streams.

```java
ProgressBar p1 = new ProgressBar();

Flux.interval(Duration.ofMillis(1000))
    .map(l -> l/100.0)
    .publishOn(FxSchedulers.platform())
    .subscribe(p1::setProgress);
```

