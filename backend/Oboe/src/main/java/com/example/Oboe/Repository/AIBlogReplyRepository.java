package com.example.Oboe.Repository;

import com.example.Oboe.Entity.AIBlogReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AIBlogReplyRepository extends JpaRepository<AIBlogReply, UUID> {
    AIBlogReply findByBlog_BlogId(UUID blogId);
}
