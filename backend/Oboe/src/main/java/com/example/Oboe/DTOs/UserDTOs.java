package com.example.Oboe.DTOs;

import com.example.Oboe.Entity.AccountType;
import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserDTOs {

    private String userName;              // Có thể là email, sdt hoặc username
    private String passWord;              // Chỉ sử dụng khi đăng nhập bằng EMAIL
    private String lastName;
    private String firstName;
    private LocalDate day_of_birth;
    private String address;
    private String avatar;
    private String providerId;

    private Role role = Role.ROLE_USER;   // Mặc định là USER
    private boolean verified = false;     // Mặc định chưa xác minh
    private AccountType accountType = AccountType.FREE; // Mặc định tài khoản FREE
    private LocalDateTime create_at = LocalDateTime.now();
    private LocalDateTime update_at = LocalDateTime.now();


    private AuthProvider authProvider = AuthProvider.EMAIL; // Mặc định là EMAIL


    public String getUserName() {
        return userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDate getDay_of_birth() {
        return day_of_birth;
    }

    public void setDay_of_birth(LocalDate day_of_birth) {
        this.day_of_birth = day_of_birth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

}
