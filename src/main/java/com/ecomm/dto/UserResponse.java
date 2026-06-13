package com.ecomm.dto;

import com.ecomm.entity.User;

public record UserResponse(Long id, String name, String email, User.ROLE role) {}
