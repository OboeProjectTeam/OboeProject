package com.example.Oboe.Repository;

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

    // Method lấy tất cả blog của user
    @Query("SELECT b FROM Blog b WHERE b.user.user_id = :userId")
    List<Blog> findBlogsByUserId(@Param("userId") UUID userId);




}