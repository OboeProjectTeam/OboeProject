package com.example.Oboe.Service;


import com.example.Oboe.DTOs.ReportDtos;
import com.example.Oboe.Entity.Blog;
import com.example.Oboe.Entity.Report;
import com.example.Oboe.Entity.ReportStatus;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.BlogRepository;
import com.example.Oboe.Repository.ReportRepository;
import com.example.Oboe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private BlogRepository blogRepository;

    // Tạo báo cáo
    public Report createReport(ReportDtos reportDtos, UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        Report report = new Report();
        report.setTitle(reportDtos.getTitle());
        report.setContent(reportDtos.getContent());
        report.setUser(user);
        report.setReport_at(LocalDate.now());
        report.setStatus(ReportStatus.PENDING);

        // Nếu có blogId thì đây là report blog
        if (reportDtos.getBlogId() != null) {
            Blog blog = blogRepository.findById(reportDtos.getBlogId()).orElse(null);
            if (blog != null) {
                report.setBlog(blog);
            }
        }

        return reportRepository.save(report);
    }


    // Lấy toàn bộ báo cáo
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    // Cập nhật trạng thái
    public boolean updateStatus(UUID reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId).orElse(null);
        if (report == null) return false;
        report.setStatus(status);
        reportRepository.save(report);
        return true;
    }
    // Lấy báo cáo theo blog
    public List<Report> getReportsByBlogId(UUID blogId) {
        return reportRepository.findByBlogId(blogId);
    }
    // Lấy báo cáo theo user
    public List<Report> getReportsByUserId(UUID userId) {
        return reportRepository.findByUserId(userId);
    }

    // Xoá báo cáo
    public boolean deleteReport(UUID reportId) {
        if (!reportRepository.existsById(reportId)) return false;
        reportRepository.deleteById(reportId);
        return true;
    }
}
