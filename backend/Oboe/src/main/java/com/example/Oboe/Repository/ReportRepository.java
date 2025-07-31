package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    @Query("SELECT r FROM Report r WHERE r.user.user_id = :userId")
    List<Report> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT r FROM Report r WHERE r.blog.blogId = :blogId")
    List<Report> findByBlogId(@Param("blogId") UUID blogId);
    @Query("SELECT COUNT(r) FROM Report r WHERE r.status = 'PENDING'")
    Long countPendingReports();

    @Query("SELECT r FROM Report r ORDER BY r.report_at DESC")
    List<LocalDate> getLatestReportTime();

    @Query("SELECT r FROM Report r ORDER BY r.report_at DESC")
    List<Report> findLatestReport();

    @Query("SELECT COUNT(r) FROM Report r WHERE r.blog IS NOT NULL AND r.status = 'PENDING'")
    Long countPendingBlogReports();

    @Query("SELECT COUNT(r) FROM Report r WHERE r.blog IS NULL AND r.status = 'PENDING'")
    Long countPendingFeedbackReports();
}
