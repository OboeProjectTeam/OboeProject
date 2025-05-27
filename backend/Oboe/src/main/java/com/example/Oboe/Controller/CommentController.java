package com.example.Oboe.Controller;

import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Service.CommentService;
import org.springframework.http.ResponseEntity;
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
    //lấy tất cả comment của blogs
    @GetMapping("/blog/{blogId}")
    public List<Comment> getCommentsByBlog(@PathVariable UUID blogId) {
        return commentService.getCommentsByBlogId(blogId);
    }
    //lấy tất cả comment của Userid
//    @GetMapping("/user/{userId}")
//    public List<Comment> findByUserId(@PathVariable UUID userId) {
//        return commentService.getCommentsByUserId(userId);
//    }
    //comment vào blogs
    @PostMapping("/blog/{blogId}/user/{userId}")
    public ResponseEntity<Comment> createComment(
            @PathVariable UUID blogId,
            @PathVariable UUID userId,
            @RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.createComment(comment, userId, blogId));
    }
    //delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
    //update
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable UUID id, @RequestBody Comment comment) {
        Comment updatedComment = commentService.updateComment(id, comment);
        return ResponseEntity.ok(updatedComment);
    }
}