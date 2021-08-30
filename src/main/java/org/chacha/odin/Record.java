/*
 * Copyright (c) 2021 Hukumraj Singh Deora
 */

package org.chacha.odin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@AllArgsConstructor
class Record {
    private String tag;
    private LocalDateTime createdAt;
    private ArrayList<String> consumersIntended;
    private String data;
}
