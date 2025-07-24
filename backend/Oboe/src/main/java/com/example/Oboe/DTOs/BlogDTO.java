package com.example.Oboe.DTOs;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

public class BlogDTO {
    private UUID id;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private String tags;
    private String topics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID userId;
    private String author;



    private String avatarUrl;

    // Các trường mới:
    private int commentCount;

    public LocalDateTime getLatestCommentTime() {
        return latestCommentTime;
    }

    public void setLatestCommentTime(LocalDateTime latestCommentTime) {
        this.latestCommentTime = latestCommentTime;
    }

    public String getLatestCommenterName() {
        return latestCommenterName;
    }

    public void setLatestCommenterName(String latestCommenterName) {
        this.latestCommenterName = latestCommenterName;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    private String latestCommenterName;
    private LocalDateTime latestCommentTime;

    public BlogDTO() {}

    public BlogDTO(UUID id, String title, String content, String tags, String topics,
                   LocalDateTime createdAt, LocalDateTime updatedAt, UUID userId, String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.topics = topics;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.author = author;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }


    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
