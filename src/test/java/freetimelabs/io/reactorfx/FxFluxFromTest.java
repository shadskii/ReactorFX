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

import freetimelabs.io.reactorfx.flux.FxFluxFrom;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class FxFluxFromTest
{
    private static final Phaser p = new Phaser(2);
    private static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();
    private Scheduler thread = Schedulers.immediate();

    @BeforeClass
    public static void initApplication() throws TimeoutException, InterruptedException
    {
        SERVICE.submit(() -> Application.launch(TestApp.class));
        p.awaitAdvanceInterruptibly(p.arrive(), 3, TimeUnit.SECONDS);
    }

    private void setStage(Consumer<Stage> stageConsumer) throws TimeoutException, InterruptedException
    {
        Phaser barrier = new Phaser(2);
        Platform.runLater(() ->
        {
            stageConsumer.accept(TestApp.primaryStage());
            barrier.arrive();
        });
        barrier.awaitAdvanceInterruptibly(barrier.arrive(), 3, TimeUnit.SECONDS);

    }

    @Test
    public void testDialog() throws TimeoutException, InterruptedException
    {
        AtomicReference<TextInputDialog> actual = new AtomicReference<>();
        Phaser start = new Phaser(2);
        Platform.runLater(() ->
        {
            TextInputDialog dialog = new TextInputDialog();
            dialog.initOwner(null);
            actual.set(dialog);
            start.arrive();
        });

        start.awaitAdvanceInterruptibly(start.arrive(), 3, TimeUnit.SECONDS);
        TextInputDialog dialog = actual.get();
        AtomicReference<Object> res = new AtomicReference<>();
        Phaser p = new Phaser(2);
        FxFluxFrom.dialog(dialog)
                  .subscribe(o ->
                  {
                      res.set(o);
                      p.arrive();
                  });

        String hello = "Hello";
        Platform.runLater(() ->
        {
            dialog.getEditor()
                  .setText(hello);
            Node isNull =
                    dialog.getDialogPane()
                          .lookupButton(ButtonType.OK);
            if (Objects.nonNull(isNull))
            {
                isNull.fireEvent(new ActionEvent());
            }

        });


        p.awaitAdvanceInterruptibly(p.arrive(), 3, TimeUnit.SECONDS);
        assertThat(res.get()).isEqualTo(hello);

    }

    @Test
    public void testNodeEvent() throws TimeoutException, InterruptedException
    {
        AtomicReference<Node> actual = new AtomicReference<>();
        setStage(stage ->
        {
            Pane pane = new Pane();
            actual.set(pane);
            stage.setScene(new Scene(pane));
        });

        AtomicReference<Event> event = new AtomicReference<>();
        Node pane = actual.get();

        FxFluxFrom.nodeEvent(pane, KeyEvent.KEY_TYPED)
                  .publishOn(thread)
                  .subscribe(event::set);

        actual.get()
              .fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.CODE_INPUT, false, false, false, false));
        assertThat(event.get()
                        .getSource()).isEqualTo(pane);

    }

    @Test
    public void testNodeActionEvent() throws TimeoutException, InterruptedException
    {
        AtomicReference<Node> actual = new AtomicReference<>();
        setStage(stage ->
        {
            Pane pane = new Pane();
            actual.set(pane);
            stage.setScene(new Scene(pane));
        });

        AtomicReference<Event> event = new AtomicReference<>();
        Node pane = actual.get();
        FxFluxFrom.nodeActionEvent(pane)
                  .publishOn(thread)
                  .subscribe(event::set);


        ActionEvent e = new ActionEvent();
        actual.get()
              .fireEvent(e);
        assertThat(event.get()
                        .getSource()).isEqualTo(pane);
    }

    @Test
    public void testMenuItemActionEvent() throws TimeoutException, InterruptedException
    {
        AtomicReference<MenuItem> actual = new AtomicReference<>();
        setStage(stage ->
        {
            MenuItem item = new MenuItem("look I'm a menu");
            MenuBar bar = new MenuBar();
            Menu menu = new Menu();
            menu.getItems()
                .add(item);
            bar.getMenus()
               .add(menu);
            actual.set(item);
            stage.setScene(new Scene(bar));
        });

        AtomicReference<Event> event = new AtomicReference<>();
        MenuItem menuItem = actual.get();
        FxFluxFrom.menuItemEvent(menuItem, ActionEvent.ANY)
                  .publishOn(thread)
                  .subscribe(event::set);


        ActionEvent e = new ActionEvent();
        menuItem
                .fire();
        assertThat(event.get()
                        .getSource()).isEqualTo(menuItem);
    }

    @Test
    public void testObservable()
    {
        SimpleObjectProperty<String> observable = new SimpleObjectProperty<>("hi");
        AtomicReference<Object> actual = new AtomicReference<>();
        FxFluxFrom.observable(observable)
                  .publishOn(thread)
                  .subscribe(actual::set);
        observable.set("hello");
        assertThat(actual.get()).isEqualTo("hello");

        observable.set(null);
        assertThat(actual.get()).isEqualTo("hello");
    }

    @Test
    public void testObservableList()
    {
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 2, 3);
        AtomicReference<List> actual = new AtomicReference<>();
        Disposable disposable = FxFluxFrom.observableList(list)
                                          .publishOn(thread)
                                          .subscribe(actual::set);
        list.add(4);
        assertThat(actual.get()).containsExactly(1, 2, 3, 4);

        list.remove(3);
        assertThat(actual.get()).containsExactly(1, 2, 3);
        Platform.runLater(() -> disposable.dispose());
    }

    @Test
    public void testSceneEvent() throws TimeoutException, InterruptedException
    {
        AtomicReference<Scene> actual = new AtomicReference<>();
        AtomicReference<Node> actualNode = new AtomicReference<>();
        setStage(stage ->
        {
            Pane pane = new Pane();
            Scene scene = new Scene(pane);
            actualNode.set(pane);
            actual.set(scene);
            stage.setScene(scene);
        });

        AtomicReference<Event> event = new AtomicReference<>();
        Scene scene = actual.get();

        FxFluxFrom.sceneEvent(scene, KeyEvent.KEY_TYPED)
                  .publishOn(thread)
                  .subscribe(event::set);


        actualNode.get()
                  .fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.CODE_INPUT, false, false, false, false));
        assertThat(event.get()
                        .getSource()).isEqualTo(scene);
    }

    @Test
    public void testStageEvent() throws TimeoutException, InterruptedException
    {
        AtomicReference<Stage> actual = new AtomicReference<>();
        AtomicReference<Node> actualNode = new AtomicReference<>();
        setStage(stage ->
        {
            Pane pane = new Pane();
            Scene scene = new Scene(pane);
            actualNode.set(pane);
            actual.set(stage);
            stage.setScene(scene);
        });

        AtomicReference<Event> event = new AtomicReference<>();
        Stage stage = actual.get();

        FxFluxFrom.stageEvent(stage, KeyEvent.KEY_TYPED)
                  .publishOn(thread)
                  .subscribe(event::set);

        actualNode.get()
                  .fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.CODE_INPUT, false, false, false, false));
        assertThat(event.get()
                        .getSource()).isEqualTo(stage);
    }

    @Test
    public void testWindowEvent() throws TimeoutException, InterruptedException
    {
        AtomicReference<Window> actual = new AtomicReference<>();
        AtomicReference<Node> actualNode = new AtomicReference<>();
        setStage(stage ->
        {
            Pane pane = new Pane();
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            actualNode.set(pane);
            actual.set(stage);
        });

        AtomicReference<Event> event = new AtomicReference<>();
        Window window = actual.get();

        FxFluxFrom.windowEvent(window, KeyEvent.KEY_TYPED)
                  .publishOn(thread)
                  .subscribe(event::set);

        actualNode.get()
                  .fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.CODE_INPUT, false, false, false, false));
        assertThat(event.get()
                        .getSource()).isEqualTo(window);
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
