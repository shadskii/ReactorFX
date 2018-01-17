# (FX)cellent Reactor
[![Build Status](https://travis-ci.org/shadskii/FXcellent-Reactor.svg?branch=master)](https://travis-ci.org/shadskii/FXcellent-Reactor)
[![codecov](https://codecov.io/gh/shadskii/FXcellent-Reactor/branch/master/graph/badge.svg)](https://codecov.io/gh/shadskii/FXcellent-Reactor)

This library allows for better integration between Project Reactor and JavaFX. `FxFlux` minimizes the effort required for using Project Reactor for JavaFX event handling.

## Example Usage

```java
private Button btn;

FxFlux.fromActionEventsOf(btn)
          .subscribeOn(fxThead)
          .publishOn(anotherScheduler)
          .map(ActionEvent::getSource)
          .subscribe(System.out::println);
```

## API

#### Flux Sources
```java
fromActionEventsOf(Node source)
```

```java
from(Node source, EventType<T> eventType)
```

```java
from(Scene source, EventType<T> eventType)
```

```java
from(Stage source, EventType<T> eventType)
```

```java
from(Window source, EventType<T> eventType)
```

##### Observable
```java
from(ObservableValue<T> observableValue)
```

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
<br />

#### Schedulers
```java
platform()
```
A naive implementation of a JavaFX Scheduler that provides access to the JavaFX Event Dispatch thread.
