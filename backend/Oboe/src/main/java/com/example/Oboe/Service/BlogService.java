package com.example.Oboe.Service;

import com.example.Oboe.Constant.Constant;
import com.example.Oboe.DTOs.BlogDTO;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.CommentRepository;
import com.example.Oboe.response.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    public BlogService(BlogRepository blogRepository, UserService userService,CommentRepository commentRepository) {
        this.blogRepository = blogRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
    }

    //  Lấy danh sách tất cả Blog
    public Map<String, Object> getAllBlogDTOs(int page, int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Blog> blogPage = blogRepository.findAll(pageable);

            List<BlogDTO> blogDTOs = blogPage.getContent()
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());

            response.put("blogs", blogDTOs);
            response.put("currentPage", blogPage.getNumber());
            response.put("totalPages", blogPage.getTotalPages());
            response.put("totalElements", blogPage.getTotalElements());
            response.put("pageSize", blogPage.getSize());
            response.put("last", blogPage.isLast());
            response.put("code", 200);
            response.put("message", "Lấy danh sách blog thành công.");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "Đã xảy ra lỗi khi lấy danh sách blog.");
            response.put("blogs", Collections.emptyList());
        }
        return response;
    }


    //  Lấy Blog theo ID
    public BlogDTO getBlogDTOById(UUID id) {
        Optional<Blog> blogOpt = blogRepository.findById(id);
        if (blogOpt.isEmpty()) {
            System.out.println(Constant.BLOG_NOT_FOUND);
            return null;
        }
        return toDTO(blogOpt.get());
    }

    // Tạo Blog mới từ DTO và userId
    public BlogDTO createBlogFromDTO(BlogDTO blogDTO, UUID userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return null;

        Blog blog = new Blog();
        blog.setTitle(blogDTO.getTitle());
        blog.setContent(blogDTO.getContent());
        blog.setUser(userOpt.get());
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        //  Thêm tags và topics
        blog.setTags(blogDTO.getTags());
        blog.setTopics(blogDTO.getTopics());

        Blog saved = blogRepository.save(blog);
        return toDTO(saved);
    }

    // Cập nhật blog (chỉ nếu người dùng là chủ sở hữu)
    public BlogDTO updateBlogFromDTO(UUID id, BlogDTO blogDTO, UUID userId) {
        Optional<Blog> blogOpt = blogRepository.findById(id);
        if (blogOpt.isEmpty()) return null;

        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return null;

        Blog blog = blogOpt.get();
        User currentUser = userOpt.get();

        if (blog.getUser() == null || !blog.getUser().getUser_id().equals(currentUser.getUser_id())) {
            return null;
        }

        blog.setTitle(blogDTO.getTitle());
        blog.setContent(blogDTO.getContent());
        blog.setUpdatedAt(LocalDateTime.now());

        // Cập nhật tags và topics
        blog.setTags(blogDTO.getTags());
        blog.setTopics(blogDTO.getTopics());

        Blog updated = blogRepository.save(blog);
        return toDTO(updated);
    }

    //  Xóa blog nếu người dùng là chủ sở hữu
    public boolean deleteBlogById(UUID id, UUID userId) {
        Optional<Blog> blogOpt = blogRepository.findById(id);
        if (blogOpt.isEmpty()) return false;

        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return false;

        Blog blog = blogOpt.get();
        User user = userOpt.get();

        // Kiểm tra quyền sở hữu
        if (blog.getUser() == null || !blog.getUser().getUser_id().equals(user.getUser_id())) {
            return false;
        }

        blogRepository.deleteById(id);
        return true;
    }

    //  Tìm kiếm Blog theo từ khóa tiêu đề

    public List<BlogDTO> searchBlogs(String keyword, String field) {
        List<Blog> blogs;

        switch (field.toLowerCase()) {
            case "title":
                blogs = blogRepository.findByTitleContainingIgnoreCase(keyword);
                break;
            case "tags":
                blogs = blogRepository.findByTagsContainingIgnoreCase(keyword);
                break;
            case "topics":
                blogs = blogRepository.findByTopicsContainingIgnoreCase(keyword);
                break;
            default: // "all"
                blogs = blogRepository.searchByKeyword(keyword);
                break;
        }

        return blogs.stream().map(this::toDTO).collect(Collectors.toList());
    }


    //  Lấy tất cả Blog của một User cụ thể
    public List<BlogDTO> getAllBlogbyUserId(UUID userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return null;

        List<Blog> blogs = blogRepository.findBlogsByUserId(userId);
        //map từ Blog  về BlogDto qua hàm toDto()
        return blogs.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    //  Chuyển đổi từ Entity sang DTO
    private BlogDTO toDTO(Blog blog) {
        BlogDTO dto = new BlogDTO();
        dto.setId(blog.getBlogId());
        dto.setTitle(blog.getTitle());
        dto.setContent(blog.getContent());
        dto.setCreatedAt(blog.getCreatedAt());
        dto.setUpdatedAt(blog.getUpdatedAt());

        dto.setTags(blog.getTags());
        dto.setTopics(blog.getTopics());

        if (blog.getUser() != null) {
            dto.setUserId(blog.getUser().getUser_id());
            dto.setAuthor(blog.getUser().getUserName());
        }

        // Đếm số comment
        long count = commentRepository.countByReferenceId(blog.getBlogId());
        dto.setCommentCount((int) count);

        //  Lấy comment gần nhất
        commentRepository.findTopByReferenceIdOrderByCreatedAtDesc(blog.getBlogId())
                .ifPresent(latestComment -> {
                    dto.setLatestCommentTime(latestComment.getCreatedAt());
                    dto.setLatestCommenterName(latestComment.getUser().getUserName());
                });

        return dto;
    }


}
