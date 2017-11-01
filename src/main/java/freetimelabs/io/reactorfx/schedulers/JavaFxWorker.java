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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.scheduler.Scheduler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p> <b> This is a naive implementation based on the RXJava implementation of a JavaFX scheduler used in RXJavaFX.
 * </b> </p> A Worker implementation which manages a queue of QueuedRunnable for execution on the Java FX Application
 * thread For a simpler implementation the queue always contains at least one element. {@link #head} is the element,
 * which is in execution or was last executed {@link #tail} is an atomic reference to the last element in the queue, or
 * null when the worker was disposed Recursive actions are not preferred and inserted at the tail of the queue as any
 * other action would be The Worker will only schedule a single job with {@link Platform#runLater(Runnable)} for when
 * the queue was previously empty.
 */
class JavaFxWorker implements Scheduler.Worker, Runnable
{
    private volatile QueuedRunnable head = new QueuedRunnable(null);
    private final AtomicReference<QueuedRunnable> tail = new AtomicReference<>(head);

    private static void assertThatTheDelayIsValidForTheJavaFxTimer(long delay)
    {
        if (delay < 0 || delay > Integer.MAX_VALUE)
        {
            throw new IllegalArgumentException(String.format("The JavaFx timer only accepts non-negative delays up to %d milliseconds.", Integer.MAX_VALUE));
        }
    }

    @Override
    public void dispose()
    {
        tail.set(null);
        QueuedRunnable qr = this.head;
        while (qr != null)
        {
            qr.dispose();
            qr = qr.getAndSet(null);
        }
    }

    @Override
    public boolean isDisposed()
    {
        return tail.get() == null;
    }

    @Override
    public Disposable schedule(Runnable task, long delayTime, TimeUnit unit)
    {
        long delay = Math.max(0, unit.toMillis(delayTime));
        assertThatTheDelayIsValidForTheJavaFxTimer(delay);

        QueuedRunnable queuedRunnable = new QueuedRunnable(task);
        if (delay == 0)
        {
            return schedule(queuedRunnable);
        }

        final Timeline timer = new Timeline(new KeyFrame(Duration.millis(delay), event -> schedule(queuedRunnable)));
        timer.play();

        return (() ->
        {
            queuedRunnable.dispose();
            timer.stop();
        });
    }

    @Override
    public Disposable schedulePeriodically(Runnable task, long initialDelay, long period, TimeUnit unit)
    {
        long delay = Math.max(0, unit.toMillis(initialDelay));
        assertThatTheDelayIsValidForTheJavaFxTimer(delay);

        QueuedRunnable queuedRunnable = new QueuedRunnable(task);

        Duration initial = Duration.millis(delay);
        Timeline timer = new Timeline(new KeyFrame(initial, event -> schedule(queuedRunnable)), new KeyFrame(Duration.millis(delay + period)));
        EventHandler<ActionEvent> handler = e -> timer.playFrom(initial);
        timer.setOnFinished(handler);
        timer.play();
        return () ->
        {
            queuedRunnable.dispose();
            timer.stop();
        };
    }

    @Override
    public Disposable schedule(Runnable action)
    {
        if (isDisposed())
        {
            return Disposables.disposed();
        }

        QueuedRunnable queuedRunnable = action instanceof QueuedRunnable ? (QueuedRunnable) action : new QueuedRunnable(action);

        QueuedRunnable tailPivot;
        do
        {
            tailPivot = tail.get();
        } while (tailPivot != null && !tailPivot.compareAndSet(null, queuedRunnable));

        if (tailPivot == null)
        {
            queuedRunnable.dispose();
        } else
        {
            tail.compareAndSet(tailPivot, queuedRunnable);
            if (tailPivot == head)
            {
                if (Platform.isFxApplicationThread())
                {
                    run();
                } else
                {
                    Platform.runLater(this);
                }
            }
        }
        return queuedRunnable;
    }

    @Override
    public void run()
    {
        for (QueuedRunnable qr = head.get(); qr != null; qr = qr.get())
        {
            qr.run();
            head = qr;
        }
    }

    private static class QueuedRunnable extends AtomicReference<QueuedRunnable> implements Disposable, Runnable
    {
        private volatile Runnable action;

        private QueuedRunnable(Runnable action)
        {
            this.action = action;
        }

        @Override
        public void dispose()
        {
            action = null;
        }

        @Override
        public boolean isDisposed()
        {
            return action == null;
        }

        @Override
        public void run()
        {
            if (!isDisposed())
            {
                this.action.run();
            }
            this.action = null;
        }
    }
}