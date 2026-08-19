package com.petunincloud.delivery.service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseService<E, D, F extends BaseFilter> {

    protected abstract List<E> findWithFilter(F filter, Pageable pageable);
    protected abstract BaseMapper<E, D> getMapper();

    public List<D> search(F filter) {
        Logger log = LoggerFactory.getLogger(getClass());

        int pageSize = filter.pageSize() != null ? filter.pageSize() : 10;
        int pageNumber = filter.pageNumber() != null ? filter.pageNumber() : 0;
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        log.debug("Search {} with filter: {}, page: {}, size: {}",
                getClass().getSimpleName(), filter, pageNumber, pageSize);

        List<E> entities = findWithFilter(filter, pageable);
        log.debug("Found {} entities", entities.size());

        return entities.stream()
                .map(getMapper()::toResponse)
                .collect(Collectors.toList());
    }
}