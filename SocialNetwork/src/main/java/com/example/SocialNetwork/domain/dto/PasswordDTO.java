package com.example.SocialNetwork.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordDTO {
    private String token;
    private String newPassword;
}
