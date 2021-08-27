/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

public interface IPollingScheduler {
    void startScheduling(Runnable pollingMethod, int intervalInSeconds);

    void stopScheduling();
}
