package com.example.Oboe.Service;

import com.example.Oboe.DTOs.VocabularyDTOs;
import com.example.Oboe.DTOs.ReadingDTO;
import com.example.Oboe.Entity.Vocabulary;
import com.example.Oboe.Entity.Reading;
import com.example.Oboe.Repository.VocabularyRepository;
import com.example.Oboe.Repository.ReadingRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final ReadingRepository readingRepository;

    public VocabularyService(VocabularyRepository vocabularyRepository, ReadingRepository readingRepository) {
        this.vocabularyRepository = vocabularyRepository;
        this.readingRepository = readingRepository;
    }

    // ✅ Get all vocabularies with pagination
    public Map<String, Object> getAllVocabulary(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vocabulary> vocabPage = vocabularyRepository.findAll(pageable);

        List<VocabularyDTOs> vocabDTOs = vocabPage.getContent().stream()
                .map(this::vocabToDTO) .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("vocabularies", vocabDTOs);
        response.put("currentPage", vocabPage.getNumber());
        response.put("pageSize", vocabPage.getSize());
        response.put("totalElements", vocabPage.getTotalElements());
        response.put("totalPages", vocabPage.getTotalPages());
        response.put("isLastPage", vocabPage.isLast());

        return response;
    }

    // ✅ Create new vocabulary
    public VocabularyDTOs createVocabulary(VocabularyDTOs dto) {
        checkAdminAccess();

        Vocabulary vocab = new Vocabulary();
        vocab.setWords(dto.getWords());
        vocab.setMeanning(dto.getMeanning());
        vocab.setWordType(dto.getWordType());
        vocab.setScriptType(dto.getScriptType());

        Vocabulary saved = vocabularyRepository.save(vocab);

        if (dto.getReadings() != null && !dto.getReadings().isEmpty()) {
            List<Reading> readings = dto.getReadings().stream().map(r -> {
                r.setOwnerType("vocabulary");
                r.setOwnerId(saved.getVocalbId());
                return readingToEntity(r);
            }).collect(Collectors.toList());
            readingRepository.saveAll(readings);
        }

        return vocabToDTO(saved);
    }

    // ✅ Get by ID
    public VocabularyDTOs getVocabularyById(UUID id) {
        Vocabulary vocab = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy từ vựng với ID: " + id));
        return vocabToDTO(vocab);
    }

    // ✅ Update
    public VocabularyDTOs updateVocabulary(UUID id, VocabularyDTOs dto) {
        checkAdminAccess();

        Vocabulary vocab = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Từ vựng không tồn tại"));

        if (dto.getWords() != null) vocab.setWords(dto.getWords());
        if (dto.getMeanning() != null) vocab.setMeanning(dto.getMeanning());
        if (dto.getWordType() != null) vocab.setWordType(dto.getWordType());
        if (dto.getScriptType() != null) vocab.setScriptType(dto.getScriptType());

        Vocabulary updated = vocabularyRepository.save(vocab);

        // Xoá và thêm lại readings
        List<Reading> oldReadings = readingRepository.findByOwnerTypeAndOwnerId("vocabulary", id);
        readingRepository.deleteAll(oldReadings);

        if (dto.getReadings() != null && !dto.getReadings().isEmpty()) {
            List<Reading> newReadings = dto.getReadings().stream().map(r -> {
                r.setOwnerType("vocabulary");
                r.setOwnerId(id);
                return readingToEntity(r);
            }).collect(Collectors.toList());
            readingRepository.saveAll(newReadings);
        }

        return vocabToDTO(updated);
    }

    // ✅ Delete
    public void deleteVocabulary(UUID id) {
        checkAdminAccess();

        Vocabulary vocab = vocabularyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Từ vựng không tồn tại"));

        List<Reading> readings = readingRepository.findByOwnerTypeAndOwnerId("vocabulary", id);
        readingRepository.deleteAll(readings);

        vocabularyRepository.delete(vocab);
    }

    // ✅ Search
    public List<VocabularyDTOs> searchVocabulary(String keyword) {
        List<Vocabulary> results = vocabularyRepository.searchVocabulary(keyword); // @Query cần định nghĩa
        return results.stream().map(this::vocabToDTO).collect(Collectors.toList());
    }

    // 🔄 Entity → DTO
    private VocabularyDTOs vocabToDTO(Vocabulary vocab) {
        VocabularyDTOs dto = new VocabularyDTOs();
        dto.setVocalbId(vocab.getVocalbId());
        dto.setWords(vocab.getWords());
        dto.setMeanning(vocab.getMeanning());
        dto.setWordType(vocab.getWordType());
        dto.setScriptType(vocab.getScriptType());

        List<ReadingDTO> readingDTOs = readingRepository.findByOwnerTypeAndOwnerId("vocabulary", vocab.getVocalbId())
                .stream().map(this::readingToDTO).collect(Collectors.toList());
        dto.setReadings(readingDTOs);

        return dto;
    }

    private ReadingDTO readingToDTO(Reading r) {
        return new ReadingDTO(
                r.getReadingID(),
                r.getReadingText(),
                r.getReadingType(),
                r.getOwnerType(),
                r.getOwnerId()
        );
    }

    private Reading readingToEntity(ReadingDTO dto) {
        Reading r = new Reading();
        r.setReadingID(dto.getReadingID());
        r.setReadingText(dto.getReadingText());
        r.setReadingType(dto.getReadingType());
        r.setOwnerType(dto.getOwnerType());
        r.setOwnerId(dto.getOwnerId());
        return r;
    }

    private void checkAdminAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Bạn không có quyền thực hiện thao tác này.");
        }
    }
}
