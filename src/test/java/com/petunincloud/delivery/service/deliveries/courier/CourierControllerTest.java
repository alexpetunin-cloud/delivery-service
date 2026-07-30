package com.petunincloud.delivery.service.deliveries.courier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierRequest;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class CourierControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourierService courierService;

    @Test
    void createCourier_ShouldCreateCourier() throws Exception {
        CourierRequest request = new CourierRequest(
                "Михаил",
                "+70001234567"
        );

        CourierResponse courierResponse = new CourierResponse(
                1L,
                "Михаил",
                "+70001234567",
                CourierStatus.AVAILABLE
        );

        when(courierService.createCourier(request))
                .thenReturn(courierResponse);

        mockMvc.perform(post("/api/couriers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void findAvailableCourier_ShouldReturnAvailableCourier() throws Exception {
        CourierResponse courierResponse = new CourierResponse(
                1L,
                "Михаил",
                "+70001234567",
                CourierStatus.AVAILABLE
        );

        when(courierService.findAvailableCourier())
                .thenReturn(courierResponse);

        mockMvc.perform(get("/api/couriers/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }
}
