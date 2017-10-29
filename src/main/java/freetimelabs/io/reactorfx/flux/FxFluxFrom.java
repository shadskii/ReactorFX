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

import freetimelabs.io.reactorfx.schedulers.FXScheduler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import javafx.stage.Window;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

public final class FxFluxFrom
{
    private FxFluxFrom()
    {
        // No instance
    }

    /**
     * @param source
     * @param <T>
     * @return
     */
    public static <T> Mono<T> dialog(final Dialog<T> source)
    {
        return Mono.fromCallable(source::showAndWait)
                   .subscribeOn(FXScheduler.getFxThread())
                   .filter(Optional::isPresent)
                   .map(Optional::get);
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
        return Flux.create(emitter ->
        {
            final EventHandler<T> handler = emitter::next;
            source.addEventHandler(eventType, handler);
            emitter.onDispose(() -> source.removeEventHandler(eventType, handler));
        });
    }

    /**
     * @param source
     * @param eventType
     * @param <T>
     * @return
     */
    public static <T extends Event> Flux<T> sceneEvent(Scene source, EventType<T> eventType)
    {
        return Flux.create(emitter ->
        {
            final EventHandler<T> handler = emitter::next;
            source.addEventHandler(eventType, handler);
            emitter.onDispose(() -> source.removeEventHandler(eventType, handler));
        });
    }

    /**
     * @param source
     * @param eventType
     * @param <T>
     * @return
     */
    public static <T extends Event> Flux<T> stageEvent(Stage source, EventType<T> eventType)
    {
        return Flux.create(emitter ->
        {
            final EventHandler<T> handler = emitter::next;
            source.addEventHandler(eventType, handler);
            emitter.onDispose(() -> source.removeEventHandler(eventType, handler));
        });
    }

    /**
     * @param source
     * @param eventType
     * @param <T>
     * @return
     */
    public static <T extends Event> Flux<T> windowEvent(Window source, EventType<T> eventType)
    {
        return Flux.create(emitter ->
        {
            final EventHandler<T> handler = emitter::next;
            source.addEventHandler(eventType, handler);
            emitter.onDispose(() -> source.removeEventFilter(eventType, handler));
        });
    }

    /**
     * @param observableValue
     * @param <T>
     * @return
     */
    public static <T> Flux<T> oberservable(ObservableValue<T> observableValue)
    {
        return Flux.create(emitter ->
        {
            final ChangeListener<T> handler = (obs, oldVal, newVal) ->
            {
                if (Objects.nonNull(newVal))
                {
                    emitter.next(newVal);
                }
            };
            observableValue.addListener(handler);
            emitter.onDispose(() -> observableValue.removeListener(handler));
        });
    }

    public static Flux<ActionEvent> nodeActionEvent(Node source)
    {
        return nodeEvent(source, ActionEvent.ANY);
    }
}
