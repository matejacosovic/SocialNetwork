package com.example.SocialNetwork.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.SocialNetwork.domain.AccessTokenDTO;
import com.example.SocialNetwork.domain.dto.JwtAuthDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${social-network.security.secret}")
    private String secret;

    @Value("${social-network.name}")
    private String appName;

    @Value("${social-network.security.token-duration}")
    private Integer tokenDuration;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenDTO> createAuthenticationToken(
            @RequestBody JwtAuthDTO authenticationRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());

        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+tokenDuration))
                .withIssuer(appName)
                .withClaim("roles", user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .sign(algorithm);

        return ResponseEntity.ok(new AccessTokenDTO(accessToken));
    }
}
