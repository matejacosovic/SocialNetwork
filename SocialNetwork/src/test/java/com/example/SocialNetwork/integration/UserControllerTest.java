package com.example.SocialNetwork.integration;

import com.example.SocialNetwork.controller.UserController;
import com.example.SocialNetwork.domain.dto.JwtAuthDTO;
import com.example.SocialNetwork.domain.dto.PasswordDTO;
import com.example.SocialNetwork.domain.dto.UserDTO;
import com.example.SocialNetwork.domain.enums.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {

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
    public void listAllUsers_withAccessToken_returnsAllUsers() throws Exception {
        String accessToken = obtainAccessToken("admin", "password");

        this.mvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].email", equalTo("mateja.test@vegait.rs")));

    }

    @Test
    public void listUsersWithKeyword_withAccessToken_returnsOneUser() throws Exception {
        String accessToken = obtainAccessToken("admin", "password");

        this.mvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("search", "mak"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", equalTo("mateja.test1@vegait.rs")));
    }

    @Test
    public void listUsers_noAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void listUsers_wrongAccessToken_statusIsForbidden() throws Exception {
        String accessToken = obtainAccessToken("user", "password");

        this.mvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createUser_validRequestBody_returnsUserDto() throws Exception {

        UserDTO userForSavingDTO = new UserDTO();
        userForSavingDTO.setEmail("doesntexist@gmail.com");
        userForSavingDTO.setPassword("password");
        userForSavingDTO.setName("Valid");
        userForSavingDTO.setSurname("Validic");
        userForSavingDTO.setUsername("doesntexist");

        this.mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userForSavingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", equalTo("doesntexist@gmail.com")));

    }

    @Test
    public void createUser_emailAlreadyExists_throwsException() throws Exception {

        UserDTO userForSavingDTO = new UserDTO();
        userForSavingDTO.setEmail("mateja.test1@vegait.rs");
        userForSavingDTO.setPassword("password");
        userForSavingDTO.setName("Valid");
        userForSavingDTO.setSurname("Validic");
        userForSavingDTO.setUsername("doesntexist");

        this.mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userForSavingDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("A user with this email already exists!"));

    }

    @Test
    public void createUser_usernameAlreadyExists_throwsException() throws Exception {

        UserDTO userForSavingDTO = new UserDTO();
        userForSavingDTO.setEmail("mateja.test11@vegait.rs");
        userForSavingDTO.setPassword("password");
        userForSavingDTO.setName("Valid");
        userForSavingDTO.setSurname("Validic");
        userForSavingDTO.setUsername("admin");

        this.mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userForSavingDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("A user with this username already exists!"));

    }

    @Test
    public void forgotPassword_returnsSuccess_validEmail() throws Exception {

        this.mvc.perform(post("/api/v1/users/forgotPassword")
                        .param("email", "mateja.test1@vegait.rs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Success!")));
    }

    @Test
    public void forgotPassword_throwsException_invalidEmail() throws Exception {

        this.mvc.perform(post("/api/v1/users/forgotPassword")
                        .param("email", "mateja.test11@vegait.rs"))
                .andExpect(status().isConflict())
                .andExpect(content().string("No user with the given email!"));
    }

    @Test
    public void changePassword_returnsFail_expiredToken() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO("expired", "novasifra");

        this.mvc.perform(post("/api/v1/users/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Token is not valid!")));
    }

    @Test
    public void changePassword_returnsFail_nonExistingToken() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO("non_existing", "novasifra");

        this.mvc.perform(post("/api/v1/users/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Token is not valid!")));
    }

    @Test
    public void changePassword_returnsSuccess_validToken() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO("valid", "novasifra");

        this.mvc.perform(post("/api/v1/users/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Successfully changed password!")));
    }


    @Test
    public void deactivateUser_withAccessTokenAndValidUserId_deactivatesUser() throws Exception {
        String accessToken = obtainAccessToken("admin", "password");
        this.mvc.perform(put("/api/v1/users/deactivate")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("userId", "test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo("DEACTIVATED")))
                .andExpect(jsonPath("$.email", equalTo("mateja.test@vegait.rs")));

    }

    @Test
    public void deactivateUser_withAccessTokenAndInvalidUserId_throwsException() throws Exception {
        String accessToken = obtainAccessToken("admin", "password");
        this.mvc.perform(put("/api/v1/users/deactivate")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("userId", "mak")
                        )
                .andExpect(status().isConflict())
                .andExpect(content().string("User with id: mak doesn't exist!"));

    }

    @Test
    public void deactivateUser_withWrongAccessToken_statusIsForbidden() throws Exception {
        String accessToken = obtainAccessToken("user", "password");
        this.mvc.perform(put("/api/v1/users/deactivate")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("userId", "maka"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deactivateUser_noAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(put("/api/v1/users/deactivate"))
                .andExpect(status().isUnauthorized());
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
