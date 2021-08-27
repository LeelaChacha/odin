/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.util.List;

class MongoDBIntermediate implements IDatabaseIntermediate {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBIntermediate.class);
    private final MongoClient client;

    public MongoDBIntermediate(String connectionString) {
        logger.info("MongoDB Intermediate attempting to connect");
        client = MongoClients.create(connectionString);
    }

    @Override
    public void submitSubscribePledgeIfNotAlreadyPresent(String tag, String subscriberName) {
        // Also add subscriber name to existing records with same tag
    }

    @Override
    public void pushRecord(String tag, String data) {
    }

    @Override
    public List<String> getAllSubscribersForTag(String tag) {
        return null;
    }

    @Override
    public List<String> pullRecordAndRemoveSubscriberNameFromIt(String tag, String subscriberName) {
        return null;
    }

}
