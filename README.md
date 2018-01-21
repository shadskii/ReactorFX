# (FX)cellent Reactor
[![Build Status](https://travis-ci.org/shadskii/FXcellent-Reactor.svg?branch=master)](https://travis-ci.org/shadskii/FXcellent-Reactor)
[![codecov](https://codecov.io/gh/shadskii/FXcellent-Reactor/branch/master/graph/badge.svg)](https://codecov.io/gh/shadskii/FXcellent-Reactor)

This library allows for better integration between Project Reactor and JavaFX. `FxFlux` minimizes the effort required for using Project Reactor for JavaFX event handling.

## Example Usage

```java
private Button btn;

FxFlux.from(btn)
          .subscribeOn(fxThead)
          .publishOn(anotherScheduler)
          .map(ActionEvent::getSource)
          .subscribe(System.out::println);
```

## API

#### Flux Sources
```java
from(Node source)
```

```java
from(Node source, EventType<T> eventType)
```

```java
from(Scene source)
```

```java
from(Scene source, EventType<T> eventType)
```

```java
from(Stage source)
```

```java
from(Stage source, EventType<T> eventType)
```

```java
from(Window source)
```

```java
from(Window source, EventType<T> eventType)
```

##### Observable
```java
from(ObservableValue<T> observableValue)
```

<br />

#### JavaFX Collections Support

##### ObservableList
```java
fromList(ObservableList<T> source)
```

```java
fromListAdditions(ObservableList<T> source)
```

```java
fromListRemovals(ObservableList<T> source)
```

##### ObservableMap
```java
fromMap(ObservableMap<T,V> source)
```

```java
fromMapAdditions(ObservableMap<T,V> source)
```

```java
fromMapRemovals(ObservableMap<T,V> source)
```

##### ObservableSet
```java
fromSet(ObservableSet<T> source)
```

```java
fromSetAdditions(ObservableSet<T> source)
```

```java
fromSetRemovals(ObservableSet<T> source)
```

##### ObservableArray
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

#### Schedulers
```java
platform()
```
A naive implementation of a JavaFX Scheduler that provides access to the JavaFX Event Dispatch thread.
