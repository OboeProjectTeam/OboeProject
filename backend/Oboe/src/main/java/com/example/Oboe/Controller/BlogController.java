package com.example.Oboe.Controller;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.BlogDTO;
import com.example.Oboe.DTOs.TopicPostProjection;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Service.BlogService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

        @GetMapping("/get_all")
        public ResponseEntity<Map<String, Object>> getBlogs(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size) {
            return ResponseEntity.ok(blogService.getAllBlogDTOs(page, size));
        }


    @GetMapping("/{id}")
    public ResponseEntity<BlogDTO> getBlogById(@PathVariable UUID id) {
        BlogDTO dto = blogService.getBlogDTOById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createBlog(@Valid @RequestBody BlogDTO blogDTO, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();
        BlogDTO created = blogService.createBlogFromDTO(blogDTO, userId);
        if (created != null) {
            return ResponseEntity.ok(
                    Map.of(
                            "message", "Đăng bài thành công!",
                            "data", created
                    )
            );
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Tạo blog thất bại!"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogDTO> updateBlog(@PathVariable UUID id,
                                              @Valid @RequestBody BlogDTO blogDTO,
                                              Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();
        BlogDTO updated = blogService.updateBlogFromDTO(id, blogDTO, userId);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.status(403).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable UUID id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();
        boolean deleted = blogService.deleteBlogById(id, userId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(403).build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<BlogDTO>> searchBlogs(@RequestParam("keyword") String keyword) {
        List<BlogDTO> results = blogService.searchBlogs(keyword, "all");
        return ResponseEntity.ok(results);
    }

    @GetMapping("/user/blogs")
    public ResponseEntity<?> getUserBlogs(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();

        Page<BlogDTO> blogsPage = blogService.getAllBlogByUserIds(userId, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("content", blogsPage.getContent());
        response.put("page", blogsPage.getNumber());
        response.put("size", blogsPage.getSize());
        response.put("totalElements", blogsPage.getTotalElements());
        response.put("totalPages", blogsPage.getTotalPages());
        response.put("last", blogsPage.isLast());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/featuredcomment")
    public ResponseEntity<List<TopicPostProjection>> getTopTopics() {
        List<TopicPostProjection> topTopics = blogService.getTop5TopicsWithMostPosts();
        return ResponseEntity.ok(topTopics);
    }
    @DeleteMapping("/admin/delete-blog/{blogId}")
    public ResponseEntity<?> deleteBlogByAdmin(@PathVariable UUID blogId) {
        boolean deleted = blogService.deleteBlogAsAdmin(blogId);
        if (deleted) {
            return ResponseEntity.ok("Blog và các báo cáo liên quan đã bị xóa");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blog không tồn tại");
        }
    }

}
