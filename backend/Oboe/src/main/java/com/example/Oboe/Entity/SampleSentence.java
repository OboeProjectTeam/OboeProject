package com.example.Oboe.Entity;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "sample_sentence")
public class SampleSentence {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "sample_sentence_id", updatable = false, nullable = false)
    private UUID sample_sentence_id;

    @Column(name = "japanese_text", nullable = false, columnDefinition = "TEXT")
    private String japaneseText; // Câu tiếng Nhật

    @Column(name = "vietnamese_meaning", columnDefinition = "TEXT")
    private String vietnameseMeaning; // Nghĩa tiếng Việt


    @Column(name = "vietnamese_pronunciation")
    private String vietnamesePronunciation;

    public String getVietnamesePronunciation() {
        return vietnamesePronunciation;
    }

    public void setVietnamesePronunciation(String vietnamesePronunciation) {
        this.vietnamesePronunciation = vietnamesePronunciation;
    }


    // Constructors
    public SampleSentence() {}

    public SampleSentence(String japaneseText, String vietnameseMeaning) {
        this.japaneseText = japaneseText;
        this.vietnameseMeaning = vietnameseMeaning;

    }

    public UUID getSample_sentence_id() {
        return sample_sentence_id;
    }

    public void setSample_sentence_id(UUID sample_sentence_id) {
        this.sample_sentence_id = sample_sentence_id;
    }

    public String getJapaneseText() {
        return japaneseText;
    }

    public void setJapaneseText(String japaneseText) {
        this.japaneseText = japaneseText;
    }

    public String getVietnameseMeaning() {
        return vietnameseMeaning;
    }

    public void setVietnameseMeaning(String vietnameseMeaning) {
        this.vietnameseMeaning = vietnameseMeaning;
    }

}
