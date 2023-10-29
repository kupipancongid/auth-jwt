package com.kupipancongid.authjwt.controller;

import com.kupipancongid.authjwt.entity.User;
import com.kupipancongid.authjwt.model.response.WebResponse;
import com.kupipancongid.authjwt.service.AuthenticationService;
import com.kupipancongid.authjwt.service.MainService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    MainService mainService;



    @GetMapping(
            path = "/api/dashboard",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> index(HttpServletRequest request){
        String message = mainService.getWelcomeMessage(request);
        return WebResponse.<String>builder()
                .data(message)
                .build();
    }
}
