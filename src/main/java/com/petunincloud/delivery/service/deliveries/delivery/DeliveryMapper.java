package com.petunincloud.delivery.service.deliveries.delivery;

import com.petunincloud.delivery.service.common.BaseMapper;
import com.petunincloud.delivery.service.deliveries.delivery.dto.DeliveryResponse;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper implements BaseMapper<DeliveryEntity, DeliveryResponse> {

    @Override
    public DeliveryResponse toResponse(DeliveryEntity entity) {
        return new DeliveryResponse(
                entity.getId(),
                entity.getOrder().getId(),
                entity.getCourier().getId(),
                entity.getCourier().getName(),
                entity.getStatus(),
                entity.getPickupAddress(),
                entity.getDeliveryAddress(),
                entity.getAssignedAt(),
                entity.getDeliveredAt()
        );
    }

    @Override
    public DeliveryEntity toEntity(DeliveryResponse dto) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
