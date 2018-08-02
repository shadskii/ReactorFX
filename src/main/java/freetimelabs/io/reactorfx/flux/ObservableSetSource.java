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

import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import reactor.core.publisher.Flux;

import static freetimelabs.io.reactorfx.flux.DisposeUtilities.onFx;

/**
 * Contains all Flux sources for {@link ObservableSet}
 */
class ObservableSetSource
{
    static <T> Flux<ObservableSet<T>> observableSet(ObservableSet<T> source)
    {
        return Flux.create(emitter ->
        {
            final SetChangeListener<T> listener = c -> emitter.next(source);
            source.addListener(listener);
            emitter.onDispose(onFx(() -> source.removeListener(listener)));
            emitter.next(source);
        });
    }

    static <T> Flux<T> additions(ObservableSet<T> source)
    {
        return Flux.create(emitter ->
        {
            final SetChangeListener<T> listener = c ->
            {
                if (c.wasAdded())
                {
                    emitter.next(c.getElementAdded());
                }
            };
            source.addListener(listener);
            emitter.onDispose(onFx(() -> source.removeListener(listener)));
        });
    }

    static <T> Flux<T> removals(ObservableSet<T> source)
    {
        return Flux.create(emitter ->
        {
            final SetChangeListener<T> listener = c ->
            {
                if (c.wasRemoved())
                {
                    emitter.next(c.getElementRemoved());
                }
            };
            source.addListener(listener);
            emitter.onDispose(onFx(() -> source.removeListener(listener)));
        });
    }

    static <T> Flux<SetChangeListener.Change<? extends T>> changes(ObservableSet<T> source)
    {
        return Flux.create(emitter ->
        {
            final SetChangeListener<T> listener = emitter::next;
            source.addListener(listener);
            emitter.onDispose(onFx(() -> source.removeListener(listener)));
        });
    }

}
