package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.BlogDTO;
import com.example.Oboe.Service.BlogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    public ResponseEntity<List<BlogDTO>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogDTOs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogDTO> getBlogById(@PathVariable UUID id) {
        BlogDTO dto = blogService.getBlogDTOById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<BlogDTO> createBlog(@Valid @RequestBody BlogDTO blogDTO, Authentication authentication) {
        BlogDTO created = blogService.createBlogFromDTO(blogDTO, authentication.getName());
        return created != null ? ResponseEntity.ok(created) : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogDTO> updateBlog(@PathVariable UUID id,
                                              @Valid @RequestBody BlogDTO blogDTO,
                                              Authentication authentication) {
        BlogDTO updated = blogService.updateBlogFromDTO(id, blogDTO, authentication.getName());
        if (updated == null) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable UUID id, Authentication authentication) {
        boolean deleted = blogService.deleteBlogById(id, authentication.getName());
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.status(403).build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<BlogDTO>> searchBlogs(@RequestParam("title") String title) {
        return ResponseEntity.ok(blogService.searchBlogDTOsByTitle(title));
    }

    @GetMapping("/my-blogs")
    public ResponseEntity<List<BlogDTO>> getMyBlogs(Authentication authentication) {
        return ResponseEntity.ok(blogService.getBlogDTOsByUsername(authentication.getName()));
    }
}
