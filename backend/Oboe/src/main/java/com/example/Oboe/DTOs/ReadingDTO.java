package com.example.Oboe.DTOs;

import java.util.UUID;

public class ReadingDTO {
    private UUID readingID;
    private String readingText;
    private String readingType;
    private String ownerType;
    private UUID ownerId;

    // Constructors
    public ReadingDTO() {}

    public ReadingDTO(UUID readingID, String readingText, String readingType, String ownerType, UUID ownerId) {
        this.readingID = readingID;
        this.readingText = readingText;
        this.readingType = readingType;
        this.ownerType = ownerType;
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public UUID getReadingID() {
        return readingID;
    }

    public void setReadingID(UUID readingID) {
        this.readingID = readingID;
    }

    public String getReadingText() {
        return readingText;
    }

    public void setReadingText(String readingText) {
        this.readingText = readingText;
    }

    public String getReadingType() {
        return readingType;
    }

    public void setReadingType(String readingType) {
        this.readingType = readingType;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
}
