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

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import reactor.core.publisher.Flux;

import java.util.AbstractMap;
import java.util.Map;

import static freetimelabs.io.reactorfx.flux.DisposeUtilities.onFx;

class ObservableMapSource
{
    static <T, V> Flux<ObservableMap<T, V>> observableMap(ObservableMap<T, V> source)
    {
        return Flux.create(emitter ->
        {
            final MapChangeListener<T, V> listener = c -> emitter.next(source);
            source.addListener(listener);
            emitter.onDispose(onFx(() -> source.removeListener(listener)));
        });
    }

    static <T, V> Flux<Map.Entry<T, V>> additions(ObservableMap<T, V> source)
    {
        return Flux.create(emitter ->
        {
            final MapChangeListener<T, V> listener = change ->
            {
                if (change.wasAdded())
                {
                    emitter.next(new AbstractMap.SimpleEntry<>(change.getKey(), change.getValueAdded()));
                }
            };
            source.addListener(listener);
            emitter.onDispose(onFx(() -> source.removeListener(listener)));
        });
    }

    static <T, V> Flux<Map.Entry<T, V>> removals(ObservableMap<T, V> source)
    {
        return Flux.create(emitter ->
        {
            final MapChangeListener<T, V> listener = change ->
            {
                if (change.wasRemoved())
                {
                    emitter.next(new AbstractMap.SimpleEntry<>(change.getKey(), change.getValueRemoved()));
                }
            };
            source.addListener(listener);
            emitter.onDispose(onFx(() -> source.removeListener(listener)));
        });
    }

}
