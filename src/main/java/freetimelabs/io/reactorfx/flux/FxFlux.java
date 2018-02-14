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
 * This class allows for easy creation of listeners to JavaFX components as well as JavaFX Observables such as: <ul>
 * <li>{@link ObservableValue}</li> <li>{@link ObservableList}</li> <li>{@link ObservableMap}</li> <li>{@link
 * ObservableSet}</li> <li>{@link ObservableIntegerArray}</li> <li>{@link ObservableFloatArray}</li> </ul>
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
     * FxSchedulers#platform()}. Equivalent to calling {@link FxFlux#from(Dialog, Scheduler)} with {@link
     * FxSchedulers#platform()}.
     *
     * @param source The Dialog to listen to.
     * @param <T>    The type of the from.
     * @return A Mono which emits when the from has been selected.
     */
    public static <T> Mono<T> from(final Dialog<T> source)
    {
        return DialogSource.fromDialog(source, FxSchedulers.platform());
    }

    /**
     * Creates a {@link Mono} which emits when the argument Dialog has been finished. This will not emit if nothing is
     * selected from the from. The argument {@link Scheduler} will be used for listening for events.
     *
     * @param source    The Dialog to listen to.
     * @param scheduler The Scheduler that the from will show on. This should provide access to the JavaFX application
     *                  thread.
     * @param <T>       The type of the Dialog
     * @return A mono which emits when the Dialog has been selected.
     */
    public static <T> Mono<T> from(final Dialog<T> source, Scheduler scheduler)
    {
        return DialogSource.fromDialog(source, scheduler);
    }

    /**
     * Creates a {@link Flux} which emits all {@link Event} of the argument {@link EventType} from the argument {@link
     * MenuItem}.
     *
     * @param source    The target MenuItem where UI events are emitted from.
     * @param eventType The type of event to listen for.
     * @param <T>       The event type
     * @return A Flux that emits all events of the argument type that originate form the argument node.
     */
    public static <T extends Event> Flux<T> from(MenuItem source, EventType<T> eventType)
    {
        return SceneGraphSource.menuItemEvent(source, eventType);
    }

    /**
     * Creates a {@link Flux} which emits any {@link ActionEvent} that originates from the argument {@link MenuItem}.
     * Equivalent to using {@link #from(MenuItem, EventType)} with {@link ActionEvent#ANY}.
     *
     * @param source The target MenuItem where UI events are emitted from.
     * @return A Flux that emits all events of the argument type that originate form the argument node.
     */
    public static Flux<ActionEvent> from(MenuItem source)
    {
        return SceneGraphSource.menuItemEvent(source, ActionEvent.ANY);
    }

    /**
     * Creates a {@link Flux} which emits all Events of the argument {@link EventType} from the argument {@link Node}
     *
     * @param source    The target Node where UI events are emitted from.
     * @param eventType The type of event to listen for.
     * @param <T>       The event type.
     * @return A Flux that emits all events of the argument type that originate from the argument node.
     */
    public static <T extends Event> Flux<T> from(Node source, EventType<T> eventType)
    {
        return SceneGraphSource.nodeEvent(source, eventType);
    }

    /**
     * Creates a {@link Flux} that emits any {@link ActionEvent} that originates from the argument Node. Equivalent to
     * using {@link #from(Node, EventType)} with {@link ActionEvent#ANY}.
     *
     * @param source The target node where events originate from.
     * @return A Flux containing all {@link ActionEvent}s from the argument node.
     */
    public static Flux<ActionEvent> from(Node source)
    {
        return from(source, ActionEvent.ANY);
    }

    /**
     * Creates a {@link Flux} which emits all Events of the argument {@link EventType} from the argument {@link Scene}.
     *
     * @param source    The target Scene where UI events are emitted from.
     * @param eventType The type of event to listen for.
     * @param <T>       The event type.
     * @return A Flux that emits all events of the argument type that originate from the argument Scene.
     */
    public static <T extends Event> Flux<T> from(Scene source, EventType<T> eventType)
    {
        return SceneGraphSource.sceneEvent(source, eventType);
    }

    /**
     * Creates a {@link Flux} which emits any {@link ActionEvent} from the argument {@link Scene}. Equivalent to using
     * {@link #from(Scene, EventType)} with {@link ActionEvent#ANY}.
     *
     * @param source The target Scene where UI events are emitted from.
     * @return A Flux that emits all ActionEvents from the argument scene.
     */
    public static Flux<ActionEvent> from(Scene source)
    {
        return SceneGraphSource.sceneEvent(source, ActionEvent.ANY);
    }

    /**
     * Creates a {@link Flux} which emits all Events of the argument {@link EventType} from the argument {@link
     * Window}.
     *
     * @param source    The target Window where UI events are emitted from.
     * @param eventType The type of event to listen for.
     * @param <T>       The event type.
     * @return A Flux that emits all events of the argument type that originate from the argument Window.
     */
    public static <T extends Event> Flux<T> from(Window source, EventType<T> eventType)
    {
        return SceneGraphSource.windowEvent(source, eventType);
    }

    /**
     * Creates a {@link Flux} which emits any {@link ActionEvent} from the argument {@link Window}. Equivalent to using
     * {@link #from(Window, EventType)} with {@link ActionEvent#ANY}.
     *
     * @param source The target Window where UI events are emitted from.
     * @return A Flux that emits all events of the argument type that originate from the argument Window.
     */
    public static Flux<ActionEvent> from(Window source)
    {
        return SceneGraphSource.windowEvent(source, ActionEvent.ANY);
    }

    /**
     * Creates a {@link Flux} which emits whenever the argument {@link ObservableValue} is changed. This will not
     * provide an emission if the changed value is null. The initial value of the {@link ObservableValue} will be
     * emitted as the first emission of this {@link Flux}.
     *
     * @param observableValue The ObservableValue to listen for changes.
     * @param <T>             The type of the Observable.
     * @return A Flux that emits the newest value of the argument from when it has been changed.
     */
    public static <T> Flux<T> from(ObservableValue<T> observableValue)
    {
        return ObservableSource.from(observableValue);
    }

    /**
     * Creates a {@link Flux} which emits whenever the argument {@link ObservableValue} is changed. This emits a {@link
     * Change} which contains both the new value and the old value of the change to the observable.
     *
     * @param observableValue The ObservableValue to listen to for changes.
     * @param <T>             The type of the Observable.
     * @return A Flux that emits a change that contains both the new and old values of a change.
     */
    public static <T> Flux<Change<T>> fromChangesOf(ObservableValue<T> observableValue)
    {
        return ObservableSource.fromChangesOf(observableValue);
    }

    /**
     * Creates a {@link Flux} that emits the argument {@link ObservableList} every time it has been updated.
     *
     * @param source The ObservableList to listen to.
     * @param <T>    The type of the ObservableList
     * @return A Flux that emits the argument list whenever it has ben changed.
     */
    public static <T> Flux<ObservableList<T>> from(ObservableList<T> source)
    {
        return ObservableListSource.observableList(source);
    }

    /**
     * Creates a {@link Flux} that listens for changes to the argument {@link ObservableList} and emits all of the
     * additions to the list whenever it has been updated.
     *
     * @param source The ObservableList to listen to.
     * @param <T>    The type of the ObservableList.
     * @return A Flux that emits the additions to the list whenever it has been changed.
     */
    public static <T> Flux<T> fromAdditionsOf(ObservableList<T> source)
    {
        return ObservableListSource.additions(source);
    }

    /**
     * Creates a {@link Flux} that listens for changes to the argument {@link ObservableList} and emits all of the
     * removals to the list whenever it has been updated.
     *
     * @param source The ObservableList to listen to.
     * @param <T>    The type of the ObservableList.
     * @return A Flux that emits the removals to the list whenever it has been changed.
     */
    public static <T> Flux<T> fromRemovalsOf(ObservableList<T> source)
    {
        return ObservableListSource.removals(source);
    }

    /**
     * Creates a {@link Flux} that listens for changes to am {@link ObservableMap} and emits the argument ObservableMap
     * whenever it has been updated.
     *
     * @param source The ObservableMap to listen to.
     * @param <T>    The key type of the ObservableMap.
     * @param <V>    The value type of the ObservableMap.
     * @return A Flux that emits the ObservableMap whenever it gets updated.
     */
    public static <T, V> Flux<ObservableMap<T, V>> from(ObservableMap<T, V> source)
    {
        return ObservableMapSource.observableMap(source);
    }

    /**
     * Creates a {@link Flux} that listens for changes to an {@link ObservableMap} and emits any additions to the
     * argument ObservableMap.
     *
     * @param source The ObservableMap to listen to for additions.
     * @param <T>    The key type of the ObservableMap.
     * @param <V>    The value type of the ObservableMap.
     * @return A Flux that emits any entry added to the argument ObservableMap.
     */
    public static <T, V> Flux<Map.Entry<T, V>> fromMapAdditions(ObservableMap<T, V> source)
    {
        return ObservableMapSource.additions(source);
    }

    /**
     * Creates a {@link Flux} that listens for changes to an {@link ObservableMap} and emits any removals to the
     * argument ObservableMap.
     *
     * @param source The ObservableMap to listen to for removals.
     * @param <T>    The key type of the ObservableMap.
     * @param <V>    The value type of the ObservableMap.
     * @return A Flux that emits any entry removed from the argument ObservableMap.
     */
    public static <T, V> Flux<Map.Entry<T, V>> fromMapRemovals(ObservableMap<T, V> source)
    {
        return ObservableMapSource.removals(source);
    }

    /**
     * Creates a Flux that listens for changes to an {@link ObservableSet} and emits the set whenever there is a change
     * to it.
     *
     * @param source The ObservableSet to listen to.
     * @param <T>    The type contained by the ObservableSet.
     * @return A flux that emits the argument ObservableSet whenever it has been updated.
     */
    public static <T> Flux<ObservableSet<T>> fromSet(ObservableSet<T> source)
    {
        return ObservableSetSource.observableSet(source);
    }

    /**
     * Creates a Flux that listens for changes to {@link ObservableSet} and emits any additions to it.
     *
     * @param source The ObservableSet to listen to for additions.
     * @param <T>    The type contained by the ObservableSet.
     * @return A Flux that emits any addition to the argument ObservableSet.
     */
    public static <T> Flux<T> fromSetAdditions(ObservableSet<T> source)
    {
        return ObservableSetSource.additions(source);
    }

    /**
     * Creates a Flux that listens for changes to {@link ObservableSet} and emits any removals to it.
     *
     * @param source The ObservableSet to listen to for removals.
     * @param <T>    Type contained by the ObservableSet
     * @return A Flux that emits any removals to the argument ObservableSet.
     */
    public static <T> Flux<T> fromSetRemovals(ObservableSet<T> source)
    {
        return ObservableSetSource.removals(source);
    }

    /**
     * Creates a Flux that listens for changes to a {@link ObservableIntegerArray} and emits the entire array whenever
     * it has been changed.
     *
     * @param source - The ObservableIntegerArray to listen to for changes.
     * @return A Flux that emits the argument ObservableIntegerArray whenever it has been updated.
     */
    public static Flux<ObservableIntegerArray> fromArray(ObservableIntegerArray source)
    {
        return ObservableArraySource.observableArray(source);
    }

    /**
     * Creates a Flux that listens for changes to a {@link ObservableFloatArray} and emits the entire array whenever it
     * has been changed.
     *
     * @param source - The ObservableFloatArray to listen to for changes.
     * @return A Flux that emits the argument ObservableFloatArray whenever it has been updated.
     */
    public static Flux<ObservableFloatArray> fromArray(ObservableFloatArray source)
    {
        return ObservableArraySource.observableArray(source);
    }

    /**
     * Creates a Flux that listens for changes to a {@link ObservableIntegerArray} and emits the changed sub-array of
     * the array whenever it has been changed.
     *
     * @param source - The ObservableIntegerArray to listen to for changes.
     * @return A Flux that emits the changed sub-array of the argument ObservableIntegerArray whenever it has been
     * updated.
     */
    public static Flux<ObservableIntegerArray> fromArrayChanges(ObservableIntegerArray source)
    {
        return ObservableArraySource.observableIntegerSubArray(source);
    }

    /**
     * Creates a Flux that listens for changes to a {@link ObservableFloatArray} and emits the changed sub-array of the
     * array whenever it has been changed.
     *
     * @param source - The ObservableFloatArray to listen to for changes.
     * @return A Flux that emits the changed sub-array of the argument ObservableFloatArray whenever it has been
     * updated.
     */
    public static Flux<ObservableFloatArray> fromArrayChanges(ObservableFloatArray source)
    {
        return ObservableArraySource.observableFloatSubArray(source);
    }
}
