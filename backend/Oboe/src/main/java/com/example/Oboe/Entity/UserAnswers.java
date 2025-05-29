package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "UserAnswers")
public class UserAnswers {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UserAnswersId", updatable = false, nullable = false)
    private UUID UserAnswersId;

    private String answer;
    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "QuestionID")
    private Questions question;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "QuizzesID")
    private Quizzes quiz;
}
