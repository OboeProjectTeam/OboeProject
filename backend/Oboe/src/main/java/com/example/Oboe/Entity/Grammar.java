package com.example.Oboe.Entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "Gramma")
public class Grammar {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "grammaID", updatable = false, nullable = false)
    private UUID grammaID;
    private String structure;

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

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

    private String explanation;
    private String example;
    private String grammarType;

}
