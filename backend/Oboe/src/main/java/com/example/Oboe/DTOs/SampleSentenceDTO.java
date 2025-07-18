package com.example.Oboe.DTOs;

import java.util.UUID;

public class SampleSentenceDTO {
    private UUID id;
    private String japaneseText;
    private String vietnameseMeaning;

    // Constructors
    public SampleSentenceDTO() {}

    public SampleSentenceDTO(UUID id, String japaneseText, String vietnameseMeaning) {
        this.id = id;
        this.japaneseText = japaneseText;
        this.vietnameseMeaning = vietnameseMeaning;
    }

    // Getters & Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getJapaneseText() {
        return japaneseText;
    }

    public void setJapaneseText(String japaneseText) {
        this.japaneseText = japaneseText;
    }

    public String getVietnameseMeaning() {
        return vietnameseMeaning;
    }

    public void setVietnameseMeaning(String vietnameseMeaning) {
        this.vietnameseMeaning = vietnameseMeaning;
    }
}
