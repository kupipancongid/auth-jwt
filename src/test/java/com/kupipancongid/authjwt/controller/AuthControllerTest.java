package com.kupipancongid.authjwt.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kupipancongid.authjwt.entity.User;
import com.kupipancongid.authjwt.model.JwtToken;
import com.kupipancongid.authjwt.model.request.LoginRequest;
import com.kupipancongid.authjwt.model.request.RegisterRequest;
import com.kupipancongid.authjwt.model.response.TokenResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;
    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception{
        RegisterRequest request = new RegisterRequest();
        request.setEmail("idkupipancong@gmail.com");
        request.setName("kupipancongid");
        request.setPassword("secret");
        request.setPasswordConfirmation("secret");

        mockMvc.perform(
            post("/api/register")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isOk()
        ).andDo(
                result -> {
                    WebResponse<String> response= objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });

                    assertEquals("OK", response.getData());
                }
        );
    }

    @Test
    void testRegisterBadRequest() throws Exception{
        RegisterRequest request = new RegisterRequest();
        request.setEmail("");
        request.setName("");
        request.setPassword("");
        request.setPasswordConfirmation("");

        mockMvc.perform(
                post("/api/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result -> {
                    WebResponse<String> response= objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });

                    assertNotNull(response.getErrors());
                }
        );
    }

    @Test
    void testLoginSuccess() throws Exception{
        User user = new User();
        user.setEmail("idkupipancong@gmail.com");
        user.setName("kupipancongid");
        user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("idkupipancong@gmail.com");
        request.setPassword("secret");

        mockMvc.perform(
                post("/api/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<TokenResponse> response= objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenResponse>>() {
                    });

                    assertNotNull(response.getData().getAccessToken());
                    assertNotNull(response.getData().getRefreshToken());
                    assertNull(response.getErrors());
                }
        );
    }

    @Test
    void testLoginFailedWrongCredentials() throws Exception{
        User user = new User();
        user.setEmail("idkupipancong@gmail.com");
        user.setName("kupipancongid");
        user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("idkupipancong@gmail.com");
        request.setPassword("wrong");

        mockMvc.perform(
                post("/api/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(
                result -> {
                    WebResponse<String> response= objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });

                    assertNotNull(response.getErrors());
                }
        );
    }

    @Test
    void testLogoutSuccess() throws Exception{
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
                delete("/api/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("access_token", user.getAccessToken())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });

                    assertEquals("OK", response.getData());
                    assertNull(response.getErrors());
                }
        );
    }

    @Test
    void testRefreshTokenSuccess() throws Exception{
        User user = new User();
        user.setEmail("idkupipancong@gmail.com");
        user.setName("kupipancongid");
        user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
        userRepository.save(user);
        JwtToken refreshToken = new JwtToken(
                user.getId(),
                user.getName(),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()+10_000L)
        );
        user.setRefreshToken(jwtUtil.generateTokenString(refreshToken));
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("idkupipancong@gmail.com");
        request.setPassword("secret");

        mockMvc.perform(
                post("/api/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("refresh_token", user.getRefreshToken())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<TokenResponse> response= objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenResponse>>() {
                    });

                    assertNotNull(response.getData().getAccessToken());
                    assertNotNull(response.getData().getRefreshToken());
                    assertNotEquals(response.getData().getRefreshToken(), user.getRefreshToken());
                    assertNull(response.getErrors());
                }
        );
    }

    @Test
    void testRefreshTokenFailedExpired() throws Exception{
        User user = new User();
        user.setEmail("idkupipancong@gmail.com");
        user.setName("kupipancongid");
        user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
        userRepository.save(user);
        JwtToken refreshToken = new JwtToken(
                user.getId(),
                user.getName(),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()-10_000L)
        );
        user.setRefreshToken(jwtUtil.generateTokenString(refreshToken));
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("idkupipancong@gmail.com");
        request.setPassword("secret");

        mockMvc.perform(
                post("/api/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("refresh_token", user.getRefreshToken())
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(
                result -> {
                    WebResponse<TokenResponse> response= objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenResponse>>() {
                    });

                    assertNull(response.getData());
                    assertNotNull(response.getErrors());
                    assertEquals("Unauthorized", response.getErrors());
                }
        );
    }

    @Test
    void testRefreshTokenFailedBlank() throws Exception{
        User user = new User();
        user.setEmail("idkupipancong@gmail.com");
        user.setName("kupipancongid");
        user.setPassword(BCrypt.hashpw("secret", BCrypt.gensalt()));
        userRepository.save(user);
        JwtToken refreshToken = new JwtToken(
                user.getId(),
                user.getName(),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()-10_000L)
        );
        user.setRefreshToken(jwtUtil.generateTokenString(refreshToken));
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setEmail("idkupipancong@gmail.com");
        request.setPassword("secret");

        mockMvc.perform(
                post("/api/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("refresh_token", "")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(
                result -> {
                    WebResponse<TokenResponse> response= objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenResponse>>() {
                    });

                    assertNull(response.getData());
                    assertNotNull(response.getErrors());
                    assertEquals("Unauthorized", response.getErrors());
                }
        );
    }
}