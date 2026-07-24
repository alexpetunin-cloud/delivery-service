package com.petunincloud.delivery.service.orders.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemRequest;
import com.petunincloud.delivery.service.orders.order.dto.OrderRequest;
import com.petunincloud.delivery.service.orders.order.dto.OrderResponse;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemResponse;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrderService orderService;

    @Test
    void getOrderById_ShouldGetOrder() throws Exception{
        Long orderId = 1L;
        Long restaurantId = 10L;
        Long dishId = 100L;

        OrderResponse mockResponse = new OrderResponse(
                1L,
                1L,
                restaurantId,
                "Ресторан",
                LocalDateTime.now(),
                OrderStatus.PENDING,
                BigDecimal.valueOf(300),
                List.of(new OrderItemResponse(dishId, "Пицца", 2, BigDecimal.valueOf(150)))
        );

        when(orderService.getOrderResponseById(orderId))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId));
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        String email = "client@delivery.com";
        Long restaurantId = 10L;
        Long dishId = 100L;

        OrderItemRequest itemRequest = new OrderItemRequest(dishId, 2);
        OrderRequest request = new OrderRequest(email, restaurantId, List.of(itemRequest));

        OrderResponse mockResponse = new OrderResponse(
                1L,
                1L,
                restaurantId,
                "Ресторан",
                LocalDateTime.now(),
                OrderStatus.PENDING,
                BigDecimal.valueOf(300),
                List.of(new OrderItemResponse(dishId, "Пицца", 2, BigDecimal.valueOf(150)))
        );

        when(orderService.createOrder(any(OrderRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.totalPrice").value(300));
    }

    @Test
    void cancelOrder_ShouldReturnCanceledOrder() throws Exception {
        Long orderId = 1L;

        OrderResponse mockResponse = new OrderResponse(
                orderId,
                1L,
                10L,
                "Ресторан",
                LocalDateTime.now(),
                OrderStatus.CANCELED,
                BigDecimal.ZERO,
                List.of()
        );

        when(orderService.cancelOrder(orderId)).thenReturn(mockResponse);

        mockMvc.perform(patch("/api/orders/{orderId}/cancel", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"))
                .andExpect(jsonPath("$.id").value(orderId));
    }

    @Test
    void cancelOrder_ShouldReturn400_WhenOrderNotFound() throws Exception {
        Long orderId = 999L;

        when(orderService.cancelOrder(orderId))
                .thenThrow(new IllegalArgumentException("Order not found: " + orderId));

        mockMvc.perform(patch("/api/orders/{orderId}/cancel", orderId))
                .andExpect(status().isBadRequest());
    }
}