package com.example.Oboe.DTOs;

import java.util.List;
import java.util.UUID;

public class FlashCardDto {

    private UUID flashcardID;
    private String term;
    private String description;
    private List<CardItemDto> cardItems;



    public void setFlashcardID(UUID flashcardID) {
        this.flashcardID = flashcardID;
    }
    public UUID getFlashcardID() {
        return flashcardID;
    }
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<CardItemDto> getCardItems() { return cardItems; }
    public void setCardItems(List<CardItemDto> cardItems) { this.cardItems = cardItems; }

}

