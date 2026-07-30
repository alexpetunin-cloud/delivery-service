package com.petunincloud.delivery.service.security;

import com.petunincloud.delivery.service.security.dto.AuthRequest;
import com.petunincloud.delivery.service.security.dto.AuthResponse;
import com.petunincloud.delivery.service.users.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtUtils jwtUtils,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            UserMapper userMapper
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody @Valid AuthRequest request
    ) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        RoleEntity clientRole = roleRepository.findByName("ROLE_CLIENT")
                .orElseThrow(() -> new IllegalArgumentException("Default role not found"));

        UserEntity user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(clientRole));

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
}