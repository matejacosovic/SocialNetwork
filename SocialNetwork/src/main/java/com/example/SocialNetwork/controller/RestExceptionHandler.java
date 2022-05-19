package com.example.SocialNetwork.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity handleException(RuntimeException runtimeException) {
        return new ResponseEntity(runtimeException.getMessage(), HttpStatus.CONFLICT);
    }
}
