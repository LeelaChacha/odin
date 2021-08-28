/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.List;

class ConfigurationTest {
    private Configuration configuration;

    @Test
    void shouldReturnValuesFromPropertiesFile() throws IOException, Configuration.MissingPropertyException {
        configuration = new Configuration("src/test/resources/test.properties");

        String subscriberName = configuration.getSubscriberName();
        List<String> listOfTagsToMonitor = configuration.getListOfTagsToMonitor();

        assertThat(subscriberName).isNotNull().isNotEmpty().isEqualTo("MyApplication");
        assertThat(listOfTagsToMonitor).isNotNull().hasSize(2).hasSameElementsAs(List.of("Tag1", "Tag2"));
    }

    @Test
    void shouldReturnDefaultValuesIfPropertyNotFound() throws IOException {

        configuration = new Configuration("src/test/resources/test.properties");

        int threadPoolSize = configuration.getPollingSchedulerThreadPoolSize();
        assertThat(threadPoolSize).isNotNull().isEqualTo(1);
    }

    @Test
    void shouldThrowExceptionIfPropertyNotFound() throws IOException {

        configuration = new Configuration("src/test/resources/empty.properties");

        assertThatThrownBy(() -> configuration.getSubscriberName())
                .isInstanceOf(Configuration.MissingPropertyException.class);
        assertThatThrownBy(() -> configuration.getListOfTagsToMonitor())
                .isInstanceOf(Configuration.MissingPropertyException.class);
    }
}
