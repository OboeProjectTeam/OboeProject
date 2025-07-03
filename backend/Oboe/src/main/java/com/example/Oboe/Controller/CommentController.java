package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.CommentDTOs;
import com.example.Oboe.Service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // ✅ 1. Lấy tất cả comment theo ID (blog, kanji, etc.)
    @GetMapping("/{id}")
    public ResponseEntity<List<CommentDTOs>> getCommentsByTeamId(@PathVariable("id") UUID id) {
        List<CommentDTOs> comments = commentService.getCommentsByTeamId(id);
        return ResponseEntity.ok(comments);
    }

    // ✅ 2. Tạo comment mới cho 1 id
    @PostMapping("/{id}")
    public ResponseEntity<CommentDTOs> createComment(
            @PathVariable("id") UUID id,
            @RequestBody CommentDTOs dto,
            Authentication authentication
    ) {
        String username = authentication.getName();
        CommentDTOs created = commentService.createComment(id, username, dto);
        if (created == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(created);
    }

    // ✅ 3. Trả lời comment
    @PostMapping("/reply/{commentId}")
    public ResponseEntity<CommentDTOs> replyComment(
            @PathVariable UUID commentId,
            @RequestBody CommentDTOs dto,
            Authentication authentication
    ) {
        String username = authentication.getName();
        CommentDTOs reply = commentService.Commentreply(commentId, username, dto);
        if (reply == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(reply);
    }

    // ✅ 4. Cập nhật comment
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTOs> updateComment(
            @PathVariable UUID commentId,
            @RequestBody CommentDTOs dto,
            Authentication authentication
    ) {
        String username = authentication.getName();
        CommentDTOs updated = commentService.updateComment(commentId, username, dto);
        if (updated == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(updated);
    }

    // ✅ 5. Xóa comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable UUID commentId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        boolean deleted = commentService.deleteComment(commentId, username);
        if (!deleted) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    // ✅ 6. Đếm comment theo id
    @GetMapping("/count/{id}")
    public ResponseEntity<Long> countComments(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(commentService.getCommentCountByTeamId(id));
    }
}
