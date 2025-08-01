package com.example.Oboe.Controller;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.BlogDTO;
import com.example.Oboe.DTOs.CommentDTOs;
import com.example.Oboe.Service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    //   Lấy tất cả comment theo ID (blog, kanji, etc.)
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getComments(
            @PathVariable("id") UUID teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(commentService.getCommentsByTeamId(teamId, page, size));
    }

    //  Tạo comment mới cho 1 id
    @PostMapping("/{teamId}")
    public ResponseEntity<CommentDTOs> createComment(
            @PathVariable UUID teamId,
            @RequestBody CommentDTOs dto,
            Authentication authentication
    ) {
        UUID userId = ((CustomUserDetails) authentication.getPrincipal()).getUserID();
        CommentDTOs createdComment = commentService.createComment(teamId, userId, dto);
        return ResponseEntity.ok(createdComment);
    }
    // Trả lời comment
    @PostMapping("/reply/{commentId}")
    public ResponseEntity<CommentDTOs> replyComment(
            @PathVariable UUID commentId,
            @RequestBody CommentDTOs dto,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();
        CommentDTOs reply = commentService.Commentreply(commentId, userId, dto);
        if (reply == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(reply);
    }

    //  Cập nhật comment
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTOs> updateComment(
            @PathVariable UUID commentId,
            @RequestBody CommentDTOs dto,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();
        CommentDTOs updated = commentService.updateComment(commentId, userId, dto);
        if (updated == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(updated);
    }

    //  Xóa comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable UUID commentId,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();
        boolean deleted = commentService.deleteComment(commentId, userId);
        if (!deleted) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }
    // Đếm comment theo id
    @GetMapping("/count/{id}")
    public ResponseEntity<Long> countComments(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(commentService.getCommentCountByTeamId(id));
    }
    @GetMapping("/user")
    public ResponseEntity<?> getUserComments(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();

        Page<CommentDTOs> commentsPage = commentService.getCommentByUserIds(userId, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("content", commentsPage.getContent());
        response.put("page", commentsPage.getNumber());
        response.put("size", commentsPage.getSize());
        response.put("totalElements", commentsPage.getTotalElements());
        response.put("totalPages", commentsPage.getTotalPages());
        response.put("last", commentsPage.isLast());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentDTOs> getCommentById(@PathVariable UUID id) {
        return ResponseEntity.ok(commentService.getcommentbyID(id));
    }
}
