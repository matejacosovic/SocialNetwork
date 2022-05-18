package com.example.SocialNetwork.controller;

import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.service.UserService;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {


    private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity handleException(RuntimeException runtimeException) {
        return new ResponseEntity(runtimeException.getMessage(), HttpStatus.CONFLICT);
    }

    @PostMapping()
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.create(userDTO));
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> listUsers() {
        return ResponseEntity.ok(userService.listUsers(null));
    }

    @GetMapping(value="/{keyword}")
    public ResponseEntity<List<UserDTO>> listUsers(@PathVariable String keyword) {
        return ResponseEntity.ok(userService.listUsers(keyword));
    }

    @PostMapping(value="/connect/{who}/{withWho}")
    public ResponseEntity<UserDTO> connect(@PathVariable Integer who, @PathVariable Integer withWho){
        return ResponseEntity.ok(userService.connect(who, withWho));
    }

    @PostMapping(value="/remove-connect/{who}/{withWho}")
    public ResponseEntity<UserDTO> removeConnect(@PathVariable Integer who, @PathVariable Integer withWho){
        return ResponseEntity.ok(userService.removeConnect(who, withWho));
    }
}
