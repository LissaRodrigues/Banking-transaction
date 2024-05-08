package com.transaction.transation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testPingEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/ping")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverTime").isNotEmpty());
    }

    @Test
    public void testAuthorizeTransaction() throws Exception {
        AuthorizationRequest request = new AuthorizationRequest();
        request.setUserId("user1");
        request.setMessageId("message1");
        Amount amount = new Amount();
        amount.setAmount("100");
        amount.setCurrency("USD");
        amount.setDebitOrCredit("DEBIT");
        request.setTransactionAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders.put("/authorization/message1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.messageId").value("message1"))
                .andExpect(jsonPath("$.responseCode").value("DECLINED"));
    }

    @Test
    public void testLoadTransaction() throws Exception {
        LoadRequest request = new LoadRequest();
        request.setUserId("user2");
        request.setMessageId("message2");
        Amount amount = new Amount();
        amount.setAmount("100");
        amount.setCurrency("USD");
        amount.setDebitOrCredit("CREDIT");
        request.setTransactionAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders.put("/load/message2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user2"))
                .andExpect(jsonPath("$.messageId").value("message2"))
                .andExpect(jsonPath("$.balance.amount").value("100"))
                .andExpect(jsonPath("$.balance.currency").value("USD"))
                .andExpect(jsonPath("$.balance.debitOrCredit").value("CREDIT"));
    }
}

