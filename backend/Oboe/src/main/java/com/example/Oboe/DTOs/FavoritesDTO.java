package com.example.Oboe.DTOs;

import java.util.UUID;

import java.time.LocalDate;

public class FavoritesDTO {
    private UUID favoritesId;
    private String title;
    private String content;
    private LocalDate favoritesAt;
    private UUID userId;
    private UUID grammaId;
    private UUID kanjiId;

    private UUID sampleSentenceId;
    private UUID vocabularyId;
    //gọi DTo của Kanji để trả ve dữ liệu
    private String type;
    // Constructors
    public FavoritesDTO() {}

    public FavoritesDTO(UUID favoritesId, String title, String content, LocalDate favoritesAt,
                        UUID userId, UUID grammaId, UUID kanjiId, UUID sampleSentenceId, UUID vocabularyId) {
        this.favoritesId = favoritesId;
        this.title = title;
        this.content = content;
        this.favoritesAt = favoritesAt;
        this.userId = userId;
        this.grammaId = grammaId;
        this.kanjiId = kanjiId;
        this.sampleSentenceId = sampleSentenceId;
        this.vocabularyId = vocabularyId;
    }
    // Getters and Setters
    public UUID getFavoritesId() {
        return favoritesId;
    }

    public void setFavoritesId(UUID favoritesId) {
        this.favoritesId = favoritesId;
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

    public LocalDate getFavoritesAt() {
        return favoritesAt;
    }

    public void setFavoritesAt(LocalDate favoritesAt) {
        this.favoritesAt = favoritesAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getGrammaId() {
        return grammaId;
    }

    public void setGrammaId(UUID grammaId) {
        this.grammaId = grammaId;
    }

    public UUID getKanjiId() {
        return kanjiId;
    }

    public void setKanjiId(UUID kanjiId) {
        this.kanjiId = kanjiId;
    }


    public UUID getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(UUID vocabularyId) {
        this.vocabularyId = vocabularyId;
    }
   public String getType() {
        return type;
   }
   public void setType(String type) {
        this.type = type;
   }

    public UUID getSampleSentenceId() {
        return sampleSentenceId;
    }

    public void setSampleSentenceId(UUID sampleSentenceId) {
        this.sampleSentenceId = sampleSentenceId;
    }


}

