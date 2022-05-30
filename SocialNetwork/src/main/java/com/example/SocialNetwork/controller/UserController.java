package com.example.SocialNetwork.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import static com.example.SocialNetwork.security.AuthenticationUtil.getUsernameFromJwt;
import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.create(userDTO));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserDTO>> listUsers(@RequestParam(value = "search", defaultValue = "") String search) {
        return ResponseEntity.ok(userService.listUsers(search));
    }

    @PostMapping("/connect/{withWho}")
    @PreAuthorize("hasAnyAuthority('ROLE_APP_USER', 'ROLE_ADMIN')")
    public ResponseEntity<UserDTO> connect(@PathVariable String withWho){
        return ResponseEntity.ok(userService.connect(getUsernameFromJwt(), withWho));
    }

    @DeleteMapping("/connect/{withWho}")
    @PreAuthorize("hasAnyAuthority('ROLE_APP_USER', 'ROLE_ADMIN')")
    public ResponseEntity<UserDTO> removeConnect(@PathVariable String withWho){
        return ResponseEntity.ok(userService.removeConnect(getUsernameFromJwt(), withWho));
    }
}
