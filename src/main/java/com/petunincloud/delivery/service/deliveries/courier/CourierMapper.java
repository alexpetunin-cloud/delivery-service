package com.petunincloud.delivery.service.deliveries.courier;

import com.petunincloud.delivery.service.common.BaseMapper;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierRequest;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierResponse;
import com.petunincloud.delivery.service.deliveries.courier.dto.CreateCourierRequest;
import com.petunincloud.delivery.service.users.UserEntity;
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

    public CourierEntity toEntity(CreateCourierRequest request, UserEntity user) {
        CourierEntity courier = new CourierEntity();

        courier.setUser(user);
        courier.setName(request.name());
        courier.setPhone(request.phone());
        courier.setStatus(CourierStatus.AVAILABLE);

        return courier;
    }
}
