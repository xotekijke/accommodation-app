package com.example.accommodation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.accommodation.dto.payment.PaymentDto;
import com.example.accommodation.model.User;
import com.example.accommodation.model.enums.PaymentStatus;
import com.example.accommodation.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PaymentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    private String customerToken;

    @BeforeEach
    void setUp() throws Exception {
        customerToken = registerAndLogin("payment.customer@example.com");

        PaymentDto stubbedDto = new PaymentDto(
                1L, PaymentStatus.PENDING, 1L,
                "https://checkout.stripe.com/pay/cs_test_dummy", "cs_test_dummy",
                BigDecimal.valueOf(500));
        when(paymentService.createPaymentSession(any(User.class), any(), anyString(), anyString()))
                .thenReturn(stubbedDto);
    }

    @Test
    void createSession_validRequest_returns201() throws Exception {
        mockMvc.perform(post("/payments")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bookingId":1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").value("cs_test_dummy"));
    }

    @Test
    void createSession_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bookingId":1}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createSession_invalidBody_returns400() throws Exception {
        mockMvc.perform(post("/payments")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    private String registerAndLogin(String email) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {"email":"%s","password":"password123",
                                 "firstName":"Test","lastName":"User"}
                                """, email)))
                .andExpect(status().isCreated());
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {"email":"%s","password":"password123"}
                                """, email)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }
}
