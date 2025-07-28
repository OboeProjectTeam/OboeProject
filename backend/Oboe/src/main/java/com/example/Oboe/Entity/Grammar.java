package com.example.Oboe.Entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "Gramma")
public class Grammar {
    public UUID getGrammaID() {
        return grammaID;
    }

    public void setGrammaID(UUID grammaID) {
        this.grammaID = grammaID;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getGrammarType() {
        return grammarType;
    }

    public void setGrammarType(String grammarType) {
        this.grammarType = grammarType;
    }

    public String getVietnamesePronunciation() {
        return vietnamesePronunciation;
    }

    public void setVietnamesePronunciation(String vietnamesePronunciation) {
        this.vietnamesePronunciation = vietnamesePronunciation;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "grammaID", updatable = false, nullable = false)
    private UUID grammaID;
    private String structure;
    private String explanation;
    private String example;
    private String grammarType;
    @Column(name = "vietnamese_pronunciation")
    private String vietnamesePronunciation;

}
