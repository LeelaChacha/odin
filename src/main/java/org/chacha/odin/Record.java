/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
class Record {
    private String tag;
    private LocalDateTime createdAtUtc;
    private ArrayList<String> consumersIntended;
    private String data;
}
