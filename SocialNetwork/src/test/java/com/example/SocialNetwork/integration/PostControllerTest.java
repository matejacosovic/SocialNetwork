package com.example.SocialNetwork.integration;


import com.example.SocialNetwork.domain.dto.JwtAuthDTO;
import com.example.SocialNetwork.domain.dto.PostDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.pro.packaged.P;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PostControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void setup(){
        this.mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }


    @Test
    public void createPost_noAccessToken_statusIsUnauthorized() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("some_text");
        postDTO.setImage("some_img");

        this.mvc.perform(post("/api/v1/posts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void createPost_withAccessToken_statusIsOk() throws Exception {
        String accessToken = obtainAccessToken("user", "password");

        PostDTO postDTO = new PostDTO();
        postDTO.setText("some_text");
        postDTO.setImage("some_img");

        this.mvc.perform(post("/api/v1/posts")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", equalTo("some_text")));
    }

    @Test
    public void readPost_noAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(get("/api/v1/posts/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void readPost_withAccessTokenAndValidPostId_returnsPost() throws Exception {
        String accessToken = obtainAccessToken("user", "password");

        this.mvc.perform(get("/api/v1/posts/test-post1")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", equalTo("test text1")));
    }

    @Test
    public void readPost_withAccessTokenAndInvalidPostId_throwsException() throws Exception {
        String accessToken = obtainAccessToken("user", "password");

        this.mvc.perform(get("/api/v1/posts/14")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isConflict())
                .andExpect(content().string("There is no post with the given id: 14"));
    }

    @Test
    public void updatePost_noAccessToken_statusIsUnauthorized() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setText("some_text");
        postDTO.setImage("some_img");

        this.mvc.perform(put("/api/v1/posts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updatePost_withAccessTokenAndValidPostId_returnsUpdatedPost() throws Exception {
        String accessToken = obtainAccessToken("user", "password");

        PostDTO postDTO = new PostDTO();
        postDTO.setText("neki_tekst");
        postDTO.setImage("neka_slika");
        postDTO.setId("test-post1");

        this.mvc.perform(put("/api/v1/posts")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", equalTo("neki_tekst")));
    }

    @Test
    public void updatePost_withAccessTokenAndInvalidPostId_throwsException() throws Exception {
        String accessToken = obtainAccessToken("user", "password");

        PostDTO postDTO = new PostDTO();
        postDTO.setText("neki_tekst");
        postDTO.setImage("neka_slika");
        postDTO.setId("test-post1241");

        this.mvc.perform(put("/api/v1/posts")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("There is no post with the given id: test-post1241"));
    }

    @Test
    public void deletePost_noAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(delete("/api/v1/posts/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deletePost_withAccessTokenAndValidPostId_statusIsOk() throws Exception {
        String accessToken = obtainAccessToken("user", "password");

        this.mvc.perform(delete("/api/v1/posts/test-post1")
                        .header("Authorization", "Bearer " + accessToken)
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", equalTo("test text1")));
    }

    @Test
    public void deletePost_withAccessTokenAndInvalidPostId_throwsException() throws Exception {
        String accessToken = obtainAccessToken("user", "password");

        this.mvc.perform(delete("/api/v1/posts/13412")
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isConflict())
                .andExpect(content().string("There is no post with the given id: 13412"));
    }

    @Test
    public void getAll_withAccessToken_returnsAllPosts() throws Exception {
        String accessToken = obtainAccessToken("admin", "password");
        this.mvc.perform(get("/api/v1/posts")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].text", equalTo("test text1")));
    }

    @Test
    public void getAll_withWrongAccessToken_statusIsForbidden() throws Exception {
        String accessToken = obtainAccessToken("user", "password");

        this.mvc.perform(get("/api/v1/posts")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAll_noAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(get("/api/v1/posts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllByUser_withAccessTokenAndValidId_returnsAllUserPosts() throws Exception {
        String accessToken = obtainAccessToken("admin", "password");
        this.mvc.perform(get("/api/v1/posts/wall")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("userId", "test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].text", equalTo("test text1")));
    }

    @Test
    public void getAllByUser_withAccessTokenAndInvalidId_throwsException() throws Exception {
        String accessToken = obtainAccessToken("admin", "password");
        this.mvc.perform(get("/api/v1/posts/wall")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("userId", "adadadsmin"))
                .andExpect(status().isConflict())
                .andExpect(content().string("User with id: adadadsmin doesn't exist!"));
    }

    @Test
    public void getAllByUser_noAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(get("/api/v1/posts/wall"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getFeedForUser_withAccessToken_returnsFeedForUser() throws Exception {
        String accessToken = obtainAccessToken("admin", "password");
        this.mvc.perform(get("/api/v1/posts/feed")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].text", equalTo("test text1")));
    }

    @Test
    public void getFeedForUser_noAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(get("/api/v1/posts/feed"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void hidePost_noAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(put("/api/v1/posts/hide")
                        .param("postId", "adadadsmin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void hidePost_wrongAccessToken_statusIsForbidden() throws Exception {
        String accessToken = obtainAccessToken("user", "password");

        this.mvc.perform(put("/api/v1/posts/hide")
                        .param("postId", "adadadsmin")
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void hidePost_withAccessTokenAndInvalidPostId_throwsException() throws Exception {
        String accessToken = obtainAccessToken("admin", "password");

        this.mvc.perform(put("/api/v1/posts/hide")
                        .param("postId", "adadadsmin")
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isConflict())
                .andExpect(content().string("There is no post with the given id: adadadsmin"));
    }

    @Test
    public void hidePost_withAccessTokenAndValidPostId_hidesPost() throws Exception {
        String accessToken = obtainAccessToken("admin", "password");

        this.mvc.perform(put("/api/v1/posts/hide")
                        .param("postId", "test-post1")
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", equalTo("test text1")))
                .andExpect(jsonPath("$.status", equalTo("HIDDEN")));

    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String obtainAccessToken(String username, String password) throws Exception {
        JwtAuthDTO authDTO = new JwtAuthDTO();
        authDTO.setUsername(username);
        authDTO.setPassword(password);
        ResultActions result
                = mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authDTO)))
                .andExpect(status().isOk());

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("accessToken").toString();
    }
}
