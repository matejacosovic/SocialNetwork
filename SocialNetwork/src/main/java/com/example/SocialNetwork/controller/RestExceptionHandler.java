package com.example.SocialNetwork.controller;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

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

    @ExceptionHandler(value = JWTVerificationException.class)
    public ResponseEntity handleException(JWTVerificationException jwtVerificationException) {
        return new ResponseEntity(jwtVerificationException.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
