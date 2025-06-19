package com.example.Oboe.Service;

import com.example.Oboe.DTOs.BlogDTO;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserService userService;

    public BlogService(BlogRepository blogRepository, UserService userService) {
        this.blogRepository = blogRepository;
        this.userService = userService;
    }

    public List<BlogDTO> getAllBlogDTOs() {
        return blogRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public BlogDTO getBlogDTOById(UUID id) {
        return blogRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public BlogDTO createBlogFromDTO(BlogDTO blogDTO, String username) {
        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) return null;

        Blog blog = new Blog();
        blog.setTitle(blogDTO.getTitle());
        blog.setContent(blogDTO.getContent());
        blog.setTags(blogDTO.getTags());
        blog.setUser(userOpt.get());
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());

        Blog saved = blogRepository.save(blog);
        return toDTO(saved);
    }

    public BlogDTO updateBlogFromDTO(UUID id, BlogDTO blogDTO, String username) {
        Optional<Blog> blogOpt = blogRepository.findById(id);
        if (blogOpt.isEmpty()) return null;

        Blog blog = blogOpt.get();
        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) return null;

        User user = userOpt.get();
        if (!Objects.equals(blog.getUser().getUser_id(), user.getUser_id())) return null;

        blog.setTitle(blogDTO.getTitle());
        blog.setContent(blogDTO.getContent());
        blog.setTags(blogDTO.getTags());
        blog.setUpdatedAt(LocalDateTime.now());

        Blog updated = blogRepository.save(blog);
        return toDTO(updated);
    }

    public boolean deleteBlogById(UUID id, String username) {
        Optional<Blog> blogOpt = blogRepository.findById(id);
        if (blogOpt.isEmpty()) return false;

        Blog blog = blogOpt.get();
        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) return false;

        if (!Objects.equals(blog.getUser().getUser_id(), userOpt.get().getUser_id())) return false;

        blogRepository.deleteById(id);
        return true;
    }

    public List<BlogDTO> searchBlogDTOsByTitle(String keyword) {
        return blogRepository.findByTitleContainingIgnoreCase(keyword)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<BlogDTO> getBlogDTOsByUsername(String username) {
        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) return Collections.emptyList();

        return blogRepository.findBlogsByUserId(userOpt.get().getUser_id())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private BlogDTO toDTO(Blog blog) {
        BlogDTO dto = new BlogDTO();
        dto.setId(blog.getBlogId());
        dto.setTitle(blog.getTitle());
        dto.setContent(blog.getContent());
        dto.setTags(blog.getTags());
        dto.setCreatedAt(blog.getCreatedAt());
        dto.setUpdatedAt(blog.getUpdatedAt());
        dto.setUserId(blog.getUser() != null ? blog.getUser().getUser_id() : null);
        return dto;
    }
    public Blog getBlogById(UUID id) {
        return blogRepository.findById(id).orElse(null);
    }

}
