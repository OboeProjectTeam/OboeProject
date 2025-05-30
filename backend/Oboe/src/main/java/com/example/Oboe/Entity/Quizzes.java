package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Quizzes")
public class Quizzes {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "QuizzesID", updatable = false, nullable = false)
    private UUID quizzesID;

    private String title;
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LevelId", nullable = false)
    private Level level;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Questions> questions;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizResults> quizResults;

    public UUID getQuizzesID() {
        return quizzesID;
    }

    public void setQuizzesID(UUID quizzesID) {
        this.quizzesID = quizzesID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<Questions> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Questions> questions) {
        this.questions = questions;
    }

    public List<QuizResults> getQuizResults() {
        return quizResults;
    }

    public void setQuizResults(List<QuizResults> quizResults) {
        this.quizResults = quizResults;
    }
}
