package com.petunincloud.delivery.service.users;

import com.petunincloud.delivery.service.common.BaseController;
import com.petunincloud.delivery.service.users.dto.UserPatchRequest;
import com.petunincloud.delivery.service.users.dto.UserRequest;
import com.petunincloud.delivery.service.users.dto.UserResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController extends BaseController<UserService, UserEntity, UserResponse, UserSearchFilter> {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService service) {
        super(service);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody @Valid UserRequest request
    ) {
        log.info("POST /api/users with request: {}", request);
        long startTime = System.currentTimeMillis();

        try {
            UserResponse response = service.createUser(request);

            long duration = System.currentTimeMillis() - startTime;
            log.info("POST /api/users with request: {} completed: {}, duration={}ms",
                    request, HttpStatus.CREATED, duration);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException e) {
            log.warn("POST /api/users with request: {} failed: {}", request, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("POST /api/users with request: {} unexpected error", request, e);
            throw e;
        }
    }

    @PatchMapping("/{email}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable("email") String email,
            @RequestBody @Valid UserPatchRequest request
    ) {
        log.info("PATCH /api/users/{} with request: {}", email, request);
        long startTime = System.currentTimeMillis();

        try {
            UserResponse response = service.updateUser(email, request);

            long duration = System.currentTimeMillis() - startTime;
            log.info("PATCH /api/users/{} with request: {} completed: {}, duration={}ms",
                    email, request, HttpStatus.OK, duration);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("PATCH /api/users/{} with request: {} failed: {}", email, request, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("PATCH /api/users/{} with request: {} unexpected error", email, request, e);
            throw e;
        }
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("email") String email
    ) {
        log.info("DELETE /api/users/{}", email);
        long startTime = System.currentTimeMillis();

        try {
            service.deleteUser(email);

            long duration = System.currentTimeMillis() - startTime;
            log.info("DELETE /api/users/{} completed: {}, duration={}ms",
                    email, HttpStatus.NO_CONTENT, duration);

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("DELETE /api/users/{} failed: {}", email, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("DELETE /api/users/{} unexpected error", email, e);
            throw e;
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> findByEmail(
            @PathVariable("email") String email
    ) {
        log.info("GET /api/users/email/{}", email);
        long startTime = System.currentTimeMillis();

        try {
            UserResponse response = service.findByEmail(email);

            long duration = System.currentTimeMillis() - startTime;
            log.info("GET /api/users/email/{} completed: {}, duration={}ms",
                    email, HttpStatus.OK, duration);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("GET /api/users/email/{} failed: {}", email, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("GET /api/users/email/{} unexpected error", email, e);
            throw e;
        }
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<UserResponse> findByPhone(
            @PathVariable("phone") String phone
    ) {
        log.info("GET /api/users/phone/{}", phone);
        long startTime = System.currentTimeMillis();

        try {
            UserResponse response = service.findByPhone(phone);

            long duration = System.currentTimeMillis() - startTime;
            log.info("GET /api/users/phone/{} completed: {}, duration={}ms",
                    phone, HttpStatus.OK, duration);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("GET /api/users/phone/{} failed: {}", phone, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("GET /api/users/phone/{} unexpected error", phone, e);
            throw e;
        }
    }
}
