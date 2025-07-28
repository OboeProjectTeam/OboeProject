package com.example.Oboe.Entity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "Vocabulary")
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Vocalb_id", updatable = false, nullable = false)

    private UUID vocalbId;

    private String words;

    private String meanning;

    @Column(name = "vietnamese_pronunciation")
    private String vietnamesePronunciation;

    public String getVietnamesePronunciation() {
        return vietnamesePronunciation;
    }

    public void setVietnamesePronunciation(String vietnamesePronunciation) {
        this.vietnamesePronunciation = vietnamesePronunciation;
    }


    public String getWordType() {
        return wordType;
    }

    public void setWordType(String wordType) {
        this.wordType = wordType;
    }

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

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public Kanji getKanji() {
        return kanji;
    }

    public void setKanji(Kanji kanji) {
        this.kanji = kanji;
    }

    private String wordType; //  lưu loại từ: noun, verb, adj...

    private String scriptType; //  hiragana, katakana

    @ManyToOne
    @JoinColumn(name ="kanjiId")
    private Kanji kanji;
}