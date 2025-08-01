package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.BlogReportDTO;
import com.example.Oboe.Entity.Report;
import com.example.Oboe.Entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("""
    SELECT new com.example.Oboe.DTOs.BlogReportDTO(
        r.reportID,
        b.title,
        b.content,
        u.userName,
        u.authProvider,
        u.avatarUrl,
        r.title,
        r.content,
        r.status,
        r.report_at,
        b.blogId,
        (SELECT COUNT(r2) FROM Report r2 WHERE r2.user.user_id = u.user_id)
    )
    FROM Report r
    JOIN r.blog b
    JOIN r.user u
    WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
      AND (:type IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :type, '%')))
      AND (:status IS NULL OR r.status = :status)
    ORDER BY r.report_at DESC
""")
    List<BlogReportDTO> searchBlogReports(
            @Param("title") String title,
            @Param("type") String type,
            @Param("status") ReportStatus status
    );
    @Query("""
    SELECT new com.example.Oboe.DTOs.BlogReportDTO(
        r.reportID,
        b.title,
        b.content,
        u.userName,
        u.authProvider,
        u.avatarUrl,
        r.title,
        r.content,
        r.status,
        r.report_at,
        b.blogId,
        (SELECT COUNT(r2) FROM Report r2 WHERE r2.user.user_id = u.user_id)
    )
    FROM Report r
    JOIN r.blog b
    JOIN r.user u
    ORDER BY r.report_at DESC
""")
    List<BlogReportDTO> findAllBlogReports();
    @Modifying
    @Query("UPDATE Report r SET r.status = :status WHERE r.reportID = :reportId")
    void updateReportStatus(@Param("reportId") UUID reportId, @Param("status") ReportStatus status);

}
