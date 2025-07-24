package com.example.Oboe.Util;

import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthencation extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Qualifier("userService")
    private UserService userDetailsService;

    public JwtAuthencation(@Qualifier("userService") UserService userDetails, JwtUtil jwtUtil) {
        this.userDetailsService = userDetails;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        
        System.out.println("JWT Filter - Request URI: " + request.getRequestURI());
        System.out.println("JWT Filter - Auth Header: " + (authHeader != null ? "Bearer " + authHeader.substring(7, Math.min(authHeader.length(), 20)) + "..." : "null"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("JWT Filter - No valid auth header, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            final String username = jwtUtil.getUsernameFromToken(jwt);
            final String providerStr = jwtUtil.getProviderFromToken(jwt);
            
            System.out.println("JWT Filter - Extracted username: " + username);
            System.out.println("JWT Filter - Extracted provider: " + providerStr);

            if (username != null && providerStr != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                AuthProvider provider = AuthProvider.valueOf(providerStr);
                System.out.println("JWT Filter - Loading user: " + username + " with provider: " + provider);

                UserDetails userDetails = userDetailsService.loadUserByUsernameAndProvider(username, provider);
                System.out.println("JWT Filter - User loaded successfully: " + userDetails.getUsername());

                if (jwtUtil.validateToken(jwt)) {
                    System.out.println("JWT Filter - Token is valid, setting authentication");
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("JWT Filter - Token validation failed");
                }
            } else {
                System.out.println("JWT Filter - Missing username/provider or auth already exists");
            }

        } catch (Exception e) {
            System.out.println("JWT Filter - Exception occurred: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or malformed token: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}