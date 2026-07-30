package com.petunincloud.delivery.service.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.users.dto.UserPatchRequest;
import com.petunincloud.delivery.service.users.dto.UserRequest;
import com.petunincloud.delivery.service.users.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @Test
    void createUser_ShouldCreateUser() throws Exception {
        UserRequest request = new UserRequest(
                "user@gmail.com",
                "+79001234567",
                "Денис",
                "ул. Шишкина, 30",
                null,
                null
        );

        UserResponse response = new UserResponse(
                1L,
                "user@gmail.com",
                "+79001234567",
                "Денис",
                "ул. Шишкина, 30",
                null,
                null
        );

        when(userService.createUser(request))
                .thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value(request.email()));
    }

    @Test
    void updateUser_ShouldUpdateUser() throws Exception {
        String email = "user@gmail.com";
        UserPatchRequest request = new UserPatchRequest(
                "user@gmail.com",
                "+79001234567",
                "Денис",
                "ул. Шишкина, 30",
                "5 этаж",
                "нет домофона"
        );

        UserResponse response = new UserResponse(
                1L,
                "user2@gmail.com",
                "+79001234567",
                "Денис",
                "ул. Шишкина, 30",
                "5 этаж",
                "нет домофона"
        );

        when(userService.updateUser(email, request))
                .thenReturn(response);

        mockMvc.perform(patch("/api/users/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user2@gmail.com"))
                .andExpect(jsonPath("$.name").value("Денис"))
                .andExpect(jsonPath("$.apartment").isNotEmpty())
                .andExpect(jsonPath("$.deliveryInstructions").isNotEmpty());
    }

    @Test
    void deleteUser_ShouldDeleteUser() throws Exception {
        String email = "user@gmail.com";

        mockMvc.perform(delete("/api/users/{email}", email))
                .andExpect(status().isNoContent());

        verify(userService, times(1))
                .deleteUser(email);
    }

    @Test
    void findByEmail_ShouldReturnUser() throws Exception {
        String email = "user@gmail.com";

        UserResponse response = new UserResponse(
                1L,
                "user@gmail.com",
                "+79001234567",
                "Денис",
                "ул. Шишкина, 30",
                "5 этаж",
                "нет домофона"
        );

        when(userService.findByEmail(email))
                .thenReturn(response);

        mockMvc.perform(get("/api/users/email/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value("Денис"));
    }

    @Test
    void findByPhone_ShouldReturnUser() throws Exception {
        String phone = "+79001234567";

        UserResponse response = new UserResponse(
                1L,
                "user@gmail.com",
                "+79001234567",
                "Денис",
                "ул. Шишкина, 30",
                "5 этаж",
                "нет домофона"
        );

        when(userService.findByPhone(phone))
                .thenReturn(response);

        mockMvc.perform(get("/api/users/phone/{phone}", phone))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value(phone))
                .andExpect(jsonPath("$.name").value("Денис"));
    }
}
