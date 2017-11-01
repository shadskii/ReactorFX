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

import reactor.core.Disposable;
import reactor.core.scheduler.Scheduler;

/**
 * Scheduler to be used to run tasks on the FX thread. This should only be used for short tasks. This is a naive
 * implementation so users should be wary.
 */
final class JavaFxScheduler implements Scheduler
{
    private static final JavaFxScheduler INSTANCE = new JavaFxScheduler();


    /**
     * Instantiation of this is restricted to a singleton. All calls to this method will return the same instance.
     *
     * @return The only instance of this Schedulers.
     */
    static JavaFxScheduler platform()
    {
        return INSTANCE;
    }


    @Override
    public Disposable schedule(Runnable task)
    {
        return INSTANCE.createWorker()
                       .schedule(task);
    }

    @Override
    public Worker createWorker()
    {
        return new JavaFxWorker();
    }
}