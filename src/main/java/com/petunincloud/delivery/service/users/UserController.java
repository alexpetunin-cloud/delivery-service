package com.petunincloud.delivery.service.users;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.users.dto.UserRequest;
import com.petunincloud.delivery.service.users.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController<UserService, UserEntity, UserResponse, UserSearchFilter> {
    public UserController(UserService service) {
        super(service);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody @Valid UserRequest request
    ) {
        UserResponse response = service.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
