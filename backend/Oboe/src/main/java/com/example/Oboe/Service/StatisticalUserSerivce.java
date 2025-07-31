package com.example.Oboe.Service;

import com.example.Oboe.DTOs.*;
import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.CommentRepository;
import com.example.Oboe.Repository.FlashCardRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class StatisticalUserSerivce {
    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;
    private final FlashCardRepository flashCardRepository;
    private final BlogService blogService;
    private final CommentService commentService;
    private final FlashCardService flashCardService;



    public StatisticalUserSerivce(
            BlogRepository blogRepository,
            CommentRepository commentRepository,
            BlogService blogService,
            CommentService commentService,
            FlashCardService flashCardService,
            FlashCardRepository flashCardRepository
    ) {
        this.blogRepository = blogRepository;
        this.commentRepository = commentRepository;
        this.blogService = blogService;
        this.commentService = commentService;
        this.flashCardService = flashCardService;
        this.flashCardRepository = flashCardRepository;
    }

    /**
     * Đếm số lượng blog, comment và flashcard của user.
     */
    public StatisticalUserDTOs countUserContent(UUID userId) {
        long blogCount = blogRepository.countBlogsByUserId(userId);
        long commentCount = commentRepository.countCommentsByUserId(userId);
        long flashCardCount = flashCardRepository.countFlashCardByUserId(userId);
        return new StatisticalUserDTOs(blogCount, commentCount, flashCardCount);
    }


    /**
     * Trả về danh sách hoạt động của user (blog, comment, flashcard) dưới dạng phân trang.
     */
    public Page<ActivityDTO> getUserActivities(UUID userId, Pageable pageable) {
        List<ActivityDTO> activities = new ArrayList<>();

        // Lấy danh sách blog
        List<BlogDTO> blogs = blogService.getAllBlogByUserId(userId);
        if (blogs != null) {
            blogs.forEach(blog -> activities.add(new ActivityDTO("blog", blog)));
        }

        List<CommentDTOs> comments = commentService.getCommentByUserId(userId);
        if (comments != null) {
            for (CommentDTOs comment : comments) {
                UUID refId = comment.getReferenceId();
                if (blogService.blogExists(refId)) {
                    activities.add(new ActivityDTO("comment", comment));
                }
            }
        }
        // Lấy danh sách flashcard
        List<FlashCardDto> flashcards = flashCardService.getAllflashByUserId(userId);
        if (flashcards != null) {
            flashcards.forEach(flashcard -> activities.add(new ActivityDTO("flashcard", flashcard)));
        }

        // Tính chỉ mục bắt đầu và kết thúc cho phân trang
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<ActivityDTO> pagedList;
        if (startItem >= activities.size()) {
            pagedList = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, activities.size());
            pagedList = activities.subList(startItem, toIndex);
        }

        return new PageImpl<>(pagedList, pageable, activities.size());
    }
}
