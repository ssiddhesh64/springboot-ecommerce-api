package com.ecomm.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    public List<String> getUsers() {
        return List.of("User1", "User2", "User3");
    }
}
