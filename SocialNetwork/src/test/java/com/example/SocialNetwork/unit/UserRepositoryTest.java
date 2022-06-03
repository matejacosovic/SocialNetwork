package com.example.SocialNetwork.unit;

import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void searchUserNoKeyword_returnsAllUsers(){
        List<User> users =  userRepository.search("");
        assertEquals(users.size(), 3);
    }

    @Test
    void searchUserByKeyword_returnsOneUser(){
        List<User> users =  userRepository.search("mak");
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getEmail(), "mateja.test1@vegait.rs");
    }

    @Test
    void searchUserByKeyword_returnsEmptyList(){
        List<User> users =  userRepository.search("axcxa");
        assertEquals(users.size(), 0);
    }
}
