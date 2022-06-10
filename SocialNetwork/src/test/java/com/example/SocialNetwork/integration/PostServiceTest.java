package com.example.SocialNetwork.integration;

import com.example.SocialNetwork.domain.dto.PostDTO;
import com.example.SocialNetwork.domain.enums.PostStatus;
import com.example.SocialNetwork.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Test
    void createPost_throwsException_userDoesntExist() {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("sometext");
        postDTO.setImage("someimg");
        assertThrows(IllegalArgumentException.class, ()->{
            postService.create(postDTO, "non_existing");
        });
    }

    @Test
    void createPost_returnsPostDto_validUser() {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("sometext");
        postDTO.setImage("someimg");

        PostDTO returnDTO = postService.create(postDTO, "admin");
        assertEquals("sometext", returnDTO.getText());
    }

    @Test
    void readPost_throwsException_invalidPostId() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.read("non_existing");
        });
    }

    @Test
    void readPost_returnsPostDto_validPostId() {
        PostDTO returnDTO = postService.read("test-post1");
        assertEquals("test text1", returnDTO.getText());
    }

    @Test
    void updatePost_throwsException_invalidPostId() {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("sometext");
        postDTO.setImage("someimg");
        postDTO.setId("non_existing");
        assertThrows(IllegalArgumentException.class, ()->{
            postService.update(postDTO);
        });
    }

    @Test
    void updatePost_returnsUpdatedPostDto_validPostId() {
        PostDTO postDTO = new PostDTO();
        postDTO.setId("test-post1");
        postDTO.setText("newtext");
        postService.update(postDTO);
        PostDTO updatedDTO = postService.read("test-post1");
        assertEquals("newtext", updatedDTO.getText());
    }

    @Test
    void hidePost_throwsException_invalidPostId() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.hidePost("non_existing");
        });
    }

    @Test
    void hidePost_returnsHiddenPostDto_validPostId() {
        postService.hidePost("test-post1");
        PostDTO updatedDTO = postService.read("test-post1");
        assertEquals(PostStatus.HIDDEN, updatedDTO.getStatus());
    }

    @Test
    void deletePost_throwsException_invalidPostId() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.delete("non_existing");
        });
    }

    @Test
    void deletePost_returnsDeletedPostDto_validPostId() {
        postService.delete("test-post1");
        List<PostDTO> allPosts = postService.getAll();
        assertEquals(2, allPosts.size());
    }

    @Test
    void listAllPosts_returnsAllExistingPosts() {
        List<PostDTO> posts = postService.getAll();
        assertEquals(3, posts.size());
    }

    @Test
    void listAllByUser_returnsEveryUserPost_validId() {
        List<PostDTO> posts = postService.getAllByUser("test-id");
        assertEquals(2, posts.size());
    }

    @Test
    void listAllByUser_throwsException_invalidId() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.getAllByUser("non_existing");
        });
    }

    @Test
    void listAllForUser_returnsAllPostsForUserFeed_validId() {
        List<PostDTO> posts = postService.getAllForUser("admin");
        assertEquals(3, posts.size());
    }

    @Test
    void listAllForUser_throwsException_invalidId() {
        assertThrows(IllegalArgumentException.class, ()->{
            postService.getAllForUser("non_existing");
        });
    }
}
