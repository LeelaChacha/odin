/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package party.hukumraj_singh_deora.odin;

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

    private static final String SUBSCRIBE_PLEDGE_FIELD_NAME_TAG = "tag";
    private static final String SUBSCRIBE_PLEDGE_FIELD_NAME_SUBSCRIBER = "subscriber";

    private static final String RECORD_FIELD_NAME_TAG = "tag";
    private static final String RECORD_FIELD_NAME_CREATED_AT = "createdAt";
    private static final String RECORD_FIELD_NAME_CONSUMERS = "consumersIntended";
    private static final String RECORD_FIELD_NAME_DATA = "data";

    public MongoDBIntermediate(String connectionString, String databaseName) {
        logger.info("MongoDB Intermediate attempting to connect");
        MongoClient client = MongoClients.create(connectionString);
        MongoDatabase database = client.getDatabase(databaseName);

        this.subscribePledgeCollection = database.getCollection("subscribers");
        this.recordsCollection = database.getCollection("records");
        ensureIndices();
    }

    private void ensureIndices(){
        subscribePledgeCollection.createIndex(Indexes.text(SUBSCRIBE_PLEDGE_FIELD_NAME_TAG));
        recordsCollection.createIndex(Indexes.text(RECORD_FIELD_NAME_TAG));
        recordsCollection.createIndex(Indexes.descending(RECORD_FIELD_NAME_CREATED_AT));
    }

    @Override
    public void ensureSubscribePledge(String tag, String subscriberName) {
        SubscribePledge subscribePledge = new SubscribePledge(subscriberName, tag);

        if (!doesSubscribePledgeExist(subscribePledge))
        {
            logger.debug("Creating [{}] in the Database.", subscribePledge);
            subscribePledgeCollection.insertOne(convertToDocument(subscribePledge));
            addSubscriberNameToExistingRecordsWithTag(tag, subscriberName);
        }
    }

    private boolean doesSubscribePledgeExist(SubscribePledge subscribePledge) {
        Document result = subscribePledgeCollection.find(
                and(
                        eq(SUBSCRIBE_PLEDGE_FIELD_NAME_SUBSCRIBER, subscribePledge.getSubscriberName()),
                        eq(SUBSCRIBE_PLEDGE_FIELD_NAME_TAG, subscribePledge.getTag())
                )
        ).first();
        return result!=null;
    }

    private void addSubscriberNameToExistingRecordsWithTag(String tag, String subscriberName) {
        logger.debug("Adding Subscriber Name to existing Records with Tag {}", tag);
        recordsCollection.find(eq(RECORD_FIELD_NAME_TAG, tag))
                .forEach(document -> {
                    var consumers = document.getList(RECORD_FIELD_NAME_CONSUMERS, String.class);
                    consumers.add(subscriberName);
                    recordsCollection.updateOne(eq("_id",document.get("_id")), set(RECORD_FIELD_NAME_CONSUMERS, consumers));
                });
    }

    @Override
    public void pushRecord(String tag, String data) {
        var tagSubscribers = getSubscribersForTag(tag);
        Record rec = new Record(tag, LocalDateTime.now(),
                (ArrayList<String>) tagSubscribers, data);
        logger.debug("Pushing Record to Database: {}", rec);
        recordsCollection.insertOne(convertToDocument(rec));
    }

    private List<String> getSubscribersForTag(String tag) {
        List<String> allSubscribers = new ArrayList<>();
        subscribePledgeCollection.find(eq(SUBSCRIBE_PLEDGE_FIELD_NAME_TAG, tag))
                .forEach( document ->
                    allSubscribers.add(document.getString(SUBSCRIBE_PLEDGE_FIELD_NAME_SUBSCRIBER))
                );
        return allSubscribers;
    }

    @Override
    public List<String> pullRecords(String tag, String subscriberName) {
        logger.debug("Pulling all Records with Tag: {}", tag);
        List<String> recordsData = new ArrayList<>();
        recordsCollection.find(eq(RECORD_FIELD_NAME_TAG, tag))
                .sort(Sorts.descending(RECORD_FIELD_NAME_CREATED_AT))
                .forEach(document -> {
                    recordsData.add(document.getString(RECORD_FIELD_NAME_DATA));
                    var consumers = document.getList(RECORD_FIELD_NAME_CONSUMERS, String.class);
                    consumers.remove(subscriberName);
                    recordsCollection.updateOne(eq("_id",document.get("_id")), set(RECORD_FIELD_NAME_CONSUMERS, consumers));
                });
        return recordsData;
    }

    @Override
    public void deleteAllReadRecords() {
        logger.debug("Deleting empty Records.");
        recordsCollection.deleteMany(size(RECORD_FIELD_NAME_CONSUMERS, 0));
    }

    private Document convertToDocument(SubscribePledge sp){
        Document document = new Document();
        document.append(SUBSCRIBE_PLEDGE_FIELD_NAME_SUBSCRIBER, sp.getSubscriberName());
        document.append(SUBSCRIBE_PLEDGE_FIELD_NAME_TAG, sp.getTag());
        return document;
    }

    private Document convertToDocument(Record rec){
        Document document = new Document();
        document.append(RECORD_FIELD_NAME_TAG, rec.getTag());
        document.append(RECORD_FIELD_NAME_CREATED_AT, rec.getCreatedAt());
        document.append(RECORD_FIELD_NAME_CONSUMERS, rec.getConsumersIntended());
        document.append(RECORD_FIELD_NAME_DATA, rec.getData());
        return document;
    }
}
