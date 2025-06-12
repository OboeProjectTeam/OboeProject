package com.example.Oboe.Service;

import com.example.Oboe.DTOs.CommentDTOs;
import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<CommentDTOs> getCommentsByBlogId(UUID blogId) {
        return commentRepository.findByBlog_BlogId(blogId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CommentDTOs createComment(Comment comment) {
        Comment savedComment = commentRepository.save(comment);
        return toDTO(savedComment);
    }

    public CommentDTOs getCommentById(UUID commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        return comment.map(this::toDTO).orElse(null);
    }

    public void deleteComment(UUID commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<CommentDTOs> getCommentsByUserId(UUID userId) {
        return commentRepository.findByUser_UserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CommentDTOs updateComment(UUID commentId, CommentDTOs commentDTO) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.setTitle(commentDTO.getTitle());
            comment.setContent(commentDTO.getContent());
            Comment updatedComment = commentRepository.save(comment);
            return toDTO(updatedComment);
        }
        return null;
    }

    public Long getCommentCountByBlogId(UUID blogId) {
        return commentRepository.countByBlogId(blogId);
    }

    // Chuyển đổi Comment entity sang CommentDTO
    private CommentDTOs toDTO(Comment comment) {
        CommentDTOs dto = new CommentDTOs();
        dto.setCommentId(comment.getCommentId());
        dto.setTitle(comment.getTitle());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());

        // Thông tin user
        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getUser_id());
            dto.setUserName(comment.getUser().getUserName());
        }

        // Thông tin blog
        if (comment.getBlog() != null) {
            dto.setBlogId(comment.getBlog().getBlogId());
            dto.setBlogTitle(comment.getBlog().getTitle());
        }

        return dto;
    }
}