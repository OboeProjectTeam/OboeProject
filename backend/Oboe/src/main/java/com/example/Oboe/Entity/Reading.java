package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "reading")
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "readingid", updatable = false, nullable = false)
    private UUID readingID;

    @Column(name = "reading_text", nullable = false)
    private String readingText;  // Cách đọc thực tế

    @Column(name = "reading_type", nullable = false)
    private String readingType;  // Loại đọc: onyomi, kunyomi, nanori, hiragana, katakana, grammar,...

    @Column(name = "owner_type", nullable = false)
    private String ownerType;    // Bảng cha: "kanji", "vocabulary", "gramma"

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;        // ID từ bảng cha (UUID của Kanji, Vocabulary hoặc Gramma)

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
