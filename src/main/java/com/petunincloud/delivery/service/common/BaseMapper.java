package com.petunincloud.delivery.service.common;

public interface BaseMapper<E, D> {
    D toDto(E entity);
    E toEntity(D dto);
}
