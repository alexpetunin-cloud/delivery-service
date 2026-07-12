package com.petunincloud.delivery.service.deliveries.courier;

import com.petunincloud.delivery.service.common.BaseService;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierRequest;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourierService extends BaseService<CourierEntity, CourierResponse, CourierSearchFilter> {

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
    protected List<CourierEntity> findWithFilter(CourierSearchFilter filter, Pageable pageable) {
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
        CourierEntity courier = courierRepository.findTopByStatus(CourierStatus.AVAILABLE)
                .orElseThrow(() -> new IllegalStateException("No available couriers"));
        return courierMapper.toResponse(courier);
    }

    @Transactional
    public CourierResponse createCourier(CourierRequest request) {
        CourierEntity courier = courierMapper.toEntity(request);
        courier.setStatus(CourierStatus.AVAILABLE);

        CourierEntity saved = courierRepository.save(courier);
        return courierMapper.toResponse(saved);
    }
}
