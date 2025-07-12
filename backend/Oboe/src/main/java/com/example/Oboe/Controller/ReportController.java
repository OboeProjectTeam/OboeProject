package com.example.Oboe.Controller;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.ReportDtos;
import com.example.Oboe.Entity.Report;
import com.example.Oboe.Entity.ReportStatus;
import com.example.Oboe.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // 1. Gửi báo cáo
    @PostMapping
    public Report createReport(@RequestBody ReportDtos reportDtos,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        return reportService.createReport(reportDtos, userDetails.getUserID());
    }


    // 2. Lấy tất cả báo cáo
    @GetMapping
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    // 3. Cập nhật trạng thái
    @PatchMapping("/{reportId}/status")
    public String updateStatus(@PathVariable UUID reportId, @RequestParam ReportStatus status) {
        boolean updated = reportService.updateStatus(reportId, status);
        return updated ? "Cập nhật thành công" : "Không tìm thấy báo cáo";
    }

    // 4. Lấy báo cáo theo user
    @GetMapping("/user/{userId}")
    public List<Report> getReportsByUser(@PathVariable UUID userId) {
        return reportService.getReportsByUserId(userId);
    }

    // 5. Xóa báo cáo
    @DeleteMapping("/{reportId}")
    public String deleteReport(@PathVariable UUID reportId) {
        boolean deleted = reportService.deleteReport(reportId);
        return deleted ? "Đã xoá" : "Không tìm thấy báo cáo";
    }
}
