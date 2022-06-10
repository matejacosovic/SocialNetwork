package com.example.SocialNetwork.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDTO {
    private String token;
    @NotBlank(message = "New password is mandatory")
    private String newPassword;
}
