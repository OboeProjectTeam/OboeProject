package com.example.Oboe.Controller;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.BlogDTO;
import com.example.Oboe.Service.BlogService;
import com.example.Oboe.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public ResponseEntity<BlogDTO> createBlog(@Valid @RequestBody BlogDTO blogDTO, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();
        BlogDTO created = blogService.createBlogFromDTO(blogDTO, userId);
        return created != null ? ResponseEntity.ok(created) : ResponseEntity.badRequest().build();
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
    public ResponseEntity<List<BlogDTO>> searchBlogs(@RequestParam("title") String title) {
        return ResponseEntity.ok(blogService.searchBlogDTOsByTitle(title));
    }

    @GetMapping("/user/blogs")
    public ResponseEntity<List<BlogDTO>> getUserBlogs(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserID();
        List<BlogDTO> blogs = blogService.getAllBlogbyUserId(userId);
        return ResponseEntity.ok(blogs);
    }


}
