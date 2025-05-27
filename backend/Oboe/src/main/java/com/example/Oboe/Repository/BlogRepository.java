package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BlogRepository extends JpaRepository<Blog, UUID> {
    List<Blog> findByTitleContainingIgnoreCase(String keyword);
}
