package com.kupipancongid.authjwt.util;

import com.kupipancongid.authjwt.model.JwtToken;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static String SECRET_KEY = "53crEt@text53crEt@text53crEt@text53crEt@text53crEt@text";

    private SecretKeySpec getSecretKeySpec(){
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), signatureAlgorithm.getJcaName());
        return secretKeySpec;
    }

    public String generateTokenString(JwtToken jwtToken){
        SecretKeySpec secretKeySpec = getSecretKeySpec();
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(jwtToken.getIssuer())
                .setSubject(jwtToken.getSubject())
                .setIssuedAt(jwtToken.getIssuedAt())
                .setExpiration(jwtToken.getExpireddAt())
                .signWith(secretKeySpec)
                .compact();
    }


    public Boolean isTokenValid(String token){
        return getSubjectFromToken(token) != null;
    }

    public String getSubjectFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getIssuerFromToken(String token)  {
        return getClaimFromToken(token, Claims::getIssuer);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            SecretKeySpec secretKeySpec = getSecretKeySpec();
            Claims claims = Jwts.parserBuilder().setSigningKey(secretKeySpec).build().parseClaimsJws(token).getBody();
            return claims;
        }catch (MalformedJwtException exception){
            throw  new MalformedJwtException("Token format invalid");
        }
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    public Boolean isTokenExpired(String token) {
        Date expireDate = null;
        try {
            expireDate = getExpirationDateFromToken(token);
            return expireDate.before(new Date());
        }catch (ExpiredJwtException exception){
            return true;
        }
    }
}
