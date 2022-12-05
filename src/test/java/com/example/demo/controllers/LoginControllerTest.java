package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginControllerTest {

    private LoginController loginController;

    @Before
    public void setUp() {
        loginController = new LoginController();
    }

    @Test
    public void validLoginRequestReturnsStatusCodeOK() {
        User user = new User();
        user.setUsername("test user");
        user.setPassword("testPassword");
        ResponseEntity res = loginController.login(user);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }
}
