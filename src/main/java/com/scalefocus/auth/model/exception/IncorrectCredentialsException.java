package com.scalefocus.auth.model.exception;

public class IncorrectCredentialsException extends RuntimeException {
    public IncorrectCredentialsException() {
        super("Incorrect password");
    }
}
