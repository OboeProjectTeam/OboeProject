package com.example.Oboe.DTOs;

public class StatisticalUserDTOs {
    private long blogCount;
    private long commentCount;
    private long quizCount;

    public StatisticalUserDTOs() {}

    public StatisticalUserDTOs(long blogCount, long commentCount, long quizCount) {
        this.blogCount = blogCount;
        this.commentCount = commentCount;
        this.quizCount = quizCount;
    }

    public long getBlogCount() {
        return blogCount;
    }

    public void setBlogCount(long blogCount) {
        this.blogCount = blogCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public long getQuizCount() {
        return quizCount;
    }

    public void setQuizCount(long quizCount) {
        this.quizCount = quizCount;
    }
}
