package com.petunincloud.delivery.service.users;

import com.petunincloud.delivery.service.common.BaseFilter;

public record UserSearchFilter(
        String name,
        String email,
        String phone,
        String address,
        Integer pageSize,
        Integer pageNumber
) implements BaseFilter {}
