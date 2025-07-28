package com.example.Oboe.DTOs;

import java.util.List;
import java.util.UUID;

public class KanjiDTOs {
    private UUID kanjiId;
    private String characterName;
    private String meaning;
    private String strokes;
    private String VietnamesePronunciation;


    // Constructors
    public KanjiDTOs() {}

    public KanjiDTOs(UUID kanjiId, String characterName, String meaning, String strokes, String VietnamesePronunciation) {
        this.kanjiId = kanjiId;
        this.characterName = characterName;
        this.meaning = meaning;
        this.strokes = strokes;
        this.VietnamesePronunciation = VietnamesePronunciation;

    }

    // Getters and Setters
    public UUID getKanjiId() {
        return kanjiId;
    }

    public void setKanjiId(UUID kanjiId) {
        this.kanjiId = kanjiId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getStrokes() {
        return strokes;
    }

    public void setStrokes(String strokes) {
        this.strokes = strokes;
    }

    public String getVietnamesePronunciation() {
        return VietnamesePronunciation;
    }

    public void setVietnamesePronunciation(String vietnamesePronunciation) {
        VietnamesePronunciation = vietnamesePronunciation;
    }
}
