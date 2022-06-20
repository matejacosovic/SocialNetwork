package com.example.SocialNetwork.controller;

import static com.example.SocialNetwork.security.AuthenticationUtil.getUsernameFromJwt;

import java.util.List;

import javax.validation.Valid;

import com.example.SocialNetwork.domain.dto.EmailDTO;
import com.example.SocialNetwork.domain.dto.FriendDTO;
import com.example.SocialNetwork.domain.dto.MessageDTO;
import com.example.SocialNetwork.domain.dto.PasswordDTO;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.create(userDTO));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserDTO>> listUsers(@RequestParam(value = "search", defaultValue = "") String search) {
        return ResponseEntity.ok(userService.listUsers(search));
    }

    @PostMapping("/connect")
    @PreAuthorize("hasAnyAuthority('ROLE_APP_USER', 'ROLE_ADMIN')")
    public ResponseEntity<UserDTO> connect(@RequestBody FriendDTO dto){
        return ResponseEntity.ok(userService.connect(getUsernameFromJwt(), dto.getWithWho()));
    }

    @DeleteMapping("/connect/{withWho}")
    @PreAuthorize("hasAnyAuthority('ROLE_APP_USER', 'ROLE_ADMIN')")
    public ResponseEntity<UserDTO> removeConnect(@PathVariable String withWho){
        return ResponseEntity.ok(userService.removeConnect(getUsernameFromJwt(), withWho));
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<MessageDTO> resetPassword(@RequestBody EmailDTO dto) {
        return ResponseEntity.ok(userService.forgotPassword(dto.getUserEmail()));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<MessageDTO> changePassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        return ResponseEntity.ok(userService.changePassword(passwordDTO));
    }

    @PutMapping(value = "/deactivate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> deactivateUser(@RequestBody UserDTO dto){
        return ResponseEntity.ok(userService.deactivateUser(dto.getId()));
    }
}
