package com.example.Oboe.DTOs;

import java.util.List;
import java.util.UUID;

public class VocabularyDTOs {

    private UUID vocalbId;
    private String words;
    private String meanning;
    private String wordType;
    private String scriptType;
    private UUID kanjiId;
    private String vietnamese_pronunciation;

    public String getVietnamese_pronunciation() {
        return vietnamese_pronunciation;
    }

    public void setVietnamese_pronunciation(String vietnamese_pronunciation) {
        this.vietnamese_pronunciation = vietnamese_pronunciation;
    }




    private List<ReadingDTO> readings;

    // Getters & Setters
    public UUID getVocalbId() {
        return vocalbId;
    }

    public void setVocalbId(UUID vocalbId) {
        this.vocalbId = vocalbId;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getMeanning() {
        return meanning;
    }

    public void setMeanning(String meanning) {
        this.meanning = meanning;
    }

    public String getWordType() {
        return wordType;
    }

    public void setWordType(String wordType) {
        this.wordType = wordType;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public UUID getKanjiId() {
        return kanjiId;
    }

    public void setKanjiId(UUID kanjiId) {
        this.kanjiId = kanjiId;
    }

    public List<ReadingDTO> getReadings() {
        return readings;
    }

    public void setReadings(List<ReadingDTO> readings) {
        this.readings = readings;
    }
}
