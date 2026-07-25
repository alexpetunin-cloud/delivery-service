package com.petunincloud.delivery.service.users;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.users.dto.UserPatchRequest;
import com.petunincloud.delivery.service.users.dto.UserRequest;
import com.petunincloud.delivery.service.users.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/{email}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String email,
            @RequestBody @Valid UserPatchRequest request
    ) {
        UserResponse response = service.updateUser(email, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String email
    ) {
        service.deleteUser(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> findByEmail(
            @PathVariable String email
    ) {
        UserResponse response = service.findByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<UserResponse> findByPhone(
            @PathVariable String phone
    ) {
        UserResponse response = service.findByPhone(phone);
        return ResponseEntity.ok(response);
    }
}
