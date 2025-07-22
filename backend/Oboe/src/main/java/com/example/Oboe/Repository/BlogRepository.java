package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.TopicPostProjection;
import com.example.Oboe.Entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlogRepository extends JpaRepository<Blog, UUID> {

    // Method tìm kiếm theo title
    List<Blog> findByTitleContainingIgnoreCase(String keyword);
    List<Blog> findByTagsContainingIgnoreCase(String keyword);
    List<Blog> findByTopicsContainingIgnoreCase(String keyword);

    @Query("SELECT b FROM Blog b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.tags) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.topics) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Blog> searchByKeyword(@Param("keyword") String keyword);


    // Method lấy tất cả blog của user
    @Query("SELECT b FROM Blog b WHERE b.user.user_id = :userId")
    List<Blog> findBlogsByUserId(@Param("userId") UUID userId);

    // lấy chủ đề nổi bật sử dụng interface TopicPostProjection
    @Query(value = """
        SELECT b.topics AS topic, COUNT(*) AS totalPosts
        FROM blogs b
        GROUP BY b.topics
        ORDER BY totalPosts DESC
        LIMIT 10
    """, nativeQuery = true)
    List<TopicPostProjection> findTop5TopicsWithMostPosts();





}