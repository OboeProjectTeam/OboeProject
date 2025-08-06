package com.example.Oboe.Service;

import com.example.Oboe.DTOs.SampleSentenceDTO;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;

public interface SampleSentenceService {
    SampleSentenceDTO create(SampleSentenceDTO dto);
    SampleSentenceDTO update(UUID id, SampleSentenceDTO dto);
    void delete(UUID id);
    SampleSentenceDTO getById(UUID id);
    Map<String, Object> getAll(Pageable pageable);
}