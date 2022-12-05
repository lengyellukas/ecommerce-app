package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        System.out.println("username: " + user.getUsername());
        System.out.println("password: " + user.getPassword());
        return new ResponseEntity(HttpStatus.OK);
    }
}
