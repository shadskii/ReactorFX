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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import reactor.core.publisher.Flux;

import java.util.Objects;

import static freetimelabs.io.reactorfx.flux.DisposeUtilities.onFx;

/**
 * Contains all flux sources for {@link ObservableValue}
 */
class ObservableSource
{
    static <T> Flux<T> from(ObservableValue<T> observableValue)
    {
        return Flux.create(emitter ->
        {
            T initialValue = observableValue.getValue();
            if (Objects.nonNull(initialValue))
            {
                emitter.next(initialValue);
            }
            final ChangeListener<T> handler = (obs, oldVal, newVal) ->
            {
                if (Objects.nonNull(newVal))
                {
                    emitter.next(newVal);
                }
            };
            observableValue.addListener(handler);
            emitter.onDispose(onFx(() -> observableValue.removeListener(handler)));
        });
    }

    static <T> Flux<Change<T>> fromChangesOf(ObservableValue<T> observableValue)
    {
        return Flux.create(emitter ->
        {
            final ChangeListener<T> listener = (obs, oldVal, newVal) ->
                    emitter.next(new Change<>(oldVal, newVal));
            observableValue.addListener(listener);
            emitter.onDispose(onFx(() -> observableValue.removeListener(listener)));
        });
    }
}
