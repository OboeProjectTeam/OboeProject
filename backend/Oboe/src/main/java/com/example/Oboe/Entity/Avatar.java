package com.example.Oboe.Entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="Avatar")
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="avatarId")
    private UUID avatarId;
    private String avatarName;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-Avatar")
    private User user;
    private LocalDate updateAvatar = LocalDate.now();

    public Avatar() {}
    public Avatar(String avatarName, User user) {
        this.avatarName = avatarName;
        this.user = user;
    }

    public UUID getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(UUID avatarId) {
        this.avatarId = avatarId;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getUpdateAvatar() {
        return updateAvatar;
    }

    public void setUpdateAvatar(LocalDate updateAvatar) {
        this.updateAvatar = updateAvatar;
    }
}
