/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class PollingSchedulerTest {
    private IPollingScheduler pollingScheduler;

    @BeforeEach
    void setup(){
        pollingScheduler = new PollingScheduler(1);
    }

    @Test
    void shouldScheduleMethodAtProvidedInterval() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();
        counter.set(0);

        pollingScheduler.startScheduling(counter::getAndIncrement, 5);
        Thread.sleep(12000);
        pollingScheduler.stopScheduling();

        assertThat(counter.get()).isEqualTo(2);
    }
}
