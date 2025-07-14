package com.example.Oboe.DTOs;

import java.util.List;

public class FlashCardDto {
    private String term;
    private String description;
    private List<CardItemDto> cardItems;

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<CardItemDto> getCardItems() { return cardItems; }
    public void setCardItems(List<CardItemDto> cardItems) { this.cardItems = cardItems; }
}
