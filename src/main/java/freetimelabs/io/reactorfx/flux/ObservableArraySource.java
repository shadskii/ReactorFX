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

import javafx.collections.*;
import reactor.core.publisher.Flux;

import static freetimelabs.io.reactorfx.flux.DisposeUtilities.onFx;

class ObservableArraySource
{
    static <T extends ObservableArray<T>> Flux<T> observableArray(T source)
    {
        return Flux.create(emitter ->
        {
            final ArrayChangeListener<T> listener = (arr, sizeChanged, from, to) -> emitter.next(source);
            source.addListener(listener);
            emitter.onDispose(onFx(() -> source.removeListener(listener)));
        });
    }

    static Flux<ObservableIntegerArray> observableIntegerSubArray(ObservableIntegerArray source)
    {
        return Flux.create(emitter ->
        {
            final ArrayChangeListener<ObservableIntegerArray> listener = (arr, sizeChanged, from, to) ->
            {
                ObservableIntegerArray newArr = FXCollections.observableIntegerArray();
                arr.set(0, newArr, from, to - from);
                emitter.next(newArr);
            };
            source.addListener(listener);
            emitter.onDispose(onFx(() -> source.removeListener(listener)));
        });
    }

    static Flux<ObservableFloatArray> observableFloatSubArray(ObservableFloatArray source)
    {
        return Flux.create(emitter ->
        {
            final ArrayChangeListener<ObservableFloatArray> listener = (arr, sizeChanged, from, to) ->
            {
                ObservableFloatArray newArr = FXCollections.observableFloatArray();
                arr.set(0, newArr, from, to - from);
                emitter.next(newArr);
            };
            source.addListener(listener);
            emitter.onDispose(onFx(() -> source.removeListener(listener)));
        });
    }
}
