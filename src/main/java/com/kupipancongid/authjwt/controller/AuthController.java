package com.kupipancongid.authjwt.controller;

import com.kupipancongid.authjwt.model.request.LoginRequest;
import com.kupipancongid.authjwt.model.request.RegisterRequest;
import com.kupipancongid.authjwt.model.response.TokenResponse;
import com.kupipancongid.authjwt.model.response.WebResponse;
import com.kupipancongid.authjwt.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping(
            path = "/api/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(
            @RequestBody RegisterRequest request
            ){
        authenticationService.register(request);
        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }

    @PostMapping(
            path = "/api/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> login(
            @RequestBody LoginRequest request
            ){
        TokenResponse tokenResponse = authenticationService.login(request);
        return WebResponse.<TokenResponse>builder()
                .data(tokenResponse)
                .build();
    }
    @PostMapping(
            path = "/api/refresh",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> refresh(HttpServletRequest request){
        TokenResponse tokenResponse = authenticationService.refresh(request);
        return WebResponse.<TokenResponse>builder()
                .data(tokenResponse)
                .build();
    }



}
