package com.example.SocialNetwork.integration;

import com.example.SocialNetwork.domain.dto.JwtAuthDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthControllerTest extends TestMapper {

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
    public void login_validCredentials_statusOk() throws Exception {
        JwtAuthDTO authDTO = new JwtAuthDTO();
        authDTO.setUsername("admin");
        authDTO.setPassword("password");
        this.mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authDTO)))
                .andExpect(status().isOk());

    }

    @Test
    public void login_invalidCredentials_statusIsUnauthorized() throws Exception {
        JwtAuthDTO authDTO = new JwtAuthDTO();
        authDTO.setUsername("admin");
        authDTO.setPassword("passworad");
        this.mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.debugMessage", equalTo("Bad credentials")));

    }


}
