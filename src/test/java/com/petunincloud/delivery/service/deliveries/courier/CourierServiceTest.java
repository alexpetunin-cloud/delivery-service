package com.petunincloud.delivery.service.deliveries.courier;

import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierRequest;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierResponse;
import com.petunincloud.delivery.service.deliveries.courier.dto.CreateCourierRequest;
import com.petunincloud.delivery.service.security.SecurityUtils;
import com.petunincloud.delivery.service.users.RoleEntity;
import com.petunincloud.delivery.service.users.RoleRepository;
import com.petunincloud.delivery.service.users.UserEntity;
import com.petunincloud.delivery.service.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
public class CourierServiceTest {

    @Mock
    private CourierMapper courierMapper;

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CourierService courierService;

    @Test
    void createCourier_ShouldCreateCourier() {
        CreateCourierRequest request = new CreateCourierRequest(
                "alex@gmail.com",
                "alex123",
                "Алексей",
                "+77779992345",
                "ул. Преображенского, 15"
        );

        RoleEntity role = new RoleEntity(
                1L,
                "ROLE_COURIER"
        );

        UserEntity user = new UserEntity();

        user.setId(1L);
        user.setEmail(request.email());
        user.setPassword("alex123");
        user.setPhone(request.phone());
        user.setName(request.name());
        user.setAddress(request.address());
        user.setRoles(Set.of(role));

        CourierEntity courierEntity = new CourierEntity(
                1L,
                "Алексей",
                "+77779992345",
                CourierStatus.AVAILABLE,
                user
        );

        CourierResponse courierResponse = new CourierResponse(
                1L,
                "Алексей",
                "+77779992345",
                CourierStatus.AVAILABLE
        );

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());
        when(courierRepository.findByPhone("+77779992345"))
                .thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_COURIER"))
                .thenReturn(Optional.of(role));
        when(passwordEncoder.encode(request.password()))
                .thenReturn("alex123");
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(user);
        when(courierMapper.toEntity(eq(request), any(UserEntity.class)))
                .thenReturn(courierEntity);
        when(courierRepository.save(any(CourierEntity.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));
        when(courierMapper.toResponse(courierEntity))
                .thenReturn(courierResponse);

        CourierResponse result = courierService.createCourier(request);

        assertEquals(CourierStatus.AVAILABLE, result.status());

        verify(userRepository, times(1))
                .findByEmail(request.email());
        verify(courierRepository, times(1))
                .findByPhone("+77779992345");
        verify(roleRepository, times(1))
                .findByName("ROLE_COURIER");
        verify(userRepository, times(1))
                .save(any(UserEntity.class));
        verify(courierMapper, times(1))
                .toEntity(eq(request), any(UserEntity.class));
        verify(courierRepository, times(1))
                .save(any(CourierEntity.class));
        verify(courierMapper, times(1))
                .toResponse(courierEntity);
    }

    @Test
    void createCourier_ShouldThrowException_WithExistsEmail() {
        CreateCourierRequest request = new CreateCourierRequest(
                "alex@gmail.com",
                "alex123",
                "Алексей",
                "+77779992345",
                "ул. Преображенского, 15"
        );

        RoleEntity role = new RoleEntity(
                1L,
                "ROLE_COURIER"
        );

        UserEntity user = new UserEntity();

        user.setId(1L);
        user.setEmail("alex@gmail.com");
        user.setPassword("alex123");
        user.setPhone("+77779992345");
        user.setName("Алексей");
        user.setAddress("ул. Преображенского, 15");
        user.setRoles(Set.of(role));

        CourierEntity courierEntity = new CourierEntity(
                300L,
                "Константин",
                "+77779992345",
                CourierStatus.AVAILABLE,
                user
        );

        when(userRepository.findByEmail("alex@gmail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> courierService.createCourier(request));

        verify(courierRepository, never())
                .save(any(CourierEntity.class));
        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    @Test
    void createCourier_ShouldThrowException_WithExistsPhoneNumber() {
        CreateCourierRequest request = new CreateCourierRequest(
                "alex@gmail.com",
                "alex123",
                "Алексей",
                "+77779992345",
                "ул. Преображенского, 15"
        );

        RoleEntity role = new RoleEntity(
                1L,
                "ROLE_COURIER"
        );

        UserEntity user = new UserEntity();

        user.setId(1L);
        user.setEmail("alex@gmail.com");
        user.setPassword("alex123");
        user.setPhone("+77779992345");
        user.setName("Алексей");
        user.setAddress("ул. Преображенского, 15");
        user.setRoles(Set.of(role));

        CourierEntity courierEntity = new CourierEntity(
                300L,
                "Константин",
                "+77779992345",
                CourierStatus.AVAILABLE,
                user
        );

        when(courierRepository.findByPhone("+77779992345"))
                .thenReturn(Optional.of(courierEntity));

        assertThrows(IllegalArgumentException.class,
                () -> courierService.createCourier(request));

        verify(courierRepository, never())
                .save(any(CourierEntity.class));
        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    @Test
    void createCourier_ShouldThrowException_NotFoundRole() {
        CreateCourierRequest request = new CreateCourierRequest(
                "alex@gmail.com",
                "alex123",
                "Алексей",
                "+77779992345",
                "ул. Преображенского, 15"
        );

        UserEntity user = new UserEntity();

        user.setId(1L);
        user.setEmail("alex@gmail.com");
        user.setPassword("alex123");
        user.setPhone("+77779992345");
        user.setName("Алексей");
        user.setAddress("ул. Преображенского, 15");

        CourierEntity courierEntity = new CourierEntity(
                300L,
                "Константин",
                "+77779992345",
                CourierStatus.AVAILABLE,
                user
        );

        when(roleRepository.findByName("ROLE_COURIER"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> courierService.createCourier(request));

        verify(courierRepository, never())
                .save(any(CourierEntity.class));
        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    @Test
    void findAvailableCourier_ShouldReturnAvailableCourier() {
        RoleEntity role = new RoleEntity(
                1L,
                "ROLE_COURIER"
        );

        UserEntity user = new UserEntity();

        user.setId(1L);
        user.setEmail("alex@gmail.com");
        user.setPassword("alex123");
        user.setPhone("+77779992345");
        user.setName("Михаил");
        user.setAddress("ул. Преображенского, 15");
        user.setRoles(Set.of(role));

        CourierEntity courierEntity = new CourierEntity(
                1L,
                "Михаил",
                "+77779992345",
                CourierStatus.AVAILABLE,
                user
        );

        CourierResponse courierResponse = new CourierResponse(
                1L,
                "Михаил",
                "+77779992345",
                CourierStatus.AVAILABLE
        );

        when(courierRepository.findTopByStatus(CourierStatus.AVAILABLE))
                .thenReturn(Optional.of(courierEntity));
        when(courierMapper.toResponse(courierEntity))
                .thenReturn(courierResponse);

        CourierResponse result = courierService.findAvailableCourier();

        assertNotNull(result);
        assertEquals(CourierStatus.AVAILABLE, result.status());

        verify(courierRepository, times(1))
                .findTopByStatus(CourierStatus.AVAILABLE);
        verify(courierMapper, times(1))
                .toResponse(courierEntity);
    }

    @Test
    void findAvailableCourier_ShouldThrowException_WhenNoAvailableCouriers() {
        when(courierRepository.findTopByStatus(CourierStatus.AVAILABLE))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> courierService.findAvailableCourier());
    }
}
