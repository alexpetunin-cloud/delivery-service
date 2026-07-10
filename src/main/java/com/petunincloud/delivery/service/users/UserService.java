package com.petunincloud.delivery.service.users;

import com.petunincloud.delivery.service.common.BaseService;
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

    public UserService(
            UserMapper userMapper,
            UserRepository userRepository
    ) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
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
        // 1. Проверяем, что email уникален
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        // 2. Проверяем, что phone уникален
        if (userRepository.findByPhone(request.phone()).isPresent()) {
            throw new IllegalArgumentException("Phone already exists: " + request.phone());
        }

        // 3. Создаём сущность
        UserEntity entity = userMapper.toEntity(request);

        // 4. Сохраняем в БД
        UserEntity saved = userRepository.save(entity);

        // 5. Возвращаем DTO
        return userMapper.toResponse(saved);
    }

    @Override
    protected UserMapper getMapper() {
        return userMapper;
    }

    public UserResponse findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("User not found by email: " + email));
    }

    public UserResponse findByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("User not found by phone: " + phone));
    }
}
