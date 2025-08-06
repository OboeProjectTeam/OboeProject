package com.example.Oboe.Config;

import com.example.Oboe.Entity.AccountType;
import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.Status;
import com.example.Oboe.Entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {

        return user.getPassWord() != null ? user.getPassWord() : "";
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }
    public AuthProvider getAuthProvider() {
        return user.getAuthProvider();
    }
    public UUID getUserID() {
        return user.getUser_id();
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() {
        return user.getStatus() != null && user.getStatus() != Status.BAN;
    }
    public AccountType getAccountType() {
        return user.getAccountType();
    }
}

