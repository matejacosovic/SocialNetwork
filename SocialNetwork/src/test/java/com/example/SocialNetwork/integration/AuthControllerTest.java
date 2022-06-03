package com.example.SocialNetwork.integration;

import com.example.SocialNetwork.domain.dto.JwtAuthDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends AbstractControllerTest{

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
                .andExpect(content().string("Bad credentials"));

    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
