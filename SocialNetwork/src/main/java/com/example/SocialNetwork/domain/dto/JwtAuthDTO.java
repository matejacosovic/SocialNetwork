package com.example.SocialNetwork.domain.dto;

import lombok.Data;

@Data
public class JwtAuthDTO {
    private String username;
    private String password;
}
