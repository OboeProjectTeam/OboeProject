package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.CommentDTOs;
import com.example.Oboe.Service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Lấy tất cả comment của một blog
    @GetMapping("/blog/{blogId}")
    public ResponseEntity<List<CommentDTOs>> getCommentsByBlog(@PathVariable UUID blogId) {
        return ResponseEntity.ok(commentService.getCommentsByBlogId(blogId));
    }

    // Lấy tất cả comment của user hiện tại
    @GetMapping("/my-comments")
    public ResponseEntity<List<CommentDTOs>> getMyComments(Authentication authentication) {
        return ResponseEntity.ok(commentService.getCommentsByUsername(authentication.getName()));
    }

    // Tạo comment mới cho blog
    @PostMapping("/blog/{blogId}")
    public ResponseEntity<CommentDTOs> createComment(
            @PathVariable UUID blogId,
            @Valid @RequestBody CommentDTOs commentDTO,
            Authentication authentication) {

        CommentDTOs created = commentService.createComment(blogId, authentication.getName(), commentDTO);
        return created != null ? ResponseEntity.ok(created) : ResponseEntity.badRequest().build();
    }

    // Lấy comment theo ID
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTOs> getCommentById(@PathVariable UUID id) {
        CommentDTOs comment = commentService.getCommentDTOById(id);
        return comment != null ? ResponseEntity.ok(comment) : ResponseEntity.notFound().build();
    }

    // Xóa comment (chỉ nếu là owner)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID id, Authentication authentication) {
        boolean deleted = commentService.deleteComment(id, authentication.getName());
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(403).build();
    }

    // Cập nhật comment (chỉ nếu là owner)
    @PutMapping("/{id}")
    public ResponseEntity<CommentDTOs> updateComment(
            @PathVariable UUID id,
            @Valid @RequestBody CommentDTOs commentDTO,
            Authentication authentication) {

        CommentDTOs updated = commentService.updateComment(id, authentication.getName(), commentDTO);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.status(403).build();
    }

    // Đếm số comment của một blog
    @GetMapping("/blog/{blogId}/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable UUID blogId) {
        return ResponseEntity.ok(commentService.getCommentCountByBlogId(blogId));
    }
}
