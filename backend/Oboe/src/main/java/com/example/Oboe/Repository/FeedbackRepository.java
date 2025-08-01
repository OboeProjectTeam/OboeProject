package com.example.Oboe.Repository;


import com.example.Oboe.Entity.Feedback;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    @Query("SELECT f FROM Feedback f ORDER BY f.createdAt DESC")
    List<Feedback> findTop1ByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COUNT(f) FROM Feedback f")
    long countAllFeedbacks();
}
