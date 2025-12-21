package com.ecomm.controller;

import com.ecomm.entity.User;
import com.ecomm.security.JwtTokenProvider;
import com.ecomm.service.UserService;
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

    private UserService userService;
    private JwtTokenProvider tokenProvider;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        return userService.authenticate(user.getEmail(), user.getPassword())
                .map((authUser) -> {
                    String token = tokenProvider.generateToken(authUser.getEmail());
                    return ResponseEntity.ok(new AuthResponse(token));
                }).orElse(ResponseEntity.status(401).body(new AuthResponse("Invalid Credentials")));
    }

    @Data
    @AllArgsConstructor
    static class AuthResponse {
        private String token;
    }
}
