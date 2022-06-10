package com.example.SocialNetwork.integration;

import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.MessageDTO;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.domain.enums.UserStatus;
import com.example.SocialNetwork.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void listUsers_returnsAllUsers_whenNoKeyword(){
        List<UserDTO> users =  userService.listUsers("");
        assertEquals(users.size(), 3);
        assertEquals(users.get(0).getEmail(), "mateja.test@vegait.rs");
    }

    @Test
    public void listUsers_returnsOneUser_withAKeyword(){
        List<UserDTO> users =  userService.listUsers("mak");
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getEmail(), "mateja.test1@vegait.rs");
    }

    @Test
    void listUsers_returnsNoUsers_withAKeywordWithNoMatches(){
        List<UserDTO> users =  userService.listUsers("msadfasdak");
        assertEquals(users.size(), 0);
    }

    @Test
    void createUser_throwsException_whenUsernameExists() {
        UserDTO userForSavingDTO = new UserDTO();
        userForSavingDTO.setUsername("user");

        assertThrows(IllegalArgumentException.class, ()->{
            userService.create(userForSavingDTO);
        });
    }

    @Test
    void createUser_throwsException_whenEmailExists() {
        UserDTO userForSavingDTO = new UserDTO();
        userForSavingDTO.setEmail("mateja.test1@vegait.rs");

        assertThrows(IllegalArgumentException.class, ()->{
            userService.create(userForSavingDTO);
        });
    }

    @Test
    void createUser_returnsUserDto_whenInputIsValid() {
        List<UserDTO> usersBeforeAdd =  userService.listUsers("");
        int sizeBeforeAdd = usersBeforeAdd.size();

        UserDTO userForSavingDTO = new UserDTO();
        userForSavingDTO.setEmail("doesntexist@gmail.com");
        userForSavingDTO.setPassword("Password123!");
        userForSavingDTO.setName("Valid");
        userForSavingDTO.setSurname("Validic");
        userForSavingDTO.setUsername("doesntexist");

        UserDTO userDTO = userService.create(userForSavingDTO);

        List<UserDTO> users =  userService.listUsers("");

        assertEquals(sizeBeforeAdd+1, users.size());

        assertEquals(userDTO.getUsername(), "doesntexist");
        assertEquals(userDTO.getStatus(), UserStatus.ACTIVATED);
        assertEquals(userDTO.getFriends().size(), 0);
        //hashed password
        assertNotEquals("password", userDTO.getPassword());
    }

    @Test
    void checkIfUserExists_returnsUser_existingId() {
        User user = userService.checkIfUserExists("test-id");
        assertEquals(user.getUsername(), "admin");
    }

    @Test
    void checkIfUserExists_throwsException_nonExistingId() {
        assertThrows(IllegalArgumentException.class, ()->{
            userService.checkIfUserExists("55");
        });
    }

    @Test
    void deactivateUser_returnsDeactivatedUser_validId() {
        UserDTO user = userService.deactivateUser("test-id");
        assertEquals(user.getUsername(), "admin");
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
        UserDetails user = userService.loadUserByUsername("admin");
        assertEquals(user.getPassword(), "$2a$12$.yaNGFMfd9ueDqT3LArDwOj6V0Ody4fMlteBIrYgJni0UnCx2gHfS");
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
            userService.loadUserByUsername("maka");
        });
    }

    @Test
    void validateToken_returnsSuccessfulAnswer_tokenValid() {
        User user = userService.checkIfUserExists("test-id");
        userService.createPasswordResetTokenForUser(user, "valid_token");
        MessageDTO messageDTO = userService.validatePasswordToken("valid_token");
        assertEquals(messageDTO.getMessage(), "Token is valid!");
    }

    @Test
    void validateToken_returnsFailAnswer_tokenNonExistent() {
        MessageDTO messageDTO = userService.validatePasswordToken("non_existing_token");
        assertEquals(messageDTO.getMessage(), "Token does not exist!");
    }

    @Test
    void forgotPassword_returnsSuccess_emailExists() {
        MessageDTO messageDTO = userService.forgotPassword("mateja.test1@vegait.rs");
        assertEquals(messageDTO.getMessage(), "Success!");
    }

    @Test
    void forgotPassword_throwsException_emailDoesntExists() {
        assertThrows(IllegalArgumentException.class, ()->{
            userService.forgotPassword("doesntexist@gmail.com");
        });
    }
}
