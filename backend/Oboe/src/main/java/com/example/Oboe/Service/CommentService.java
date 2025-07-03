package com.example.Oboe.Service;

import com.example.Oboe.DTOs.CommentDTOs;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.CommentRepository;
import com.example.Oboe.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private  BlogRepository BLogRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, UserService userService, BlogRepository BlogRepository) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.BLogRepository = BlogRepository;
    }

    public List<CommentDTOs> getCommentsByTeamId(UUID teamId) {
        List<Comment> comments = commentRepository.findByReferenceId(teamId);
        List<CommentDTOs> allDtos = comments.stream().map(this::toDTO).collect(Collectors.toList());

        Map<UUID, CommentDTOs> dtoMap = allDtos.stream()
                .collect(Collectors.toMap(CommentDTOs::getCommentId, dto -> dto));

        List<CommentDTOs> rootComments = new ArrayList<>();
        for (CommentDTOs dto : allDtos) {
            UUID parentId = dto.getCommentIdParent();
            if (parentId == null) {
                rootComments.add(dto);
            } else {
                CommentDTOs parentDto = dtoMap.get(parentId);
                if (parentDto != null) {
                    parentDto.getReplies().add(dto);
                }
            }
        }
        return rootComments;
    }

    public CommentDTOs createComment(UUID teamId, String username, CommentDTOs dto) {
        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Người dùng không hợp lệ");
        }
        if (!BLogRepository.existsById(teamId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Blog ID không tồn tại");
        }
        Comment comment = new Comment();
        comment.setTitle(dto.getTitle());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(userOpt.get());
        comment.setreferenceId(teamId);

        Comment saved = commentRepository.save(comment);
        return toDTO(saved);
    }

    public CommentDTOs Commentreply(UUID parentCommentId, String username, CommentDTOs dto) {
        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) return null;

        Optional<Comment> parentOpt = commentRepository.findById(parentCommentId);
        if (parentOpt.isEmpty()) return null;

        Comment parent = parentOpt.get();

        Comment reply = new Comment();
        reply.setTitle(dto.getTitle());
        reply.setContent(dto.getContent());
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUser(userOpt.get());
        reply.setParentComment(parent);
        reply.setreferenceId(parent.getreferenceId()); // kế thừa teamId từ comment cha

        Comment saved = commentRepository.save(reply);
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

        return toDTO(commentRepository.save(comment));
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

    public Comment getCommentEntityById(UUID commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    public Long getCommentCountByTeamId(UUID teamId) {
        return commentRepository.countByReferenceId(teamId);
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

        if (comment.getParentComment() != null) {
            dto.setCommentIdParent(comment.getParentComment().getCommentId());
        }

        dto.setReferenceId(comment.getreferenceId());
        dto.setReplies(new ArrayList<>());
        return dto;
    }
}
