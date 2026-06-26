package com.petunincloud.delivery.service.common;

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
        return ResponseEntity.ok(service.search(filter));
    }
}