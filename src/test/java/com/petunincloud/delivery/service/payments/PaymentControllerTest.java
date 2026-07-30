package com.petunincloud.delivery.service.payments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.payments.dto.PaymentRequest;
import com.petunincloud.delivery.service.payments.dto.PaymentResponse;
import com.petunincloud.delivery.service.security.SecurityUtils;
import com.petunincloud.delivery.service.users.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class PaymentControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SecurityUtils securityUtils;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void initiatePayment_ShouldInitiatePayment() throws Exception{
        PaymentRequest request = new PaymentRequest(
                1L,
                "user@gmail.com"
        );

        PaymentResponse response = new PaymentResponse(
                1L,
                1L,
                1L,
                BigDecimal.valueOf(300),
                "CARD",
                PaymentStatus.PENDING,
                null,
                null,
                LocalDateTime.now().withNano(0)
        );

        UserEntity user = new UserEntity();

        when(securityUtils.getCurrentUser())
                .thenReturn(user);
        when(paymentService.initiatePayment(request, user))
                .thenReturn(response);

        mockMvc.perform(post("/api/payments/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void processPayment_ShouldProcessPayment() throws Exception {
        Long paymentId = 1L;

        PaymentResponse response = new PaymentResponse(
                1L,
                1L,
                1L,
                BigDecimal.valueOf(300),
                "CARD",
                PaymentStatus.SUCCESS,
                UUID.randomUUID().toString(),
                LocalDateTime.now().withNano(0),
                LocalDateTime.now().withNano(0)
        );

        when(paymentService.processPayment(paymentId))
                .thenReturn(response);

        mockMvc.perform(post("/api/payments/{paymentId}/process", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.completedAt").isNotEmpty())
                .andExpect(jsonPath("$.transactionId").isNotEmpty());
    }
}
