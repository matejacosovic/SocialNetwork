package com.example.SocialNetwork.integration;

import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.domain.enums.UserStatus;
import com.example.SocialNetwork.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    void list_users_should_return_all_users_when_keyword_isnt_present(){
        List<UserDTO> users =  userService.listUsers("");
        assertEquals(users.size(), 2);
        assertEquals(users.get(0).getEmail(), "mateja.test@vegait.rs");
    }

    @Test
    void list_users_should_return_one_user_when_keyword_is_present(){
        List<UserDTO> users =  userService.listUsers("mak");
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getEmail(), "mateja.test1@vegait.rs");
    }

    @Test
    void list_users_should_return_no_users_when_keyword_is_present_but_no_matches(){
        List<UserDTO> users =  userService.listUsers("msadfasdak");
        assertEquals(users.size(), 0);
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
        userForSavingDTO.setEmail("mateja.test1@vegait.rs");

        assertThrows(IllegalArgumentException.class, ()->{
            userService.create(userForSavingDTO);
        });
    }

    @Test
    void create_user_exist_should_save_when_valid_dto_is_sent() {
        List<UserDTO> usersBeforeAdd =  userService.listUsers("");
        int sizeBeforeAdd = usersBeforeAdd.size();

        UserDTO userForSavingDTO = new UserDTO();
        userForSavingDTO.setEmail("doesntexist@gmail.com");
        userForSavingDTO.setPassword("password");
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
}
