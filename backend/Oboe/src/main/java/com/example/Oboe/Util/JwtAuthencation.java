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

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            final String username = jwtUtil.getUsernameFromToken(jwt);
            final String providerStr = jwtUtil.getProviderFromToken(jwt);

            if (username != null && providerStr != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                AuthProvider provider = AuthProvider.valueOf(providerStr);

                UserDetails userDetails = userDetailsService.loadUserByUsernameAndProvider(username, provider);

                if (jwtUtil.validateToken(jwt)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or malformed token: " + e.getMessage());
            return;
        }


        filterChain.doFilter(request, response);
    }
}