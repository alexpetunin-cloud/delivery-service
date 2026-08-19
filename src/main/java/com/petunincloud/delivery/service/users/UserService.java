package com.petunincloud.delivery.service.users;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.security.SecurityUtils;
import com.petunincloud.delivery.service.users.dto.UserPatchRequest;
import com.petunincloud.delivery.service.users.dto.UserRequest;
import com.petunincloud.delivery.service.users.dto.UserResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends BaseService<UserEntity, UserResponse, UserSearchFilter> {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final static Logger log = LoggerFactory.getLogger(UserService.class);

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
    protected List<UserEntity> findWithFilter(
            UserSearchFilter filter,
            Pageable pageable
    ) {
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
        log.info("Create user with request: {}", request);
        long startTime = System.currentTimeMillis();

        try {
            if (userRepository.findByEmail(request.email()).isPresent()) {
                log.warn("Email already exists: {}", request.email());
                throw new IllegalArgumentException("Email already exists");
            }

            if (userRepository.findByPhone(request.phone()).isPresent()) {
                log.warn("Phone already exists: {}", request.phone());
                throw new IllegalArgumentException("Phone already exists");
            }

            UserEntity entity = userMapper.toEntity(request);

            UserEntity saved = userRepository.save(entity);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success create user with request: {}, duration={}ms", request, duration);

            return userMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Failed create user with request: {}. Error: {}", request, e.getMessage());
            throw e;
        }
    }

    @Override
    protected UserMapper getMapper() {
        return userMapper;
    }

    public UserResponse findByEmail(String email) {
        log.info("Find user by email: {}", email);
        long startTime = System.currentTimeMillis();

        try {
            UserEntity currentUser = securityUtils.getCurrentUser();

            if (!currentUser.getEmail().equals(email)) {
                log.warn("Access denied: user {} tried to access user: {}", currentUser.getEmail(), email);
                throw new IllegalArgumentException("You can only access your own profile");
            }

            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found by email: {}", email);
                        return new IllegalArgumentException("User not found by email");
            });

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success find user by email: {}, duration={}ms", email, duration);

            return userMapper.toResponse(user);

        } catch (Exception e) {
            log.error("Failed to find user by email: {}. Error: {}", email, e.getMessage());
            throw e;
        }
    }

    public UserResponse findByPhone(String phone) {
        log.info("Find user by phone: {}", phone);
        long startTime = System.currentTimeMillis();

        try {
            UserEntity currentUser = securityUtils.getCurrentUser();

            if (!currentUser.getPhone().equals(phone)) {
                log.warn("Access denied: user {} tried to access user: {}", currentUser.getPhone(), phone);
                throw new IllegalArgumentException("You can only access your own profile");
            }

            UserEntity user = userRepository.findByPhone(phone)
                    .orElseThrow(() -> {
                        log.warn("User not found by phone: {}", phone);
                        return new IllegalArgumentException("User not found by phone");
            });

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success find user by phone: {}, duration={}ms", phone, duration);

            return userMapper.toResponse(user);

        } catch (Exception e) {
            log.error("Failed to find user by phone: {}. Error: {}", phone, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public UserResponse updateUser(
            String email,
            UserPatchRequest request
    ) {
        log.info("Update user: {} with request: {}", email, request);
        long startTime = System.currentTimeMillis();

        try {
            UserEntity currentUser = securityUtils.getCurrentUser();

            if (!currentUser.getEmail().equals(email)) {
                log.warn("Access denied: user {} tried to access user: {}", currentUser.getEmail(), email);
                throw new IllegalArgumentException("You can only update your own profile");
            }

            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found by email: {}", email);
                        return new IllegalArgumentException("User not found by email");
                    });

            if (request.email() != null && !request.email().equals(user.getEmail())) {
                userRepository.findByEmail(request.email())
                        .ifPresent(existingUser -> {
                            if (!existingUser.getId().equals(user.getId())) {
                                log.warn("Email already exists: {}", request.email());
                                throw new IllegalArgumentException("Email already exists");
                            }
                        });

                user.setEmail(request.email());
            }

            if (request.phone() != null && !request.phone().equals(user.getPhone())) {
                userRepository.findByPhone(request.phone())
                        .ifPresent(existingUser -> {
                            if (!existingUser.getId().equals(user.getId())) {
                                log.warn("Phone already exists: {}", request.phone());
                                throw new IllegalArgumentException("Phone already exists");
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

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success update user: {} with request: {}, duration={}ms", email, request, duration);

            return userMapper.toResponse(updated);

        } catch (Exception e) {
            log.error("Failed update user: {} with request: {}. Error: {}", email, request, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void deleteUser(String email) {
        log.info("Delete user: {}", email);
        long startTime = System.currentTimeMillis();

        try {
            UserEntity currentUser = securityUtils.getCurrentUser();

            if (!currentUser.getEmail().equals(email)) {
                log.warn("Access denied: user {} tried to access user: {}", currentUser.getEmail(), email);
                throw new IllegalArgumentException("You can only delete your own profile");
            }

            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found by email: {}", email);
                        return new IllegalArgumentException("User not found by email");
                    });

            userRepository.delete(user);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success delete user: {}, duration={}ms", email, duration);

        } catch (Exception e) {
            log.error("Failed delete user: {}. Error: {}", email, e.getMessage());
            throw e;
        }
    }
}
