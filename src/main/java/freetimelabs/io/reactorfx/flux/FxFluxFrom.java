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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.Window;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/**
 * This class allows for easy creation of listeners to JavaFX components.
 */
public final class FxFluxFrom
{
    private FxFluxFrom()
    {
        // No instance
    }

    /**
     * Creates a {@link Mono} which emits when the argument Dialog has been finished. This will not emit if nothing is
     * selected from the dialog. The {@link Scheduler} used to listen for events will be {@link
     * FxSchedulers#platform()}. Equivalent to calling {@link FxFluxFrom#dialog(Dialog, Scheduler)} with {@link
     * FxSchedulers#platform()}.
     *
     * @param source - The dialog to listen to.
     * @param <T>    - The type of the dialog.
     * @return A Mono which emits when the dialog has been selected.
     */
    public static <T> Mono<T> dialog(final Dialog<T> source)
    {
        return DialogSource.fromDialog(source, FxSchedulers.platform());
    }

    /**
     * Creates a {@link Mono} which emits when the argument Dialog has been finished. This will not emit if nothing is
     * selected from the dialog. The argument {@link Scheduler} will be used for listening for events
     *
     * @param source    - The dialog ot listen to.
     * @param scheduler - The Scheduler that the dialog will show on. This should provide access to the JavaFX
     *                  application thread.
     * @param <T>       - The type of the dialog
     * @return A mono which emits when the dialog has been selected.
     */
    public static <T> Mono<T> dialog(final Dialog<T> source, Scheduler scheduler)
    {
        return DialogSource.fromDialog(source, scheduler);
    }

    /**
     * Creates a {@link Flux} which emits all {@link Event} of the argument {@link EventType} from the argument {@link
     * MenuItem}.
     *
     * @param source    - The target MenuItem where UI events are emitted from.
     * @param eventType - The type of event to listen for.
     * @param <T>       - The event type
     * @return A Flux that emits all events of the argument type that originate form the argument node.
     */
    public static <T extends Event> Flux<T> menuItemEvent(MenuItem source, EventType<T> eventType)
    {
        return SceneGraphSource.menuItemEvent(source, eventType);
    }

    /**
     * Creates a {@link Flux} which emits all Events of the argument {@link EventType} from the argument {@link Node}
     *
     * @param source    - The target Node where UI events are emitted from.
     * @param eventType - The type of event to listen for.
     * @param <T>       - The event type.
     * @return A Flux that emits all events of the argument type that originate from the argument node.
     */
    public static <T extends Event> Flux<T> nodeEvent(Node source, EventType<T> eventType)
    {
        return SceneGraphSource.nodeEvent(source, eventType);
    }

    /**
     * Creates a {@link Flux} which emits all Events of the argument {@link EventType} from the argument {@link Scene}.
     *
     * @param source    - The target Scene where UI events are emitted from.
     * @param eventType - The type of event to listen for.
     * @param <T>       - The event type.
     * @return A Flux that emits all events of the argument type that originate from the argument Scene.
     */
    public static <T extends Event> Flux<T> sceneEvent(Scene source, EventType<T> eventType)
    {
        return SceneGraphSource.sceneEvent(source, eventType);
    }

    /**
     * Creates a {@link Flux} which emits all Events of the argument {@link EventType} from the argument {@link Stage}.
     *
     * @param source    - The target Stage where UI events are emitted from.
     * @param eventType - The type of event to listen for.
     * @param <T>       - The event type.
     * @return A Flux that emits all events of the argument type that originates from the argument Stage.
     */
    public static <T extends Event> Flux<T> stageEvent(Stage source, EventType<T> eventType)
    {
        return SceneGraphSource.stageEvent(source, eventType);
    }

    /**
     * Creates a {@link Flux} which emits all Events of the argument {@link EventType} from the argument {@link
     * Window}.
     *
     * @param source    - The target Window where UI events are emitted from.
     * @param eventType - The type of event to listen for.
     * @param <T>       - The event type.
     * @return A Flux that emits all events of the argument type that originate from the argument Window.
     */
    public static <T extends Event> Flux<T> windowEvent(Window source, EventType<T> eventType)
    {
        return SceneGraphSource.windowEvent(source, eventType);
    }

    /**
     * Crates a {@link Flux} which emits whenever the argument observable is changed.
     *
     * @param observableValue - The observable to listen for changes.
     * @param <T>             - The type of the observable.
     * @return A Flux that emits the newest value of the argument observable when it has been changed.
     */
    public static <T> Flux<T> observable(ObservableValue<T> observableValue)
    {
        return ObservableSource.from(observableValue);
    }

    /**
     * Creates a Flux that emits all ActionEvents that originate from the argument Node. Equivalent to using {@link
     * #nodeEvent(Node, EventType)}
     *
     * @param source - The target node where events originate from.
     * @return A Flux containing all {@link ActionEvent}s from the argument node.
     */
    public static Flux<ActionEvent> nodeActionEvent(Node source)
    {
        return nodeEvent(source, ActionEvent.ANY);
    }

    /**
     * Creates a Flux that emits the argument {@link ObservableList} every time it has been updated.
     *
     * @param source - The ObservableList to listen to.
     * @param <T>    - The type of the ObservableList
     * @return A Flux that emits the argument list whenever it has ben changed.
     */
    public static <T> Flux<ObservableList<T>> observableList(ObservableList<T> source)
    {
        return ObservableListSource.observableList(source);
    }

    /**
     * Creates a Flux that listens for changes to the argument {@link ObservableList} and emits all of the additions to
     * the list whenever it has been updated.
     *
     * @param source - The ObservableList to listen to.
     * @param <T>    - The type of the ObservableList
     * @return A Flux that emits the additions to the list whenever it has been changed.
     */
    public static <T> Flux<T> observableListAdditions(ObservableList<T> source)
    {
        return ObservableListSource.additions(source);
    }

    /**
     * Creates a Flux that listens for changes to the argument {@link ObservableList} and emits all of the removals to
     * the list whenever it has been updated.
     *
     * @param source - The ObservableList to listen to.
     * @param <T>    - The type of the ObservableList
     * @return A Flux that emits the removals to the list whenever it has been changed.
     */
    public static <T> Flux<T> observableListRemovals(ObservableList<T> source)
    {
        return ObservableListSource.removals(source);
    }
}
