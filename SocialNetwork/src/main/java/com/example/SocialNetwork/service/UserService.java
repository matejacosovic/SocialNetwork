package com.example.SocialNetwork.service;

import com.example.SocialNetwork.domain.PasswordResetToken;
import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.MessageDTO;
import com.example.SocialNetwork.domain.dto.PasswordDTO;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.domain.enums.UserStatus;
import com.example.SocialNetwork.repository.PasswordTokenRepository;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordTokenRepository passwordTokenRepository;
    private final MailSenderService mailSenderService;

    public UserDTO create(UserDTO userDTO) {
        Optional<User> userOptionalEmail = userRepository.findByEmail(userDTO.getEmail());
        if (userOptionalEmail.isPresent()) {
            throw new IllegalArgumentException("A user with this email already exists!");
        }

        Optional<User> userOptionalUsername = userRepository.findByUsername(userDTO.getUsername());
        if (userOptionalUsername.isPresent()) {
            throw new IllegalArgumentException("A user with this username already exists!");
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
        User connector = findUser(who);
        User connected = checkIfUserExists(withWho);

        connector.addFriend(connected);
        userRepository.save(connector);

        return new UserDTO(connector);
    }

    public UserDTO removeConnect(String who, String withWho) {
        User connector = findUser(who);
        User connected = checkIfUserExists(withWho);

        connector.removeFriend(connected);
        userRepository.save(connector);

        return new UserDTO(connector);
    }

    public User checkIfUserExists(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional
                .orElseThrow(() -> new IllegalArgumentException("User with id: " + id + " doesn't exist!"));
    }

    public User findUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional
                .orElseThrow(() -> new IllegalArgumentException("User with username: " + username + " doesn't exist!"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found!");
        }
        User user = optionalUser.get();

        if(user.getStatus().equals(UserStatus.DEACTIVATED)){
            throw new UsernameNotFoundException("Account disabled!");
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                authorities);
    }

    public MessageDTO forgotPassword(String userEmail) {
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("No user with the given email!");
        }

        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(userOptional.get(), token);
        mailSenderService.sendMessage(userEmail, "Reset password token", token);
        return new MessageDTO("Success!");
    }

    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);
        myToken.setExpiryDate(LocalDateTime.now());
        passwordTokenRepository.save(myToken);
    }

    public MessageDTO validatePasswordToken(String token) {
        PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? new MessageDTO("Token does not exist!")
                : isTokenExpired(passToken) ? new MessageDTO("Token is expired!")
                : new MessageDTO("Token is valid!");
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        LocalDateTime currentTime = LocalDateTime.now();
        return passToken.getExpiryDate().isBefore(currentTime);
    }

    public MessageDTO changePassword(PasswordDTO passwordDTO) {

        MessageDTO result = validatePasswordToken(passwordDTO.getToken());

        if(!result.getMessage().equals("Token is valid!")) {
            return new MessageDTO("Token is not valid!");
        }

        PasswordResetToken passToken = passwordTokenRepository.findByToken(passwordDTO.getToken());
        User user = passToken.getUser();
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        userRepository.save(user);

        return new MessageDTO("Successfully changed password!");
    }

    public UserDTO deactivateUser(String userId) {
        User user = checkIfUserExists(userId);
        user.setStatus(UserStatus.DEACTIVATED);
        userRepository.save(user);
        return new UserDTO(user);
    }
}

