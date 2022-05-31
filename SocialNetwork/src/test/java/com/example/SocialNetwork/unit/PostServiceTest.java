package com.example.SocialNetwork.unit;

import com.example.SocialNetwork.domain.PasswordResetToken;
import com.example.SocialNetwork.domain.Post;
import com.example.SocialNetwork.domain.Role;
import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.PostDTO;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.domain.enums.PostStatus;
import com.example.SocialNetwork.domain.enums.UserStatus;
import com.example.SocialNetwork.repository.PostRepository;
import com.example.SocialNetwork.repository.RoleRepository;
import com.example.SocialNetwork.repository.UserRepository;
import com.example.SocialNetwork.service.PostService;
import com.example.SocialNetwork.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
class PostServiceTest {

    @MockBean
    private PostRepository postRepository;
    @MockBean
    private UserService userService;
    @Autowired
    private PostService postService;

    @BeforeEach
    void setup(){
        User user1 = new User("user1@gmail.com",
                "password",
                "User",
                "1",
                "user1");

        User user2 = new User("user1@gmail.com",
                "password",
                "User",
                "1",
                "user1");

        user1.addFriend(user2);

        Post post1 = new Post(
                "created post",
                "created post",
                user1
        );
        var currentDate = new Date();
        var futureDate = new Date(currentDate.getTime() + 5*60000);
        post1.setCreatedDate(futureDate);

        Post post2 = new Post(
                "created post2",
                "created post2",
                user2
        );
        post2.setCreatedDate(new Date());
        Post post3 = new Post(
                "created post3",
                "created post3",
                user1
        );
        List<Post> postsAll = new ArrayList<>();
        postsAll.add(post1);
        postsAll.add(post2);
        postsAll.add(post3);

        List<Post> postsByUser1 = new ArrayList<>();
        postsByUser1.add(post1);

        List<Post> postsByUser2 = new ArrayList<>();
        postsByUser2.add(post2);


        given(userService.checkIfUserExists("1")).willReturn(user1);
        given(userService.checkIfUserExists("non_existing")).willThrow(new IllegalArgumentException("User with id: non_existing doesn't exist!"));
        given(postRepository.save(any(Post.class))).willReturn(post1);
        given(postRepository.findById("non_existing")).willReturn(Optional.empty());
        given(postRepository.findById("1")).willReturn(Optional.of(post1));

        given(postRepository.findAll()).willReturn(postsAll);
        given(postRepository.findByUser(user1)).willReturn(postsByUser1);
        given(postRepository.findByUser(user2)).willReturn(postsByUser2);


    }

    @Test
    void create_post_should_throw_exception_when_user_doesnt_exist() {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("neki_tekst");
        postDTO.setImage("neka_slika");
        postDTO.setUserId("non_existing");
        assertThrows(IllegalArgumentException.class, ()->{
            postService.create(postDTO);
        });
    }

    @Test
    void create_post_should_return_a_post_dto() {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("neki_tekst");
        postDTO.setImage("neka_slika");
        postDTO.setUserId("1");

        PostDTO returnDTO = postService.create(postDTO);
        assertEquals(returnDTO.getText(), "created post");
    }


    @Test
    void read_post_should_throw_exception_when_post_doesnt_exist() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.read("non_existing");
        });
    }

    @Test
    void read_post_should_return_post_dto_when_post_exists() {
        PostDTO returnDTO = postService.read("1");
        assertEquals(returnDTO.getText(), "created post");
    }

    @Test
    void update_invalid_id_should_throw_exception() {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("neki_tekst1");
        postDTO.setImage("neka_slika1");
        postDTO.setId("non_existing");
        assertThrows(IllegalArgumentException.class, ()->{
            postService.update(postDTO);
        });
    }

    @Test
    void update_valid_id_should_work() {
        PostDTO postDTO = new PostDTO();
        postDTO.setId("1");
        postDTO.setText("novi_tekst");
        PostDTO resultDTO = postService.update(postDTO);
        assertEquals(resultDTO.getText(), "novi_tekst");
    }

    @Test
    void hide_post_invalid_id_should_throw_exception() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.hidePost("non_existing");
        });
    }

    @Test
    void hide_post_valid_id_should_work() {
        PostDTO resultDTO = postService.hidePost("1");
        assertEquals(resultDTO.getStatus(), PostStatus.HIDDEN);
    }

    @Test
    void delete_invalid_id_should_throw_exception() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.delete("non_existing");
        });
    }

    @Test
    void list_all_posts_should_return_every_existing_post() {
        List<PostDTO> posts = postService.getAll();
        assertEquals(posts.size(), 3);
    }

    @Test
    void list_all_by_user_should_return_every_existing_post_from_user_if_id_is_valid() {
        List<PostDTO> posts = postService.getAllByUser("1");
        assertEquals(posts.size(), 1);
    }

    @Test
    void list_all_by_user_should_throw_exception_if_id_isnt_valid() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.getAllByUser("non_existing");
        });
    }

    @Test
    void list_all_for_user_should_return_every_existing_post_for_user_feed_if_id_is_valid() {
        List<PostDTO> posts = postService.getAllForUser("1");
        assertEquals(posts.size(), 2);
    }

    @Test
    void list_all_for_user_should_throw_exception_if_id_isnt_valid() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.getAllForUser("non_existing");
        });
    }
}
