package com.example.SocialNetwork.controller;

import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.create(userDTO));
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> listUsers(@RequestParam(value = "search", defaultValue = "") String search) {
        return ResponseEntity.ok(userService.listUsers(search));
    }

    @PostMapping(value="/connect/{who}/{withWho}")
    public ResponseEntity<UserDTO> connect(@PathVariable String who, @PathVariable String withWho){
        return ResponseEntity.ok(userService.connect(who, withWho));
    }

    @DeleteMapping(value="/connect/{who}/{withWho}")
    public ResponseEntity<UserDTO> removeConnect(@PathVariable String who, @PathVariable String withWho){
        return ResponseEntity.ok(userService.removeConnect(who, withWho));
    }
}
