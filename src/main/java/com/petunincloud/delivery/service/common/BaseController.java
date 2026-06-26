package com.petunincloud.delivery.service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public abstract class BaseController<S extends BaseService<E, D, F>, E, D, F extends BaseFilter> {
    protected final S service;

    public BaseController(S service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<D>> getAll(F filter) {
        Logger log = LoggerFactory.getLogger(getClass()); // Логирование происходит под класс, который вызывает этот метод
        log.info("Called getAll with filter: {}", filter);

        return ResponseEntity.ok(service.search(filter));
    }
}