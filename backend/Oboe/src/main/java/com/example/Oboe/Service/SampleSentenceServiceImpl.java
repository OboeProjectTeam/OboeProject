package com.example.Oboe.Service;

import com.example.Oboe.DTOs.SampleSentenceDTO;
import com.example.Oboe.Entity.SampleSentence;
import com.example.Oboe.Repository.SampleSentenceRepository;
import com.example.Oboe.Service.SampleSentenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SampleSentenceServiceImpl implements SampleSentenceService {

    @Autowired
    private SampleSentenceRepository repository;

    private SampleSentenceDTO convertToDTO(SampleSentence entity) {
        return new SampleSentenceDTO(
                entity.getSample_sentence_id(),
                entity.getJapaneseText(),
                entity.getVietnameseMeaning()
        );
    }

    private SampleSentence convertToEntity(SampleSentenceDTO dto) {
        SampleSentence entity = new SampleSentence();
        entity.setSample_sentence_id(dto.getId());
        entity.setJapaneseText(dto.getJapaneseText());
        entity.setVietnameseMeaning(dto.getVietnameseMeaning());
        return entity;
    }

    @Override
    public SampleSentenceDTO create(SampleSentenceDTO dto) {
        SampleSentence entity = convertToEntity(dto);
        return convertToDTO(repository.save(entity));
    }

    @Override
    public SampleSentenceDTO update(UUID id, SampleSentenceDTO dto) {
        SampleSentence entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SampleSentence not found"));
        entity.setJapaneseText(dto.getJapaneseText());
        entity.setVietnameseMeaning(dto.getVietnameseMeaning());
        return convertToDTO(repository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public SampleSentenceDTO getById(UUID id) {
        SampleSentence entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SampleSentence not found"));
        return convertToDTO(entity);
    }

    @Override
    public List<SampleSentenceDTO> getAll() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
