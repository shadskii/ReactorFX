# reactor-javafx
[![Build Status](https://travis-ci.org/shadskii/reactor-javafx.svg?branch=master)](https://travis-ci.org/shadskii/reactor-javafx)
[![codecov](https://codecov.io/gh/shadskii/reactor-javafx/branch/master/graph/badge.svg)](https://codecov.io/gh/shadskii/reactor-javafx)

This library allows for better integration between Project Reactor and JavaFX. `FxFluxFrom` minimizes the effort required for using Project Reactor for JavaFX event handling.

### Example Usage

```java
private Button btn;

FxFluxFrom.nodeActionEvent(btn)
          .subscribeOn(fxThead)
          .publishOn(anotherScheduler)
          .map(ActionEvent::getSource)
          .subscribe(System.out::println);
```
