package com.scalefocus.auth.service;

import com.scalefocus.auth.model.dto.UserDTO;
import com.scalefocus.auth.model.dto.UserDetailsDTO;
import com.scalefocus.auth.model.enums.Role;
import com.scalefocus.auth.model.exception.*;
import com.scalefocus.auth.model.User;
import com.scalefocus.auth.repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Builder
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public String register(UserDTO userModel) {
        if(userModel.getUsername() == null || userModel.getPassword() == null ||
            userModel.getUsername().isEmpty() || userModel.getPassword().isEmpty()) {
            throw new MissingCredentialsException();
        }
        logger.trace("Registering user: {}", userModel);
        var user = User.builder()
                .username(userModel.getUsername())
                .password(passwordEncoder.encode(userModel.getPassword()))
                .role(userModel.getRole())
                .build();
        if(repository.findByUsername(userModel.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        repository.save(user);
        jwtService.generateToken(user);
        return "Successfully registered!";
    }

    public String login(UserDTO userModel) {
        if(userModel.getUsername() == null || userModel.getPassword() == null ||
                userModel.getUsername().isEmpty() || userModel.getPassword().isEmpty()) {
            throw new MissingCredentialsException();
        }
        if(userModel.getRole() == null)
            userModel.setRole(Role.ROLE_USER);
        logger.trace("Login user: {}", userModel);
        var user = repository.findByUsername(userModel.getUsername()).orElseThrow(() -> {
            logger.warn("User not found: {}", userModel.getUsername());
            return new UserNotFoundException();
        });
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userModel.getUsername(),
                            userModel.getPassword())
            );
        }
        catch(AuthenticationException e) {
            throw new IncorrectCredentialsException();
        }

        return jwtService.generateToken(user);
    }

    public String validate(String token) {
        logger.trace("Validate token: {}", token);
        if(!jwtService.isTokenValid(token))
            throw new TokenNotValidException();
        return "Valid";
    }

    public UserDetailsDTO getUserDetails(String token) {
        logger.trace("Get user details: {}", token);
        User user = repository.findByUsername(jwtService.extractUsername(token)).orElseThrow(() -> {
            logger.warn("User not found");
            return new UserNotFoundException();
        });
        return new UserDetailsDTO(user.getUsername(), token, user.getRole());
    }

    public void deleteUser(String username) {
        logger.trace("Delete user: {}", username);
        User user = repository.findByUsername(username).orElseThrow(() -> {
            logger.warn("User {} not found", username);
            return new UserNotFoundException();
        });
        repository.delete(user);
    }
}
