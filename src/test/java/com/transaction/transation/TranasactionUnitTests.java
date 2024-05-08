package com.transaction.transation;


import com.transaction.transation.Contollers.TransactionController;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class TranasactionUnitTests {

    @Mock
    private Map<String, BigDecimal> userBalances;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testPingEndpoint() {
        ResponseEntity<Ping> response = transactionController.ping();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testAuthorizeTransaction_InsufficientFunds() {
        AuthorizationRequest request = new AuthorizationRequest();
        request.setUserId("user1");
        request.setMessageId("message1");
        Amount amount = new Amount();
        amount.setAmount("100");
        amount.setCurrency("USD");
        amount.setDebitOrCredit("DEBIT");
        request.setTransactionAmount(amount);

        when(userBalances.getOrDefault(any(), any())).thenReturn(BigDecimal.ZERO);

        ResponseEntity<AuthorizationResponse> response = transactionController.authorizeTransaction("message1", request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        AuthorizationResponse responseBody = response.getBody();
        assertEquals("user1", responseBody.getUserId());
        assertEquals("message1", responseBody.getMessageId());
        assertEquals(ResponseCode.DECLINED, responseBody.getResponseCode());
    }

    @Test
    void testAuthorizeTransaction_SufficientFunds() {
        AuthorizationRequest request = new AuthorizationRequest();
        request.setUserId("user1");
        request.setMessageId("message1");
        Amount amount = new Amount();
        amount.setAmount("100");
        amount.setCurrency("USD");
        amount.setDebitOrCredit("DEBIT");
        request.setTransactionAmount(amount);

        when(userBalances.getOrDefault(any(), any())).thenReturn(BigDecimal.valueOf(150));

        ResponseEntity<AuthorizationResponse> response = transactionController.authorizeTransaction("message1", request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        AuthorizationResponse responseBody = response.getBody();
        assertEquals("user1", responseBody.getUserId());
        assertEquals("message1", responseBody.getMessageId());
        assertEquals(ResponseCode.APPROVED, responseBody.getResponseCode());
    }

    @Test
    void testLoadTransaction() {
        LoadRequest request = new LoadRequest();
        request.setUserId("user1");
        request.setMessageId("message1");
        Amount amount = new Amount();
        amount.setAmount("100");
        amount.setCurrency("USD");
        amount.setDebitOrCredit("CREDIT");
        request.setTransactionAmount(amount);

        when(userBalances.getOrDefault(any(), any())).thenReturn(BigDecimal.ZERO);

        ResponseEntity<LoadResponse> response = transactionController.loadTransaction("message1", request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        LoadResponse responseBody = response.getBody();
        assertEquals("user1", responseBody.getUserId());
        assertEquals("message1", responseBody.getMessageId());
        assertEquals("100", responseBody.getBalance().getAmount());
        assertEquals("USD", responseBody.getBalance().getCurrency());
        assertEquals(DebitCredit.CREDIT.toString(), responseBody.getBalance().getDebitOrCredit());
    }
}
