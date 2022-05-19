package com.example.SocialNetwork.domain.dto;

import com.example.SocialNetwork.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String username;
    private List<String> friends = new ArrayList<>();

    public UserDTO(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.username = user.getUsername();
        this.friends.addAll(user.getFriends().stream().map(User::getUsername).toList());
        this.friends.addAll(user.getFriendOf().stream().map(User::getUsername).toList());
    }

}
