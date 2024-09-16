package com.scalefocus.auth.service;

import com.scalefocus.auth.model.User;
import com.scalefocus.auth.model.exception.TokenNotValidException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;
    private String token;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        jwtService = new JwtService();
        user = User.builder().username("username").password("password").build();
        token = jwtService.generateToken(user);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void is_token_valid_success() {
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void is_token_valid_fail() {
        assertThrows(TokenNotValidException.class, () -> jwtService.isTokenValid("NotValid"));
    }

    @Test
    void is_user_token_valid_success() {
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void is_user_token_valid_fail() {
        User newUser = User.builder().username("user").password("pw").build();
        assertFalse(jwtService.isTokenValid(token, newUser));
    }

}
