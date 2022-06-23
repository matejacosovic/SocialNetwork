package com.example.SocialNetwork.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.SocialNetwork.domain.PasswordResetToken;
import com.example.SocialNetwork.domain.Role;
import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.UserNode;
import com.example.SocialNetwork.domain.dto.MessageDTO;
import com.example.SocialNetwork.domain.dto.PasswordDTO;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.domain.enums.UserStatus;
import com.example.SocialNetwork.repository.PasswordTokenRepository;
import com.example.SocialNetwork.repository.RoleRepository;
import com.example.SocialNetwork.repository.UserNodeRepository;
import com.example.SocialNetwork.repository.UserRepository;
import com.example.SocialNetwork.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordTokenRepository passwordTokenRepository;

    @MockBean
    private UserNodeRepository userNodeRepository;

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

        given(userNodeRepository.save(any(UserNode.class))).willReturn(null);

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
        assertEquals("doesntexist", userDTO.getUsername());
        assertEquals(UserStatus.ACTIVATED, userDTO.getStatus());
        assertEquals(0, userDTO.getFriends().size());
        //hashed password
        assertNotEquals(userDTO.getPassword(),"password");
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
        assertEquals(4, users.size());
    }

    @Test
    void listUsers_returnsOneUser_withAKeyword() {
        List<UserDTO> users = userService.listUsers("1");
        assertEquals(1, users.size());
        assertEquals("user1", users.get(0).getUsername());
    }

    @Test
    void checkIfUserExists_returnsUser_existingId() {
        User user = userService.checkIfUserExists("1");
        assertEquals("user1", user.getUsername());
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
        assertEquals("user2", user.getUsername());
        assertEquals(UserStatus.DEACTIVATED, user.getStatus());
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
        assertEquals("password", user.getPassword());
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
        assertEquals("Token is valid!", messageDTO.getMessage());
    }

    @Test
    void validateToken_returnsFailAnswer_tokenExpired() {
        MessageDTO messageDTO = userService.validatePasswordToken("expired_token");
        assertEquals("Token is expired!", messageDTO.getMessage());
    }

    @Test
    void validateToken_returnsFailAnswer_tokenNonExistent() {
        MessageDTO messageDTO = userService.validatePasswordToken("non_existing_token");
        assertEquals("Token does not exist!", messageDTO.getMessage());
    }

    @Test
    void forgotPassword_returnsSuccess_emailExists() {
        MessageDTO messageDTO = userService.forgotPassword("user1@gmail.com");
        assertEquals("Success!", messageDTO.getMessage());
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
        assertEquals("Token is not valid!", messageDTO.getMessage());
    }

    @Test
    void change_password_should_return_success_for_valid_token() {
        PasswordDTO passwordDTO = new PasswordDTO("valid_token", "nova");
        MessageDTO messageDTO = userService.changePassword(passwordDTO);
        assertEquals("Successfully changed password!", messageDTO.getMessage());
    }
}
