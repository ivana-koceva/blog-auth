package com.scalefocus.auth.controller;

import com.scalefocus.auth.model.dto.UserDTO;
import com.scalefocus.auth.model.dto.UserDetailsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "All The User Endpoints")
public interface UserApi {

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user and saves it to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<String> register(@Valid @RequestBody UserDTO userModel);

    @Operation(
            summary = "Login as an existing user",
            description = "Returns the users token from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<String> login(@Valid @RequestBody UserDTO userModel);

    @Operation(
            summary = "Check token validity",
            description = "Checks if a users token is valid or not")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<String> validate(@RequestHeader("Authorization") String authorization);

    @Operation(
            summary = "Get user from token",
            description = "Returns a users details from token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<UserDetailsDTO> getUserDetails(@RequestHeader("Authorization") String authorization);

    @Operation(
            summary = "Delete a user",
            description = "Deletes a user by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    String deleteUser(@RequestHeader("Authorization") String authorization, @RequestParam String username);
}
