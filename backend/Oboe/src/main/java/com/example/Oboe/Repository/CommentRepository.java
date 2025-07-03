package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByTeamId(UUID teamId);
    Long countByTeamId(UUID teamId);

}
