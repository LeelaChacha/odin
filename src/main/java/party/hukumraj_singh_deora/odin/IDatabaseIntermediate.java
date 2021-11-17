/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package party.hukumraj_singh_deora.odin;

import java.util.List;

interface IDatabaseIntermediate {
    void ensureSubscribePledge(String tag, String subscriberName);

    void pushRecord(String tag, String data);

    List<String> pullRecords(String tag, String subscriberName);

    void deleteAllReadRecords();
}
