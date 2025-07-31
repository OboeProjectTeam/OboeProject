package com.example.Oboe.DTOs;

import java.util.UUID;

public class FlashcardSearchResultDTO {
    private UUID flashcardId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTermCount() {
        return termCount;
    }

    public void setTermCount(Long termCount) {
        this.termCount = termCount;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public UUID getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(UUID flashcardId) {
        this.flashcardId = flashcardId;
    }

    private String title;
    private String authorName;
    private Long termCount;

    public FlashcardSearchResultDTO(UUID flashcardId, String title, String authorName, long termCount) {
        this.flashcardId = flashcardId;
        this.title = title;
        this.authorName = authorName;
        this.termCount = termCount;
    }
}
