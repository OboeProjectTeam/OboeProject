package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.CommentDTOs;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Service.BlogService;
import com.example.Oboe.Service.CommentService;
import com.example.Oboe.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final BlogService blogService;

    public CommentController(CommentService commentService, UserService userService, BlogService blogService) {
        this.commentService = commentService;
        this.userService = userService;
        this.blogService = blogService;
    }

    // Lấy tất cả comment của một blog
    @GetMapping("/blog/{blogId}")
    public ResponseEntity<List<CommentDTOs>> getCommentsByBlog(@PathVariable UUID blogId) {
        try {
            List<CommentDTOs> comments = commentService.getCommentsByBlogId(blogId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Lấy tất cả comment của user hiện tại
    @GetMapping("/my-comments")
    public ResponseEntity<List<CommentDTOs>> getMyComments(Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);

            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User user = userOptional.get();
            List<CommentDTOs> comments = commentService.getCommentsByUserId(user.getUser_id());
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Tạo comment mới cho blog
    @PostMapping("/blog/{blogId}")
    public ResponseEntity<CommentDTOs> createComment(
            @PathVariable UUID blogId,
            @Valid @RequestBody CommentDTOs commentDTO,
            Authentication authentication) {
        try {
            // Lấy user từ authentication
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);

            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User user = userOptional.get();

            // Kiểm tra blog có tồn tại không
            Blog blog = blogService.getBlogById(blogId);
            if (blog == null) {
                return ResponseEntity.notFound().build();
            }

            // Tạo comment entity
            Comment comment = new Comment();
            comment.setTitle(commentDTO.getTitle());
            comment.setContent(commentDTO.getContent());
            comment.setUser(user);
            comment.setBlog(blog);

            CommentDTOs savedComment = commentService.createComment(comment);
            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Lấy comment theo ID
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTOs> getCommentById(@PathVariable UUID id) {
        try {
            CommentDTOs comment = commentService.getCommentById(id);
            if (comment == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Xóa comment (chỉ cho phép user sở hữu comment)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID id, Authentication authentication) {
        try {
            // Lấy user từ authentication
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);

            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User user = userOptional.get();

            // Kiểm tra comment có tồn tại không
            CommentDTOs commentDTO = commentService.getCommentById(id);
            if (commentDTO == null) {
                return ResponseEntity.notFound().build();
            }

            // Kiểm tra quyền sở hữu comment
            if (!commentDTO.getUserId().equals(user.getUser_id())) {
                return ResponseEntity.status(403).build(); // Forbidden
            }

            commentService.deleteComment(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Cập nhật comment (chỉ cho phép user sở hữu comment)
    @PutMapping("/{id}")
    public ResponseEntity<CommentDTOs> updateComment(
            @PathVariable UUID id,
            @Valid @RequestBody CommentDTOs commentDTO,
            Authentication authentication) {
        try {
            // Lấy user từ authentication
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);

            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User user = userOptional.get();

            // Kiểm tra comment có tồn tại không
            CommentDTOs existingComment = commentService.getCommentById(id);
            if (existingComment == null) {
                return ResponseEntity.notFound().build();
            }

            // Kiểm tra quyền sở hữu comment
            if (!existingComment.getUserId().equals(user.getUser_id())) {
                return ResponseEntity.status(403).build(); // Forbidden
            }

            CommentDTOs updatedComment = commentService.updateComment(id, commentDTO);
            return ResponseEntity.ok(updatedComment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Đếm số comment của một blog
    @GetMapping("/blog/{blogId}/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable UUID blogId) {
        try {
            Long count = commentService.getCommentCountByBlogId(blogId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}