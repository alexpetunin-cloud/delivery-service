package com.petunincloud.delivery.service.common;

public interface BaseMapper<E, D> {
    D toResponse(E entity);
    E toEntity(D dto);
}
