/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import java.util.List;

interface IDatabaseIntermediate {
    boolean submitSubscriberPledge(String tag, String subscriberName);

    boolean pushRecord(String tag, String data);

    List<String> pullRecord(String tag, String subscriberName);

    List<String> getAllSubscribersForTag(String tag);
}
