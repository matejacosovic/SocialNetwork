package com.example.SocialNetwork.service;

import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserDTO create(UserDTO userDTO) {
        Optional<User> userOptionalEmail = userRepository.findByEmail(userDTO.getEmail());
        if(userOptionalEmail.isPresent()){
            throw new RuntimeException("A user with this email already exists!");
        }

        Optional<User> userOptionalUsername = userRepository.findByUsername(userDTO.getUsername());
        if(userOptionalUsername.isPresent()){
            throw new RuntimeException("A user with this username already exists!");
        }

        User user = new User(userDTO.getEmail(),
                userDTO.getPassword(),
                userDTO.getName(),
                userDTO.getSurname(),
                userDTO.getUsername());
        userRepository.save(user);
        return new UserDTO(user);
    }

    public List<UserDTO> listUsers(String keyword) {
        return userRepository.search(keyword.trim().toLowerCase())
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }


    public UserDTO connect(String who, String withWho) {
        User connector = checkIfUserExists(who);
        User connected = checkIfUserExists(withWho);

        connector.addFriend(connected);
        userRepository.save(connector);

        return new UserDTO(connector);
    }

    public UserDTO removeConnect(String who, String withWho) {
        User connector = checkIfUserExists(who);
        User connected = checkIfUserExists(withWho);

        connector.removeFriend(connected);
        userRepository.save(connector);

        return new UserDTO(connector);
    }

    public User checkIfUserExists(String id){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()){
            throw new RuntimeException("User with id: " + id + " doesn't exist!");
        }
        return userOptional.get();
    }
}
