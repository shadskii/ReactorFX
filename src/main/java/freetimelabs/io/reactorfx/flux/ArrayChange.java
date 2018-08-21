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

import javafx.collections.ObservableArray;

/**
 * This class represent a change emitted from a JavaFX {@link ObservableArray}
 *
 * @param <T> The type of object held by this ObservableArray
 */
public class ArrayChange<T extends ObservableArray<T>>
{
    private final T observableArray;
    private final boolean sizeChanged;
    private final int from;
    private final int to;

    ArrayChange(T observableArray, boolean sizeChanged, int from, int to) {
        this.observableArray = observableArray;
        this.sizeChanged = sizeChanged;
        this.from = from;
        this.to = to;
    }

    /**
     * @return underlying {@link ObservableArray}
     */
    public T getObservableArray() {
        return observableArray;
    }

    /**
     * indicates if the size of array has changed
     */
    public boolean isSizeChanged() {
        return sizeChanged;
    }

    /**
     * A beginning (inclusive) of an interval related to the change
     */
    public int getFrom() {
        return from;
    }

    /**
     * An end (exclusive) of an interval related to the change.
     */
    public int getTo() {
        return to;
    }
}
