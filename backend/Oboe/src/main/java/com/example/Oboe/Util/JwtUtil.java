package com.example.Oboe.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {


    private final String jwtSecret = "secretKey";

    // Thời gian sống của JWT: 1 ngày = 86400000 ms
    private final long jwtExpirationMs = 86400000;


    //  Tạo token với provider cụ thể (GOOGLE, FACEBOOK, EMAIL,...)
    public String generateToken(UserDetails userDetails, String provider) {
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", roles)
                .claim("provider", provider.toUpperCase())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }


    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }


    public String getRolesFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }


    public String getProviderFromToken(String token) {
        return parseClaims(token).get("provider", String.class);
    }


    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }
}