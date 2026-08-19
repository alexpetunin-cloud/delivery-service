package com.petunincloud.delivery.service.deliveries.courier;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierRequest;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourierService extends BaseService<CourierEntity, CourierResponse, CourierSearchFilter> {

    private static final Logger log = LoggerFactory.getLogger(CourierService.class);
    private final CourierMapper courierMapper;
    private final CourierRepository courierRepository;

    public CourierService(
            CourierMapper courierMapper,
            CourierRepository courierRepository
    ) {
        this.courierMapper = courierMapper;
        this.courierRepository = courierRepository;
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

            CourierEntity courier = courierMapper.toEntity(request);

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
}
