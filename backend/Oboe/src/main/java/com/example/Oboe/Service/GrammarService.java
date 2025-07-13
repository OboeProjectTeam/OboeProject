package com.example.Oboe.Service;

import com.example.Oboe.DTOs.GrammarDTO;
import com.example.Oboe.DTOs.ReadingDTO;
import com.example.Oboe.Entity.Grammar;
import com.example.Oboe.Entity.Reading;
import com.example.Oboe.Repository.GrammarRepository;
import com.example.Oboe.Repository.ReadingRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class GrammarService {

    private final ReadingRepository readingRepository;
    private final GrammarRepository grammarRepository;

    public GrammarService(GrammarRepository grammarRepository, ReadingRepository readingRepository) {
        this.grammarRepository = grammarRepository;
        this.readingRepository = readingRepository;
    }

    // Get all grammar with pagination
    public Map<String, Object> getAllGrammar(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Grammar> grammarPage = grammarRepository.findAll(pageable);

        List<GrammarDTO> grammarDTOs = grammarPage.getContent()
                .stream()
                .map(this::grammarToDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("grammars", grammarDTOs);
        response.put("currentPage", grammarPage.getNumber());
        response.put("pageSize", grammarPage.getSize());
        response.put("totalElements", grammarPage.getTotalElements());
        response.put("totalPages", grammarPage.getTotalPages());
        response.put("isLastPage", grammarPage.isLast());

        return response;
    }

    //  Create new grammar (ROLE_ADMIN)
    public GrammarDTO createGrammar(GrammarDTO dto) {
        checkAdminAccess();

        Grammar grammar = new Grammar();
        grammar.setStructure(dto.getStructure());
        grammar.setExplanation(dto.getExplanation());
        grammar.setExample(dto.getExample());
        grammar.setGrammarType(dto.getGrammarType());

        Grammar saved = grammarRepository.save(grammar);

        if (dto.getReadings() != null && !dto.getReadings().isEmpty()) {
            List<Reading> readings = dto.getReadings().stream().map(r -> {
                r.setOwnerType("grammar");
                r.setOwnerId(saved.getGrammaID());
                return readingToEntity(r);
            }).collect(Collectors.toList());
            readingRepository.saveAll(readings);
        }

        return grammarToDTO(saved);
    }

    //  Get grammar by ID
    public GrammarDTO getGrammarById(UUID id) {
        Grammar grammar = grammarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Grammar với ID: " + id));
        return grammarToDTO(grammar);
    }

    //  Update grammar (ROLE_ADMIN)
    public GrammarDTO updateGrammar(UUID id, GrammarDTO dto) {
        checkAdminAccess();

        Grammar grammar = grammarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grammar không tồn tại"));

        if (dto.getStructure() != null) grammar.setStructure(dto.getStructure());
        if (dto.getExplanation() != null) grammar.setExplanation(dto.getExplanation());
        if (dto.getExample() != null) grammar.setExample(dto.getExample());
        if (dto.getGrammarType() != null) grammar.setGrammarType(dto.getGrammarType());

        Grammar updated = grammarRepository.save(grammar);

        // Xoá readings cũ
        List<Reading> oldReadings = readingRepository.findByOwnerTypeAndOwnerId("grammar", grammar.getGrammaID());
        readingRepository.deleteAll(oldReadings);

        // Thêm readings mới nếu có
        if (dto.getReadings() != null && !dto.getReadings().isEmpty()) {
            List<Reading> newReadings = dto.getReadings().stream().map(r -> {
                r.setOwnerType("grammar");
                r.setOwnerId(grammar.getGrammaID());
                return readingToEntity(r);
            }).collect(Collectors.toList());
            readingRepository.saveAll(newReadings);
        }

        return grammarToDTO(updated);
    }

    //  Delete grammar (ROLE_ADMIN)
    public void deleteGrammar(UUID id) {
        checkAdminAccess();

        Grammar grammar = grammarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grammar không tồn tại"));

        // Xoá readings trước
        List<Reading> readings = readingRepository.findByOwnerTypeAndOwnerId("grammar", grammar.getGrammaID());
        readingRepository.deleteAll(readings);

        grammarRepository.delete(grammar);
    }

    //  Search grammar
    public List<GrammarDTO> searchGrammar(String keyword) {
        List<Grammar> grammars = grammarRepository.searchGrammar(keyword); // cần @Query bên repo
        return grammars.stream()
                .map(this::grammarToDTO)
                .collect(Collectors.toList());
    }

    //  Convert Grammar → DTO
    private GrammarDTO grammarToDTO(Grammar grammar) {
        GrammarDTO dto = new GrammarDTO();
        dto.setGrammarId(grammar.getGrammaID().toString());
        dto.setStructure(grammar.getStructure());
        dto.setExplanation(grammar.getExplanation());
        dto.setExample(grammar.getExample());
        dto.setGrammarType(grammar.getGrammarType());

        // Lấy danh sách Reading
        List<ReadingDTO> readingDTOs = readingRepository
                .findByOwnerTypeAndOwnerId("grammar", grammar.getGrammaID())
                .stream()
                .map(this::readingToDTO)
                .collect(Collectors.toList());

        dto.setReadings(readingDTOs);
        return dto;
    }

    //  Convert Reading → DTO
    private ReadingDTO readingToDTO(Reading reading) {
        return new ReadingDTO(
                reading.getReadingID(),
                reading.getReadingText(),
                reading.getReadingType(),
                reading.getOwnerType(),
                reading.getOwnerId()
        );
    }

    //  Convert DTO → Reading entity
    private Reading readingToEntity(ReadingDTO dto) {
        Reading reading = new Reading();
        reading.setReadingID(dto.getReadingID());
        reading.setReadingText(dto.getReadingText());
        reading.setReadingType(dto.getReadingType());
        reading.setOwnerType(dto.getOwnerType());
        reading.setOwnerId(dto.getOwnerId());
        return reading;
    }

    //  Check role
    private void checkAdminAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Bạn không có quyền thực hiện thao tác này.");
        }
    }
}
