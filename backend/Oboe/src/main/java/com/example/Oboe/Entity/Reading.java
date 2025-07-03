package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "Reading")
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "readingID", updatable = false, nullable = false)
    private UUID readingID;

    @Column(nullable = false)
    private String readingText;      // Cách đọc thực tế

    @Column(nullable = false)
    private String readingType;      // Loại đọc: onyomi, kunyomi, nanori, hiragana, katakana, grammar,...

    @Column(nullable = false)
    private String ownerType;        // Bảng cha: "kanji", "vocabulary", "gramma"

    @Column(nullable = false)
    private UUID ownerId;// ID từ bảng cha (UUID của Kanji, Vocabulary hoặc Gramma)

}
