package com.example.SocialNetwork.service;

import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.repository.RoleRepository;
import com.example.SocialNetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO create(UserDTO userDTO) {
        Optional<User> userOptionalEmail = userRepository.findByEmail(userDTO.getEmail());
        if (userOptionalEmail.isPresent()) {
            throw new RuntimeException("A user with this email already exists!");
        }

        Optional<User> userOptionalUsername = userRepository.findByUsername(userDTO.getUsername());
        if (userOptionalUsername.isPresent()) {
            throw new RuntimeException("A user with this username already exists!");
        }

        User user = new User(userDTO.getEmail(),
                passwordEncoder.encode(userDTO.getPassword()),
                userDTO.getName(),
                userDTO.getSurname(),
                userDTO.getUsername());

        user.getRoles().add(roleRepository.findByName("ROLE_APP_USER"));

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
        User connector = checkUsername(who);
        User connected = checkIfUserExists(withWho);

        connector.addFriend(connected);
        userRepository.save(connector);

        return new UserDTO(connector);
    }

    public UserDTO removeConnect(String who, String withWho) {
        User connector = checkUsername(who);
        User connected = checkIfUserExists(withWho);

        connector.removeFriend(connected);
        userRepository.save(connector);

        return new UserDTO(connector);
    }

    public User checkIfUserExists(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User with id: " + id + " doesn't exist!");
        }
        return userOptional.get();
    }

    public User checkUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User with username: " + username + " doesn't exist!");
        }
        return userOptional.get();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found!");
        }
        User user = optionalUser.get();

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                authorities);
    }
}
