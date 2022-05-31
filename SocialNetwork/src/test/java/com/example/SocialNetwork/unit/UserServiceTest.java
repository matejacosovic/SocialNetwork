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
    void create_user_exist_should_save_when_valid_dto_is_sent() {
        UserDTO userForSavingDTO = new UserDTO();
        userForSavingDTO.setEmail("doesntexist@gmail.com");
        userForSavingDTO.setPassword("password");
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
    void create_user_exist_should_throw_exception_when_username_already_exists() {
        UserDTO userForSavingDTO = new UserDTO();
        userForSavingDTO.setUsername("user1");

        assertThrows(IllegalArgumentException.class, ()->{
            userService.create(userForSavingDTO);
        });
    }

    @Test
    void create_user_exist_should_throw_exception_when_email_already_exists() {
        UserDTO userForSavingDTO = new UserDTO();
        userForSavingDTO.setEmail("user1@gmail.com");

        assertThrows(IllegalArgumentException.class, ()->{
            userService.create(userForSavingDTO);
        });
    }

    @Test
    void list_users_without_a_search_word_should_return_all_users() {
        List<UserDTO> users = userService.listUsers("");
        assertEquals(users.size(), 4);
    }

    @Test
    void list_users_with_a_search_word_should_return_one_user() {
        List<UserDTO> users = userService.listUsers("1");
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getUsername(), "user1");
    }

    @Test
    void check_if_user_exist_should_return_a_user_for_a_existing_id() {
        User user = userService.checkIfUserExists("1");
        assertEquals(user.getUsername(), "user1");
    }

    @Test
    void check_if_user_exist_should_throw_exception_for_non_existing_id() {
        assertThrows(IllegalArgumentException.class, ()->{
            userService.checkIfUserExists("55");
        });
    }

    @Test
    void deactivate_user_exist_should_return_a_user_with_deactivated_status() {
        UserDTO user = userService.deactivateUser("2");
        assertEquals(user.getUsername(), "user2");
        assertEquals(user.getStatus(), UserStatus.DEACTIVATED);
    }

    @Test
    void deactivate_user_exist_should_throw_exception_for_non_existing_id() {
        assertThrows(IllegalArgumentException.class, ()->{
            userService.deactivateUser("55");
        });
    }

    @Test
    void load_by_username_exist_should_return_user_details_for_a_existing_username() {
        UserDetails user = userService.loadUserByUsername("user1");
        assertEquals(user.getPassword(), "password");
    }

    @Test
    void load_by_username_should_throw_exception_for_non_existing_username() {
        assertThrows(UsernameNotFoundException.class, ()->{
            userService.loadUserByUsername("doesntexist");
        });
    }

    @Test
    void load_by_username_should_throw_exception_for_disabled_account() {
        assertThrows(UsernameNotFoundException.class, ()->{
            userService.loadUserByUsername("disabled");
        });
    }

    @Test
    void validate_token_should_return_successful_answer_if_token_is_valid() {
        MessageDTO messageDTO = userService.validatePasswordToken("valid_token");
        assertEquals(messageDTO.getMessage(), "Token is valid!");
    }

    @Test
    void validate_token_should_return_expired_answer_if_token_is_expired() {
        MessageDTO messageDTO = userService.validatePasswordToken("expired_token");
        assertEquals(messageDTO.getMessage(), "Token is expired!");
    }

    @Test
    void validate_token_should_return_does_not_exist_answer_if_token_isnt_present() {
        MessageDTO messageDTO = userService.validatePasswordToken("non_existing_token");
        assertEquals(messageDTO.getMessage(), "Token does not exist!");
    }

    @Test
    void forgot_password_should_return_success_if_email_exists() {
        MessageDTO messageDTO = userService.forgotPassword("user1@gmail.com");
        assertEquals(messageDTO.getMessage(), "Success!");
    }

    @Test
    void forgot_password_should_throw_exception_if_email_does_not_exist() {
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
