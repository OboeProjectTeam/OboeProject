package com.example.Oboe.Service;

import com.example.Oboe.DTOs.SampleSentenceDTO;

import java.util.List;
import java.util.UUID;

public interface SampleSentenceService {
    SampleSentenceDTO create(SampleSentenceDTO dto);
    SampleSentenceDTO update(UUID id, SampleSentenceDTO dto);
    void delete(UUID id);
    SampleSentenceDTO getById(UUID id);
    List<SampleSentenceDTO> getAll();
}