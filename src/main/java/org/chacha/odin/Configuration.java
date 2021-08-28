/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

class Configuration {
    private final Properties properties;

    public Configuration() throws IOException {
        properties = new Properties();
        try(InputStream inputStream = getClass().getResourceAsStream("application.properties")){
            properties.load(inputStream);
        }
    }

    Configuration(String pathToPropertiesFile) throws IOException {
        properties = new Properties();
        try(InputStream inputStream = new FileInputStream(pathToPropertiesFile)){
            properties.load(inputStream);
        }
    }

    public IDatabaseIntermediate getDatabaseIntermediate() throws MissingPropertyException {
        String connectionString = getPropertyAndThrowExceptionIfMissing("odin.connectionString");
        return new MongoDBIntermediate(connectionString);
    }

    public String getSubscriberName() throws MissingPropertyException {
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

    private String getPropertyAndThrowExceptionIfMissing(String propertyName) throws MissingPropertyException {
        String property = properties.getProperty(propertyName);
        if(property == null)
            throw new MissingPropertyException("odin.subscriberName");
        return property;
    }

    static class MissingPropertyException extends Exception{
        public MissingPropertyException(String propertyName) {
            super("The required property '" + propertyName + "' is missing from application.properties");
        }
    }
}
