package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "Kanji")
public class Kanji {

    public UUID getKanjiID;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "kanjiID", updatable = false, nullable = false)
    private UUID kanjiID;

    @Column(nullable = false)
    private String characterName; // Ký tự Kanji
    private String onyomi;     // Âm Hán Nhật
    private String kunyomi;    // Âm Nhật
    private String meaning;    // Nghĩa
    private String strokes;    // Số nét
    private String example;    // Ví dụ

    @ManyToOne
    @JoinColumn(name = "LevelID")
    private Level level;

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getStrokes() {
        return strokes;
    }

    public void setStrokes(String strokes) {
        this.strokes = strokes;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getKunyomi() {
        return kunyomi;
    }

    public void setKunyomi(String kunyomi) {
        this.kunyomi = kunyomi;
    }

    public String getOnyomi() {
        return onyomi;
    }

    public void setOnyomi(String onyomi) {
        this.onyomi = onyomi;
    }

    public String getCharacter_name() {
        return characterName;
    }

    public void setCharacter_name(String character_name) {
        this.characterName = character_name;
    }

    public UUID getKanjiID() {
        return kanjiID;
    }

    public void setKanjiID(UUID kanjiID) {
        this.kanjiID = kanjiID;
    }

    public UUID getGetKanjiID() {
        return getKanjiID;
    }

    public void setGetKanjiID(UUID getKanjiID) {
        this.getKanjiID = getKanjiID;
    }


}
