package com.example.SocialNetwork.mapper;


import java.util.ArrayList;
import java.util.List;

import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.UserNode;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.domain.enums.UserStatus;
import com.example.SocialNetwork.repository.RoleRepository;
import com.example.SocialNetwork.repository.UserNodeRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserNodeRepository userNodeRepository;
    public User toUser(UserDTO userDTO) {
        return User
                .builder()
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail())
                .role(roleRepository.findByName("ROLE_APP_USER"))
                .status(UserStatus.ACTIVATED)
                .build();
    }

    public UserDTO toUserDTO(User user) {
        List<String> friends = new ArrayList<>(userNodeRepository
                                                .findUserFriendsById(user.getId())
                                                .stream()
                                                .map(UserNode::getUsername)
                                                .toList());
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
