package com.scalefocus.auth.model.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User with that username already exists");
    }
}
