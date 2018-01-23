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

/**
 * This class represent a single change emitted from a JavaFX Observable.
 * @param <T> The type contained by this change.
 */
public final class Change<T>
{
    private final T oldVal;
    private final T newVal;

    public Change(T oldVal, T newVal)
    {
        this.oldVal = oldVal;
        this.newVal = newVal;
    }

    /**
     * The old value of this change that how now been replaced by the new value.
     * @return The old value of this change.
     */
    public T getOldVal()
    {
        return oldVal;
    }

    /**
     * The new value of this change that has replaced the old value.
     * @return The new value of this change.
     */
    public T getNewVal()
    {
        return newVal;
    }
}
