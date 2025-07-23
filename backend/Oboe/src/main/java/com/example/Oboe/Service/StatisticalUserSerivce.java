package com.example.Oboe.Service;

import com.example.Oboe.DTOs.*;
import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.CommentRepository;
import com.example.Oboe.Repository.QuizzesRepository;
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
    private final QuizzesRepository quizRepository;

    private final BlogService blogService;
    private final CommentService commentService;
    private final QuizzesService quizService;

    public StatisticalUserSerivce(
            BlogRepository blogRepository,
            CommentRepository commentRepository,
            QuizzesRepository quizRepository,
            BlogService blogService,
            CommentService commentService,
            QuizzesService quizService
    ) {
        this.blogRepository = blogRepository;
        this.commentRepository = commentRepository;
        this.quizRepository = quizRepository;
        this.blogService = blogService;
        this.commentService = commentService;
        this.quizService = quizService;
    }

    public StatisticalUserDTOs countUserContent(UUID userId) {
        long blogCount = blogRepository.countBlogsByUserId(userId);
        long commentCount = commentRepository.countCommentsByUserId(userId);
        long quizCount = quizRepository.countQuizzesByUserId(userId);
        return new StatisticalUserDTOs(blogCount, commentCount, quizCount);
    }

    // Phương thức trả về danh sách hoạt động (blogs, comments, quizzes) của user dưới dạng phân trang
    public Page<ActivityDTO> getUserActivities(UUID userId, Pageable pageable) {
        // Lấy tất cả blog của người dùng theo userId
        List<BlogDTO> blogs = blogService.getAllBlogByUserId(userId);
        // Lấy tất cả comment của người dùng
        List<CommentDTOs> comments = commentService.getCommentByUserId(userId);
        // Lấy tất cả quiz của người dùng
        List<QuizDTO> quizzes = quizService.getAllByUserId(userId);
        // Khởi tạo danh sách để chứa tất cả hoạt động
        List<ActivityDTO> activities = new ArrayList<>();
        // Duyệt qua từng blog và chuyển thành đối tượng ActivityDTO, gắn thêm type = "blog"
        blogs.forEach(blog -> activities.add(new ActivityDTO("blog", blog)));
        // Duyệt qua từng comment và chuyển thành ActivityDTO, type = "comment"
        comments.forEach(comment -> activities.add(new ActivityDTO("comment", comment)));
        // Duyệt qua từng quiz và chuyển thành ActivityDTO, type = "quiz"
        quizzes.forEach(quiz -> activities.add(new ActivityDTO("quiz", quiz)));
        // Lấy kích thước của mỗi trang (số phần tử muốn hiển thị trên 1 trang)
        int pageSize = pageable.getPageSize();
        // Lấy số trang hiện tại (đánh số từ 0)
        int currentPage = pageable.getPageNumber();
        // Tính chỉ số bắt đầu cho phần tử đầu tiên của trang hiện tại
        int startItem = currentPage * pageSize;
        List<ActivityDTO> pagedList;
        // Nếu chỉ số bắt đầu vượt quá tổng số phần tử, trả về danh sách rỗng
        if (startItem >= activities.size()) {
            pagedList = Collections.emptyList();
        } else {
            // Tính chỉ số kết thúc (giới hạn trong size của danh sách)
            int toIndex = Math.min(startItem + pageSize, activities.size());
            // Cắt danh sách từ start đến toIndex để tạo danh sách phân trang
            pagedList = activities.subList(startItem, toIndex);
        }
        // Trả về đối tượng Page chứa danh sách hoạt động đã phân trang
        return new PageImpl<>(pagedList, pageable, activities.size());
    }






}
