# reactor-javafx
This library allows for better integration between Project Reactor and JavaFX. `FxFluxFrom` minimizes the effort required for using Project Reactor for JavaFX event handling.

### Example Usage

```java
private Button btn;

FxFluxFrom.nodeActionEvent(btn)
          .publishOn(anotherScheduler)
          .map(ActionEvent::getSource)
          .subscribe(System.out::println);
```
