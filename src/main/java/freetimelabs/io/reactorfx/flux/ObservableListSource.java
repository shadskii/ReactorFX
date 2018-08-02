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

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import reactor.core.publisher.Flux;

import static freetimelabs.io.reactorfx.flux.DisposeUtilities.onFx;

/**
 * Contains all flux sources for {@link ObservableList}
 */
class ObservableListSource
{

    static <T> Flux<ObservableList<T>> observableList(ObservableList<T> source)
    {
        return Flux.create(emitter ->
        {
            final ListChangeListener<T> listener = c -> emitter.next(source);
            source.addListener(listener);
            emitter.onDispose(onFx(() -> source.removeListener(listener)));
            emitter.next(source);
        });
    }

    static <T> Flux<T> removals(ObservableList<T> source)
    {
        return Flux.create(emitter ->
        {
            final ListChangeListener<T> listener = c ->
            {
                while (c.next())
                {
                    if (c.wasRemoved())
                    {
                        c.getRemoved()
                         .forEach(emitter::next);
                    }
                }

            };
            source.addListener(listener);
            emitter.onDispose(onFx(() -> source.removeListener(listener)));
        });
    }

    static <T> Flux<T> additions(ObservableList<T> source)
    {
        return Flux.create(emitter ->
        {
            final ListChangeListener<T> listener = c ->
            {
                while (c.next())
                {
                    if (c.wasAdded())
                    {
                        c.getAddedSubList()
                         .forEach(emitter::next);
                    }
                }

            };
            source.addListener(listener);
            emitter.onDispose(onFx(() -> source.removeListener(listener)));
        });
    }

    static <T> Flux<ListChangeListener.Change<? extends T>> changes(ObservableList<T> source)
    {
        return Flux.create(emitter ->
        {
            final ListChangeListener<T> listener = new ListChangeListener<T>() {
                @Override
                public void onChanged(Change<? extends T> c) {
                    if (c.next()) {
                        emitter.next(c);
                    }
                }
            };
            source.addListener(listener);
            emitter.onDispose(onFx(() -> source.removeListener(listener)));
        });
    }
}
