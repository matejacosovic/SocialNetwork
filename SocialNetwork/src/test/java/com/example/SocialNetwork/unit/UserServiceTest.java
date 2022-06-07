package com.example.SocialNetwork.unit;

import com.example.SocialNetwork.domain.PasswordResetToken;
import com.example.SocialNetwork.domain.Role;
import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.MessageDTO;
import com.example.SocialNetwork.domain.dto.PasswordDTO;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.domain.enums.UserStatus;
import com.example.SocialNetwork.repository.PasswordTokenRepository;
import com.example.SocialNetwork.repository.RoleRepository;
import com.example.SocialNetwork.repository.UserRepository;
import com.example.SocialNetwork.service.MailSenderService;
import com.example.SocialNetwork.service.UserService;
import liquibase.pro.packaged.P;
import liquibase.pro.packaged.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordTokenRepository passwordTokenRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setup(){
        User user1 = new User("user1@gmail.com",
                "password",
                "User",
                "1",
                "user1");

        User user2 = new User("user2@gmail.com",
                "password",
                "User",
                "2",
                "user2");

        User user3 = new User("user3@gmail.com",
                "password",
                "User",
                "3",
                "user3");

        User disabled = new User("disabled@gmail.com",
                "password",
                "Disabled",
                "Disablic",
                "disabled");
        disabled.setStatus(UserStatus.DEACTIVATED);

        Role role = new Role();
        role.setName("ROLE_APPUSER");
        user1.getRoles().add(role);

        List<User> userListAll = new ArrayList<>();
        List<User> userListOne = new ArrayList<>();


        userListAll.add(user1);
        userListOne.add(user1);
        userListAll.add(user2);
        userListAll.add(user3);
        userListAll.add(disabled);

        given(userRepository.search("")).willReturn(userListAll);
        given(userRepository.search("1")).willReturn(userListOne);
        given(userRepository.findById("1")).willReturn(Optional.of(user1));
        given(userRepository.findById("2")).willReturn(Optional.of(user2));
        given(userRepository.findById("55")).willReturn(Optional.empty());
        given(userRepository.findByUsername("user1")).willReturn(Optional.of(user1));
        given(userRepository.findByEmail("user1@gmail.com")).willReturn(Optional.of(user1));
        given(userRepository.findByUsername("doesntexist")).willReturn(Optional.empty());
        given(userRepository.findByEmail("doesntexist@gmail.com")).willReturn(Optional.empty());
        given(userRepository.findByUsername("disabled")).willReturn(Optional.of(disabled));

        given(roleRepository.findByName("ROLE_APPUSER")).willReturn(role);

        User userForSaving = new User("doesntexist@gmail.com",
                "password",
                "Valid",
                "Validic",
                "doesntexist");
        given(userRepository.save(any(User.class))).willReturn(userForSaving);

        PasswordResetToken validToken = new PasswordResetToken();
        validToken.setToken("valid_token");
        validToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        validToken.setUser(userForSaving);

        PasswordResetToken expiredToken = new PasswordResetToken();
        expiredToken.setToken("expired_token");
        expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(70));
        given(passwordTokenRepository.findByToken("expired_token")).willReturn(expiredToken);
        given(passwordTokenRepository.findByToken("valid_token")).willReturn(validToken);
        given(passwordTokenRepository.findByToken("non_existing_token")).willReturn(null);

    }

    @Test
    void createUser_returnsUserDto_whenInputIsValid() {
        UserDTO userForSavingDTO = new UserDTO();
        userForSavingDTO.setEmail("doesntexist@gmail.com");
        userForSavingDTO.setPassword("Password123!");
        userForSavingDTO.setName("Valid");
        userForSavingDTO.setSurname("Validic");
        userForSavingDTO.setUsername("doesntexist");

        UserDTO userDTO = userService.create(userForSavingDTO);
        assertEquals(userDTO.getUsername(), "doesntexist");
        assertEquals(userDTO.getStatus(), UserStatus.ACTIVATED);
        assertEquals(userDTO.getFriends().size(), 0);
        //hashed password
        assertNotEquals("password", userDTO.getPassword());
    }

    @Test
    void createUser_throwsException_whenUsernameExists() {
        UserDTO userForSavingDTO = new UserDTO();
        userForSavingDTO.setUsername("user1");

        assertThrows(IllegalArgumentException.class, ()->{
            userService.create(userForSavingDTO);
        });
    }

    @Test
    void createUser_throwsException_whenEmailExists() {
        UserDTO userForSavingDTO = new UserDTO();
        userForSavingDTO.setEmail("user1@gmail.com");

        assertThrows(IllegalArgumentException.class, ()->{
            userService.create(userForSavingDTO);
        });
    }

    @Test
    void listUsers_returnsAllUsers_whenNoKeyword() {
        List<UserDTO> users = userService.listUsers("");
        assertEquals(users.size(), 4);
    }

    @Test
    void listUsers_returnsOneUser_withAKeyword() {
        List<UserDTO> users = userService.listUsers("1");
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getUsername(), "user1");
    }

    @Test
    void checkIfUserExists_returnsUser_existingId() {
        User user = userService.checkIfUserExists("1");
        assertEquals(user.getUsername(), "user1");
    }

    @Test
    void checkIfUserExists_throwsException_nonExistingId() {
        assertThrows(IllegalArgumentException.class, ()->{
            userService.checkIfUserExists("55");
        });
    }

    @Test
    void deactivateUser_returnsDeactivatedUser_validId() {
        UserDTO user = userService.deactivateUser("2");
        assertEquals(user.getUsername(), "user2");
        assertEquals(user.getStatus(), UserStatus.DEACTIVATED);
    }

    @Test
    void deactivateUser_throwsException_invalidId() {
        assertThrows(IllegalArgumentException.class, ()->{
            userService.deactivateUser("55");
        });
    }

    @Test
    void loadByUsername_returnsUserDetails_existingUsername() {
        UserDetails user = userService.loadUserByUsername("user1");
        assertEquals(user.getPassword(), "password");
    }

    @Test
    void loadByUsername_throwsException_nonexistingUsername() {
        assertThrows(UsernameNotFoundException.class, ()->{
            userService.loadUserByUsername("doesntexist");
        });
    }

    @Test
    void loadByUsername_throwsException_disabledAccount() {
        assertThrows(UsernameNotFoundException.class, ()->{
            userService.loadUserByUsername("disabled");
        });
    }

    @Test
    void validateToken_returnsSuccessfulAnswer_tokenValid() {
        MessageDTO messageDTO = userService.validatePasswordToken("valid_token");
        assertEquals(messageDTO.getMessage(), "Token is valid!");
    }

    @Test
    void validateToken_returnsFailAnswer_tokenExpired() {
        MessageDTO messageDTO = userService.validatePasswordToken("expired_token");
        assertEquals(messageDTO.getMessage(), "Token is expired!");
    }

    @Test
    void validateToken_returnsFailAnswer_tokenNonExistent() {
        MessageDTO messageDTO = userService.validatePasswordToken("non_existing_token");
        assertEquals(messageDTO.getMessage(), "Token does not exist!");
    }

    @Test
    void forgotPassword_returnsSuccess_emailExists() {
        MessageDTO messageDTO = userService.forgotPassword("user1@gmail.com");
        assertEquals(messageDTO.getMessage(), "Success!");
    }

    @Test
    void forgotPassword_throwsException_emailDoesntExists() {
        assertThrows(IllegalArgumentException.class, ()->{
            userService.forgotPassword("doesntexist@gmail.com");
        });
    }

    @Test
    void change_password_should_throw_exception_if_token_is_invalid() {
        PasswordDTO passwordDTO = new PasswordDTO("non_existing_token", "nova");
        MessageDTO messageDTO = userService.changePassword(passwordDTO);
        assertEquals(messageDTO.getMessage(), "Token is not valid!");
    }

    @Test
    void change_password_should_return_success_for_valid_token() {
        PasswordDTO passwordDTO = new PasswordDTO("valid_token", "nova");
        MessageDTO messageDTO = userService.changePassword(passwordDTO);
        assertEquals(messageDTO.getMessage(), "Successfully changed password!");
    }
}
