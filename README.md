# (FX)cellent Reactor
[![Build Status](https://travis-ci.org/shadskii/reactor-javafx.svg?branch=master)](https://travis-ci.org/shadskii/reactor-javafx)
[![codecov](https://codecov.io/gh/shadskii/reactor-javafx/branch/master/graph/badge.svg)](https://codecov.io/gh/shadskii/reactor-javafx)

This library allows for better integration between Project Reactor and JavaFX. `FxFluxFrom` minimizes the effort required for using Project Reactor for JavaFX event handling.

## Example Usage

```java
private Button btn;

FxFluxFrom.nodeActionEvent(btn)
          .subscribeOn(fxThead)
          .publishOn(anotherScheduler)
          .map(ActionEvent::getSource)
          .subscribe(System.out::println);
```

## API

#### Flux Sources
```java
nodeActionEvent(Node source)
```

```java
nodeEvent(Node source, EventType<T> eventType)
```

```java
sceneEvent(Scene source, EventType<T> eventType)
```

```java
stageEvent(Stage source, EventType<T> eventType)
```

```java
windowEvent(Window source, EventType<T> eventType)
```

##### Observable
```java
observable(ObservableValue<T> observableValue)
```

##### ObservableList
```java
observableList(ObservableList<T> source)
```

```java
observableListAdditions(ObservableList<T> source)
```

```java
observableListRemovals(ObservableList<T> source)
```
<br />

#### Schedulers
```java
platform()
```
A naive implementation of a JavaFX Scheduler that provides access to the JavaFX Event Dispatch thread.
