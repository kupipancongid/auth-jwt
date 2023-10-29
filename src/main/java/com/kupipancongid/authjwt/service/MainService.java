package com.kupipancongid.authjwt.service;

import com.kupipancongid.authjwt.entity.User;
import com.kupipancongid.authjwt.repository.UserRepository;
import com.kupipancongid.authjwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MainService {
    @Autowired
    AuthenticationService authenticationService;


    public String getWelcomeMessage(HttpServletRequest request){
        String accessToken = request.getHeader("access_token");
        try {
            User user = authenticationService.getUserByAccessToken(accessToken);
            return "Hello, "+user.getName();
        }catch (Exception e){
            return "Hello World! Welcome to our website.";
        }
    }
}
