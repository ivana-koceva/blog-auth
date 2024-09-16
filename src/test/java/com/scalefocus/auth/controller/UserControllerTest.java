package com.scalefocus.auth.controller;

import com.scalefocus.auth.model.User;
import com.scalefocus.auth.model.dto.UserDTO;
import com.scalefocus.auth.model.dto.UserDetailsDTO;
import com.scalefocus.auth.model.enums.Role;
import com.scalefocus.auth.service.JwtService;
import com.scalefocus.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private JwtService jwtService;
    private User user;
    private UserDTO userDTO;
    private final String token = "Token";

    @BeforeEach
    void setUp() {
        user = User.builder().username("username").password("password").role(Role.ROLE_USER).build();
        userDTO = UserDTO.builder().username("username").password("password").role(Role.ROLE_USER).build();
    }

    @Test
    void register_user_success() throws Exception {
        String success = "Successfully registered!";
        when(this.userService.register(userDTO)).thenReturn(success);
        when(jwtService.generateToken(user)).thenReturn(token);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"username\", \"password\": \"password\", \"role\":  \"ROLE_USER\"}"))
                .andExpect(status().isOk()).andExpect(content().string(success)).andDo(print());
    }

    @Test
    void login_user_success() throws Exception {
        when(this.userService.login(userDTO)).thenReturn(token);

        this.mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"username\", \"password\": \"password\"}"))
                .andExpect(status().isOk()).andExpect(content().string(token)).andDo(print());
    }

    @Test
    void validate_user_success() throws Exception {
        String valid = "Valid";
        when(this.userService.validate(token)).thenReturn(valid);

        this.mockMvc.perform(get("/auth/validate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(content().string(valid)).andDo(print());
    }

    @Test
    void get_user_details() throws Exception {
        UserDetailsDTO userDetailsDTO = UserDetailsDTO.builder().username(userDTO.getUsername()).token(token).role(userDTO.getRole()).build();
        when(this.userService.getUserDetails(token)).thenReturn(userDetailsDTO);
        this.mockMvc.perform(get("/auth/details")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andDo(print());
    }


    @Test
    void delete_user_success() throws Exception {
        String valid = "User successfully deleted";
        when(this.userService.validate(token)).thenReturn("Valid");

        this.mockMvc.perform(delete("/auth")
                        .header("Authorization", "Bearer " + token)
                        .param("username", userDTO.getUsername()))
                .andExpect(status().isOk()).andExpect(content().string(valid)).andDo(print());
    }
}
