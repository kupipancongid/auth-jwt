package com.kupipancongid.authjwt.controller;

import com.kupipancongid.authjwt.entity.User;
import com.kupipancongid.authjwt.model.response.WebResponse;
import com.kupipancongid.authjwt.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    AuthenticationService authenticationService;

    @GetMapping(
            path = "/api/dashboard",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> index(User user){
        String message = "Hello World! Welcome to our website.";
        if (user != null){
            message = "Hello, "+user.getName();
        }
        return WebResponse.<String>builder()
                .data(message)
                .build();
    }
}
