package com.example.Oboe.DTOs;

import java.util.List;
import java.util.UUID;

public class KanjiDTOs {
    private UUID kanjiId;
    private String characterName;
    private String meaning;
    private String strokes;
    private List<ReadingDTO> readings;

    // Constructors
    public KanjiDTOs() {}

    public KanjiDTOs(UUID kanjiId, String characterName, String meaning, String strokes, List<ReadingDTO> readings) {
        this.kanjiId = kanjiId;
        this.characterName = characterName;
        this.meaning = meaning;
        this.strokes = strokes;
        this.readings = readings;
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

    public List<ReadingDTO> getReadings() {
        return readings;
    }

    public void setReadings(List<ReadingDTO> readings) {
        this.readings = readings;
    }

}
