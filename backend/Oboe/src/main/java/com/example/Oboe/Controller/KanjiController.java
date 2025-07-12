package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.KanjiDTOs;
import com.example.Oboe.Service.KanjiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/kanji")
public class KanjiController {

    private final KanjiService kanjiService;

    public KanjiController(KanjiService kanjiService) {
        this.kanjiService = kanjiService;
    }

    //  GET all kanji
    @GetMapping
    public ResponseEntity<Map<String, Object>> getKanjis(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    )
    {
        return ResponseEntity.ok(kanjiService.getAllKanji(page, size));
    }


    //  GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<KanjiDTOs> getKanjiById(@PathVariable UUID id) {
        KanjiDTOs dto = kanjiService.getKanjiByKanjiId(id);
        return ResponseEntity.ok(dto);
    }

    //  POST - create (ADMIN only)
    @PostMapping
    public ResponseEntity<KanjiDTOs> createKanji(@RequestBody KanjiDTOs dto) {
        try {
            KanjiDTOs saved = kanjiService.createKanji(dto);
            return ResponseEntity.ok(saved);
        } catch (SecurityException e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }

    //  PUT - update (ADMIN only)
    @PutMapping("/{id}")
    public ResponseEntity<KanjiDTOs> updateKanji(@PathVariable UUID id, @RequestBody KanjiDTOs dto) {
        try {
            KanjiDTOs updated = kanjiService.updateKanji(dto, id);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }

    // DELETE - delete (ADMIN only)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteKanji(@PathVariable UUID id) {
        try {
            kanjiService.deleteKanji(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (SecurityException e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }
    @GetMapping("/search")
    public ResponseEntity<?> searchKanji(@RequestParam String keyword) {
        return ResponseEntity.ok(kanjiService.searchKanji(keyword));
    }
    // từ liên quan
    @GetMapping("/{kanjiId}/related")
    public ResponseEntity<List<KanjiDTOs>> getRelatedKanjis(@PathVariable UUID kanjiId) {
        return ResponseEntity.ok(kanjiService.getRelatedKanji(kanjiId));
    }


}
