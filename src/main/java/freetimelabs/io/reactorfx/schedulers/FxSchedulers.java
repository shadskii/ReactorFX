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

package freetimelabs.io.reactorfx.schedulers;

import javafx.application.Platform;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;

/**
 * This class aggregates all of the different flavors of JavaFX {@link Scheduler} available as part of this repository.
 */
public final class FxSchedulers
{
    private static final Scheduler FX_THREAD = Schedulers.fromExecutor(Platform::runLater);

    private FxSchedulers()
    {
        // No instance
    }

    /**
     * This is the standard implementation of a JavaFX scheduler. It is created via {@link
     * Schedulers#fromExecutor(Executor)}. This should be considered the baseline for any performance testing done with
     * comparing JavaFX schedulers.
     *
     * @return A {@link Scheduler} that provides access to the JavaFX Application Thread.
     */
    public static Scheduler fxThread()
    {
        return FX_THREAD;
    }

}
