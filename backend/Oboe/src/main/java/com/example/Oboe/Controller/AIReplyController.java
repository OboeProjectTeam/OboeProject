package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.AIBlogReplyDTO;
import com.example.Oboe.Entity.AIBlogReply;
import com.example.Oboe.Entity.AICommentReply;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.Comment;
import com.example.Oboe.Repository.AIBlogReplyRepository;
import com.example.Oboe.Repository.AICommentReplyRepository;
import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.CommentRepository;
import com.example.Oboe.Service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai-reply")
public class AIReplyController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AIBlogReplyRepository aiBlogReplyRepository;

    @Autowired
    private AICommentReplyRepository aiCommentReplyRepository;

    @PostMapping("/blog/{blogId}")
    public ResponseEntity<?> getOrCreateAIBlogReply(@PathVariable UUID blogId) {
        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Blog not found with ID: " + blogId);
            return ResponseEntity.status(404).body(error);
        }

        // Kiểm tra đã có phản hồi AI chưa
        AIBlogReply existingReply = aiBlogReplyRepository.findByBlog_BlogId(blogId);
        AIBlogReplyDTO dto = new AIBlogReplyDTO();

        if (existingReply != null) {
            // Dùng lại phản hồi đã có
            dto.setId(existingReply.getId());
            dto.setBlogId(blogId);
            dto.setContent(existingReply.getContent());
            dto.setCreatedAt(existingReply.getCreatedAt());

            return ResponseEntity.ok(dto);
        }

        // Nếu chưa có, gọi Gemini
        String prompt = "Bạn là một AI chuyên viết bình luận giá trị cho các bài blog liên quan đến tiếng Nhật. Hãy dựa vào nội dung bài viết bên dưới để đưa ra phản hồi phù hợp:\n\n" +
                "Tiêu đề: " + blog.getTitle() +
                "\nNội dung: " + blog.getContent() +
                "\n\nNếu đây là bài viết nêu câu hỏi hoặc vấn đề, hãy trả lời rõ ràng, đúng trọng tâm, giúp người viết hiểu và giải quyết triệt để vấn đề. Nếu đây là bài chia sẻ kinh nghiệm, tâm sự hay bí quyết học tập (ví dụ như 'Tôi đã hoàn thành khóa học Kanji như thế nào...'), hãy phản hồi một cách thân thiện, đồng cảm và cổ vũ tích cực. Luôn dùng giọng điệu thân thiện, không dùng markdown, câu văn rõ ràng, ngắn gọn nhưng đầy đủ ý nghĩa, giúp người đọc cảm thấy được lắng nghe và trân trọng." +
                " và hãy viết chỉ tầm khoảng dưới 150 chữ thôi";

        String response = geminiService.generateTextFromPrompt(prompt);

        // Tạo phản hồi mới
        AIBlogReply newReply = new AIBlogReply();
        newReply.setId(UUID.randomUUID());
        newReply.setBlog(blog);
        newReply.setContent(response);
        newReply.setCreatedAt(LocalDateTime.now());

        aiBlogReplyRepository.save(newReply);

        dto.setId(newReply.getId());
        dto.setBlogId(blogId);
        dto.setContent(newReply.getContent());
        dto.setCreatedAt(newReply.getCreatedAt());

        return ResponseEntity.ok(dto);
    }



    @PostMapping("/comment/{commentId}")
    public ResponseEntity<?> replyToComment(@PathVariable UUID commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Comment not found with ID: " + commentId);
            return ResponseEntity.status(404).body(error);
        }

        String prompt = "Bạn là một AI chuyên phản hồi các bình luận trên blog. Hãy phản hồi lại bình luận sau:\n" +
                comment.getContent() +
                "\n\nHãy trả lời lịch sự, thân thiện, phù hợp ngữ cảnh. Không dùng markdown.";

        String response = geminiService.generateTextFromPrompt(prompt);

        AICommentReply reply = new AICommentReply();
        reply.setId(UUID.randomUUID());
        reply.setComment(comment);
        reply.setContent(response);
        reply.setCreatedAt(LocalDateTime.now());

        aiCommentReplyRepository.save(reply);

        // Trả JSON
        Map<String, Object> result = new HashMap<>();
        result.put("commentId", commentId);
        result.put("reply", response);
        result.put("createdAt", reply.getCreatedAt());

        return ResponseEntity.ok(result);
    }
}
