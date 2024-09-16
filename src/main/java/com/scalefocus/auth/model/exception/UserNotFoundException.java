package com.scalefocus.auth.model.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Username is incorrect");
    }
}
