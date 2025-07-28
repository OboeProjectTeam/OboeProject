package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VocabularyRepository extends JpaRepository<Vocabulary, UUID> {
    @Query("SELECT v FROM Vocabulary v WHERE LOWER(v.words) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.meanning) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.vietnamesePronunciation) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Vocabulary> searchVocabulary(@Param("keyword") String keyword);

    // Nếu cần tìm từ theo KanjiId
    List<Vocabulary> findByKanji_KanjiId(UUID kanjiId);
}