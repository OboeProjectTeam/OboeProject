package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.TopicPostProjection;
import com.example.Oboe.Entity.Blog;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("SELECT b FROM Blog b WHERE b.user.user_id = :userId")
    Page<Blog> findBlogsByUserIds(@Param("userId") UUID userId,Pageable pageabl);

    // lấy chủ đề nổi bật sử dụng interface TopicPostProjection
    @Query(value = """
    SELECT 
        BIN_TO_UUID(b.blog_id) AS blogId,
        b.title AS title,
        COUNT(c.comment_id) AS commentCount
    FROM blogs b
    JOIN comments c ON b.blog_id = c.reference_id
    GROUP BY b.blog_id, b.title
    ORDER BY commentCount DESC
    LIMIT 3
    """, nativeQuery = true)
    List<TopicPostProjection> findTop3BlogsByCommentCount();




    @Query("SELECT COUNT(b) FROM Blog b WHERE b.user.user_id = :userId")
    long countBlogsByUserId(@Param("userId") UUID userId);
    @Modifying
    @Transactional

    @Query("DELETE FROM Blog b WHERE b.user.user_id = :userId")
    void deleteBlogsbyUser(@Param("userId") UUID userId);

    // Tổng số blog
    @Query("SELECT COUNT(b) FROM Blog b")
    Long countAllPosts();

    // Số blog trong tháng hiện tại
    @Query("SELECT COUNT(b) FROM Blog b WHERE FUNCTION('MONTH', b.createdAt) = FUNCTION('MONTH', CURRENT_DATE)")
    Long countPostsThisMonth();



}