package com.example.Oboe.DTOs;

import java.util.List;
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
    private List<CardItemDto> cardItems;

    public List<CardItemDto> getCardItems() {
        return cardItems;
    }

    public void setCardItems(List<CardItemDto> cardItems) {
        this.cardItems = cardItems;
    }


    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    private String avatarUrl;

    public FlashcardSearchResultDTO(UUID flashcardId, String title, String authorName, long termCount ,String avatarUrl) {
        this.flashcardId = flashcardId;
        this.title = title;
        this.authorName = authorName;
        this.termCount = termCount;
        this.avatarUrl =avatarUrl;
    }
}