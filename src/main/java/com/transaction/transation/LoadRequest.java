package com.transaction.transation;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadRequest {
    private String userId;
    private String messageId;
    private Amount transactionAmount;
}
