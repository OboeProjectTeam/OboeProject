package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByBlog_BlogId(UUID blogId);
//    List<Comment> findByUser_UserId(UUID user_id);
//    List<Comment> findByUser_User_id(UUID user_id);
}
