package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.BlogDTO;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Service.BlogService;
import com.example.Oboe.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;
    private final UserService userService;

    public BlogController(BlogService blogService, UserService userService) {
        this.blogService = blogService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<BlogDTO>> getAllBlogs() {
        List<BlogDTO> dtoList = blogService.getAllBlogs().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogDTO> getBlogById(@PathVariable UUID id) {
        Blog blog = blogService.getBlogById(id);
        if (blog == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDTO(blog));
    }

    @PostMapping
    public ResponseEntity<BlogDTO> createBlog(@Valid @RequestBody BlogDTO blogDTO, Authentication authentication) {
        try {
            // Lấy user từ authentication context thay vì từ DTO
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);

            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User user = userOptional.get();

            Blog blog = new Blog();
            blog.setTitle(blogDTO.getTitle());
            blog.setContent(blogDTO.getContent());

            blog.setUser(user);

            Blog savedBlog = blogService.createBlog(blog);
            return ResponseEntity.ok(toDTO(savedBlog));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogDTO> updateBlog(@PathVariable UUID id, @Valid @RequestBody BlogDTO blogDTO, Authentication authentication) {
        try {
            // Kiểm tra blog có tồn tại không
            Blog existingBlog = blogService.getBlogById(id);
            if (existingBlog == null) {
                return ResponseEntity.notFound().build();
            }

            // Lấy user từ authentication
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);

            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User user = userOptional.get();

            // Kiểm tra quyền sở hữu blog (chỉ cho phép user sửa blog của chính họ)
            if (!existingBlog.getUser().getUser_id().equals(user.getUser_id())) {
                return ResponseEntity.status(403).build(); // Forbidden
            }

            Blog blogDetails = new Blog();
            blogDetails.setTitle(blogDTO.getTitle());
            blogDetails.setContent(blogDTO.getContent());

            blogDetails.setUser(user);

            Blog updated = blogService.updateBlog(id, blogDetails);
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable UUID id, Authentication authentication) {
        try {
            // Kiểm tra blog có tồn tại không
            Blog existingBlog = blogService.getBlogById(id);
            if (existingBlog == null) {
                return ResponseEntity.notFound().build();
            }

            // Lấy user từ authentication
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);

            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User user = userOptional.get();

            // Kiểm tra quyền sở hữu blog (chỉ cho phép user xóa blog của chính họ)
            if (!existingBlog.getUser().getUser_id().equals(user.getUser_id())) {
                return ResponseEntity.status(403).build(); // Forbidden
            }

            blogService.deleteBlog(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<BlogDTO>> searchBlogs(@RequestParam("title") String title) {
        List<BlogDTO> result = blogService.searchBlogsByTitle(title).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }


    @GetMapping("/my-blogs")
    public ResponseEntity<List<BlogDTO>> getMyBlogs(Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);

            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User user = userOptional.get();

            // Lấy tất cả blog của user này
            List<BlogDTO> myBlogs = blogService.getBlogsByUserId(user.getUser_id()).stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(myBlogs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Chuyển đổi entity -> DTO
    private BlogDTO toDTO(Blog blog) {
        BlogDTO dto = new BlogDTO();
        // Sửa lỗi method name - sử dụng getId() thay vì getBlogId()
        dto.setId(blog.getBlogId()); // hoặc blog.getBlogId() tùy theo entity của bạn
        dto.setTitle(blog.getTitle());
        dto.setContent(blog.getContent());

        dto.setCreatedAt(blog.getCreatedAt());
        dto.setUpdatedAt(blog.getUpdatedAt());
        // Sửa lỗi method name - sử dụng getUserId() thay vì getUser_id()
        dto.setUserId(blog.getUser() != null ? blog.getUser().getUser_id() : null);
        return dto;
    }
}