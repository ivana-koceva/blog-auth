package com.scalefocus.auth.controller;

import com.scalefocus.auth.model.dto.UserDTO;
import com.scalefocus.auth.model.dto.UserDetailsDTO;
import com.scalefocus.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserDTO userModel) {
        logger.trace("UserController - Register user: {}", userModel);
        return ResponseEntity.ok().body(userService.register(userModel));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserDTO userModel) {
        logger.trace("UserController - Login user: {}", userModel);
        return ResponseEntity.ok().body(userService.login(userModel));
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validate(@RequestHeader("Authorization") String authorization) {
        logger.trace("UserController - Validate token: {}", authorization);
        return ResponseEntity.ok().body(userService.validate(authorization.substring(7)));
    }

    @GetMapping("/details")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@RequestHeader("Authorization") String authorization) {
        logger.trace("UserController - Get user details: {}", authorization);
        return ResponseEntity.ok().body(userService.getUserDetails(authorization.substring(7)));
    }

    @DeleteMapping()
    public String deleteUser(@RequestHeader("Authorization") String authorization, @RequestParam String username) {
        logger.trace("UserController - Delete user: {}", username);
        userService.validate(authorization.substring(7));
        userService.deleteUser(username);
        return "User successfully deleted";
    }
}
