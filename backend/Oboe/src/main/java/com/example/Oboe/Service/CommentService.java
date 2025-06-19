package com.example.Oboe.Service;

import com.example.Oboe.DTOs.CommentDTOs;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BlogService blogService;

    public CommentService(CommentRepository commentRepository, UserService userService, BlogService blogService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.blogService = blogService;
    }

    public List<CommentDTOs> getCommentsByBlogId(UUID blogId) {
        return commentRepository.findByBlog_BlogId(blogId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CommentDTOs> getCommentsByUsername(String username) {
        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) return Collections.emptyList();

        return commentRepository.findByUser_UserId(userOpt.get().getUser_id()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CommentDTOs getCommentDTOById(UUID commentId) {
        return commentRepository.findById(commentId).map(this::toDTO).orElse(null);
    }

    public Comment getCommentEntityById(UUID commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    public CommentDTOs createComment(UUID blogId, String username, CommentDTOs dto) {
        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) return null;

        Blog blog = blogService.getBlogById(blogId);
        if (blog == null) return null;

        Comment comment = new Comment();
        comment.setTitle(dto.getTitle());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(userOpt.get());
        comment.setBlog(blog);

        Comment saved = commentRepository.save(comment);
        return toDTO(saved);
    }

    public CommentDTOs updateComment(UUID commentId, String username, CommentDTOs dto) {
        Comment comment = getCommentEntityById(commentId);
        if (comment == null) return null;

        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) return null;

        if (!comment.getUser().getUser_id().equals(userOpt.get().getUser_id())) return null;

        comment.setTitle(dto.getTitle());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        Comment updated = commentRepository.save(comment);
        return toDTO(updated);
    }

    public boolean deleteComment(UUID commentId, String username) {
        Comment comment = getCommentEntityById(commentId);
        if (comment == null) return false;

        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) return false;

        if (!comment.getUser().getUser_id().equals(userOpt.get().getUser_id())) return false;

        commentRepository.deleteById(commentId);
        return true;
    }

    public Long getCommentCountByBlogId(UUID blogId) {
        return commentRepository.countByBlogId(blogId);
    }

    private CommentDTOs toDTO(Comment comment) {
        CommentDTOs dto = new CommentDTOs();
        dto.setCommentId(comment.getCommentId());
        dto.setTitle(comment.getTitle());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());

        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getUser_id());
            dto.setUserName(comment.getUser().getUserName());
        }

        if (comment.getBlog() != null) {
            dto.setBlogId(comment.getBlog().getBlogId());
            dto.setBlogTitle(comment.getBlog().getTitle());
        }

        return dto;
    }
}
