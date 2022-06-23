package com.example.SocialNetwork.unit;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void searchUserNoKeyword_returnsAllUsers(){
        List<User> users =  userRepository.search("");
        assertEquals(3, users.size());
    }

    @Test
    void searchUserByKeyword_returnsOneUser(){
        List<User> users =  userRepository.search("mak");
        assertEquals(1, users.size());
        assertEquals("mateja.test1@vegait.rs", users.get(0).getEmail());
    }

    @Test
    void searchUserByKeyword_returnsEmptyList(){
        List<User> users =  userRepository.search("axcxa");
        assertEquals(0, users.size());
    }
}
