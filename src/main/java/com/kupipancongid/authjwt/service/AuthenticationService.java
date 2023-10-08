package com.kupipancongid.authjwt.service;

import com.kupipancongid.authjwt.entity.User;
import com.kupipancongid.authjwt.model.JwtToken;
import com.kupipancongid.authjwt.model.request.LoginRequest;
import com.kupipancongid.authjwt.model.request.RegisterRequest;
import com.kupipancongid.authjwt.model.response.TokenResponse;
import com.kupipancongid.authjwt.model.response.WebResponse;
import com.kupipancongid.authjwt.repository.UserRepository;
import com.kupipancongid.authjwt.security.BCrypt;
import com.kupipancongid.authjwt.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
public class AuthenticationService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ValidationService validationService;

    @Autowired
    private JwtUtil jwtUtil;
    public static final long ACCESS_TOKEN_EXPIRED_TIMEMILIS = 3_600_000L;
    public static final long REFRESH_TOKEN_EXPIRED_TIMEMILIS = 18_000_000L;

    @Transactional
    public TokenResponse login(LoginRequest request){
        validationService.validate(request);

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Login failed. wrong credentials.")
        );
        if (BCrypt.checkpw(request.getPassword(), user.getPassword())){
            Date accessTokenIssuedAt = new Date(System.currentTimeMillis());
            Date accessTokenExpiredAt = new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRED_TIMEMILIS);
            Date refreshTokenExpiredAt = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRED_TIMEMILIS);
            JwtToken jwtAccessToken = new JwtToken(user.getId(), user.getName(), accessTokenIssuedAt,accessTokenExpiredAt);
            String accessToken = jwtUtil.generateTokenString(jwtAccessToken);
            JwtToken jwtRefreshToken = new JwtToken(user.getId(), user.getName(), accessTokenIssuedAt,refreshTokenExpiredAt);
            String refreshToken = jwtUtil.generateTokenString(jwtRefreshToken);

            user.setAccessToken(accessToken);
            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login failed. wrong credentials.");
        }
    }

    @Transactional
    public void register(RegisterRequest request){
        validationService.validate(request);

        if (!userRepository.existsByEmail(request.getEmail())){
            if (!request.getPassword().equals(request.getPasswordConfirmation())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password do not match");
            }
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
            userRepository.save(user);
        }else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already taken.");
        }
    }

    @Transactional(readOnly = true)
    public User getUserByToken(String accessToken){
        if (accessToken==null){
            return null;
        }

        String userId = jwtUtil.getIssuerFromToken(accessToken);
        User user = userRepository.findById(userId).orElse(null);
        return user;
    }




}
