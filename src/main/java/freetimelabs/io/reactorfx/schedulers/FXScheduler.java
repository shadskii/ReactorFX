package freetimelabs.io.reactorfx.schedulers;

import javafx.application.Platform;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public final class FXScheduler
{
    private static final Scheduler FX_THREAD = Schedulers.fromExecutor(Platform::runLater);

    private FXScheduler()
    {
        // Nothing
    }

    /**
     * This is the scheduler that should be used to do any FX work.
     * @return A Scheduler that provides access to the JavaFX application thread.
     */
    public static Scheduler getFxThread()
    {
        return FX_THREAD;
    }


}
