package com.example.SocialNetwork.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationUtil {
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getUsernameFromJwt() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
