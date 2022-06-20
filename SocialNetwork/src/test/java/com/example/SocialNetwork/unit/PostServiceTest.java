package com.example.SocialNetwork.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.example.SocialNetwork.domain.Post;
import com.example.SocialNetwork.domain.User;
import com.example.SocialNetwork.domain.dto.PostDTO;
import com.example.SocialNetwork.domain.enums.PostStatus;
import com.example.SocialNetwork.repository.PostRepository;
import com.example.SocialNetwork.repository.UserNodeRepository;
import com.example.SocialNetwork.service.PostService;
import com.example.SocialNetwork.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
class PostServiceTest {

    @MockBean
    private PostRepository postRepository;
    @MockBean
    private UserService userService;

    @MockBean
    private UserNodeRepository userNodeRepository;
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

        given(userNodeRepository.findUserFriendsByUsername("user1")).willReturn(List.of());

        given(userService.findUser("user1")).willReturn(user1);
        given(userService.findUser("non_existing")).willThrow(new IllegalArgumentException("User with id: non_existing doesn't exist!"));
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
    void createPost_throwsException_userDoesntExist() {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("neki_tekst");
        postDTO.setImage("neka_slika");
        assertThrows(IllegalArgumentException.class, ()->{
            postService.create(postDTO, "non_existing");
        });
    }

    @Test
    void createPost_returnsPostDto_validUser() {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("neki_tekst");
        postDTO.setImage("neka_slika");

        PostDTO returnDTO = postService.create(postDTO, "user1");
        assertEquals("created post", returnDTO.getText());
    }


    @Test
    void readPost_throwsException_invalidPostId() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.read("non_existing");
        });
    }

    @Test
    void readPost_returnsPostDto_validPostId() {
        PostDTO returnDTO = postService.read("1");
        assertEquals("created post", returnDTO.getText());
    }

    @Test
    void updatePost_throwsException_invalidPostId() {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("neki_tekst1");
        postDTO.setImage("neka_slika1");
        postDTO.setId("non_existing");
        assertThrows(IllegalArgumentException.class, ()->{
            postService.update(postDTO);
        });
    }

    @Test
    void updatePost_returnsUpdatedPostDto_validPostId() {
        PostDTO postDTO = new PostDTO();
        postDTO.setId("1");
        postDTO.setText("novi_tekst");
        PostDTO resultDTO = postService.update(postDTO);
        assertEquals("novi_tekst", resultDTO.getText());
    }

    @Test
    void hidePost_throwsException_invalidPostId() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.hidePost("non_existing");
        });
    }

    @Test
    void hidePost_returnsHiddenPostDto_validPostId() {
        PostDTO resultDTO = postService.hidePost("1");
        assertEquals(PostStatus.HIDDEN, resultDTO.getStatus());
    }

    @Test
    void deletePost_throwsException_invalidPostId() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.delete("non_existing");
        });
    }

    @Test
    void listAllPosts_returnsAllExistingPosts() {
        List<PostDTO> posts = postService.getAll();
        assertEquals(3, posts.size());
    }

    @Test
    void listAllByUser_returnsEveryUserPost_validId() {
        List<PostDTO> posts = postService.getAllByUser("1");
        assertEquals(1, posts.size());
    }

    @Test
    void listAllByUser_throwsException_invalidId() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.getAllByUser("non_existing");
        });
    }

    @Test
    void listAllForUser_returnsAllPostsForUserFeed_validId() {
        List<PostDTO> posts = postService.getAllForUser("user1");
        assertEquals(1, posts.size());
    }

    @Test
    void listAllForUser_throwsException_invalidId() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.getAllForUser("non_existing");
        });
    }
}
