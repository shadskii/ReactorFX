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

package freetimelabs.io.reactorfx.sources;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import reactor.core.publisher.Flux;

public class EventSource
{

    /**
     * Creates a {@link Flux} which emits all Events of the argument {@link EventType} from the argument {@link Node}
     *
     * @param source
     * @param eventType
     * @param <T>
     * @return
     */
    public static <T extends Event> Flux<T> fromNode(Node source, EventType<T> eventType)
    {
        return Flux.create(emitter -> source.addEventHandler(eventType, emitter::next));
    }

    /**
     * @param source
     * @param eventType
     * @param <T>
     * @return
     */
    public static <T extends Event> Flux<T> fromScene(Scene source, EventType<T> eventType)
    {
        return Flux.create(emitter -> source.addEventHandler(eventType, emitter::next));
    }

    /**
     * @param source
     * @param eventType
     * @param <T>
     * @return
     */
    public static <T extends Event> Flux<T> fromStage(Stage source, EventType<T> eventType)
    {
        return Flux.create(emitter -> source.addEventHandler(eventType, emitter::next));
    }

    /**
     * @param source
     * @param eventType
     * @param <T>
     * @return
     */
    public static <T extends Event> Flux<T> fromWindow(Window source, EventType<T> eventType)
    {
        return Flux.create(emitter ->
        {
            final EventHandler<T> handler = emitter::next;
            source.addEventHandler(eventType, handler);
        });
    }

}
