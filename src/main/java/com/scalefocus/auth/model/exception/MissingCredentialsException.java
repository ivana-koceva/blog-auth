package com.scalefocus.auth.model.exception;

public class MissingCredentialsException extends RuntimeException {
    public MissingCredentialsException() {
        super("Missing username or password");
    }
}
