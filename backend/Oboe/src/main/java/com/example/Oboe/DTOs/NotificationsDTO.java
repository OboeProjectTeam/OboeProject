package com.example.Oboe.DTOs;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationsDTO {

    private UUID notifiId;
    private UUID userID;
    private String textNotification;
    private boolean isRead;
    private LocalDateTime updateAt;

    private UUID targetId;       // target của thông báo (bài viết, nhóm,...)
    private String targetType;   // loại: "Post", "Comment", "Team"...

    public NotificationsDTO() {}

    public NotificationsDTO(UUID notifiId, UUID userID, String textNotification, boolean isRead,
                            LocalDateTime updateAt, UUID targetId, String targetType) {
        this.notifiId = notifiId;
        this.userID = userID;
        this.textNotification = textNotification;
        this.isRead = isRead;
        this.updateAt = updateAt;
        this.targetId = targetId;
        this.targetType = targetType;
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

    public UUID getTargetId() {
        return targetId;
    }

    public void setTargetId(UUID targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
}
