package com.scalefocus.auth.service;

import com.scalefocus.auth.model.User;
import com.scalefocus.auth.model.dto.UserDTO;
import com.scalefocus.auth.model.dto.UserDetailsDTO;
import com.scalefocus.auth.model.exception.*;
import com.scalefocus.auth.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private AutoCloseable autoCloseable;
    private User user;
    private UserDTO userDTO;
    private final String token = "UserToken";

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        user = User.builder().username("username").password("password").build();
        userDTO = UserDTO.builder().username("username").password("password").build();
        userService = UserService.builder().repository(userRepository).jwtService(jwtService)
                .authenticationManager(authenticationManager).passwordEncoder(passwordEncoder).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void register_user_success() {
        when(userRepository.save(user)).thenReturn(user);
        assertEquals("Successfully registered!", userService.register(userDTO));
    }

    @Test
    void register_user_fail() {
        UserDTO newUser = UserDTO.builder().username(user.getUsername()).password("pw").build();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.ofNullable(user));
        assertThrows(UserAlreadyExistsException.class, () -> userService.register(newUser));
    }

    @Test
    void register_user_missing_username_thrown() {
        UserDTO incorrectCredentials = UserDTO.builder().username(null).password(user.getPassword()).build();
        assertThrows(MissingCredentialsException.class, () -> { userService.register(incorrectCredentials); });
    }

    @Test
    void register_user_missing_password_thrown() {
        UserDTO incorrectCredentials = UserDTO.builder().username(user.getUsername()).password(null).build();
        assertThrows(MissingCredentialsException.class, () -> { userService.register(incorrectCredentials); });
    }

    @Test
    void login_user_success() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.ofNullable(user));
        when(jwtService.generateToken(user)).thenReturn(token);
        assertEquals(userService.login(userDTO), token);
    }

    @Test
    void login_user_fail() {
        assertThrows(UserNotFoundException.class, () -> { userService.login(userDTO); });
    }

    @Test
    void login_user_missing_username_thrown() {
        UserDTO incorrectCredentials = UserDTO.builder().username(null).password(user.getPassword()).build();
        assertThrows(MissingCredentialsException.class, () -> { userService.login(incorrectCredentials); });
    }

    @Test
    void login_user_missing_password_thrown() {
        UserDTO incorrectCredentials = UserDTO.builder().username(user.getUsername()).password(null).build();
        assertThrows(MissingCredentialsException.class, () -> { userService.login(incorrectCredentials); });
    }

    @Test
    void validate_token_success() {
        when(jwtService.isTokenValid(token)).thenReturn(true);
        assertEquals("Valid", userService.validate(token));
    }

    @Test
    void validate_token_fail() {
        when(jwtService.isTokenValid(token)).thenReturn(false);
        assertThrows(TokenNotValidException.class, () -> userService.validate(token));
    }

    @Test
    void get_user_details_success() {
        when(jwtService.extractUsername(token)).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.ofNullable(user));
        UserDetailsDTO userDetailsDTO = UserDetailsDTO.builder().username(user.getUsername()).role(user.getRole()).token(token).build();
        assertEquals(userDetailsDTO, userService.getUserDetails(token));
    }

    @Test
    void get_user_details_fail() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserDetails(token));
    }

    @Test
    void delete_user_success() {
        when(jwtService.extractUsername(token)).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.ofNullable(user));
        doNothing().when(userRepository).delete(user);
        assertAll(() -> userService.deleteUser(user.getUsername()));
    }

    @Test
    void delete_user_fail() {
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser("username"));
    }
}
