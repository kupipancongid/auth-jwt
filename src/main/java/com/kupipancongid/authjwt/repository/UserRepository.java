package com.kupipancongid.authjwt.repository;

import com.kupipancongid.authjwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<User> findByAccessToken(String accessToken);
    Optional<User> findByRefreshToken(String refreshToken);
}
