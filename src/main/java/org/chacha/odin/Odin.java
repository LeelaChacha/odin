/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class Odin {
    private static final Logger logger = LoggerFactory.getLogger(Odin.class);

    private final String subscriberName;
    private final List<String> listOfTagsToMonitor;

    private final IDatabaseIntermediate databaseIntermediate;

    private final int schedulerThreadPoolSize;
    private final IPollingScheduler pollingScheduler;

    public Odin() throws IOException, Configuration.MissingPropertyException {
        Configuration odinConfiguration = new Configuration();
        this.subscriberName = odinConfiguration.getSubscriberName();
        this.listOfTagsToMonitor = odinConfiguration.getListOfTagsToMonitor();

        this.schedulerThreadPoolSize = odinConfiguration.getPollingSchedulerThreadPoolSize();
        this.pollingScheduler = new PollingScheduler(this.schedulerThreadPoolSize);

        this.databaseIntermediate = odinConfiguration.getDatabaseIntermediate();

        this.subscribeToTagsIfNotAlready();
    }

    Odin( String subscriberName, ArrayList<String> listOfTagsToMonitor,
          IDatabaseIntermediate databaseIntermediate,
          IPollingScheduler pollingScheduler) {

        this.subscriberName = subscriberName;
        this.listOfTagsToMonitor = listOfTagsToMonitor;
        this.schedulerThreadPoolSize = 1;

        this.pollingScheduler = pollingScheduler;
        this.databaseIntermediate = databaseIntermediate;

        this.subscribeToTagsIfNotAlready();
    }

    public void startPollingRecords(BiConsumer<String, String> callback, int intervalInSeconds){
        pollingScheduler.startScheduling(() -> pullRecords(callback), intervalInSeconds);
    }

    public void stopPollingRecords(){
        pollingScheduler.stopScheduling();
    }

    public void pullRecords(BiConsumer<String, String> callback){
        for (String tag : listOfTagsToMonitor){
            List<String> recordsData = databaseIntermediate.pullRecords(tag, subscriberName);
            for (String singleRecordData : recordsData) {
                callback.accept(tag, singleRecordData);
            }
        }
        databaseIntermediate.deleteAllReadRecords();
    }

    public void pushRecord(String tag, String data){
        databaseIntermediate.pushRecord(tag, data);
    }

    private void subscribeToTagsIfNotAlready(){
        for (String tag : listOfTagsToMonitor) {
            databaseIntermediate.ensureSubscribePledge(tag, subscriberName);
        }
    }
}
