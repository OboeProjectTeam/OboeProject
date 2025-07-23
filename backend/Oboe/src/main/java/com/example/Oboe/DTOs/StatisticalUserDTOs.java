package com.example.Oboe.DTOs;

public class StatisticalUserDTOs {
    private long blogCount;
    private long commentCount;
    private long flashCard;

    public StatisticalUserDTOs() {}

    public StatisticalUserDTOs(long blogCount, long commentCount, long flashCard) {
        this.blogCount = blogCount;
        this.commentCount = commentCount;
        this.flashCard = flashCard;
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
    public long getFlashCard() {
        return flashCard;
    }

    public void setFlashCard(long flashCard) {
        this.flashCard = flashCard;
    }


}
