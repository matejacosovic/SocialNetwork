package com.example.SocialNetwork.integration;

import com.example.SocialNetwork.domain.dto.EmailDTO;
import com.example.SocialNetwork.domain.dto.PasswordDTO;
import com.example.SocialNetwork.domain.dto.UserDTO;
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
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UserControllerTest extends TestMapper {

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
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void listAllUsers_withAccessToken_returnsAllUsers() throws Exception {
        this.mvc.perform(get("/api/v1/users")
                        .header("tenant", "tenant1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].email", equalTo("mateja.test@vegait.rs")));

    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void listUsersWithKeyword_withAccessToken_returnsOneUser() throws Exception {
        this.mvc.perform(get("/api/v1/users")
                        .header("tenant", "tenant1")
                        .param("search", "mak"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", equalTo("mateja.test1@vegait.rs")));
    }

    @Test
    public void listUsers_invalidAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(get("/api/v1/users")
                        .header("tenant", "tenant1")
                        .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_APP_USER"})
    public void listUsers_wrongRoleAccessToken_statusIsForbidden() throws Exception {
        this.mvc.perform(get("/api/v1/users")
                       .header("tenant", "tenant1"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createUser_validRequestBody_returnsUserDto() throws Exception {

        UserDTO userForSavingDTO = new UserDTO(
                "doesntexist@gmail.com",
                "Password123!",
                "Valid",
                "Validic",
                "doesntexist"
        );

        this.mvc.perform(post("/api/v1/users")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userForSavingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", equalTo("doesntexist@gmail.com")));

    }

    @Test
    public void createUser_invalidRequestBody_statusIsBadRequest() throws Exception {

        UserDTO userForSavingDTO = new UserDTO(
                " ",
                "password",
                "Valid",
                "Validic",
                "doesntexist"
        );

        this.mvc.perform(post("/api/v1/users")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userForSavingDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.debugMessage", equalTo("Email is mandatory")));

    }

    @Test
    public void createUser_invalidEmailFormat_statusIsConflict() throws Exception {

        UserDTO userForSavingDTO = new UserDTO(
                "invalidformat",
                "password",
                "Valid",
                "Validic",
                "doesntexist"
        );

        this.mvc.perform(post("/api/v1/users")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userForSavingDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.debugMessage", equalTo("Email isn't following the valid pattern!")));
    }

    @Test
    public void createUser_invalidPasswordFormat_statusIsConflict() throws Exception {

        UserDTO userForSavingDTO = new UserDTO(
                "valid@email.com",
                "password",
                "Valid",
                "Validic",
                "doesntexist"
        );

        this.mvc.perform(post("/api/v1/users")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userForSavingDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.debugMessage",
                        equalTo("Password must contain at least one upper case, one lower case, one digit, one special character and be eight characters long!")));
    }
    @Test
    public void createUser_emailAlreadyExists_throwsException() throws Exception {

        UserDTO userForSavingDTO = new UserDTO(
                "mateja.test1@vegait.rs",
                "password",
                "Valid",
                "Validic",
                "doesntexist"
        );

        this.mvc.perform(post("/api/v1/users")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userForSavingDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.debugMessage", equalTo("A user with this email already exists!")));
    }

    @Test
    public void createUser_usernameAlreadyExists_throwsException() throws Exception {
        UserDTO userForSavingDTO = new UserDTO(
                "mateja.test11@vegait.rs",
                "password",
                "Valid",
                "Validic",
                "admin"
        );

        this.mvc.perform(post("/api/v1/users")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userForSavingDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.debugMessage", equalTo("A user with this username already exists!")));
    }

    @Test
    public void forgotPassword_returnsSuccess_validEmail() throws Exception {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setUserEmail("mateja.test@vegait.rs");
        this.mvc.perform(post("/api/v1/users/forgotPassword")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(emailDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Success!")));
    }

    @Test
    public void forgotPassword_throwsException_invalidEmail() throws Exception {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setUserEmail("mateja.test11@vegait.rs");
        this.mvc.perform(post("/api/v1/users/forgotPassword")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(emailDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.debugMessage", equalTo("No user with the given email!")));
    }

    @Test
    public void changePassword_returnsFail_expiredToken() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO("expired", "novasifra");

        this.mvc.perform(post("/api/v1/users/changePassword")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Token is not valid!")));
    }

    @Test
    public void changePassword_returnsFail_nonExistingToken() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO("non_existing", "novasifra");

        this.mvc.perform(post("/api/v1/users/changePassword")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Token is not valid!")));
    }

    @Test
    public void changePassword_returnsSuccess_validToken() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO("valid", "novasifra");

        this.mvc.perform(post("/api/v1/users/changePassword")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Successfully changed password!")));
    }

    @Test
    public void changePassword_invalidReqeustBody_statusIsBadRequest() throws Exception {
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setToken("valid");
        this.mvc.perform(post("/api/v1/users/changePassword")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.debugMessage", equalTo("New password is mandatory")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void deactivateUser_withAccessTokenAndValidUserId_deactivatesUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId("test-id");
        this.mvc.perform(put("/api/v1/users/deactivate")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo("DEACTIVATED")))
                .andExpect(jsonPath("$.email", equalTo("mateja.test@vegait.rs")));

    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void deactivateUser_withAccessTokenAndInvalidUserId_throwsException() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId("maka");
        this.mvc.perform(put("/api/v1/users/deactivate")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDTO))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.debugMessage", equalTo("User with id: maka doesn't exist!")));

    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_APP_USER"})
    public void deactivateUser_withWrongRoleAccessToken_statusIsForbidden() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId("maka");
        this.mvc.perform(put("/api/v1/users/deactivate")
                        .header("tenant", "tenant1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deactivateUser_invalidAccessToken_statusIsUnauthorized() throws Exception {
        this.mvc.perform(put("/api/v1/users/deactivate")
                        .header("tenant", "tenant1")
                        .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }

}
