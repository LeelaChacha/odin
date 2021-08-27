/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import java.util.ArrayList;

// TODO: Implement class to read properties file and provide values to Odin
public class Configuration {

    public Configuration() {
    }

    public String getSubscriberName() {
        return null;
    }

    public ArrayList<String> getListOfTagsToMonitor() {
        return null;
    }

    public int getPollingSchedulerThreadPoolSize() {
        return 1;
    }

    public IDatabaseIntermediate getDatabaseIntermediate() {
        return null;
    }
}
