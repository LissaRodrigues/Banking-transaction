package com.transaction.transation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadResponse {

    private String userId;
    private String messageId;
    private Amount balance;
}
