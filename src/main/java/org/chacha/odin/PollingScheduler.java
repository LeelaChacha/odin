/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

class PollingScheduler implements IPollingScheduler {

    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> pollingHandle;

    PollingScheduler() {
        scheduler = Executors.newScheduledThreadPool(1);
    }

    PollingScheduler(int threadPoolSize) {
        scheduler = Executors.newScheduledThreadPool(threadPoolSize);
    }

    public void startScheduling(Runnable pollingMethod, int intervalInSeconds){
        pollingHandle = scheduler.scheduleAtFixedRate(pollingMethod,
                intervalInSeconds, intervalInSeconds, SECONDS);
    }

    public void stopScheduling(){
        if(pollingHandle != null){
            pollingHandle.cancel(true);
            pollingHandle = null;
        }
    }
}