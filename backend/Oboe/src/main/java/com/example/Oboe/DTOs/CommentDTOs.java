package com.example.Oboe.DTOs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommentDTOs {

    private UUID commentId;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private UUID userId;

    private String userName;

    private UUID commentIdParent;

    private UUID teamId; // đại diện cho blog, kanji, hoặc loại nội dung khác

    private List<CommentDTOs> replies = new ArrayList<>();

    // ======== Constructors =========
    public CommentDTOs() {}

    // ======== Getters and Setters =========

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

    public UUID getCommentIdParent() {
        return commentIdParent;
    }

    public void setCommentIdParent(UUID commentIdParent) {
        this.commentIdParent = commentIdParent;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public List<CommentDTOs> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentDTOs> replies) {
        this.replies = replies;
    }
}
