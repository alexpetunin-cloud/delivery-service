package com.petunincloud.delivery.service.restaurants.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.orders.order.OrderStatus;
import com.petunincloud.delivery.service.orders.order.dto.OrderResponse;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemResponse;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishRequest;
import com.petunincloud.delivery.service.restaurants.dish.dto.DishResponse;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantRequest;
import com.petunincloud.delivery.service.restaurants.restaurant.dto.RestaurantResponse;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RestaurantService restaurantService;

    @Test
    void createRestaurant_ShouldCreateRestaurant() throws Exception {
        RestaurantRequest request = new RestaurantRequest(
            "Шаурма Кинг",
            "ул. Полевая, 34"
        );

        DishResponse dishResponse = new DishResponse(
                1L,
                "Гавайская",
                BigDecimal.valueOf(350)
        );

        List<DishResponse> menu = new ArrayList<>(List.of(dishResponse));

        RestaurantResponse response = new RestaurantResponse(
                1L,
                "Шаурма Кинг",
                "ул. Полевая, 34",
                menu
        );

        when(restaurantService.createRestaurant(request))
                .thenReturn(response);

        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.menu[0].price").value(BigDecimal.valueOf(350)))
                .andExpect(jsonPath("$.name").value("Шаурма Кинг"));
    }

    @Test
    void addDishToRestaurant_ShouldAddDish() throws Exception {
        Long restaurantId = 1L;

        DishRequest request = new DishRequest(
                "Маргарита",
                BigDecimal.valueOf(360)
        );

        DishResponse response = new DishResponse(
                1L,
                "Маргарита",
                BigDecimal.valueOf(360)
        );

        when(restaurantService.addDishToRestaurant(restaurantId, request))
                .thenReturn(response);

        mockMvc.perform(post("/api/restaurants/{restaurantId}/dishes", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Маргарита"))
                .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(360)));
    }

    @Test
    void startCooking_ShouldStartCooking() throws Exception {
        Long orderId = 1L;

        OrderItemResponse item = new OrderItemResponse(
                1L,
                "Маргарита",
                1,
                BigDecimal.valueOf(350)
        );

        List<OrderItemResponse> items = new ArrayList<>(List.of(item));

        OrderResponse response = new OrderResponse(
                orderId,
                1L,
                1L,
                "Шаурма Кинг",
                LocalDateTime.now().withNano(0),
                OrderStatus.COOKING,
                BigDecimal.valueOf(350),
                items
        );

        when(restaurantService.startCooking(orderId))
                .thenReturn(response);

        mockMvc.perform(patch("/api/restaurants/orders/{orderId}/cook", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COOKING"))
                .andExpect(jsonPath("$.totalPrice").value(BigDecimal.valueOf(350)));
    }

    @Test
    void markAsReady_ShouldMarkAsReady() throws Exception {
        Long orderId = 1L;

        OrderItemResponse item = new OrderItemResponse(
                1L,
                "Маргарита",
                1,
                BigDecimal.valueOf(350)
        );

        List<OrderItemResponse> items = new ArrayList<>(List.of(item));

        OrderResponse response = new OrderResponse(
                orderId,
                1L,
                1L,
                "Шаурма Кинг",
                LocalDateTime.now().withNano(0),
                OrderStatus.READY,
                BigDecimal.valueOf(350),
                items
        );

        when(restaurantService.markAsReady(orderId))
                .thenReturn(response);

        mockMvc.perform(patch("/api/restaurants/orders/{orderId}/ready", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"))
                .andExpect(jsonPath("$.totalPrice").value(BigDecimal.valueOf(350)));
    }
}