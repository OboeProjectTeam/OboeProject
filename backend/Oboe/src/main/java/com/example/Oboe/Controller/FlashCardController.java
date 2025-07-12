package com.example.Oboe.Controller;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.FlashCardDto;
import com.example.Oboe.Entity.FlashCards;
import com.example.Oboe.Service.FlashCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/flashcards")
public class FlashCardController {

    @Autowired
    private FlashCardService flashCardService;

    // 1. Tạo flashcard
    @PostMapping
    public FlashCards create(@RequestBody FlashCardDto dto,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        return flashCardService.createFlashCard(dto, userDetails.getUserID());
    }

    // 2. Lấy tất cả flashcards của người dùng
    @GetMapping
    public List<FlashCards> getMyFlashcards(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return flashCardService.getFlashCardsByUser(userDetails.getUserID());
    }

    // 3. Xoá flashcard (chỉ người tạo được xoá)
    @DeleteMapping("/{cardId}")
    public String delete(@PathVariable UUID cardId,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean deleted = flashCardService.deleteFlashCard(cardId, userDetails.getUserID());
        return deleted ? "Đã xoá" : "Không tìm thấy hoặc không có quyền xoá";
    }
}
