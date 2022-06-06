package com.example.SocialNetwork.domain.dto;

import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String username;
    private List<String> friends = new ArrayList<>();
    private UserStatus status;

}
