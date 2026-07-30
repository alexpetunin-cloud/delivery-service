package com.petunincloud.delivery.service.deliveries.delivery;

import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.deliveries.delivery.dto.DeliveryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryService deliveryService;

    @Test
    void assignCourier_ShouldAssignCourier() throws Exception {
        Long orderId = 1L;

        DeliveryResponse deliveryResponse = new DeliveryResponse(
                1L,
                orderId,
                1L,
                "Александр",
                DeliveryStatus.ASSIGNED,
                "ул. Булгакова, 5",
                "ул. Пушкина, 10",
                LocalDateTime.now().withNano(0),
                null
        );

        when(deliveryService.assignCourierToOrder(orderId))
                .thenReturn(deliveryResponse);

        mockMvc.perform(post("/api/deliveries/assign/{orderId}", orderId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ASSIGNED"))
                .andExpect(jsonPath("$.assignedAt").isNotEmpty())
                .andExpect(jsonPath("$.deliveredAt").isEmpty());
    }

    @Test
    void completeDelivery_ShouldCompleteDelivery() throws Exception {
        Long deliveryId = 1L;
        Long orderId = 1L;

        DeliveryResponse deliveryResponse = new DeliveryResponse(
                deliveryId,
                orderId,
                1L,
                "Александр",
                DeliveryStatus.DELIVERED,
                "ул. Булгакова, 5",
                "ул. Пушкина, 10",
                LocalDateTime.now().withNano(0),
                LocalDateTime.now().withNano(0)
        );

        when(deliveryService.completeDelivery(deliveryId))
                .thenReturn(deliveryResponse);

        mockMvc.perform(patch("/api/deliveries/{deliveryId}/complete", deliveryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"))
                .andExpect(jsonPath("$.deliveredAt").isNotEmpty())
                .andExpect(jsonPath("$.id").value(deliveryId));
    }

    @Test
    void getDeliveryById_ShouldReturnDelivery() throws Exception {
        Long deliveryId = 1L;
        Long orderId = 1L;

        DeliveryResponse deliveryResponse = new DeliveryResponse(
                deliveryId,
                orderId,
                1L,
                "Александр",
                DeliveryStatus.DELIVERED,
                "ул. Булгакова, 5",
                "ул. Пушкина, 10",
                LocalDateTime.now().withNano(0),
                LocalDateTime.now().withNano(0)
        );

        when(deliveryService.getDeliveryById(deliveryId))
                .thenReturn(deliveryResponse);

        mockMvc.perform(get("/api/deliveries/{deliveryId}", deliveryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deliveryId));
    }
}
