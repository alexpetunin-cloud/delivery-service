package com.petunincloud.delivery.service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.security.dto.AuthRequest;
import com.petunincloud.delivery.service.security.dto.AuthResponse;
import com.petunincloud.delivery.service.users.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private RoleRepository roleRepository;

    @MockitoBean
    private UserMapper userMapper;

    @Test
    void login_ShouldLogin() throws Exception {
        AuthRequest request = new AuthRequest(
                "user@gmail.com",
                "123456789",
                "Юзер",
                "+79001002345",
                "ул. Пушкина, 25"
        );

        UserDetails userDetails = userDetailsService
                .loadUserByUsername("user@gmail.com");

        AuthResponse response = new AuthResponse(
                jwtUtils.generateToken(userDetails)
        );

        when(userDetailsService.loadUserByUsername(request.email()))
                .thenReturn(userDetails);
        when(jwtUtils.generateToken(userDetails))
                .thenReturn(response.token());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(response.token()));

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void register_ShouldRegister() throws Exception {
        AuthRequest request = new AuthRequest(
                "user@gmail.com",
                "123456789",
                "Юзер",
                "+79001002345",
                "ул. Пушкина, 25"
        );

        RoleEntity clientRole = new RoleEntity();
        UserEntity user = new UserEntity();

        clientRole.setId(1L);
        clientRole.setName("ROLE_CLIENT");

        user.setEmail(request.email());
        user.setName(request.name());
        user.setPhone(request.phone());
        user.setAddress(request.address());

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_CLIENT"))
                .thenReturn(Optional.of(clientRole));
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(user);
        when(userMapper.toEntity(request))
                .thenReturn(user);
        when(passwordEncoder.encode(request.password()))
                .thenReturn("abcdef");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertEquals(request.email(), user.getEmail());
        assertEquals(request.name(), user.getName());
        assertEquals(request.phone(), user.getPhone());
        assertEquals(request.address(), user.getAddress());
        assertEquals("abcdef", user.getPassword());
        assertNotNull(user.getRoles());

        verify(userRepository, times(1))
                .findByEmail(request.email());
        verify(roleRepository, times(1))
                .findByName("ROLE_CLIENT");
        verify(userMapper, times(1))
                .toEntity(request);
        verify(passwordEncoder, times(1))
                .encode(request.password());
        verify(userRepository, times(1))
                .save(any(UserEntity.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() throws Exception {
        AuthRequest request = new AuthRequest(
                "user@gmail.com",
                "123456789",
                "Юзер",
                "+79001002345",
                "ул. Пушкина, 25"
        );

        UserEntity user = new UserEntity();
        user.setEmail(request.email());

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    @Test
    void register_ShouldThrowException_WhenNotFoundRole() throws Exception {
        AuthRequest request = new AuthRequest(
                "user@gmail.com",
                "123456789",
                "Юзер",
                "+79001002345",
                "ул. Пушкина, 25"
        );

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_CLIENT"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }
}
