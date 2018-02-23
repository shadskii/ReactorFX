/*
 * Copyright 2017 Jacob Hassel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package freetimelabs.io.reactorfx.flux;

import freetimelabs.io.reactorfx.schedulers.FxSchedulers;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Map;

/**
 * In JavaFX actions from external sources are propagated through {@link Event}. These Events can be emitted from {@link
 * Node}, {@link Scene}, {@link MenuItem}, and {@link Window}. ReactorFX provides simple, fluent, and consistent
 * factories for the creation of {@link Flux} from these sources. You can create Fluxes by using {@link
 * FxFlux#from(Node)} and passing the source and {@link EventType} to listen to. {@link FxFlux#from(Node, EventType)}
 * provides overloaded factories such that omitting the {@link EventType} will result in a {@link Flux} that listens for
 * {@link ActionEvent}.
 * <p>
 * <h4>Events From A {@link javafx.scene.control.Control}</h4>
 * <pre>
 * {@code
 * Button btn = new Button("Hey I'm A Button!");
 * Flux<Event> buttonEvents = FxFlux.from(btn)
 * .subscribeOn(FxSchedulers.fxThread())
 * .publishOn(anotherScheduler);
 * }
 * </pre>
 * <p>
 * <h4>Events From A {@link Scene}</h4>
 * <pre>
 * {@code
 * Scene scene = new Scene(new Label("Hey I'm A Label!"));
 * Flux<MouseEvent> mouseEvents = FxFlux.from(scene, MouseEvent.MOUSE_CLICKED)
 * .subscribeOn(FxSchedulers.fxThread())
 * .publishOn(anotherScheduler);
 * }
 * </pre>
 * <h4>Events From A {@link Window}</h4>
 * <pre>
 * {@code
 * Flux<WindowEvent> windowEvents = FxFlux.from(primaryStage, WindowEvent.WINDOW_HIDING)
 * .subscribeOn(FxSchedulers.fxThread())
 * .publishOn(anotherScheduler);
 * }
 * </pre>
 * <h3>ObservableValue</h3> Updates of any JavaFX {@link ObservableValue} can be emitted onto a {@link Flux} by using
 * the factory {@link FxFlux#from(ObservableValue)} which creates a {@link Flux} that emits the initial value of the
 * observable followed by any subsequent changes to the {@link javafx.beans.Observable}. Often the initial value of an
 * {@link ObservableValue} is null. The reactive streams specification disallows null values in a sequence so these null
 * values are not emitted.
 * <p>
 * <pre>
 * {@code
 *      SimpleObjectProperty<String> observable = new SimpleObjectProperty<>();
 *      Flux<String> flux = FxFlux.from(observable);
 * }
 * </pre>
 * <p>
 * Changes from an {@link ObservableValue} can also be emitted as a {@link Change} which is a pairing of the old value
 * and the new value. This {@link Flux} can be produced from the factory {@link FxFlux#fromChangesOf(ObservableValue)}.
 * <pre>
 * {@code
 *      SimpleObjectProperty<String> observable = new SimpleObjectProperty<>();
 *      Flux<Change<String>> flux = FxFlux.fromChangesOf(observable)
 *          .filter(change -> "Hello".equals(change.getOldValue()))
 *          .filter(change -> "World".equals(change.getNewValue()));
 * }
 * </pre>
 * <h3>JavaFX Collections Support</h3> ReactorFX also provides fluent factories for creating a Flux from any JavaFX
 * collection by four overloaded factory methods.
 * <p>
 * {@code from()} Using this factory will produce a Flux that emits the argument JavaFX Collection whenever it has been
 * changed.
 * <p>
 * {@code fromAdditionsOf() }Using this factory produces a Flux that emits any element added to the argument collection
 * after it has been added.
 * <p>
 * {@code fromRemovalsOf()} Using this factory produces a Flux that emits any element removed from the argument
 * collection whenever it has been removed.
 * <p>
 * {@code fromChangesOf()} This factory is only provided for ObservableArray and it emits the changed sub-array of the
 * argument array whenever it has been changed.
 * <p>
 * <h4>Collections </h4> <ul> <li>{@link ObservableList}</li> <li>{@link ObservableMap}</li> <li>{@link
 * ObservableSet}</li> <li> {@link ObservableFloatArray}</li> <li>{@link ObservableIntegerArray}</li> </ul>
 */
public final class FxFlux
{
    private FxFlux()
    {
        // No instance
    }

    /**
     * Creates a {@link Mono} which emits when the argument Dialog has been finished. This will not emit if nothing is
     * selected from the Dialog. The {@link Scheduler} used to listen for events will be {@link
     * FxSchedulers#fxThread()}. Equivalent to calling {@link FxFlux#from(Dialog, Scheduler)} with {@link
     * FxSchedulers#fxThread()}.
     *
     * @param source The {@link Dialog} to listen to.
     * @param <T>    The type of the from.
     * @return A Mono which emits when {@link Dialog} has been closed.
     */
    public static <T> Mono<T> from(final Dialog<T> source)
    {
        return from(source, FxSchedulers.fxThread());
    }

    /**
     * Creates a {@link Mono} which emits when the argument {@link Dialog} has been finished. This will not emit if
     * nothing is selected from the from. The argument {@link Scheduler} will be used for listening for events.
     *
     * @param source    The {@link Dialog} to listen to.
     * @param scheduler The {@link Scheduler} that the from will show on. This should provide access to the JavaFX
     *                  application thread.
     * @param <T>       The type of the {@link Dialog}
     * @return A mono which emits when the {@link Dialog} has been selected.
     */
    public static <T> Mono<T> from(final Dialog<T> source, Scheduler scheduler)
    {
        return DialogSource.fromDialog(source, scheduler);
    }

    /**
     * Creates a {@link Flux} which emits all {@link Event} of the argument {@link EventType} from the argument {@link
     * MenuItem}.
     *
     * @param source    The target {@link MenuItem} where UI events are emitted from.
     * @param eventType The type of event to listen for.
     * @param <T>       The event type
     * @return A {@link Flux} that emits all events of the argument type that originate form the argument {@link
     * MenuItem}.
     */
    public static <T extends Event> Flux<T> from(MenuItem source, EventType<T> eventType)
    {
        return SceneGraphSource.menuItemEvent(source, eventType);
    }

    /**
     * Creates a {@link Flux} which emits any {@link ActionEvent} that originates from the argument {@link MenuItem}.
     * Equivalent to using {@link #from(MenuItem, EventType)} with {@link ActionEvent#ANY}.
     *
     * @param source The target {@link MenuItem} where UI events are emitted from.
     * @return A {@link Flux} that emits all events of the argument type that originate form the argument {@link
     * MenuItem}.
     */
    public static Flux<ActionEvent> from(MenuItem source)
    {
        return from(source, ActionEvent.ANY);
    }

    /**
     * Creates a {@link Flux} which emits all Events of the argument {@link EventType} from the argument {@link Node}
     *
     * @param source    The target {@link Node} where UI events are emitted from.
     * @param eventType The type of event to listen for.
     * @param <T>       The event type.
     * @return A {@link Flux} that emits all events of the argument type that originate from the argument {@link Node}.
     */
    public static <T extends Event> Flux<T> from(Node source, EventType<T> eventType)
    {
        return SceneGraphSource.nodeEvent(source, eventType);
    }

    /**
     * Creates a {@link Flux} that emits any {@link ActionEvent} that originates from the argument {@link Node}.
     * Equivalent to using {@link #from(Node, EventType)} with {@link ActionEvent#ANY}.
     *
     * @param source The target node where events originate from.
     * @return A {@link Flux} containing all {@link ActionEvent}s from the argument {@link Node}.
     */
    public static Flux<ActionEvent> from(Node source)
    {
        return from(source, ActionEvent.ANY);
    }

    /**
     * Creates a {@link Flux} which emits all Events of the argument {@link EventType} from the argument {@link Scene}.
     *
     * @param source    The target {@link Scene} where UI events are emitted from.
     * @param eventType The type of event to listen for.
     * @param <T>       The event type.
     * @return A {@link Flux} that emits all events of the argument type that originate from the argument {@link Scene}.
     */
    public static <T extends Event> Flux<T> from(Scene source, EventType<T> eventType)
    {
        return SceneGraphSource.sceneEvent(source, eventType);
    }

    /**
     * Creates a {@link Flux} which emits any {@link ActionEvent} from the argument {@link Scene}. Equivalent to using
     * {@link #from(Scene, EventType)} with {@link ActionEvent#ANY}.
     *
     * @param source The target {@link Scene} where UI events are emitted from.
     * @return A {@link Flux} that emits all {@link ActionEvent} from the argument {@link Scene}.
     */
    public static Flux<ActionEvent> from(Scene source)
    {
        return from(source, ActionEvent.ANY);
    }

    /**
     * Creates a {@link Flux} which emits all Events of the argument {@link EventType} from the argument {@link
     * Window}.
     *
     * @param source    The target {@link Window} where UI events are emitted from.
     * @param eventType The type of event to listen for.
     * @param <T>       The event type.
     * @return A {@link Flux} that emits all events of the argument type that originate from the argument {@link
     * Window}.
     */
    public static <T extends Event> Flux<T> from(Window source, EventType<T> eventType)
    {
        return SceneGraphSource.windowEvent(source, eventType);
    }

    /**
     * Creates a {@link Flux} which emits any {@link ActionEvent} from the argument {@link Window}. Equivalent to using
     * {@link #from(Window, EventType)} with {@link ActionEvent#ANY}.
     *
     * @param source The target {@link Window} where UI events are emitted from.
     * @return A {@link Flux} that emits all events of the argument type that originate from the argument {@link
     * Window}.
     */
    public static Flux<ActionEvent> from(Window source)
    {
        return from(source, ActionEvent.ANY);
    }

    /**
     * Creates a {@link Flux} which emits whenever the argument {@link ObservableValue} is changed. This will not
     * provide an emission if the changed value is null. The initial value of the {@link ObservableValue} will be
     * emitted as the first emission of this {@link Flux}.
     *
     * @param observableValue The {@link ObservableValue} to listen for changes.
     * @param <T>             The type of the Observable.
     * @return A {@link Flux} that emits the newest value of the argument from when it has been changed.
     */
    public static <T> Flux<T> from(ObservableValue<T> observableValue)
    {
        return ObservableSource.from(observableValue);
    }

    /**
     * Creates a {@link Flux} which emits whenever the argument {@link ObservableValue} is changed. This emits a {@link
     * Change} which contains both the new value and the old value of the change to the observable.
     *
     * @param observableValue The {@link ObservableValue} to listen to for changes.
     * @param <T>             The type of the Observable.
     * @return A {@link Flux} that emits a change that contains both the new and old values of a change.
     */
    public static <T> Flux<Change<T>> fromChangesOf(ObservableValue<T> observableValue)
    {
        return ObservableSource.fromChangesOf(observableValue);
    }

    /**
     * Creates a {@link Flux} that emits the argument {@link ObservableList} every time it has been updated.
     *
     * @param source The {@link ObservableList} to listen to.
     * @param <T>    The type of the {@link ObservableList}
     * @return A {@link Flux} that emits the argument list whenever it has ben changed.
     */
    public static <T> Flux<ObservableList<T>> from(ObservableList<T> source)
    {
        return ObservableListSource.observableList(source);
    }

    /**
     * Creates a {@link Flux} that listens for changes to the argument {@link ObservableList} and emits all of the
     * additions to the list whenever it has been updated.
     *
     * @param source The {@link ObservableList} to listen to.
     * @param <T>    The type of the {@link ObservableList}.
     * @return A {@link Flux} that emits the additions to the list whenever it has been changed.
     */
    public static <T> Flux<T> fromAdditionsOf(ObservableList<T> source)
    {
        return ObservableListSource.additions(source);
    }

    /**
     * Creates a {@link Flux} that listens for changes to the argument {@link ObservableList} and emits all of the
     * removals to the list whenever it has been updated.
     *
     * @param source The {@link ObservableList} to listen to.
     * @param <T>    The type of the {@link ObservableList}.
     * @return A {@link Flux} that emits the removals to the list whenever it has been changed.
     */
    public static <T> Flux<T> fromRemovalsOf(ObservableList<T> source)
    {
        return ObservableListSource.removals(source);
    }

    /**
     * Creates a {@link Flux} that listens for changes to am {@link ObservableMap} and emits the argument {@link
     * ObservableMap} whenever it has been updated.
     *
     * @param source The {@link ObservableMap} to listen to.
     * @param <T>    The key type of the {@link ObservableMap}.
     * @param <V>    The value type of the {@link ObservableMap}.
     * @return A {@link Flux} that emits the {@link ObservableMap} whenever it gets updated.
     */
    public static <T, V> Flux<ObservableMap<T, V>> from(ObservableMap<T, V> source)
    {
        return ObservableMapSource.observableMap(source);
    }

    /**
     * Creates a {@link Flux} that listens for changes to an {@link ObservableMap} and emits any additions to the
     * argument {@link ObservableMap}.
     *
     * @param source The {@link ObservableMap} to listen to for additions.
     * @param <T>    The key type of the {@link ObservableMap}.
     * @param <V>    The value type of the {@link ObservableMap}.
     * @return A {@link Flux} that emits any entry added to the argument {@link ObservableMap}.
     */
    public static <T, V> Flux<Map.Entry<T, V>> fromAdditionsOf(ObservableMap<T, V> source)
    {
        return ObservableMapSource.additions(source);
    }

    /**
     * Creates a {@link Flux} that listens for changes to an {@link ObservableMap} and emits any removals to the
     * argument {@link ObservableMap}.
     *
     * @param source The {@link ObservableMap} to listen to for removals.
     * @param <T>    The key type of the {@link ObservableMap}.
     * @param <V>    The value type of the ObservableMap.
     * @return A {@link Flux} that emits any entry removed from the argument {@link ObservableMap}.
     */
    public static <T, V> Flux<Map.Entry<T, V>> fromRemovalsOf(ObservableMap<T, V> source)
    {
        return ObservableMapSource.removals(source);
    }

    /**
     * Creates a {@link Flux} that listens for changes to an {@link ObservableSet} and emits the set whenever there is a
     * change to it.
     *
     * @param source The {@link ObservableSet} to listen to.
     * @param <T>    The type contained by the {@link ObservableSet}.
     * @return A {@link Flux} that emits the argument {@link ObservableSet} whenever it has been updated.
     */
    public static <T> Flux<ObservableSet<T>> from(ObservableSet<T> source)
    {
        return ObservableSetSource.observableSet(source);
    }

    /**
     * Creates a {@link Flux} that listens for changes to {@link ObservableSet} and emits any additions to it.
     *
     * @param source The {@link ObservableSet} to listen to for additions.
     * @param <T>    The type contained by the {@link ObservableSet}.
     * @return A {@link Flux} that emits any addition to the argument {@link ObservableSet}.
     */
    public static <T> Flux<T> fromAdditionsOf(ObservableSet<T> source)
    {
        return ObservableSetSource.additions(source);
    }

    /**
     * Creates a {@link Flux} that listens for changes to {@link ObservableSet} and emits any removals to it.
     *
     * @param source The {@link ObservableSet} to listen to for removals.
     * @param <T>    Type contained by the {@link ObservableSet}
     * @return A {@link Flux} that emits any removals to the argument {@link ObservableSet}.
     */
    public static <T> Flux<T> fromRemovalsOf(ObservableSet<T> source)
    {
        return ObservableSetSource.removals(source);
    }

    /**
     * Creates a Flux that listens for changes to a {@link ObservableIntegerArray} and emits the entire array whenever
     * it has been changed.
     *
     * @param source - The {@link ObservableIntegerArray} to listen to for changes.
     * @return A {@link Flux} that emits the argument {@link ObservableIntegerArray} whenever it has been updated.
     */
    public static Flux<ObservableIntegerArray> from(ObservableIntegerArray source)
    {
        return ObservableArraySource.observableArray(source);
    }

    /**
     * Creates a Flux that listens for changes to a {@link ObservableFloatArray} and emits the entire array whenever it
     * has been changed.
     *
     * @param source - The {@link ObservableFloatArray} to listen to for changes.
     * @return A {@link Flux} that emits the argument {@link ObservableFloatArray} whenever it has been updated.
     */
    public static Flux<ObservableFloatArray> from(ObservableFloatArray source)
    {
        return ObservableArraySource.observableArray(source);
    }

    /**
     * Creates a Flux that listens for changes to a {@link ObservableIntegerArray} and emits the changed sub-array of
     * the array whenever it has been changed.
     *
     * @param source - The ObservableIntegerArray to listen to for changes.
     * @return A {@link Flux} that emits the changed sub-array of the argument {@link ObservableIntegerArray} whenever
     * it has been updated.
     */
    public static Flux<ObservableIntegerArray> fromChangesOf(ObservableIntegerArray source)
    {
        return ObservableArraySource.observableIntegerSubArray(source);
    }

    /**
     * Creates a Flux that listens for changes to a {@link ObservableFloatArray} and emits the changed sub-array of the
     * array whenever it has been changed.
     *
     * @param source - The ObservableFloatArray to listen to for changes.
     * @return A {@link Flux} that emits the changed sub-array of the argument {@link ObservableFloatArray} whenever it
     * has been updated.
     */
    public static Flux<ObservableFloatArray> fromChangesOf(ObservableFloatArray source)
    {
        return ObservableArraySource.observableFloatSubArray(source);
    }
}
