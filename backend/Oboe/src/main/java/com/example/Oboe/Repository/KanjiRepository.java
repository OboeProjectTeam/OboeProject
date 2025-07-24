package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Entity.Kanji;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

public interface KanjiRepository extends JpaRepository<Kanji, UUID> {
    @Query("SELECT k FROM Kanji k WHERE LOWER(k.character_name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(k.meaning) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(k.vietnamesePronunciation) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Kanji> searchKanji(@Param("keyword") String keyword);


    @Query("SELECT k FROM Kanji k WHERE LOWER(k.meaning) LIKE LOWER(CONCAT('%', :meaning, '%')) AND k.kanjiId <> :kanjiId")
    List<Kanji> findRelatedByMeaning(@Param("meaning") String meaning, @Param("kanjiId") UUID kanjiId);

}
