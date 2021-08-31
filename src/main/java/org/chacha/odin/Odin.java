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

    private String subscriberName;
    private List<String> listOfTagsToMonitor;

    private IDatabaseIntermediate databaseIntermediate;

    private final int schedulerThreadPoolSize;
    private final IPollingScheduler pollingScheduler;

    public Odin() throws IOException {
        Configuration odinConfiguration = new Configuration();

        try {
            this.subscriberName = odinConfiguration.getSubscriberName();
            this.listOfTagsToMonitor = odinConfiguration.getListOfTagsToMonitor();
            this.databaseIntermediate = odinConfiguration.getDatabaseIntermediate();
        }
        catch (Configuration.MissingPropertyException e) {
            logger.error(e.getMessage(), e);
        }

        this.schedulerThreadPoolSize = odinConfiguration.getPollingSchedulerThreadPoolSize();
        this.pollingScheduler = new PollingScheduler(this.schedulerThreadPoolSize);

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
        logger.info("Starting to poll for records with {}s interval.", intervalInSeconds);
        pollingScheduler.startScheduling(() -> pullRecords(callback), intervalInSeconds);
    }

    public void stopPollingRecords(){
        logger.info("Stopping to poll for records.");
        pollingScheduler.stopScheduling();
    }

    public void pullRecords(BiConsumer<String, String> callback){
        logger.info("Pulling all Records.");
        for (String tag : listOfTagsToMonitor){
            logger.info("Looking for Tag: {}", tag);
            List<String> recordsData = databaseIntermediate.pullRecords(tag, subscriberName);
            for (String singleRecordData : recordsData) {
                callback.accept(tag, singleRecordData);
            }
        }
        databaseIntermediate.deleteAllReadRecords();
    }

    public void pushRecord(String tag, String data){
        logger.info("Pushing record with Tag: {}", tag);
        databaseIntermediate.pushRecord(tag, data);
    }

    private void subscribeToTagsIfNotAlready(){
        logger.info("Subscribing for the following tags: {}", listOfTagsToMonitor);
        for (String tag : listOfTagsToMonitor) {
            databaseIntermediate.ensureSubscribePledge(tag, subscriberName);
        }
    }
}
