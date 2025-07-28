package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Message;
import com.example.Oboe.Entity.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;



public interface ReadingRepository extends JpaRepository<Reading, UUID> {

    List<Reading> findByOwnerTypeAndOwnerId(String ownerType, UUID ownerId);
    @Query("""
    SELECT r FROM Reading r 
    WHERE LOWER(r.readingText) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Reading> searchReadingsByText(@Param("keyword") String keyword);



}

