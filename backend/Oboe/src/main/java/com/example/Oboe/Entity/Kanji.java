package com.example.Oboe.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;


import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "kanji")
public class Kanji {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "kanji_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID kanjiId;

    @Column(name = "character_name", nullable = false)
    private String character_name;

    @Column(name = "meaning")
    private String meaning;

    @Column(name = "strokes")
    private String strokes;


    @Column(name = "vietnamese_pronunciation")
    private String vietnamesePronunciation;

    public String getVietnamesePronunciation() {
        return vietnamesePronunciation;
    }

    public void setVietnamesePronunciation(String vietnamesePronunciation) {
        this.vietnamesePronunciation = vietnamesePronunciation;
    }

    // Getters/Setters
    public UUID getKanjiId() {
        return kanjiId;
    }

    public void setKanjiId(UUID kanjiId) {
        this.kanjiId = kanjiId;
    }

    public String getCharacter_name() {
        return character_name;
    }

    public void setCharacter_name(String character_name) {
        this.character_name = character_name;
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
}
