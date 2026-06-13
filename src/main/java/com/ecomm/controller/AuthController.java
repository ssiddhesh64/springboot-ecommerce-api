package com.ecomm.controller;

import com.ecomm.dto.LoginRequest;
import com.ecomm.dto.RegisterRequest;
import com.ecomm.dto.UserResponse;
import com.ecomm.security.JwtTokenProvider;
import com.ecomm.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return userService
                .authenticate(request.email(), request.password())
                .map((authUser) -> {
                    String token = tokenProvider.generateToken(authUser.email());
                    return ResponseEntity.ok(new AuthResponse(token));
                })
                .orElse(ResponseEntity.status(401).body(new AuthResponse("Invalid Credentials")));
    }

    @Data
    @AllArgsConstructor
    static class AuthResponse {
        private String token;
    }
}
