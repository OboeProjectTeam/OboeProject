package com.example.Oboe.Repository;

import com.example.Oboe.Entity.SampleSentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SampleSentenceRepository extends JpaRepository<SampleSentence, UUID> {
    @Query("SELECT s FROM SampleSentence s WHERE LOWER(s.vietnameseMeaning) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.vietnamesePronunciation) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.japaneseText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SampleSentence> searchByVietnameseMeaning(@Param("keyword") String keyword);

}