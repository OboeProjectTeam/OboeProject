package com.example.Oboe.Repository;

import com.example.Oboe.Entity.ReportActions;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ReportActionsRepository extends JpaRepository<ReportActions, UUID> {
    @Modifying
    @Transactional
    @Query("delete from ReportActions ra where ra.report.reportID = :reportID")
    void deleteByReportId(UUID reportID);
}
