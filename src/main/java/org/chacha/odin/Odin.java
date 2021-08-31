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

    private int schedulerThreadPoolSize;
    private IPollingScheduler pollingScheduler;

    /**
     * An Odin instance will be created by this function.
     * This instance will be configured by the application.properties file.
     * The required properties are:
     * <ul>
     *     <li>odin.subscriberName: [String] Name of the your application</li>
     *     <li>odin.tagsToMonitor: [Comma-seperated String List] With tags your application will be able to pull the records of</li>
     *     <li>odin.connectionString and odin.databaseName: Data to connect to a MongoDB Database</li>
     * </ul>
     *
     */
    public Odin() {
        try {
            Configuration odinConfiguration = new Configuration();
            this.subscriberName = odinConfiguration.getSubscriberName();
            this.listOfTagsToMonitor = odinConfiguration.getListOfTagsToMonitor();

            this.schedulerThreadPoolSize = odinConfiguration.getPollingSchedulerThreadPoolSize();
            this.pollingScheduler = new PollingScheduler(this.schedulerThreadPoolSize);
            this.databaseIntermediate = odinConfiguration.getDatabaseIntermediate();
        }
        catch (Configuration.MissingPropertyException | IOException e) {
            logger.error(e.getMessage(), e);
            return;
        }

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

    /**
     * @param callback This is the method that will be called when a record is pulled.
     *                 You can give a lambda expression with two inputs (String tag, String data).
     * @param intervalInSeconds Odin will check for new records periodically. You can decide how frequent you want to look for records.
     */
    public void startPollingRecords(BiConsumer<String, String> callback, int intervalInSeconds){
        logger.info("Starting to poll for records with {}s interval.", intervalInSeconds);
        pollingScheduler.startScheduling(() -> pullRecords(callback), intervalInSeconds);
    }

    /**
     * This method stops polling for records. You will have to provide the callback again the next time you start polling.
     */
    public void stopPollingRecords(){
        logger.info("Stopping to poll for records.");
        pollingScheduler.stopScheduling();
    }

    /**
     * @param callback This is the method that will be called when a record is pulled.
     *                 You can give a lambda expression with two inputs (String tag, String data).
     *                 The records will be pulled in a LIFO manner.
     * This function only looks for records once. If you want to look for records periodically use {@link org.chacha.odin.Odin#startPollingRecords(BiConsumer, int)}
     */
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

    /**
     * @param tag Tag that goes along with the record. Applications that configured this tag in their Odin can pull this record.
     * @param data Data that belong with the tag. It can also be a JsonString (not parsed by Odin, however)
     */
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
