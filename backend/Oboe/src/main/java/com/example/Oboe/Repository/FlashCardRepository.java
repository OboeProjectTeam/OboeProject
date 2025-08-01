package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.FlashCardDto;
import com.example.Oboe.DTOs.FlashcardSearchResultDTO;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.FlashCards;
import com.example.Oboe.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlashCardRepository extends JpaRepository<FlashCards, UUID> {

    @Query("SELECT f FROM FlashCards f WHERE f.user.user_id = :userId")
    List<FlashCards> findByUser_User_id(@Param("userId") UUID userId);

    @Query("SELECT f FROM FlashCards f WHERE f.user.user_id = :userId AND LOWER(f.term) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<FlashCards> searchByUserIdAndTerm(@Param("userId") UUID userId, @Param("term") String term);

    @Query("SELECT f FROM FlashCards f WHERE f.user.user_id = :userId")
    Page<FlashCards> findByUser(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT f FROM FlashCards f WHERE f.user.user_id = :userId")
    List<FlashCards> findflashcardByUserId(@Param("userId") UUID userId);


    @Query("SELECT f FROM FlashCards f WHERE f.user.user_id = :userId AND LOWER(f.term) LIKE LOWER(CONCAT('%', :term, '%'))")
    Page<FlashCards> searchByUserIdAndTerm(@Param("userId") UUID userId,
                                           @Param("term") String term,
                                           Pageable pageable);
    @Query("SELECT COUNT(f) FROM FlashCards f WHERE f.user.user_id = :userId")
    long countFlashCardByUserId(@Param("userId") UUID userId);

    @Query("SELECT f FROM FlashCards f WHERE f.user.user_id = :userId ORDER BY f.created DESC")
    List<FlashCards> findTop5ByUserIdOrderByCreatedDesc(@Param("userId") UUID userId);

    @Query("DELETE FROM FlashCards f WHERE f.user.user_id = :userId")
    void deleteUser(@Param("userId") UUID userId);


    @Query("SELECT f FROM FlashCards f WHERE f.set_id = :setId")
    Optional<FlashCards> findById(@Param("setId") UUID setId);

    @Query(
            value = "SELECT new com.example.Oboe.DTOs.FlashcardSearchResultDTO(" +
                    "f.set_id, f.term, f.user.userName, COUNT(c), f.user.avatarUrl) " +
                    "FROM FlashCards f LEFT JOIN f.cardItems c " +
                    "WHERE LOWER(f.term) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "GROUP BY f.set_id, f.description, f.user.userName, f.user.avatarUrl",
            countQuery = "SELECT COUNT(DISTINCT f.set_id) " +
                    "FROM FlashCards f " +
                    "WHERE LOWER(f.term) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    )
    Page<FlashcardSearchResultDTO> searchFlashcardsByKeyword(@Param("keyword") String keyword, Pageable pageable);



}