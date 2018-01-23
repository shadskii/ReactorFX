# (FX)cellent Reactor
[![Build Status](https://travis-ci.org/shadskii/FXcellent-Reactor.svg?branch=master)](https://travis-ci.org/shadskii/FXcellent-Reactor)
[![codecov](https://codecov.io/gh/shadskii/FXcellent-Reactor/branch/master/graph/badge.svg)](https://codecov.io/gh/shadskii/FXcellent-Reactor)

This library allows for better integration between Project Reactor and JavaFX. `FxFlux` minimizes the effort required for using Project Reactor for JavaFX event handling.

## Example Usage

```java
private Button btn;

Flux<Event> buttonEvents = FxFlux.from(btn)
                                 .subscribeOn(FxSchedulers.platform())
                                 .publishOn(anotherScheduler);
```

## API

#### Flux Sources
FXcellent Reactor provides provides simple and fluent factories for Fluxes
###### Node
```java
from(Node source)
```

```java
from(Node source, EventType<T> eventType)
```

###### Scene
```java
from(Scene source)
```

```java
from(Scene source, EventType<T> eventType)
```

###### Window
```java
from(Window source)
```

```java
from(Window source, EventType<T> eventType)
```

###### Observable
```java
from(ObservableValue<T> observableValue)
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

