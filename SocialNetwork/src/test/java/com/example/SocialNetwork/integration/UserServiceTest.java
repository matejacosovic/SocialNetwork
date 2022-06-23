package com.example.SocialNetwork.integration;

import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.MessageDTO;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.domain.enums.UserStatus;
import com.example.SocialNetwork.repository.UserNodeRepository;
import com.example.SocialNetwork.service.UserService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class UserServiceTest {

    @MockBean
    private UserNodeRepository userNodeRepository;
    @Autowired
    private UserService userService;

    @Test
    void listUsers_returnsAllUsers_whenNoKeyword(){
        List<UserDTO> users =  userService.listUsers("");
        assertEquals(3, users.size());
        assertEquals("mateja.test@vegait.rs", users.get(0).getEmail());
    }

    @Test
    public void listUsers_returnsOneUser_withAKeyword(){
        List<UserDTO> users =  userService.listUsers("mak");
        assertEquals(1, users.size());
        assertEquals("mateja.test1@vegait.rs", users.get(0).getEmail());
    }

    @Test
    void listUsers_returnsNoUsers_withAKeywordWithNoMatches(){
        List<UserDTO> users =  userService.listUsers("msadfasdak");
        assertEquals(0, users.size());
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
        assertNotEquals(userDTO.getPassword(), "password");
    }

    @Test
    void checkIfUserExists_returnsUser_existingId() {
        User user = userService.checkIfUserExists("test-id");
        assertEquals("admin", user.getUsername());
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
        UserDetails user = userService.loadUserByUsername("admin");
        assertEquals("$2a$12$.yaNGFMfd9ueDqT3LArDwOj6V0Ody4fMlteBIrYgJni0UnCx2gHfS", user.getPassword());
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
        assertEquals("Token is valid!", messageDTO.getMessage());
    }

    @Test
    void validateToken_returnsFailAnswer_tokenNonExistent() {
        MessageDTO messageDTO = userService.validatePasswordToken("non_existing_token");
        assertEquals("Token does not exist!", messageDTO.getMessage());
    }

    @Test
    void forgotPassword_returnsSuccess_emailExists() {
        MessageDTO messageDTO = userService.forgotPassword("mateja.test1@vegait.rs");
        assertEquals("Success!", messageDTO.getMessage());
    }

    @Test
    void forgotPassword_throwsException_emailDoesntExists() {
        assertThrows(IllegalArgumentException.class, ()->{
            userService.forgotPassword("doesntexist@gmail.com");
        });
    }
}
