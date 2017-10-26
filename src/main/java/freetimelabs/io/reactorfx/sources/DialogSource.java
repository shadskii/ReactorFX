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

import freetimelabs.io.reactorfx.schedulers.FXScheduler;
import javafx.scene.control.Dialog;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class DialogSource
{
    /**
     *
     * @param source
     * @param <T>
     * @return
     */
    public static <T> Mono<T> fromDialog(final Dialog<T> source)
    {
        return Mono.fromCallable(source::showAndWait)
                   .subscribeOn(FXScheduler.getFxThread())
                   .filter(Optional::isPresent)
                   .map(Optional::get);
    }
}
