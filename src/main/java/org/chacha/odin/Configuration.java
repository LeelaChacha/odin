/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private final Properties properties;

    public Configuration() throws IOException {
        logger.info("Odin will load configuration from application.properties file in resources.");
        this.properties = new Properties();
        try(InputStream inputStream = getClass().getResourceAsStream("application.properties")){
            this.properties.load(inputStream);
        }
    }

    Configuration(String pathToPropertiesFile) throws IOException {
        logger.info("Odin will load configuration from " + pathToPropertiesFile);
        this.properties = new Properties();
        try(InputStream inputStream = new FileInputStream(pathToPropertiesFile)){
            this.properties.load(inputStream);
        }
    }

    public IDatabaseIntermediate getDatabaseIntermediate()
            throws MissingPropertyException {
        logger.debug("Injecting Database Dependency");
        String connectionString = getPropertyAndThrowExceptionIfMissing("odin.connectionString");
        String databaseName = getPropertyAndThrowExceptionIfMissing("odin.databaseName");
        return new MongoDBIntermediate(connectionString, databaseName);
    }

    public String getSubscriberName() throws MissingPropertyException {
        logger.debug("Injecting Database Dependency");
        return getPropertyAndThrowExceptionIfMissing("odin.subscriberName");
    }

    public List<String> getListOfTagsToMonitor() throws MissingPropertyException {
        String listString = getPropertyAndThrowExceptionIfMissing("odin.tagsToMonitor");
        return Arrays.asList(listString.split(",", -1));
    }

    public int getPollingSchedulerThreadPoolSize() {
        String threadPoolSize = properties.getProperty("odin.schedulerThreadPoolSize", "1");
        return Integer.parseInt(threadPoolSize);
    }

    private String getPropertyAndThrowExceptionIfMissing(String propertyName)
            throws MissingPropertyException {
        logger.debug("Looking for Property: " + propertyName);
        String property = properties.getProperty(propertyName);
        if(property == null)
            throw new MissingPropertyException(propertyName);
        return property;
    }

    static class MissingPropertyException extends Exception{
        public MissingPropertyException(String propertyName) {
            super("The required property [" + propertyName + "] is missing from properties file");
        }
    }
}
