package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "Kanji")
public class Kanji {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "kanjiID", updatable = false, nullable = false)
    private UUID kanjiID;

    @Column(nullable = false)
    private String character_name; // Ký tự Kanji

    private String onyomi;     // Âm Hán Nhật
    private String kunyomi;    // Âm Nhật
    private String meaning;    // Nghĩa
    private String strokes;    // Số nét
    private String example;    // Ví dụ

    @ManyToOne
    @JoinColumn(name = "LevelID")
    private Level level;
}
