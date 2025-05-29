package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "Vocabulary")
public class Vocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "vocabID", updatable = false, nullable = false)
    private UUID vocabID;

    private String word;
    private String reading;
    private String meaning;
    private String example;
    private String type;

    @ManyToOne
    @JoinColumn(name = "LevelID")
    private Level level;
}
