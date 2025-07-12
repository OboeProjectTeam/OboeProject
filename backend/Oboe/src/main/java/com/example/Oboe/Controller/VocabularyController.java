package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.VocabularyDTOs;
import com.example.Oboe.Service.VocabularyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/vocabulary")
public class VocabularyController {

    private final VocabularyService vocabularyService;

    public VocabularyController(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    // GET /api/vocabulary?page=0&size=10
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllVocabulary(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(vocabularyService.getAllVocabulary(page, size));
    }

    // GET /api/vocabulary/{id}
    @GetMapping("/{id}")
    public ResponseEntity<VocabularyDTOs> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(vocabularyService.getVocabularyById(id));
    }

    // POST /api/vocabulary (ROLE_ADMIN)
    @PostMapping
    public ResponseEntity<VocabularyDTOs> create(@RequestBody VocabularyDTOs dto) {
        return ResponseEntity.ok(vocabularyService.createVocabulary(dto));
    }

    // PUT /api/vocabulary/{id} (ROLE_ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<VocabularyDTOs> update(
            @PathVariable UUID id,
            @RequestBody VocabularyDTOs dto
    ) {
        return ResponseEntity.ok(vocabularyService.updateVocabulary(id, dto));
    }

    // DELETE /api/vocabulary/{id} (ROLE_ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        vocabularyService.deleteVocabulary(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/vocabulary/search?keyword=sakura
    @GetMapping("/search")
    public ResponseEntity<List<VocabularyDTOs>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(vocabularyService.searchVocabulary(keyword));
    }
}
