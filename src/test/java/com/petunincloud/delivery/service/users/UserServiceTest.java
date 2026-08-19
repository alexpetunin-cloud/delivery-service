package com.petunincloud.delivery.service.users;

import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.security.SecurityUtils;
import com.petunincloud.delivery.service.users.dto.UserPatchRequest;
import com.petunincloud.delivery.service.users.dto.UserRequest;
import com.petunincloud.delivery.service.users.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ShouldCreateUser() {
        UserRequest request = new UserRequest(
                "user@gmail.com",
                "+79001234567",
                "Михаил",
                "ул. Ростовская, 25",
                null,
                null
        );

        UserEntity user = new UserEntity();

        UserResponse userResponse = new UserResponse(
                1L,
                "user@gmail.com",
                "+79001234567",
                "Михаил",
                "ул. Ростовская, 25",
                null,
                null
        );

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());
        when(userRepository.findByPhone(request.phone()))
                .thenReturn(Optional.empty());
        when(userMapper.toEntity(request))
                .thenReturn(user);
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(user);
        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result = userService.createUser(request);

        assertNotNull(result);

        verify(userRepository, times(1))
                .findByEmail(request.email());
        verify(userRepository, times(1))
                .findByPhone(request.phone());
        verify(userMapper, times(1))
                .toEntity(request);
        verify(userRepository, times(1))
                .save(any(UserEntity.class));
        verify(userMapper, times(1))
                .toResponse(user);
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        UserRequest request = new UserRequest(
                "user@gmail.com",
                "+79001234567",
                "Михаил",
                "ул. Ростовская, 25",
                null,
                null
        );

        UserEntity user = new UserEntity();

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(request));

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenPhoneAlreadyExists() {
        UserRequest request = new UserRequest(
                "user@gmail.com",
                "+79001234567",
                "Михаил",
                "ул. Ростовская, 25",
                null,
                null
        );

        UserEntity user = new UserEntity();

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        when(userRepository.findByPhone(request.phone()))
                .thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(request));

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        String phone = "+79001234567";
        String email = "user@gmail.com";

        UserEntity user = new UserEntity();
        user.setEmail(email);

        UserResponse userResponse = new UserResponse(
                1L,
                email,
                phone,
                "Михаил",
                "ул. Ростовская, 25",
                null,
                null
        );

        when(securityUtils.getCurrentUser())
                .thenReturn(user);
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result = userService.findByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.email());

        verify(securityUtils, times(1))
                .getCurrentUser();
        verify(userRepository, times(1))
                .findByEmail(email);
        verify(userMapper, times(1))
                .toResponse(user);
    }

    @Test
    void findByEmail_ShouldThrowException_WhenNotAccess() {
        String email = "user@gmail.com";

        when(securityUtils.getCurrentUser())
                .thenThrow(new IllegalStateException("User is not authenticated"));

        assertThrows(IllegalStateException.class,
                () -> userService.findByEmail(email));
    }

    @Test
    void findByEmail_ShouldThrowException_WhenNotOwnProfile() {
        String email = "user@gmail.com";

        UserEntity user = new UserEntity();
        user.setEmail("user2@gmail.com");

        when(securityUtils.getCurrentUser())
                .thenReturn(user);

        assertThrows(IllegalArgumentException.class,
                () -> userService.findByEmail(email));
    }

    @Test
    void findByEmail_ShouldThrowException_WhenUserNotFound() {
        String email = "user@gmail.com";

        UserEntity user = new UserEntity();
        user.setEmail(email);

        when(securityUtils.getCurrentUser())
                .thenReturn(user);
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.findByEmail(email));
    }

    @Test
    void findByPhone_ShouldReturnUser() {
        String phone = "+79001234567";
        String email = "user@gmail.com";

        UserEntity user = new UserEntity();
        user.setPhone(phone);

        UserResponse userResponse = new UserResponse(
                1L,
                email,
                phone,
                "Михаил",
                "ул. Ростовская, 25",
                null,
                null
        );

        when(securityUtils.getCurrentUser())
                .thenReturn(user);
        when(userRepository.findByPhone(phone))
                .thenReturn(Optional.of(user));
        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result = userService.findByPhone(phone);

        assertNotNull(result);
        assertEquals(phone, result.phone());

        verify(securityUtils, times(1))
                .getCurrentUser();
        verify(userRepository, times(1))
                .findByPhone(phone);
        verify(userMapper, times(1))
                .toResponse(user);
    }

    @Test
    void findByPhone_ShouldThrowException_WhenNotAccess() {
        String phone = "+79001234567";

        when(securityUtils.getCurrentUser())
                .thenThrow(new IllegalStateException("User is not authenticated"));

        assertThrows(IllegalStateException.class,
                () -> userService.findByPhone(phone));
    }

    @Test
    void findByPhone_ShouldThrowException_WhenNotOwnProfile() {
        String phone = "+79001234567";

        UserEntity user = new UserEntity();
        user.setPhone("+79011234567");

        when(securityUtils.getCurrentUser())
                .thenReturn(user);

        assertThrows(IllegalArgumentException.class,
                () -> userService.findByPhone(phone));
    }

    @Test
    void findByPhone_ShouldThrowException_WhenUserNotFound() {
        String phone = "+79001234567";

        UserEntity user = new UserEntity();
        user.setPhone(phone);

        when(securityUtils.getCurrentUser())
                .thenReturn(user);
        when(userRepository.findByPhone(phone))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.findByPhone(phone));
    }

    @Test
    void updateUser_ShouldUpdateUserAllFields() {
        String email1 = "user@gmail.com";
        String email2 = "user2@gmail.com";
        String phone = "+79901234567";
        String name = "Валентин";
        String address = "ул. Арбат, 25";
        String apartment = "5 этаж";
        String deliveryInstructions = "нет домофона";

        UserPatchRequest request = new UserPatchRequest(
                email2,
                phone,
                name,
                address,
                apartment,
                deliveryInstructions
        );

        UserEntity user = new UserEntity();
        user.setEmail(email1);

        UserResponse userResponse = new UserResponse(
                1L,
                email2,
                phone,
                name,
                address,
                apartment,
                deliveryInstructions
        );

        when(securityUtils.getCurrentUser())
                .thenReturn(user);
        when(userRepository.findByEmail(email1))
                .thenReturn(Optional.of(user));
        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());
        when(userRepository.findByPhone(request.phone()))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(user);
        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result = userService.updateUser(email1, request);

        assertNotNull(result);
        assertEquals(email2, result.email());
        assertEquals(phone, result.phone());
        assertEquals(name, result.name());
        assertEquals(address, result.address());
        assertEquals(apartment, result.apartment());
        assertEquals(deliveryInstructions, result.deliveryInstructions());

        verify(securityUtils, times(1))
                .getCurrentUser();
        verify(userRepository, times(1))
                .findByEmail(email1);
        verify(userRepository, times(1))
                .findByEmail(request.email());
        verify(userRepository, times(1))
                .findByPhone(phone);
        verify(userRepository, times(1))
                .save(any(UserEntity.class));
        verify(userMapper, times(1))
                .toResponse(user);
    }

    @Test
    void updateUser_ShouldThrowException_WhenNotAccess() {
        String email = "user@gmail.com";

        UserPatchRequest request = new UserPatchRequest(
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(securityUtils.getCurrentUser())
                .thenThrow(new IllegalStateException("User is not authenticated"));

        assertThrows(IllegalStateException.class,
                () -> userService.updateUser(email, request));

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenNotOwnProfile() {
        String email = "user@gmail.com";

        UserPatchRequest request = new UserPatchRequest(
                null,
                null,
                null,
                null,
                null,
                null
        );

        UserEntity user = new UserEntity();
        user.setEmail("user2@gmail.com");

        when(securityUtils.getCurrentUser())
                .thenReturn(user);

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(email, request));

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        String email = "user@gmail.com";

        UserPatchRequest request = new UserPatchRequest(
                null,
                null,
                null,
                null,
                null,
                null
        );

        UserEntity user = new UserEntity();
        user.setEmail(email);

        when(securityUtils.getCurrentUser())
                .thenReturn(user);
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(email, request));

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenEmailExists() {
        String email = "user@gmail.com";

        UserPatchRequest request = new UserPatchRequest(
                "user2@gmail.com",
                null,
                null,
                null,
                null,
                null
        );

        UserEntity user = new UserEntity();
        UserEntity user2 = new UserEntity();

        user.setEmail(email);
        user.setId(1L);

        user2.setEmail(request.email());
        user2.setId(2L);

        when(securityUtils.getCurrentUser())
                .thenReturn(user);
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user2));

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(email, request));

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenPhoneExists() {
        String email = "user@gmail.com";

        UserPatchRequest request = new UserPatchRequest(
                null,
                "+79921002921",
                null,
                null,
                null,
                null
        );

        UserEntity user = new UserEntity();
        UserEntity user2 = new UserEntity();

        user.setEmail(email);
        user.setId(1L);

        user2.setPhone(request.phone());
        user2.setId(2L);

        when(securityUtils.getCurrentUser())
                .thenReturn(user);
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(userRepository.findByPhone(request.phone()))
                .thenReturn(Optional.of(user2));

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(email, request));

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        String email = "user@gmail.com";

        UserEntity user = new UserEntity();
        user.setEmail(email);

        when(securityUtils.getCurrentUser())
                .thenReturn(user);
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        userService.deleteUser(email);

        verify(securityUtils, times(1))
                .getCurrentUser();
        verify(userRepository, times(1))
                .findByEmail(email);
        verify(userRepository, times(1))
                .delete(any(UserEntity.class));
    }

    @Test
    void deleteUser_ShouldThrowException_WhenNotAccess() {
        String email = "user@gmail.com";

        UserEntity user = new UserEntity();
        user.setEmail(email);

        when(securityUtils.getCurrentUser())
                .thenThrow(new IllegalStateException("User is not authenticated"));

        assertThrows(IllegalStateException.class,
                () -> userService.deleteUser(email));

        verify(userRepository, never())
                .delete(user);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        String email = "user@gmail.com";

        UserEntity user = new UserEntity();
        user.setEmail(email);

        when(securityUtils.getCurrentUser())
                .thenReturn(user);
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser(email));

        verify(userRepository, never())
                .delete(user);
    }
}