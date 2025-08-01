package com.example.Oboe.Service;

import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.FeedbackRepository;
import com.example.Oboe.Repository.ReportRepository;
import com.example.Oboe.Repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final BlogRepository blogRepository;
    private final FeedbackRepository feedbackRepository;

    public DashboardService(UserRepository userRepository,
                            ReportRepository reportRepository,
                            BlogRepository blogRepository,
                            FeedbackRepository feedbackRepository) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.blogRepository = blogRepository;
        this.feedbackRepository = feedbackRepository;
    }

    public Map<String, Object> getDashboardData() {
        Map<String, Object> result = new HashMap<>();

        // Tổng hợp
        Map<String, Object> summary = new HashMap<>();
        summary.put("users", Map.of(
                "count", userRepository.countAllUsers(),
                "monthly_change", userRepository.countUsersThisMonth()
        ));
        summary.put("posts", Map.of(
                "count", blogRepository.countAllPosts(),
                "monthly_change", blogRepository.countPostsThisMonth()
        ));
        summary.put("post_reports", Map.of(
                "count", reportRepository.countPendingBlogReports(),
                "status", "Chờ xử lý"
        ));


        summary.put("feedback", Map.of(
                "count", feedbackRepository.countAllFeedbacks(),
                "status", "Đã ghi nhận"
        ));

        // Trước đây dùng report để thống kê feedback:
        // summary.put("feedback", Map.of(
        //     "count", reportRepository.countPendingFeedbackReports(),
        //     "status", "Chờ xử lý"
        // ));

        // Hoạt động gần đây
        List<Map<String, String>> activities = new ArrayList<>();

        // Người dùng mới
        var latestUsers = userRepository.findLatestRegisteredUser();
        if (!latestUsers.isEmpty()) {
            Object[] u = latestUsers.get(0);
            activities.add(Map.of(
                    "type", "Người dùng mới đăng ký",
                    "message", u[0] + " đã tạo tài khoản mới",
                    "time", convertToTimeAgo((LocalDateTime) u[1])
            ));
        }

        // Báo cáo mới
        var latestReports = reportRepository.findLatestReport();
        if (!latestReports.isEmpty()) {
            var r = latestReports.get(0);
            String message;
            if (r.getBlog() != null) {
                message = "Bài viết (ID: " + r.getBlog().getBlogId() + ") đã bị báo cáo";
            } else if (r.getUser() != null) {
                message = "Người dùng (ID: " + r.getUser().getUser_id() + ") đã bị phản ánh";
            } else {
                message = "Báo cáo không xác định đối tượng";
            }

            activities.add(Map.of(
                    "type", "Báo cáo mới",
                    "message", message,
                    "time", convertToTimeAgo(r.getReport_at().atStartOfDay())
            ));
        }


        var latestFeedbackList = feedbackRepository.findTop1ByOrderByCreatedAtDesc(PageRequest.of(0,1));
        if (!latestFeedbackList.isEmpty()) {
            var feedback = latestFeedbackList.get(0);
            activities.add(Map.of(
                    "type", "Phản hồi mới",
                    "message", "Phản hồi: \"" + feedback.getContent() + "\"",
                    "time", convertToTimeAgo(feedback.getCreatedAt())
            ));
        }


        result.put("summary", summary);
        result.put("recent_activities", activities);

        return result;
    }

    private String convertToTimeAgo(LocalDateTime time) {
        Duration duration = Duration.between(time, LocalDateTime.now());
        if (duration.toMinutes() < 60) return duration.toMinutes() + " phút trước";
        if (duration.toHours() < 24) return duration.toHours() + " giờ trước";
        return duration.toDays() + " ngày trước";
    }
}

