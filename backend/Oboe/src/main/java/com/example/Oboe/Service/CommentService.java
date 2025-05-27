package com.example.Oboe.Service;

import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.CommentRepository;
import com.example.Oboe.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, BlogRepository blogRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
    }

    public List<Comment> getCommentsByBlogId(UUID blogId) {
        return commentRepository.findByBlog_BlogId(blogId);
    }
//    public List<Comment> getCommentsByUserId(UUID userId) {
//        return commentRepository.findByUser_User_id(userId);
//    }


    public Comment createComment(Comment comment, UUID userId, UUID blogId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Blog> blog = blogRepository.findById(blogId);

        if (user.isPresent() && blog.isPresent()) {
            comment.setUser(user.get());
            comment.setBlog(blog.get());
            return commentRepository.save(comment);
        } else {
            throw new RuntimeException("User or Blog not found.");
        }
    }

    public void deleteComment(UUID commentId) {
        commentRepository.deleteById(commentId);
    }

    public Comment updateComment(UUID commentId, Comment newCommentData) {
        return commentRepository.findById(commentId)
                .map(existingComment -> {
                    existingComment.setTitle(newCommentData.getTitle());
                    existingComment.setContent(newCommentData.getContent());
                    return commentRepository.save(existingComment);
                })
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

}