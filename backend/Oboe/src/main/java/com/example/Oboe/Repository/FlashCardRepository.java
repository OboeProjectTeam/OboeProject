package com.example.Oboe.Repository;

import com.example.Oboe.Entity.FlashCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FlashCardRepository extends JpaRepository<FlashCards, UUID> {
    @Query("SELECT f FROM Report f WHERE f.user.user_id = :userId")
    List<FlashCards> findByUser_User_id(UUID userId);
}
