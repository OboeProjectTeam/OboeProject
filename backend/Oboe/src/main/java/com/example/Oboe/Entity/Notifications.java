package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="Notifications")
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "NotifiId", updatable = false, nullable = false)
    private UUID NotifiId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("users-Notifications")
    private User user;
    private String text_notification;
    private boolean isRead;

    private LocalDateTime update_at = LocalDateTime.now();
    @PreUpdate
    public void preUpdate() {
        this.update_at = LocalDateTime.now();
    }



    public Notifications() {}
    public Notifications( User user, String text_notification, boolean isRead) {
        this.user = user;
        this.text_notification = text_notification;
        this.isRead = isRead;
    }

    public String getText_notification() {
        return text_notification;
    }

    public void setText_notification(String text_notification) {
        this.text_notification = text_notification;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public UUID getNotifiId() {
        return NotifiId;
    }

    public void setNotifiId(UUID notifiId) {
        NotifiId = notifiId;
    }

    public LocalDateTime getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(LocalDateTime update_at) {
        this.update_at = update_at;
    }

}
