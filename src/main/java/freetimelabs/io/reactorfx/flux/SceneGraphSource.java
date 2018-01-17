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

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.Window;
import reactor.core.publisher.Flux;

import static freetimelabs.io.reactorfx.flux.DisposeUtilities.onFx;

/**
 * Contains all flux sources for elements of the scenegraph
 */
class SceneGraphSource
{
    static <T extends Event> Flux<T> menuItemEvent(MenuItem source, EventType<T> eventType)
    {
        return Flux.create(emitter ->
        {
            final EventHandler<T> handler = emitter::next;
            source.addEventHandler(eventType, handler);
            emitter.onDispose(onFx(() -> source.removeEventHandler(eventType, handler)));
        });
    }

    static <T extends Event> Flux<T> nodeEvent(Node source, EventType<T> eventType)
    {
        return Flux.create(emitter ->
        {
            final EventHandler<T> handler = emitter::next;
            source.addEventHandler(eventType, handler);
            emitter.onDispose(onFx(() -> source.removeEventHandler(eventType, handler)));
        });
    }

    static <T extends Event> Flux<T> sceneEvent(Scene source, EventType<T> eventType)
    {
        return Flux.create(emitter ->
        {
            final EventHandler<T> handler = emitter::next;
            source.addEventHandler(eventType, handler);
            emitter.onDispose(onFx(() -> source.removeEventHandler(eventType, handler)));
        });
    }

    static <T extends Event> Flux<T> stageEvent(Stage source, EventType<T> eventType)
    {
        return Flux.create(emitter ->
        {
            final EventHandler<T> handler = emitter::next;
            source.addEventHandler(eventType, handler);
            emitter.onDispose(onFx(() -> source.removeEventHandler(eventType, handler)));
        });
    }

    static <T extends Event> Flux<T> windowEvent(Window source, EventType<T> eventType)
    {
        return Flux.create(emitter ->
        {
            final EventHandler<T> handler = emitter::next;
            source.addEventHandler(eventType, handler);
            emitter.onDispose(onFx(() -> source.removeEventFilter(eventType, handler)));
        });
    }
}
