package com.customer.spring.controller;

import com.customer.spring.entity.User;
import com.customer.spring.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        User registered = userService.register(user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registered Successfully");
        response.put("id", registered.getId());
        response.put("username", registered.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user){
        String token = userService.verify(user);
        Map<String, Object> response = new HashMap<>();
        response.put("user", user.getUsername());
        response.put("message", "Login successful");
        response.put("token", token);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
