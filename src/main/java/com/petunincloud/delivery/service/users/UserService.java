package com.petunincloud.delivery.service.users;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.security.SecurityUtils;
import com.petunincloud.delivery.service.users.dto.UserPatchRequest;
import com.petunincloud.delivery.service.users.dto.UserRequest;
import com.petunincloud.delivery.service.users.dto.UserResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends BaseService<UserEntity, UserResponse, UserSearchFilter> {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public UserService(
            UserMapper userMapper,
            UserRepository userRepository,
            SecurityUtils securityUtils
    ) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    @Override
    protected List<UserEntity> findWithFilter(UserSearchFilter filter, Pageable pageable) {
        return userRepository.searchAllByFilter(
                filter.name(),
                filter.email(),
                filter.phone(),
                filter.address(),
                pageable
        );
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        if (userRepository.findByPhone(request.phone()).isPresent()) {
            throw new IllegalArgumentException("Phone already exists: " + request.phone());
        }

        UserEntity entity = userMapper.toEntity(request);
        UserEntity saved = userRepository.save(entity);

        return userMapper.toResponse(saved);
    }

    @Override
    protected UserMapper getMapper() {
        return userMapper;
    }

    public UserResponse findByEmail(String email) {
        UserEntity currentUser = securityUtils.getCurrentUser();
        if (!currentUser.getEmail().equals(email)) {
            throw new IllegalArgumentException("You can only access your own profile");
        }
        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("User not found by email: " + email));
    }

    public UserResponse findByPhone(String phone) {
        UserEntity currentUser = securityUtils.getCurrentUser();
        if (!currentUser.getPhone().equals(phone)) {
            throw new IllegalArgumentException("You can only access your own profile");
        }
        return userRepository.findByPhone(phone)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("User not found by phone: " + phone));
    }

    @Transactional
    public UserResponse updateUser(String email, UserPatchRequest request) {
        UserEntity currentUser = securityUtils.getCurrentUser();
        if (!currentUser.getEmail().equals(email)) {
            throw new IllegalArgumentException("You can only update your own profile");
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            userRepository.findByEmail(request.email())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(user.getId())) {
                            throw new IllegalArgumentException("Email already exists: " + request.email());
                        }
                    });
            user.setEmail(request.email());
        }

        if (request.phone() != null && !request.phone().equals(user.getPhone())) {
            userRepository.findByPhone(request.phone())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(user.getId())) {
                            throw new IllegalArgumentException("Phone already exists: " + request.phone());
                        }
                    });
            user.setPhone(request.phone());
        }

        if (request.name() != null) {
            user.setName(request.name());
        }
        if (request.address() != null) {
            user.setAddress(request.address());
        }
        if (request.apartment() != null) {
            user.setApartment(request.apartment());
        }
        if (request.deliveryInstructions() != null) {
            user.setDeliveryInstructions(request.deliveryInstructions());
        }

        UserEntity updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Transactional
    public void deleteUser(String email) {
        UserEntity currentUser = securityUtils.getCurrentUser();
        if (!currentUser.getEmail().equals(email)) {
            throw new IllegalArgumentException("You can only delete your own profile");
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        userRepository.delete(user);
    }
}
