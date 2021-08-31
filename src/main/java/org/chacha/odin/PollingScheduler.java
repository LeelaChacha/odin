/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

class PollingScheduler implements IPollingScheduler {
    private static final Logger logger = LoggerFactory.getLogger(PollingScheduler.class);

    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> pollingHandle;

    PollingScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    PollingScheduler(int threadPoolSize) {
        logger.debug(String.format("PollingScheduler initialised with Thread Pool size: %d", threadPoolSize));
        this.scheduler = Executors.newScheduledThreadPool(threadPoolSize);
    }

    public void startScheduling(Runnable pollingMethod, int intervalInSeconds){
        logger.debug("PollingScheduler starting to schedule.");
        pollingHandle = scheduler.scheduleAtFixedRate(pollingMethod,
                intervalInSeconds, intervalInSeconds, SECONDS);
    }

    public void stopScheduling(){
        logger.debug("PollingScheduler stopping.");
        if(pollingHandle != null){
            pollingHandle.cancel(true);
            pollingHandle = null;
        }
    }
}
