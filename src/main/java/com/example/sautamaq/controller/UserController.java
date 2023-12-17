package com.example.sautamaq.controller;

import com.example.sautamaq.model.User;
import com.example.sautamaq.service.impl.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register/admin")
    public User registerAdmin(@RequestBody User user) {
        return userService.createAdmin(user);
    }

    @GetMapping("/users/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
//TODO: deleteUserByID, manageUserByID
}
