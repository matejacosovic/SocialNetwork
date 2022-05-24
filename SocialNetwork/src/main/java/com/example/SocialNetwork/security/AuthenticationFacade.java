package com.example.SocialNetwork.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade  {
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getUsernameFromJwt() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
