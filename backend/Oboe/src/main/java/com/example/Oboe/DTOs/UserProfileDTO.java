package com.example.Oboe.DTOs;

import com.example.Oboe.Entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserProfileDTO {
    private UUID user_id;
    private String userName;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String address;
    private LocalDate day_of_birth;
    private AuthProvider authProvider;
    private boolean verified;
    private AccountType accountType;
    private Role role;
    private Status status;
    private LocalDateTime create_at;
    private LocalDateTime update_at;

    // Constructor
    public UserProfileDTO(User user) {
        this.user_id = user.getUser_id();
        this.userName = user.getUserName();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.avatarUrl = user.getAvatarUrl();
        this.address = user.getAddress();
        this.day_of_birth = user.getDay_of_birth();
        this.authProvider = user.getAuthProvider();
        this.verified = user.isVerified();
        this.accountType = user.getAccountType();
        this.role = user.getRole();
        this.create_at = user.getCreate_at();
        this.update_at = user.getUpdate_at();
        this.status = user.getStatus();
    }

    public UUID getUser_id() {
        return user_id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDay_of_birth() {
        return day_of_birth;
    }

    public void setDay_of_birth(LocalDate day_of_birth) {
        this.day_of_birth = day_of_birth;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreate_at() {
        return create_at;
    }

    public void setCreate_at(LocalDateTime create_at) {
        this.create_at = create_at;
    }

    public LocalDateTime getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(LocalDateTime update_at) {
        this.update_at = update_at;
    }
}
