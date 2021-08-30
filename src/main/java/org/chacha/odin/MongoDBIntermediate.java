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

    public MongoDBIntermediate(String connectionString, String databaseName) {
        logger.info("MongoDB Intermediate attempting to connect");
        MongoClient client = MongoClients.create(connectionString);
        MongoDatabase database = client.getDatabase(databaseName);

        this.subscribePledgeCollection = database.getCollection("subscribers");
        this.recordsCollection = database.getCollection("records");
        ensureIndices();
    }

    private void ensureIndices(){
        subscribePledgeCollection.createIndex(Indexes.text("tag"));
        recordsCollection.createIndex(Indexes.text("tag"));
        recordsCollection.createIndex(Indexes.hashed("_id"));
        recordsCollection.createIndex(Indexes.descending("createdAt"));
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
                        eq("subscriber", subscribePledge.getSubscriberName()),
                        eq("tag", subscribePledge.getTag())
                )
        ).first();
        return result!=null;
    }

    private void addSubscriberNameToExistingRecordsWithTag(String tag, String subscriberName) {
        recordsCollection.find(eq("tag", tag))
                .forEach(document -> {
                    var consumers = document.getList("consumersIntended", String.class);
                    consumers.add(subscriberName);
                    recordsCollection.updateOne(eq("_id",document.get("_id")), set("consumersIntended", consumers));
                });
    }

    @Override
    public void pushRecord(String tag, String data) {
        var tagSubscribers = getSubscribersForTag(tag);
        Record record = new Record(tag, LocalDateTime.now(),
                (ArrayList<String>) tagSubscribers, data);
        recordsCollection.insertOne(convertToDocument(record));
    }

    private List<String> getSubscribersForTag(String tag) {
        List<String> allSubscribers = new ArrayList<>();
        subscribePledgeCollection.find(eq("tag", tag))
                .forEach( document ->
                    allSubscribers.add(document.getString("subscriber"))
                );
        return allSubscribers;
    }

    @Override
    public List<String> pullRecords(String tag, String subscriberName) {
        List<String> recordsData = new ArrayList<>();
        recordsCollection.find(eq("tag", tag))
                .sort(Sorts.descending("createdAt"))
                .forEach(document -> {
                    recordsData.add(document.getString("data"));
                    var consumers = document.getList("consumersIntended", String.class);
                    consumers.remove(subscriberName);
                    recordsCollection.updateOne(eq("_id",document.get("_id")), set("consumersIntended", consumers));
                });
        return recordsData;
    }

    @Override
    public void deleteAllReadRecords() {
        recordsCollection.deleteMany(size("consumersIntended", 0));
    }

    private Document convertToDocument(SubscribePledge sp){
        Document document = new Document();
        document.append("subscriber", sp.getSubscriberName());
        document.append("tag", sp.getTag());
        return document;
    }

    private Document convertToDocument(Record record){
        Document document = new Document();
        document.append("tag", record.getTag());
        document.append("createdAt", record.getCreatedAt());
        document.append("consumersIntended", record.getConsumersIntended());
        document.append("data", record.getData());
        return document;
    }
}
