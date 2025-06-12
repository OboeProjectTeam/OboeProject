package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByBlog_BlogId(UUID blogId);
    // Lấy comments theo user ID
    @Query("SELECT c FROM Comment c WHERE c.user.user_id = :userId ORDER BY c.createdAt DESC")
    List<Comment> findByUser_UserId(@Param("userId") UUID userId);
    // Đếm số comments của một blog
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.blog.blogId = :blogId")
    Long countByBlogId(@Param("blogId") UUID blogId);
}
