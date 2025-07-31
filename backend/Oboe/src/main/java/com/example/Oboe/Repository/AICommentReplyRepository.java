package com.example.Oboe.Repository;

import com.example.Oboe.Entity.AICommentReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AICommentReplyRepository extends JpaRepository<AICommentReply, UUID> {
}