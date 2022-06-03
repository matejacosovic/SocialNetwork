package com.example.SocialNetwork.integration;

import com.example.SocialNetwork.domain.dto.PasswordDTO;
import com.example.SocialNetwork.domain.dto.UserDTO;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class UserControllerTest extends AbstractControllerTest {

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void listAllUsers_withAccessToken_returnsAllUsers() throws Exception {
        this.mvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].email", equalTo("mateja.test@vegait.rs")));

    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void listUsersWithKeyword_withAccessToken_returnsOneUser() throws Exception {
        this.mvc.perform(get("/api/v1/users")
                        .param("search", "mak"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", equalTo("mateja.test1@vegait.rs")));
    }

    @Test
    public void listUsers_invalidAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_APP_USER"})
    public void listUsers_wrongRoleAccessToken_statusIsForbidden() throws Exception {
        this.mvc.perform(get("/api/v1/users"))
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
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void deactivateUser_withAccessTokenAndValidUserId_deactivatesUser() throws Exception {
        this.mvc.perform(put("/api/v1/users/deactivate")
                        .param("userId", "test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo("DEACTIVATED")))
                .andExpect(jsonPath("$.email", equalTo("mateja.test@vegait.rs")));

    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void deactivateUser_withAccessTokenAndInvalidUserId_throwsException() throws Exception {
        this.mvc.perform(put("/api/v1/users/deactivate")
                        .param("userId", "mak")
                )
                .andExpect(status().isConflict())
                .andExpect(content().string("User with id: mak doesn't exist!"));

    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_APP_USER"})
    public void deactivateUser_withWrongRoleAccessToken_statusIsForbidden() throws Exception {
        this.mvc.perform(put("/api/v1/users/deactivate")
                        .param("userId", "maka"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deactivateUser_invalidAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(put("/api/v1/users/deactivate")
                        .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }

}
