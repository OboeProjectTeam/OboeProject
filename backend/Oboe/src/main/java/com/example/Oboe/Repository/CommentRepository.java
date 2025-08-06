package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByReferenceId(UUID referenceId);
    Long countByReferenceId(UUID referenceId);
    @Query("SELECT b FROM Comment b WHERE b.user.user_id = :userId")
    List<Comment> findCommentByUserId(UUID userId);

    @Query("SELECT b FROM Comment b WHERE b.user.user_id = :userId")
    Page<Comment> findCommentByUserIds(UUID userId,Pageable pageabl);
    //Để thêm người comment gần nhất cho một bài blog
    Optional<Comment> findTopByReferenceIdOrderByCreatedAtDesc(UUID referenceId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.user.user_id = :userId")
    void deleteUserbyComment(@Param("userId") UUID userId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user.user_id = :userId")
    long countCommentsByUserId(@Param("userId") UUID userId);

}
