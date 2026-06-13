package com.ecomm.controller;

import com.ecomm.dto.UserResponse;
import com.ecomm.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/v1")
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }
}
