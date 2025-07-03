package com.example.Oboe.Entity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "Vocabulary")
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Vocalb_id", updatable = false, nullable = false)
    private UUID vocalbId;

    private String words;

    private String meanning;

    private String wordType; //  lưu loại từ: noun, verb, adj...

    private String scriptType; //  hiragana, katakana

    @ManyToOne
    @JoinColumn(name ="kanjiId")
    private Kanji kanji;
}
