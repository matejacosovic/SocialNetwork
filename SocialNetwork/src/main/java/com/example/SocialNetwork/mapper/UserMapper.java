package com.example.SocialNetwork.mapper;


import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.domain.enums.UserStatus;
import com.example.SocialNetwork.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User toNewUser(UserDTO userDTO) {
        User u =  User
                .builder()
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail())
                .role(roleRepository.findByName("ROLE_APP_USER"))
                .friends(new HashSet<>())
                .friendOf(new HashSet<>())
                .status(UserStatus.ACTIVATED)
                .build();
        return u;
    }

    public UserDTO toUserDTO(User user) {
        List<String> friends = new ArrayList<>();
        friends.addAll(user.getFriends().stream().map(User::getUsername).toList());
        friends.addAll(user.getFriendOf().stream().map(User::getUsername).toList());
        return UserDTO
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .surname(user.getSurname())
                .status(user.getStatus())
                .friends(friends)
                .build();
    }
}
