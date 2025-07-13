package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    @Query("SELECT r FROM Report r WHERE r.user.user_id = :userId")
    List<Report> findByUserId(@Param("userId") UUID userId);


}
