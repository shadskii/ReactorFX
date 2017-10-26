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

import javafx.beans.value.ObservableValue;
import reactor.core.publisher.Flux;

import javax.swing.table.TableColumn;

public class PropertySource
{
    /**
     *
     * @param observableValue
     * @param <T>
     * @return
     */
    public static <T> Flux<T> fromObservable(ObservableValue<T> observableValue)
    {
        return Flux.create(emitter -> observableValue.addListener((obs, oldVal, newVal) -> emitter.next(newVal)));
    }
}
