package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.GrammarDTO;
import com.example.Oboe.Service.GrammarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/grammar")
public class GrammarController {

    private final GrammarService grammarService;

    public GrammarController(GrammarService grammarService) {
        this.grammarService = grammarService;
    }

    // Lấy danh sách grammar có phân trang
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllGrammar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(grammarService.getAllGrammar(page, size));
    }

    // Lấy grammar theo ID
    @GetMapping("/{id}")
    public ResponseEntity<GrammarDTO> getGrammarById(@PathVariable UUID id) {
        return ResponseEntity.ok(grammarService.getGrammarById(id));
    }

    // Tạo mới grammar (ROLE_ADMIN)
    @PostMapping
    public ResponseEntity<GrammarDTO> createGrammar(@RequestBody GrammarDTO dto) {
        GrammarDTO created = grammarService.createGrammar(dto);
        return ResponseEntity.ok(created);
    }

    // Cập nhật grammar (ROLE_ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<GrammarDTO> updateGrammar(
            @PathVariable UUID id,
            @RequestBody GrammarDTO dto
    ) {
        GrammarDTO updated = grammarService.updateGrammar(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Xoá grammar (ROLE_ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrammar(@PathVariable UUID id) {
        grammarService.deleteGrammar(id);
        return ResponseEntity.noContent().build();
    }

    // Tìm kiếm grammar theo từ khoá
    @GetMapping("/search")
    public ResponseEntity<List<GrammarDTO>> searchGrammar(@RequestParam String keyword) {
        return ResponseEntity.ok(grammarService.searchGrammar(keyword));
    }
}
