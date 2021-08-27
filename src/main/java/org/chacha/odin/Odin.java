/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class Odin {
    // TODO: Implement logging
    private static final Logger logger = LoggerFactory.getLogger(Odin.class);

    // TODO: Make settings configurable - possibly make a new class to provide configuration
    private final String subscriberName;
    private final ArrayList<String> listOfTagsToMonitor;

    private final IDatabaseIntermediate databaseIntermediate;

    private final int schedulerThreadPoolSize;
    private final PollingScheduler pollingScheduler;

    public Odin() {
        Configuration odinConfiguration = new Configuration();
        this.subscriberName = odinConfiguration.getSubscriberName();
        this.listOfTagsToMonitor = odinConfiguration.getListOfTagsToMonitor();

        this.schedulerThreadPoolSize = odinConfiguration.getPollingSchedulerThreadPoolSize();
        this.pollingScheduler = new PollingScheduler(this.schedulerThreadPoolSize);

        this.databaseIntermediate = odinConfiguration.getDatabaseIntermediate();
    }

    Odin( String subscriberName, ArrayList<String> listOfTagsToMonitor,
          IDatabaseIntermediate databaseIntermediate,
          PollingScheduler pollingScheduler) {

        this.subscriberName = subscriberName;
        this.listOfTagsToMonitor = listOfTagsToMonitor;
        this.subscribeToTagsIfNotAlready();
        this.schedulerThreadPoolSize = 1;

        this.pollingScheduler = pollingScheduler;
        this.databaseIntermediate = databaseIntermediate;
    }

    // TODO: Add javadoc to public functions

    public void startPollingRecords(BiConsumer<String, String> callback, int intervalInSeconds){
        pollingScheduler.startScheduling(() -> pullRecords(callback), intervalInSeconds);
    }

    public void stopPollingRecords(){
        pollingScheduler.stopScheduling();
    }

    public void pullRecords(BiConsumer<String, String> callback){
        for (String tag : listOfTagsToMonitor){
            List<String> recordData = databaseIntermediate.pullRecord(tag, subscriberName);
            for (String singleRecordData : recordData) {
                callback.accept(singleRecordData, tag);
            }
        }
    }

    public void pushRecords(String tag, String data){
        databaseIntermediate.pushRecord(tag, data);
    }

    private void subscribeToTagsIfNotAlready(){
        for (String tag : listOfTagsToMonitor) {
            databaseIntermediate.submitSubscriberPledge(tag, subscriberName);
        }
    }
}
