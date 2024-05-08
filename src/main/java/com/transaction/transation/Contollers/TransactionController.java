package com.transaction.transation.Contollers;


import com.transaction.transation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TransactionController {

    // In-memory storage for user balances
    private Map<String, BigDecimal> userBalances = new HashMap<>();

    @GetMapping("/ping")
    public ResponseEntity<Ping> ping() {
        Ping ping = new Ping();
        ping.setServerTime(LocalDateTime.now().toString());
        return ResponseEntity.ok(ping);
    }

    @PutMapping("/authorization/{messageId}")
    public ResponseEntity<AuthorizationResponse> authorizeTransaction(@PathVariable String messageId,
                                                                      @RequestBody AuthorizationRequest request) {
        // Retrieve user balance
        BigDecimal userBalance = userBalances.getOrDefault(request.getUserId(), BigDecimal.ZERO);

        // Check if user has sufficient funds
        BigDecimal transactionAmount = new BigDecimal(request.getTransactionAmount().getAmount());
        DebitCredit debitOrCredit = DebitCredit.valueOf(request.getTransactionAmount().getDebitOrCredit());

        if (debitOrCredit == DebitCredit.DEBIT && userBalance.compareTo(transactionAmount) >= 0) {

            // Sufficient funds, deduct from balance
            userBalance = userBalance.subtract(transactionAmount);
            userBalances.put(request.getUserId(), userBalance);

            AuthorizationResponse response = new AuthorizationResponse();
            response.setUserId(request.getUserId());
            response.setMessageId(messageId);
            response.setResponseCode(ResponseCode.APPROVED);
            response.setBalance(createAmount(userBalance, request.getTransactionAmount().getCurrency(), DebitCredit.CREDIT));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            // Insufficient funds
            AuthorizationResponse response = new AuthorizationResponse();
            response.setUserId(request.getUserId());
            response.setMessageId(messageId);
            response.setResponseCode(ResponseCode.DECLINED);
            response.setBalance(createAmount(userBalance, request.getTransactionAmount().getCurrency(), DebitCredit.CREDIT));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    @PutMapping("/load/{messageId}")
    public ResponseEntity<LoadResponse> loadTransaction(@PathVariable String messageId,
                                                        @RequestBody LoadRequest request) {
        // Retrieve user balance
        BigDecimal userBalance = userBalances.getOrDefault(request.getUserId(), BigDecimal.ZERO);

        // Add transaction amount to balance
        BigDecimal transactionAmount = new BigDecimal(request.getTransactionAmount().getAmount());
        userBalance = userBalance.add(transactionAmount);
        userBalances.put(request.getUserId(), userBalance);

        LoadResponse response = new LoadResponse();
        response.setUserId(request.getUserId());
        response.setMessageId(messageId);
        response.setBalance(createAmount(userBalance, request.getTransactionAmount().getCurrency(), DebitCredit.CREDIT));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private Amount createAmount(BigDecimal amount, String currency, DebitCredit debitOrCredit) {
        Amount amountObj = new Amount();
        amountObj.setAmount(amount.toString());
        amountObj.setCurrency(currency);
        amountObj.setDebitOrCredit(String.valueOf(debitOrCredit));
        return amountObj;
    }

}
