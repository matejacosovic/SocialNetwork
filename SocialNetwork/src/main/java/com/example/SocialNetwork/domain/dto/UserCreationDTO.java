package com.example.SocialNetwork.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
public class UserCreationDTO {
    private String email;
    private String password;
    private String name;
    private String surname;
    private String username;
}
