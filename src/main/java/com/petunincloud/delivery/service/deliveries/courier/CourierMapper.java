package com.petunincloud.delivery.service.deliveries.courier;

import com.petunincloud.delivery.service.common.BaseMapper;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierRequest;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierResponse;
import org.springframework.stereotype.Component;

@Component
public class CourierMapper implements BaseMapper<CourierEntity, CourierResponse> {
    @Override
    public CourierResponse toResponse(CourierEntity entity) {
        return new CourierResponse(
                entity.getId(),
                entity.getName(),
                entity.getPhone(),
                entity.getStatus()
        );
    }

    @Override
    public CourierEntity toEntity(CourierResponse dto) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public CourierEntity toEntity(CourierRequest request) {
        CourierEntity entity = new CourierEntity();
        entity.setName(request.name());
        entity.setPhone(request.phone());
        return entity;
    }
}
