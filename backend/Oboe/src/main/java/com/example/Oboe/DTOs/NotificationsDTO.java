package com.example.Oboe.DTOs;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationsDTO {

    private UUID notifiId;
    private UUID userID;
    private String textNotification;
    private boolean isRead;
    private LocalDateTime updateAt;



    private UUID contentId;

    public NotificationsDTO() {}

    public NotificationsDTO(UUID notifiId, UUID userID, String textNotification, boolean isRead, LocalDateTime updateAt) {
        this.notifiId = notifiId;
        this.userID = userID;
        this.textNotification = textNotification;
        this.isRead = isRead;
        this.updateAt = updateAt;
    }

    public UUID getNotifiId() {
        return notifiId;
    }

    public void setNotifiId(UUID notifiId) {
        this.notifiId = notifiId;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getTextNotification() {
        return textNotification;
    }

    public void setTextNotification(String textNotification) {
        this.textNotification = textNotification;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

}
