/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import java.util.List;

interface IDatabaseIntermediate {
    void submitSubscribePledgeIfNotAlreadyPresent(String tag, String subscriberName);

    void pushRecord(String tag, String data);

    List<String> getAllSubscribersForTag(String tag);

    List<String> pullRecordAndRemoveSubscriberNameFromIt(String tag, String subscriberName);
}
