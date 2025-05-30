package com.example.Oboe.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public class CommentDTOs {
    private UUID commentId;

    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    private LocalDateTime createdAt;

    // Thông tin user (không cần toàn bộ User object)
    private UUID userId;
    private String userName;

    // Thông tin blog (không cần toàn bộ Blog object)
    private UUID blogId;
    private String blogTitle;

    // Constructors
    public CommentDTOs() {}

    public CommentDTOs(UUID commentId, String title, String content, LocalDateTime createdAt,
                      UUID userId, String userName, UUID blogId, String blogTitle) {
        this.commentId = commentId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.userName = userName;
        this.blogId = blogId;
        this.blogTitle = blogTitle;
    }

    // Getters and Setters
    public UUID getCommentId() {
        return commentId;
    }

    public void setCommentId(UUID commentId) {
        this.commentId = commentId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UUID getBlogId() {
        return blogId;
    }

    public void setBlogId(UUID blogId) {
        this.blogId = blogId;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }
}