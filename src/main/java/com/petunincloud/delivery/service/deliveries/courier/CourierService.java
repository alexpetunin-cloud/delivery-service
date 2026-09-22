package com.petunincloud.delivery.service.deliveries.courier;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierRequest;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierResponse;
import com.petunincloud.delivery.service.deliveries.courier.dto.CreateCourierRequest;
import com.petunincloud.delivery.service.security.SecurityUtils;
import com.petunincloud.delivery.service.users.RoleEntity;
import com.petunincloud.delivery.service.users.RoleRepository;
import com.petunincloud.delivery.service.users.UserEntity;
import com.petunincloud.delivery.service.users.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CourierService extends BaseService<CourierEntity, CourierResponse, CourierSearchFilter> {

    private static final Logger log = LoggerFactory.getLogger(CourierService.class);
    private final CourierMapper courierMapper;
    private final CourierRepository courierRepository;
    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CourierService(
            CourierMapper courierMapper,
            CourierRepository courierRepository,
            SecurityUtils securityUtils,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.courierMapper = courierMapper;
        this.courierRepository = courierRepository;
        this.securityUtils = securityUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected List<CourierEntity> findWithFilter(
            CourierSearchFilter filter,
            Pageable pageable
    ) {
        return courierRepository.searchAllByFilter(
                filter.name(),
                filter.phone(),
                filter.status() != null ? filter.status().name() : null,
                pageable
        );
    }

    @Override
    protected CourierMapper getMapper() {
        return courierMapper;
    }

    public CourierResponse findAvailableCourier() {
        log.info("Find available courier");
        long startTime = System.currentTimeMillis();

        try {
            CourierEntity courier = courierRepository.findTopByStatus(CourierStatus.AVAILABLE)
                    .orElseThrow(() -> {
                        log.warn("No available couriers");
                        return new IllegalStateException("No available couriers");
                    });

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success find available courier, duration={}ms", duration);

            return courierMapper.toResponse(courier);

        } catch (Exception e) {
            log.error("Failed to find available courier. Error: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public CourierResponse createCourier(CourierRequest request) {
        log.info("Create new courier with request={}", request);
        long startTime = System.currentTimeMillis();

        try {
            if (courierRepository.findByPhone(request.phone()).isPresent()) {
                log.warn("Courier with this phone: {} already exists", request.phone());
                throw new IllegalArgumentException("Courier with this phone already exists");
            }

            UserEntity currentUser = securityUtils.getCurrentUser();
            CourierEntity courier = courierMapper.toEntity(request);

            courier.setUser(currentUser);
            courier.setStatus(CourierStatus.AVAILABLE);

            CourierEntity saved = courierRepository.save(courier);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Success create courier with request: {}, duration={}ms", request, duration);

            return courierMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("Failed to create courier with request {}. Error: {}", request, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public CourierResponse createCourier(CreateCourierRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        if (courierRepository.findByPhone(request.phone()).isPresent()) {
            throw new IllegalArgumentException("Courier with this phone already exists");
        }

        RoleEntity courierRole = roleRepository.findByName("ROLE_COURIER")
                .orElseThrow(() ->
                        new IllegalStateException("ROLE_COURIER not found"));

        UserEntity user = new UserEntity();
        CourierEntity courier = new CourierEntity();

        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setName(request.name());
        user.setAddress(request.address());
        user.setRoles(Set.of(courierRole));

        UserEntity savedUser = userRepository.save(user);

        courier.setUser(savedUser);
        courier.setName(request.name());
        courier.setPhone(request.phone());
        courier.setStatus(CourierStatus.AVAILABLE);

        CourierEntity savedCourier = courierRepository.save(courier);

        return courierMapper.toResponse(savedCourier);
    }
}
