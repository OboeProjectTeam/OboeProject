package com.example.Oboe.Controller;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.BlogReportDTO;
import com.example.Oboe.DTOs.ReportDtos;
import com.example.Oboe.Entity.ActionType;
import com.example.Oboe.Entity.Report;
import com.example.Oboe.Entity.ReportStatus;
import com.example.Oboe.Service.ReportService;
import com.example.Oboe.annotation.PremiumOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        @GetMapping("/search")
        public List<BlogReportDTO> searchBlogReports(
                @RequestParam(required = false) String title,
                @RequestParam(required = false) String type,
                @RequestParam(required = false) ReportStatus status
        )
        {
            return reportService.searchBlogReports(title, type, status);
        }
    @GetMapping("/all-blog-reports")
    public List<BlogReportDTO> getAllBlogReports() {
        return reportService.getAllBlogReports();
    }
    @PutMapping("/approve/{reportId}")
    public ResponseEntity<String> approveReport(
            @PathVariable UUID reportId,
            @RequestParam ActionType actionType,
            @RequestParam(required = false) String note) {

        reportService.approveReport(reportId, actionType, note);
        return ResponseEntity.ok("Report approved successfully");
    }

    @PutMapping("/rejected/{reportId}")
    public ResponseEntity<String>rejectedReport(@PathVariable UUID reportId){
        reportService.rejectedReport(reportId);
        return ResponseEntity.ok("Report rejected successfull");
    }

}
