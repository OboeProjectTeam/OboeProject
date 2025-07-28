package com.example.Oboe.DTOs;

import java.util.List;

public class GrammarDTO {
    private String grammarId;
    private String example;
    private String explanation;
    private String structure;
    private String grammarType;
    private String VietnamesePronunciation;
    private List<ReadingDTO> readings;




    // Getter & Setter
    public String getGrammarId() {
        return grammarId;
    }

    public void setGrammarId(String grammarId) {
        this.grammarId = grammarId;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getGrammarType() {
        return grammarType;
    }

    public void setGrammarType(String grammarType) {
        this.grammarType = grammarType;
    }

    public String getVietnamesePronunciation() {
        return VietnamesePronunciation;
    }

    public void setVietnamesePronunciation(String vietnamesePronunciation) {
        VietnamesePronunciation = vietnamesePronunciation;
    }
    public List<ReadingDTO> getReadings() {
        return readings;
    }

    public void setReadings(List<ReadingDTO> readings) {
        this.readings = readings;
    }
}
