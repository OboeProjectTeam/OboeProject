package com.example.Oboe.DTOs;

import java.time.LocalDateTime;
import java.util.UUID;

public class AIBlogReplyDTO {

    private UUID id;
    private UUID blogId;
    private String content;
    private LocalDateTime createdAt;

    public AIBlogReplyDTO() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBlogId() {
        return blogId;
    }

    public void setBlogId(UUID blogId) {
        this.blogId = blogId;
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
