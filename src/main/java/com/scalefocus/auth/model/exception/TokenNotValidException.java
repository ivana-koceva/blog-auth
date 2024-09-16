package com.scalefocus.auth.model.exception;

public class TokenNotValidException extends RuntimeException {
    public TokenNotValidException() {
        super("Token not valid");
    }
}
