/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class SubscribePledge {
    private String subscriberName;
    private String tag;
}
