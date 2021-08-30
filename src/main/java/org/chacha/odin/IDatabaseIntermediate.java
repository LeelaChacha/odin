/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import java.util.List;

interface IDatabaseIntermediate {
    void ensureSubscribePledge(String tag, String subscriberName);

    void pushRecord(String tag, String data);

    List<String> pullRecords(String tag, String subscriberName);

    void deleteAllReadRecords();
}
