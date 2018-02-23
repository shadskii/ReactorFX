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

import freetimelabs.io.reactorfx.flux.Change;
import freetimelabs.io.reactorfx.flux.FxFlux;
import freetimelabs.io.reactorfx.schedulers.FxSchedulers;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import org.junit.ClassRule;
import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public class FxFluxTest
{
    private Scheduler thread = Schedulers.immediate();
    private Scheduler fxThread = FxSchedulers.fxThread();

    private static final String KEY0 = "KEY0";
    private static final String KEY1 = "KEY1";
    private static final KeyEvent KEY_EVENT = new KeyEvent(KeyEvent.KEY_TYPED, "", "", KeyCode.CODE_INPUT, false, false, false, false);

    @ClassRule
    public static final FxTestRule FX_RULE = new FxTestRule();


    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        NoInstanceTestHelper.testNoInstance(FxFlux.class);
    }

    @Test
    public void testDialog() throws TimeoutException, InterruptedException
    {
        AtomicReference<TextInputDialog> actual = new AtomicReference<>();
        final String hello = "Hello";
        Phaser show = new Phaser(2);
        FX_RULE.onStage(stage ->
        {
            TextInputDialog dialog = new TextInputDialog(hello);
            dialog.setOnShown(event -> show.arrive());
            dialog.initOwner(stage);
            actual.set(dialog);
            stage.setScene(new Scene(new Pane()));
            stage.show();
        });

        TextInputDialog dialog = actual.get();
        AtomicReference<Object> res = new AtomicReference<>();
        Phaser p = new Phaser(2);
        Disposable disposable = FxFlux.from(dialog)
                                      .subscribeOn(fxThread)
                                      .publishOn(thread)
                                      .subscribe(o ->
                                      {
                                          res.set(o);
                                          p.arrive();
                                      });

        show.awaitAdvanceInterruptibly(show.arrive(), 3, TimeUnit.SECONDS);

        Platform.runLater(() -> dialog.getDialogPane()
                                      .lookupButton(ButtonType.OK)
                                      .fireEvent(new ActionEvent()));

        p.awaitAdvanceInterruptibly(p.arrive(), 3, TimeUnit.SECONDS);
        assertThat(res.get()).isEqualTo(hello);
        disposable.dispose();

    }

    @Test
    public void testNodeEvent() throws TimeoutException, InterruptedException
    {
        AtomicReference<Node> actual = new AtomicReference<>();
        FX_RULE.onStage(stage ->
        {
            Pane pane = new Pane();
            actual.set(pane);
            stage.setScene(new Scene(pane));
        });

        AtomicReference<Event> event = new AtomicReference<>();
        Node pane = actual.get();

        Phaser p = new Phaser(2);
        Disposable disposable = FxFlux.from(pane, KeyEvent.KEY_TYPED)
                                      .subscribeOn(fxThread)
                                      .publishOn(thread)
                                      .subscribe(e ->
                                      {
                                          event.set(e);
                                          p.arrive();
                                      });

        Platform.runLater(() -> actual.get()
                                      .fireEvent(KEY_EVENT));

        p.awaitAdvanceInterruptibly(p.arrive(), 3, TimeUnit.SECONDS);
        assertThat(event.get()
                        .getSource()).isEqualTo(pane);
        disposable.dispose();
    }

    @Test
    public void testNodeActionEvent() throws TimeoutException, InterruptedException
    {
        AtomicReference<Node> actual = new AtomicReference<>();
        FX_RULE.onStage(stage ->
        {
            Pane pane = new Pane();
            actual.set(pane);
            stage.setScene(new Scene(pane));
        });

        AtomicReference<Event> event = new AtomicReference<>();
        Phaser p = new Phaser(2);
        Node pane = actual.get();
        Disposable disposable = FxFlux.from(pane)
                                      .subscribeOn(fxThread)
                                      .publishOn(thread)
                                      .subscribe(e ->
                                      {
                                          event.set(e);
                                          p.arrive();
                                      });

        ActionEvent e = new ActionEvent();
        Platform.runLater(() -> actual.get()
                                      .fireEvent(e));

        p.awaitAdvanceInterruptibly(p.arrive(), 3, TimeUnit.SECONDS);
        assertThat(event.get()
                        .getSource()).isEqualTo(pane);
        disposable.dispose();
    }

    @Test
    public void testMenuItemActionEvent() throws TimeoutException, InterruptedException
    {
        AtomicReference<MenuItem> actual = new AtomicReference<>();
        FX_RULE.onStage(stage ->
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
        Phaser p = new Phaser(2);
        Disposable disposable = FxFlux.from(menuItem)
                                      .subscribeOn(fxThread)
                                      .publishOn(thread)
                                      .subscribe(e ->
                                      {
                                          event.set(e);
                                          p.arrive();
                                      });

        Platform.runLater(() -> menuItem.fire());
        p.awaitAdvanceInterruptibly(p.arrive(), 3, TimeUnit.SECONDS);
        assertThat(event.get()
                        .getSource()).isEqualTo(menuItem);
        disposable.dispose();
    }

    @Test
    public void testObservable()
    {
        SimpleObjectProperty<String> observable = new SimpleObjectProperty<>("hi");
        AtomicReference<Object> actual = new AtomicReference<>();
        Disposable disposable = FxFlux.from(observable)
                                      .publishOn(thread)
                                      .subscribe(actual::set);
        observable.set("hello");
        assertThat(actual.get()).isEqualTo("hello");

        observable.set(null);
        assertThat(actual.get()).isEqualTo("hello");
        disposable.dispose();

        observable.set("initial");
        AtomicReference<String> actual2 = new AtomicReference<>();
        Disposable disposable1 = FxFlux.from(observable)
                                       .publishOn(thread)
                                       .subscribe(actual2::set);
        assertThat(actual2.get()).isEqualTo("initial");
        disposable1.dispose();
    }

    @Test
    public void testObservableChanges()
    {
        SimpleIntegerProperty obs = new SimpleIntegerProperty();
        AtomicReference<Change> actual = new AtomicReference<>();
        Disposable disposable = FxFlux.fromChangesOf(obs)
                                      .publishOn(thread)
                                      .subscribe(actual::set);
        obs.set(0);
        obs.set(1);
        assertThat(actual.get()
                         .getOldVal()).isEqualTo(0);
        assertThat(actual.get()
                         .getNewVal()).isEqualTo(1);

        obs.set(2);
        assertThat(actual.get()
                         .getOldVal()).isEqualTo(1);
        assertThat(actual.get()
                         .getNewVal()).isEqualTo(2);
        disposable.dispose();
    }

    @Test
    public void testObservableList() throws TimeoutException, InterruptedException
    {
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 2, 3);
        AtomicReference<List<Integer>> actual = new AtomicReference<>();
        Disposable disposable = FxFlux.from(list)
                                      .publishOn(thread)
                                      .subscribe(actual::set);
        list.add(4);
        assertThat(actual.get()).containsExactly(1, 2, 3, 4);

        list.remove(3);
        assertThat(actual.get()).containsExactly(1, 2, 3);
        Phaser p = new Phaser(2);
        Platform.runLater(() ->
        {
            disposable.dispose();
            p.arrive();
        });
        p.awaitAdvanceInterruptibly(p.arrive(), 3, TimeUnit.SECONDS);
    }

    @Test
    public void testObservableListAdditions()
    {
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 2, 3);
        AtomicReference<Integer> actual = new AtomicReference<>();
        Disposable disposable = FxFlux.fromAdditionsOf(list)
                                      .publishOn(thread)
                                      .subscribe(actual::set);
        list.add(4);
        assertThat(actual.get()).isEqualTo(4);

        list.remove(3);
        assertThat(actual.get()).isEqualTo(4);
        disposable.dispose();
    }

    @Test
    public void testObservableListRemovals()
    {
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 2, 3);
        AtomicReference<Integer> actual = new AtomicReference<>();
        Disposable disposable = FxFlux.fromRemovalsOf(list)
                                      .publishOn(thread)
                                      .subscribe(actual::set);
        list.remove(0);
        assertThat(actual.get()).isEqualTo(1);

        list.add(3);
        assertThat(actual.get()).isEqualTo(1);
        disposable.dispose();
    }

    @Test
    public void testSceneEvent() throws TimeoutException, InterruptedException
    {
        AtomicReference<Scene> actual = new AtomicReference<>();
        AtomicReference<Node> actualNode = new AtomicReference<>();
        FX_RULE.onStage(stage ->
        {
            Pane pane = new Pane();
            Scene scene = new Scene(pane);
            actualNode.set(pane);
            actual.set(scene);
            stage.setScene(scene);
            stage.show();
        });

        AtomicReference<Event> event = new AtomicReference<>();
        Scene scene = actual.get();

        Phaser p = new Phaser(2);
        Disposable disposable = FxFlux.from(scene, KeyEvent.KEY_TYPED)
                                      .subscribeOn(fxThread)
                                      .publishOn(thread)
                                      .subscribe(e ->
                                      {
                                          event.set(e);
                                          p.arrive();
                                      });

        Platform.runLater(() -> actualNode.get()
                                          .fireEvent(KEY_EVENT));

        p.awaitAdvanceInterruptibly(p.arrive(), 3, TimeUnit.SECONDS);
        assertThat(event.get()
                        .getSource()).isEqualTo(scene);
        disposable.dispose();
    }

    @Test
    public void testWindowEvent() throws TimeoutException, InterruptedException
    {
        AtomicReference<Window> actual = new AtomicReference<>();
        AtomicReference<Node> actualNode = new AtomicReference<>();
        FX_RULE.onStage(stage ->
        {
            Pane pane = new Pane();
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            actualNode.set(pane);
            actual.set(stage);
            stage.show();
        });

        AtomicReference<Event> event = new AtomicReference<>();
        Window window = actual.get();

        Phaser p = new Phaser(2);
        Disposable disposable = FxFlux.from(window, KeyEvent.KEY_TYPED)
                                      .subscribeOn(fxThread)
                                      .publishOn(thread)
                                      .subscribe(e ->
                                      {
                                          event.set(e);
                                          p.arrive();
                                      });

        Platform.runLater(() -> actualNode.get()
                                          .fireEvent(KEY_EVENT));
        p.awaitAdvanceInterruptibly(p.arrive(), 3, TimeUnit.SECONDS);
        assertThat(event.get()
                        .getSource()).isEqualTo(window);
        disposable.dispose();
    }

    @Test
    public void testObservableMap()
    {
        ObservableMap<String, Integer> map = FXCollections.observableHashMap();
        map.put(KEY0, 0);
        AtomicReference<ObservableMap<String, Integer>> actual = new AtomicReference<>();
        Disposable disposable = FxFlux.from(map)
                                      .publishOn(thread)
                                      .subscribe(actual::set);

        map.put(KEY1, 1);
        assertThat(actual.get()).containsOnlyKeys(KEY0, KEY1);
        assertThat(actual.get()).containsValues(0, 1);

        map.remove(KEY0);
        assertThat(actual.get()).containsOnlyKeys(KEY1);
        assertThat(actual.get()).containsValues(1);

        disposable.dispose();
    }

    @Test
    public void testObservableMapAdditions()
    {
        ObservableMap<String, Integer> map = FXCollections.observableHashMap();
        map.put(KEY0, 0);
        AtomicReference<Map.Entry<String, Integer>> actual = new AtomicReference<>();
        Disposable disposable = FxFlux.fromAdditionsOf(map)
                                      .publishOn(thread)
                                      .subscribe(actual::set);

        map.put(KEY1, 1);
        Map.Entry<String, Integer> added = actual.get();
        assertThat(added.getKey()).isEqualTo(KEY1);
        assertThat(added.getValue()).isEqualTo(1);

        map.remove(KEY0);
        Map.Entry<String, Integer> added2 = actual.get();
        assertThat(added2.getKey()).isEqualTo(KEY1);
        assertThat(added2.getValue()).isEqualTo(1);

        disposable.dispose();
    }

    @Test
    public void testObservableMapRemovals()
    {
        ObservableMap<String, Integer> map = FXCollections.observableHashMap();
        map.put(KEY0, 0);
        AtomicReference<Map.Entry<String, Integer>> actual = new AtomicReference<>();
        Disposable disposable = FxFlux.fromRemovalsOf(map)
                                      .publishOn(thread)
                                      .subscribe(actual::set);

        map.remove(KEY0);
        Map.Entry<String, Integer> removed = actual.get();
        assertThat(removed.getKey()).isEqualTo(KEY0);
        assertThat(removed.getValue()).isEqualTo(0);

        map.put(KEY1, 1);
        Map.Entry<String, Integer> removed2 = actual.get();
        assertThat(removed2.getKey()).isEqualTo(KEY0);
        assertThat(removed2.getValue()).isEqualTo(0);

        disposable.dispose();
    }

    @Test
    public void testObservableSet()
    {
        ObservableSet<Integer> set = FXCollections.observableSet();
        AtomicReference<ObservableSet<Integer>> actual = new AtomicReference<>();
        Disposable disposable = FxFlux.from(set)
                                      .publishOn(thread)
                                      .subscribe(actual::set);

        set.add(1);
        assertThat(actual.get()).containsOnly(1);
        assertThat(actual.get()).isEqualTo(set);

        set.remove(1);
        assertThat(actual.get()).isEmpty();
        assertThat(actual.get()).isEqualTo(set);

        disposable.dispose();
    }

    @Test
    public void testObservableSetAdditions()
    {
        ObservableSet<Integer> set = FXCollections.observableSet();
        AtomicInteger actual = new AtomicInteger();
        Disposable disposable = FxFlux.fromAdditionsOf(set)
                                      .publishOn(thread)
                                      .subscribe(actual::set);

        set.add(1);
        assertThat(actual.get()).isEqualTo(1);

        set.remove(1);
        assertThat(actual.get()).isEqualTo(1);

        set.add(2);
        assertThat(actual.get()).isEqualTo(2);

        disposable.dispose();
    }

    @Test
    public void testObservableSetRemovals()
    {
        ObservableSet<Integer> set = FXCollections.observableSet();
        AtomicInteger actual = new AtomicInteger();
        Disposable disposable = FxFlux.fromRemovalsOf(set)
                                      .publishOn(thread)
                                      .subscribe(actual::set);

        set.add(1);
        set.add(2);
        set.remove(1);
        assertThat(actual.get()).isEqualTo(1);

        set.remove(2);
        assertThat(actual.get()).isEqualTo(2);

        set.add(4);
        assertThat(actual.get()).isEqualTo(2);

        disposable.dispose();
    }

    @Test
    public void testObservableIntegerArray()
    {
        ObservableIntegerArray array = FXCollections.observableIntegerArray();
        AtomicReference<ObservableIntegerArray> actual = new AtomicReference<>();
        Disposable disposable = FxFlux.from(array)
                                      .publishOn(thread)
                                      .subscribe(actual::set);
        array.addAll(1);
        int[] dest = new int[1];
        actual.get()
              .toArray(dest);
        assertThat(dest).containsExactly(1);

        array.addAll(2, 3);
        int[] dest2 = new int[3];
        actual.get()
              .toArray(dest2);
        assertThat(dest2).containsExactly(1, 2, 3);

        disposable.dispose();
    }

    @Test
    public void testObservableFloatArray()
    {
        ObservableFloatArray array = FXCollections.observableFloatArray();
        AtomicReference<ObservableFloatArray> actual = new AtomicReference<>();
        Disposable disposable = FxFlux.from(array)
                                      .publishOn(thread)
                                      .subscribe(actual::set);
        array.addAll(1.0f);
        float[] dest = new float[1];
        actual.get()
              .toArray(dest);
        assertThat(dest).containsExactly(1.0f);

        array.addAll(2.0f, 3.0f);
        float[] dest2 = new float[3];
        actual.get()
              .toArray(dest2);
        assertThat(dest2).containsExactly(1.0f, 2.0f, 3.0f);

        disposable.dispose();
    }

    @Test
    public void testObservableIntegerArrayChanges()
    {
        ObservableIntegerArray array = FXCollections.observableIntegerArray();
        AtomicReference<ObservableIntegerArray> actual = new AtomicReference<>();
        Disposable disposable = FxFlux.fromChangesOf(array)
                                      .publishOn(thread)
                                      .subscribe(actual::set);
        array.addAll(1);
        int[] dest = new int[1];
        actual.get()
              .toArray(dest);
        assertThat(dest).containsExactly(1);

        array.addAll(2, 3);
        int[] dest2 = new int[2];
        actual.get()
              .toArray(dest2);
        assertThat(dest2).containsExactly(2, 3);

        disposable.dispose();
    }

    @Test
    public void testObservableFloatArrayChanges()
    {
        ObservableFloatArray array = FXCollections.observableFloatArray();
        AtomicReference<ObservableFloatArray> actual = new AtomicReference<>();
        Disposable disposable = FxFlux.fromChangesOf(array)
                                      .publishOn(thread)
                                      .subscribe(actual::set);
        array.addAll(1.0f);
        float[] dest = new float[1];
        actual.get()
              .toArray(dest);
        assertThat(dest).containsExactly(1.0f);

        array.addAll(2.0f, 3.0f);
        float[] dest2 = new float[2];
        actual.get()
              .toArray(dest2);
        assertThat(dest2).containsExactly(2.0f, 3.0f);

        disposable.dispose();
    }
}