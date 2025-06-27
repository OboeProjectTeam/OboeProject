package com.example.Oboe.DTOs;

import com.example.Oboe.Entity.Blog;
import jakarta.validation.constraints.NotBlank;



import java.util.UUID;

public class KanjiDTO  {

    @NotBlank(message = "character_name không được để trống")
    private String characterName;

    @NotBlank(message = "onyomi không được để trống")
    private String onyomi;

    @NotBlank(message = "kunyomi không được để trống")
    private String kunyomi;

    @NotBlank(message = "meaning không được để trống")
    private String meaning;

    @NotBlank(message = "strokes không được để trống")
    private String strokes;

    @NotBlank(message = "example không được để trống")
    private String example;

    private UUID levelID;
    private UUID kanjiID;


    public String getOnyomi() {
        return onyomi;
    }

    public void setOnyomi(String onyomi) {
        this.onyomi = onyomi;
    }

    public UUID getKanjiID() {
        return kanjiID;
    }

    public void setKanjiID(UUID kanjiID) {
        this.kanjiID = kanjiID;
    }

    public String getCharacter_name() {
        return characterName;
    }

    public void setCharacter_name(String character_name) {
        this.characterName = character_name;
    }

    public String getKunyomi() {
        return kunyomi;
    }

    public void setKunyomi(String kunyomi) {
        this.kunyomi = kunyomi;
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

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public UUID getLevelID() {
        return levelID;
    }

    public void setLevelID(UUID levelID) {
        this.levelID = levelID;
    }


}
