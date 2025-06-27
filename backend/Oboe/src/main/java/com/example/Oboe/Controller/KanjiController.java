package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.KanjiDTO;
import com.example.Oboe.Service.KanjiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/kanji")
@Validated
public class KanjiController {
    @Autowired
    private  KanjiService kanjiService;

    @GetMapping
    public ResponseEntity<List<KanjiDTO>> getAllKanjis() {
        return ResponseEntity.ok(kanjiService.getAllKanjis());
    }
    @GetMapping("/{id}")
    public ResponseEntity<KanjiDTO> getKanjiById(@PathVariable UUID id) {
        return ResponseEntity.ok(kanjiService.getKanjiById(id));
    }

    @PostMapping
    public ResponseEntity<?> createKanji(@Valid @RequestBody KanjiDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            KanjiDTO created = kanjiService.createKanji(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
   public ResponseEntity<?> deleteKanji(@PathVariable UUID id) {
        try {
            boolean deleted = kanjiService.deleteKanji(id);
            if(deleted){
                return ResponseEntity.ok("Xoá Kanji thành công!");
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không tìm thấy Kanji để xóa");
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("lỗi khi xóa " + e.getMessage());
        }
    }
    @GetMapping("/search")
    public ResponseEntity<List<KanjiDTO>> searchKanji(@RequestParam String keyword) {
        List<KanjiDTO> result = kanjiService.searchKanji(keyword);
        return ResponseEntity.ok(result);
    }


}
