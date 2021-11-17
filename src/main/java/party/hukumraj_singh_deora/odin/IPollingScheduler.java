/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package party.hukumraj_singh_deora.odin;

public interface IPollingScheduler {
    void startScheduling(Runnable pollingMethod, int intervalInSeconds);

    void stopScheduling();
}
