package com.encora.taskmanager.exception;


public class JWTTokenExpiredException extends RuntimeException {
    public JWTTokenExpiredException(String message) {
        super(message);
    }
}
