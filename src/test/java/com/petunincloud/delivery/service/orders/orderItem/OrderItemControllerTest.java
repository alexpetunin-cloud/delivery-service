package com.petunincloud.delivery.service.orders.orderItem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemRequest;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrderItemService orderItemService;

    @Test
    void getOrderItems_ShouldReturnListItems() throws Exception {
        Long orderId = 1L;

        OrderItemResponse orderItemResponse1 = new OrderItemResponse(
                1L,
                "Маргарита",
                2,
                BigDecimal.valueOf(600)
        );

        OrderItemResponse orderItemResponse2 = new OrderItemResponse(
                2L,
                "Гавайская",
                1,
                BigDecimal.valueOf(450)
        );

        List<OrderItemResponse> itemResponseList = new ArrayList<>(
                List.of(orderItemResponse1, orderItemResponse2));

        when(orderItemService.getOrderItems(orderId))
                .thenReturn(itemResponseList);

        mockMvc.perform(get("/api/orders/{orderId}/items", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].dishName").value("Маргарита"))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[1].dishName").value("Гавайская"))
                .andExpect(jsonPath("$[1].quantity").value(1));
    }

    @Test
    void addItemToOrder_ShouldAddItemToOrder() throws Exception {
        Long orderId = 1L;
        OrderItemRequest request = new OrderItemRequest(
                1L,
                1
        );

        OrderItemResponse orderItemResponse = new OrderItemResponse(
                1L,
                "Гавайская",
                1,
                BigDecimal.valueOf(450)
        );

        when(orderItemService.addItemToOrder(orderId, request))
                .thenReturn(orderItemResponse);

        mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dishId").value(1L))
                .andExpect(jsonPath("$.price").value(450));
    }

    @Test
    void removeItemFromOrder_ShouldRemoveItem() throws Exception {
        Long orderId = 1L;
        Long itemId = 1L;

        mockMvc.perform(delete("/api/orders/{orderId}/items/{itemId}", orderId, itemId))
                .andExpect(status().isNoContent());

        verify(orderItemService, times(1)).removeItemFromOrder(orderId, itemId);
    }
}
