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
import com.petunincloud.delivery.service.security.SecurityUtils;
import com.petunincloud.delivery.service.users.RoleEntity;
import com.petunincloud.delivery.service.users.RoleRepository;
import com.petunincloud.delivery.service.users.UserEntity;
import com.petunincloud.delivery.service.users.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class E2ETest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long restaurantId;
    private Long dishId;
    private UserEntity dodo;
    private UserEntity ivan;
    private UserEntity user;

    @MockitoBean
    private SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        RoleEntity clientRole = roleRepository.findByName("ROLE_CLIENT")
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName("ROLE_CLIENT");
                    return roleRepository.save(role);
                });
        RoleEntity courierRole = roleRepository.findByName("ROLE_COURIER")
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName("ROLE_COURIER");
                    return roleRepository.save(role);
                });
        RoleEntity restaurantRole = roleRepository.findByName("ROLE_RESTAURANT")
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName("ROLE_RESTAURANT");
                    return roleRepository.save(role);
                });

        user = new UserEntity();
        dodo = new UserEntity();
        ivan = new UserEntity();
        RestaurantEntity restaurant = new RestaurantEntity();
        DishEntity dish = new DishEntity();
        CourierEntity courier = new CourierEntity();

        user.setEmail("user@gmail.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setPhone("+79231001040");
        user.setName("Александр");
        user.setAddress("ул. Стахановская 1");
        user.setRoles(Set.of(clientRole));

        dodo.setEmail("dodo@gmail.com");
        dodo.setPassword(passwordEncoder.encode("dodo123"));
        dodo.setPhone("+79001231040");
        dodo.setName("Додо");
        dodo.setAddress("ул. Стахановская 10");
        dodo.setRoles(Set.of(restaurantRole));

        ivan.setEmail("ivan@gmail.com");
        ivan.setPassword(passwordEncoder.encode("ivan123"));
        ivan.setPhone("+79923050201");
        ivan.setName("Иван");
        ivan.setAddress("ул. Стахановская 13");
        ivan.setRoles(Set.of(courierRole));

        restaurant.setName("Додо");
        restaurant.setAddress("ул. Советская 207/2");
        restaurant.setUser(dodo);

        dish.setName("Воппер");
        dish.setPrice(BigDecimal.valueOf(300));
        dish.setRestaurant(restaurant);

        courier.setName("Иван");
        courier.setPhone("+79923050201");
        courier.setStatus(CourierStatus.AVAILABLE);
        courier.setUser(ivan);

        when(securityUtils.getCurrentUser()).thenReturn(user);

        dodo = userRepository.save(dodo);
        ivan = userRepository.save(ivan);
        user = userRepository.save(user);
        restaurantId = restaurantRepository.save(restaurant).getId();
        dishId = dishRepository.save(dish).getId();
        courierRepository.save(courier);
    }

    @Test
    void fullDeliveryFlow_ShouldCompleteSuccessfully() throws Exception {
        OrderItemRequest item = new OrderItemRequest(dishId, 2);
        OrderRequest orderRequest = new OrderRequest(user.getEmail(), restaurantId, java.util.List.of(item));

        String orderResponse = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(orderResponse).get("id").asLong();

        PaymentRequest paymentRequest = new PaymentRequest(orderId);

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

        when(securityUtils.getCurrentUser()).thenReturn(dodo);

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

        when(securityUtils.getCurrentUser()).thenReturn(ivan);

        mockMvc.perform(patch("/api/deliveries/{deliveryId}/complete", deliveryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));

        when(securityUtils.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }
}