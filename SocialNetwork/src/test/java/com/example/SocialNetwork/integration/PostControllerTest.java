package com.example.SocialNetwork.integration;


import com.example.SocialNetwork.domain.dto.PostDTO;
import com.example.SocialNetwork.repository.UserNodeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PostControllerTest extends TestMapper {

    @MockBean
    private UserNodeRepository userNodeRepository;
    @Autowired
    protected WebApplicationContext context;
    @Autowired
    protected MockMvc mvc;

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void createPost_invalidAccessToken_statusIsUnauthorized() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("some_text");
        postDTO.setImage("some_img");

        this.mvc.perform(post("/api/v1/posts")
                        .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void createPost_withAccessToken_statusIsOk() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("some_text");
        postDTO.setImage("some_img");

        this.mvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", equalTo("some_text")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void createPost_withAccessTokenInvalidRequestBody_statusIsBadRequest() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setImage("some_img");

        this.mvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.debugMessage", equalTo("Text is mandatory")));
    }

    @Test
    public void readPost_invalidAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(get("/api/v1/posts/1")
                        .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void readPost_withAccessTokenAndValidPostId_returnsPost() throws Exception {
        this.mvc.perform(get("/api/v1/posts/test-post1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", equalTo("test text1")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void readPost_withAccessTokenAndInvalidPostId_throwsException() throws Exception {
        this.mvc.perform(get("/api/v1/posts/14"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.debugMessage", equalTo("There is no post with the given id: 14")));
    }

    @Test
    public void updatePost_invalidAccessToken_statusIsUnauthorized() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("some_text");
        postDTO.setImage("some_img");

        this.mvc.perform(put("/api/v1/posts")
                        .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void updatePost_withAccessTokenAndValidPostId_returnsUpdatedPost() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("neki_tekst");
        postDTO.setImage("neka_slika");
        postDTO.setId("test-post1");

        this.mvc.perform(put("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", equalTo("neki_tekst")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void updatePost_withAccessTokenInvalidRequestBody_statusIsBadRequest() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setImage("some_img");

        this.mvc.perform(put("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.debugMessage", equalTo("Text is mandatory")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void updatePost_withAccessTokenAndInvalidPostId_throwsException() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("neki_tekst");
        postDTO.setImage("neka_slika");
        postDTO.setId("test-post1241");

        this.mvc.perform(put("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.debugMessage", equalTo("There is no post with the given id: test-post1241")));
    }

    @Test
    public void deletePost_invalidAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(delete("/api/v1/posts/1")
                        .header("Authorization", "Bearer invalidToken")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void deletePost_withAccessTokenAndValidPostId_statusIsOk() throws Exception {
        this.mvc.perform(delete("/api/v1/posts/test-post1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", equalTo("test text1")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void deletePost_withAccessTokenAndInvalidPostId_throwsException() throws Exception {
        this.mvc.perform(delete("/api/v1/posts/13412"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.debugMessage", equalTo("There is no post with the given id: 13412")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void getAll_withAccessToken_returnsAllPosts() throws Exception {
        this.mvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].text", equalTo("test text1")));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_APP_USER"})
    public void getAll_withWrongRoleAccessToken_statusIsForbidden() throws Exception {
        this.mvc.perform(get("/api/v1/posts"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAll_invalidAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(get("/api/v1/posts")
                        .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void getAllByUser_withAccessTokenAndValidId_returnsAllUserPosts() throws Exception {
        this.mvc.perform(get("/api/v1/posts/wall")
                        .param("userId", "test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].text", equalTo("test text1")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void getAllByUser_withAccessTokenAndInvalidId_throwsException() throws Exception {
        this.mvc.perform(get("/api/v1/posts/wall")
                        .param("userId", "adadadsmin"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.debugMessage", equalTo("User with id: adadadsmin doesn't exist!")));
    }

    @Test
    public void getAllByUser_invalidAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(get("/api/v1/posts/wall")
                        .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void getFeedForUser_withAccessToken_returnsFeedForUser() throws Exception {
        this.mvc.perform(get("/api/v1/posts/feed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].text", equalTo("test text1")));
    }

    @Test
    public void getFeedForUser_invalidAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(get("/api/v1/posts/feed")
                        .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void hidePost_invalidAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(put("/api/v1/posts/hide")
                        .param("postId", "adadadsmin")
                        .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_APP_USER"})
    public void hidePost_wrongRoleAccessToken_statusIsForbidden() throws Exception {

        PostDTO postDTO = new PostDTO();
        postDTO.setId("adadadsmin");

        this.mvc.perform(put("/api/v1/posts/hide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDTO))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void hidePost_withAccessTokenAndInvalidPostId_throwsException() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setId("adadadsmin");
        this.mvc.perform(put("/api/v1/posts/hide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDTO))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.debugMessage", equalTo("There is no post with the given id: adadadsmin")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void hidePost_withAccessTokenAndValidPostId_hidesPost() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setId("test-post1");
        this.mvc.perform(put("/api/v1/posts/hide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", equalTo("test text1")))
                .andExpect(jsonPath("$.status", equalTo("HIDDEN")));
    }

}
