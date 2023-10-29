package com.kupipancongid.authjwt.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kupipancongid.authjwt.entity.User;
import com.kupipancongid.authjwt.model.JwtToken;
import com.kupipancongid.authjwt.model.response.WebResponse;
import com.kupipancongid.authjwt.repository.UserRepository;
import com.kupipancongid.authjwt.security.BCrypt;
import com.kupipancongid.authjwt.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    @Test
    void testWelcomeMessageWithoutToken() throws Exception{
        mockMvc.perform(
                get("/api/dashboard")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });

                    assertEquals(response.getData(), "Hello World! Welcome to our website.");
                    assertNull(response.getErrors());
                }
        );
    }

    @Test
    void testWelcomeMessageWithBlankToken() throws Exception{
        mockMvc.perform(
                get("/api/dashboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("access_token", "")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });

                    assertEquals(response.getData(), "Hello World! Welcome to our website.");
                    assertNull(response.getErrors());
                }
        );
    }

    @Test
    void testWelcomeMessageWithInvalidToken() throws Exception{
        mockMvc.perform(
                get("/api/dashboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("access_token", "xxx")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });

                    assertEquals(response.getData(), "Hello World! Welcome to our website.");
                    assertNull(response.getErrors());
                }
        );
    }

    @Test
    void testWelcomeMessageWithValidToken() throws Exception{
        User user = new User();
        user.setEmail("idkupipancong@gmail.com");
        user.setName("kupipancongid");
        user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
        userRepository.save(user);
        JwtToken accessToken = new JwtToken(
                user.getId(),
                user.getName(),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()+10_000L)
                );
        user.setAccessToken(jwtUtil.generateTokenString(accessToken));
        userRepository.save(user);

        mockMvc.perform(
                get("/api/dashboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("access_token", user.getAccessToken())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });

                    assertEquals("Hello, "+user.getName(), response.getData());
                    assertNull(response.getErrors());
                }
        );
    }

    @Test
    void testWelcomeMessageWithExpiredValidToken() throws Exception{
        User user = new User();
        user.setEmail("idkupipancong@gmail.com");
        user.setName("kupipancongid");
        user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
        userRepository.save(user);
        JwtToken accessToken = new JwtToken(
                user.getId(),
                user.getName(),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()-10_000L)
        );
        user.setAccessToken(jwtUtil.generateTokenString(accessToken));
        userRepository.save(user);

        mockMvc.perform(
                get("/api/dashboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("access_token", user.getAccessToken())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });

                    assertEquals(response.getData(), "Hello World! Welcome to our website.");
                    assertNull(response.getErrors());
                }
        );
    }

}