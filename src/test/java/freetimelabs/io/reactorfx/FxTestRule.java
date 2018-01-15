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

package freetimelabs.io.reactorfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * A test rule that ensures that the JavaFX application thread has been started.
 */
public class FxTestRule implements TestRule
{
    private static final Phaser p = new Phaser(2);
    private static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();
    private static final AtomicBoolean APP_INITIALIZED = new AtomicBoolean(false);

    private static void initApplication() throws TimeoutException, InterruptedException
    {
        SERVICE.submit(() -> Application.launch(TestApp.class));
        p.awaitAdvanceInterruptibly(p.arrive(), 10, TimeUnit.SECONDS);
    }

    @Override
    public Statement apply(Statement base, Description description)
    {
        return new Statement()
        {
            @Override
            public void evaluate() throws Throwable
            {
                if (!APP_INITIALIZED.getAndSet(true))
                {
                    initApplication();
                }
                onStage(stage -> stage.setScene(null));
                base.evaluate();
            }
        };
    }

    /**
     * Used to access the stage of this test application. This method will block until its work has been completed as to
     * ensure that the test stage is correctly setup.
     *
     * @param stageConsumer - Task to be performed on the primary stage of this test application.
     */
    public void onStage(Consumer<Stage> stageConsumer)
    {
        try
        {
            Phaser barrier = new Phaser(2);
            Platform.runLater(() ->
            {
                stageConsumer.accept(TestApp.primaryStage());
                barrier.arrive();
            });
            barrier.awaitAdvanceInterruptibly(barrier.arrive(), 3, TimeUnit.SECONDS);
        }
        catch (InterruptedException | TimeoutException e)
        {
            throw new IllegalStateException("Timeout occurred during execution of onStage");
        }
    }

    public static final class TestApp extends Application
    {
        private static final AtomicReference<TestApp> TEST_APP = new AtomicReference<>();
        private Stage stage;

        public TestApp()
        {
            synchronized (TEST_APP)
            {
                TEST_APP.getAndSet(this);
            }
        }

        public static Stage primaryStage()
        {
            return TEST_APP.get().stage;
        }

        @Override
        public void start(Stage primaryStage) throws Exception
        {
            stage = primaryStage;
            p.arrive();
        }
    }
}
