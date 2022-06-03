package com.example.SocialNetwork.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity handleException(IllegalArgumentException illegalArgumentException) {
        return new ResponseEntity(illegalArgumentException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity handleException(BadCredentialsException badCredentialsException) {
        return new ResponseEntity(badCredentialsException.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
