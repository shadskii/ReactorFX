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

import freetimelabs.io.reactorfx.schedulers.FxSchedulers;
import javafx.application.Platform;
import org.junit.ClassRule;
import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

public class FxSchedulersTest
{
    @ClassRule
    public static final FxTestRule FX_RULE = new FxTestRule();

    @Test
    public void testPlatform() throws TimeoutException, InterruptedException
    {
        AtomicBoolean isFx = new AtomicBoolean(false);
        Phaser p = new Phaser(2);
        Disposable disposable = Flux.just(1)
                                    .publishOn(FxSchedulers.platform())
                                    .subscribe(l ->
                                    {
                                        isFx.set(Platform.isFxApplicationThread());
                                        p.arrive();
                                    });
        p.awaitAdvanceInterruptibly(p.arrive(), 3, TimeUnit.SECONDS);
        assertThat(isFx).isTrue();
    }

    @Test
    public void testNoInstance() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        NoInstanceTestHelper.testNoInstance(FxSchedulers.class);
    }
}
