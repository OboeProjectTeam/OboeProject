package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Kanji;
import org.springframework.data.jpa.repository.JpaRepository;

import java.rmi.server.UID;
import java.util.List;
import java.util.UUID;

public interface KanjiRepository extends JpaRepository<Kanji, UUID> {
    boolean existsByCharacterName(String characterName);
    List<Kanji> findByCharacterNameContainingIgnoreCase(String keyword);

}
