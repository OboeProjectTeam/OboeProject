package com.example.Oboe.Repository;

import com.example.Oboe.Entity.SampleSentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SampleSentenceRepository extends JpaRepository<SampleSentence, UUID> {

}