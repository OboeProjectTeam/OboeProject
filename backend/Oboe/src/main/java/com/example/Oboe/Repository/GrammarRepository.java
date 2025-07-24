package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Grammar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GrammarRepository extends JpaRepository<Grammar, UUID> {
    @Query("SELECT g FROM Grammar g WHERE LOWER(g.structure) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(g.explanation) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(g.vietnamesePronunciation) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Grammar> searchGrammar(@Param("keyword") String keyword);



}
