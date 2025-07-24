package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "UserAnswers")
public class UserAnswers {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UserAnswersId", updatable = false, nullable = false)
    private UUID userAnswersId;

    private String answer;
    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "QuestionID", nullable = false)
    private Questions question;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "QuizzesID", nullable = false)
    private Quizzes quiz;

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    @Column(name = "attempt_number")
    private int attemptNumber;

    // Getters and Setters
    public UUID getUserAnswersId() {
        return userAnswersId;
    }

    public void setUserAnswersId(UUID userAnswersId) {
        this.userAnswersId = userAnswersId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public Questions getQuestion() {
        return question;
    }

    public void setQuestion(Questions question) {
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Quizzes getQuiz() {
        return quiz;
    }

    public void setQuiz(Quizzes quiz) {
        this.quiz = quiz;
    }
}
