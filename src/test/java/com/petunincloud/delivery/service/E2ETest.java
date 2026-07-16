package com.petunincloud.delivery.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petunincloud.delivery.service.deliveries.courier.CourierEntity;
import com.petunincloud.delivery.service.deliveries.courier.CourierRepository;
import com.petunincloud.delivery.service.deliveries.courier.CourierStatus;
import com.petunincloud.delivery.service.orders.order.dto.OrderRequest;
import com.petunincloud.delivery.service.orders.order.OrderRepository;
import com.petunincloud.delivery.service.orders.orderItem.dto.OrderItemRequest;
import com.petunincloud.delivery.service.payments.dto.PaymentRequest;
import com.petunincloud.delivery.service.restaurants.dish.DishEntity;
import com.petunincloud.delivery.service.restaurants.dish.DishRepository;
import com.petunincloud.delivery.service.restaurants.restaurant.RestaurantEntity;
import com.petunincloud.delivery.service.restaurants.restaurant.RestaurantRepository;
import com.petunincloud.delivery.service.users.UserEntity;
import com.petunincloud.delivery.service.users.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class E2ETest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CourierRepository courierRepository;

    private Long userId;
    private Long restaurantId;
    private Long dishId;

    @BeforeEach
    void setUp() {
        UserEntity user = new UserEntity();
        user.setEmail("user@gmail.com");
        user.setPhone("+79231001040");
        user.setName("Александр");
        user.setAddress("ул. Стахановская 1");
        userId = userRepository.save(user).getId();

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName("Бургер Кинг");
        restaurant.setAddress("ул. Советская 207/2");
        restaurantId = restaurantRepository.save(restaurant).getId();

        DishEntity dish = new DishEntity();
        dish.setName("Воппер");
        dish.setPrice(BigDecimal.valueOf(300));
        dish.setRestaurant(restaurant);
        dishId = dishRepository.save(dish).getId();

        CourierEntity courier = new CourierEntity();
        courier.setName("Иван");
        courier.setPhone("+79923050201");
        courier.setStatus(CourierStatus.AVAILABLE);
        courierRepository.save(courier);
    }

    @Test
    void fullDeliveryFlow_ShouldCompleteSuccessfully() throws Exception {
        OrderItemRequest item = new OrderItemRequest(dishId, 2);
        OrderRequest orderRequest = new OrderRequest(userId, restaurantId, java.util.List.of(item));

        String orderResponse = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(orderResponse).get("id").asLong();

        PaymentRequest paymentRequest = new PaymentRequest(orderId, userId);

        String paymentResponse = mockMvc.perform(post("/api/payments/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long paymentId = objectMapper.readTree(paymentResponse).get("id").asLong();

        mockMvc.perform(post("/api/payments/{paymentId}/process", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        mockMvc.perform(patch("/api/restaurants/orders/{orderId}/cook", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COOKING"));

        mockMvc.perform(patch("/api/restaurants/orders/{orderId}/ready", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"));

        String deliveryResponse = mockMvc.perform(post("/api/deliveries/assign/{orderId}", orderId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ASSIGNED"))
                .andReturn().getResponse().getContentAsString();

        Long deliveryId = objectMapper.readTree(deliveryResponse).get("id").asLong();

        mockMvc.perform(patch("/api/deliveries/{deliveryId}/complete", deliveryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }
}