package com.example.SocialNetwork.controller;

import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.UserCreationDTO;
import com.example.SocialNetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createUser(@RequestBody UserCreationDTO userCreationDTO){
        return ResponseEntity.ok(userService.createUser(userCreationDTO));
    }

    @GetMapping(value="/listusers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(userService.listUsers());
    }
}
