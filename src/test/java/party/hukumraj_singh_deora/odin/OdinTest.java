/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package party.hukumraj_singh_deora.odin;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OdinTest {
    @Test
    void shouldPullRecord(){
        IPollingScheduler pollingScheduler = mock(IPollingScheduler.class);
        IDatabaseIntermediate databaseIntermediate = mock(IDatabaseIntermediate.class);

        when(databaseIntermediate.pullRecords("TestTag", "TestName"))
                .thenReturn(List.of("TestData"));

        Odin odin = new Odin("TestName", new ArrayList<>(List.of("TestTag")),
                databaseIntermediate, pollingScheduler);

        odin.pullRecords((tag, data) -> {
            assertThat(tag).isNotNull().isNotEmpty().isEqualTo("TestTag");
            assertThat(data).isNotNull().isNotEmpty().isEqualTo("TestData");
        });
    }
}
