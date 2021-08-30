/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

class MongoDBIntermediate implements IDatabaseIntermediate {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBIntermediate.class);
    private final MongoCollection<Document> subscribePledgeCollection;
    private final MongoCollection<Document> recordsCollection;

    private final String subscribePledgeFieldNameTag = "tag";
    private final String subscribePledgeFieldNameSubscriber = "subscriber";

    private final String recordFieldNameTag = "tag";
    private final String recordFieldNameCreatedAt = "createdAt";
    private final String recordFieldNameConsumers = "consumersIntended";
    private final String recordFieldNameData = "data";

    public MongoDBIntermediate(String connectionString, String databaseName) {
        logger.info("MongoDB Intermediate attempting to connect");
        MongoClient client = MongoClients.create(connectionString);
        MongoDatabase database = client.getDatabase(databaseName);

        this.subscribePledgeCollection = database.getCollection("subscribers");
        this.recordsCollection = database.getCollection("records");
        ensureIndices();
    }

    private void ensureIndices(){
        subscribePledgeCollection.createIndex(Indexes.text(subscribePledgeFieldNameTag));
        recordsCollection.createIndex(Indexes.text(recordFieldNameTag));
        recordsCollection.createIndex(Indexes.hashed("_id"));
        recordsCollection.createIndex(Indexes.descending(recordFieldNameCreatedAt));
    }

    @Override
    public void ensureSubscribePledge(String tag, String subscriberName) {
        SubscribePledge subscribePledge = new SubscribePledge(subscriberName, tag);

        if (!doesSubscribePledgeExist(subscribePledge))
        {
            subscribePledgeCollection.insertOne(convertToDocument(subscribePledge));
            addSubscriberNameToExistingRecordsWithTag(tag, subscriberName);
        }
    }

    private boolean doesSubscribePledgeExist(SubscribePledge subscribePledge) {
        Document result = subscribePledgeCollection.find(
                and(
                        eq(subscribePledgeFieldNameSubscriber, subscribePledge.getSubscriberName()),
                        eq(subscribePledgeFieldNameTag, subscribePledge.getTag())
                )
        ).first();
        return result!=null;
    }

    private void addSubscriberNameToExistingRecordsWithTag(String tag, String subscriberName) {
        recordsCollection.find(eq(recordFieldNameTag, tag))
                .forEach(document -> {
                    var consumers = document.getList(recordFieldNameConsumers, String.class);
                    consumers.add(subscriberName);
                    recordsCollection.updateOne(eq("_id",document.get("_id")), set(recordFieldNameConsumers, consumers));
                });
    }

    @Override
    public void pushRecord(String tag, String data) {
        var tagSubscribers = getSubscribersForTag(tag);
        Record rec = new Record(tag, LocalDateTime.now(),
                (ArrayList<String>) tagSubscribers, data);
        recordsCollection.insertOne(convertToDocument(rec));
    }

    private List<String> getSubscribersForTag(String tag) {
        List<String> allSubscribers = new ArrayList<>();
        subscribePledgeCollection.find(eq(subscribePledgeFieldNameTag, tag))
                .forEach( document ->
                    allSubscribers.add(document.getString(subscribePledgeFieldNameSubscriber))
                );
        return allSubscribers;
    }

    @Override
    public List<String> pullRecords(String tag, String subscriberName) {
        List<String> recordsData = new ArrayList<>();
        recordsCollection.find(eq(recordFieldNameTag, tag))
                .sort(Sorts.descending(recordFieldNameCreatedAt))
                .forEach(document -> {
                    recordsData.add(document.getString(recordFieldNameData));
                    var consumers = document.getList(recordFieldNameConsumers, String.class);
                    consumers.remove(subscriberName);
                    recordsCollection.updateOne(eq("_id",document.get("_id")), set(recordFieldNameConsumers, consumers));
                });
        return recordsData;
    }

    @Override
    public void deleteAllReadRecords() {
        recordsCollection.deleteMany(size(recordFieldNameConsumers, 0));
    }

    private Document convertToDocument(SubscribePledge sp){
        Document document = new Document();
        document.append(subscribePledgeFieldNameSubscriber, sp.getSubscriberName());
        document.append(subscribePledgeFieldNameTag, sp.getTag());
        return document;
    }

    private Document convertToDocument(Record rec){
        Document document = new Document();
        document.append(recordFieldNameTag, rec.getTag());
        document.append(recordFieldNameCreatedAt, rec.getCreatedAt());
        document.append(recordFieldNameConsumers, rec.getConsumersIntended());
        document.append(recordFieldNameData, rec.getData());
        return document;
    }
}
