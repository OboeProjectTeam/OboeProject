package com.example.Oboe.DTOs;

import java.time.LocalDateTime;
import java.util.UUID;

public class AICommentReplyDTO {

    private UUID id;
    private UUID commentId;
    private String content;
    private LocalDateTime createdAt;

    public AICommentReplyDTO() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCommentId() {
        return commentId;
    }

    public void setCommentId(UUID commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
