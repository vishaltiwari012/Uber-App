package com.codingworld.project.uber.uberApp.exceptions;

public class BadCredentialsException extends RuntimeException{
    public BadCredentialsException() {
    }

    public BadCredentialsException(String message) {
        super(message);
    }
}
