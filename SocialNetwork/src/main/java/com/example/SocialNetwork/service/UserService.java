package com.example.SocialNetwork.service;

import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.UserCreationDTO;
import com.example.SocialNetwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    public String createUser(UserCreationDTO userCreationDTO) {
        User user = new User(userCreationDTO.getEmail(),
                userCreationDTO.getPassword(),
                userCreationDTO.getName(),
                userCreationDTO.getSurname(),
                userCreationDTO.getUsername());
        userRepository.save(user);
        return "Success!";
    }

    //transfer this to dto
    public List<User> listUsers() {
        return userRepository.findAll();
    }
}
