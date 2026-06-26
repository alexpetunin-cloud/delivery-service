package com.petunincloud.delivery.service.common;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseService<E, D, F extends BaseFilter> {

    protected abstract List<E> findWithFilter(F filter, Pageable pageable);
    protected abstract BaseMapper<E, D> getMapper();

    public List<D> search(F filter) {
        int pageSize = filter.pageSize() != null ? filter.pageSize() : 10;
        int pageNumber = filter.pageNumber() != null ? filter.pageNumber() : 0;
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        List<E> entities = findWithFilter(filter, pageable);
        return entities.stream()
                .map(getMapper()::toDto)
                .collect(Collectors.toList());
    }
}