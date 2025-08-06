    package com.example.Oboe.Controller;

    import com.example.Oboe.DTOs.FlashCardDto;
    import com.example.Oboe.DTOs.FlashcardSearchResultDTO;
    import com.example.Oboe.Entity.FlashCards;
    import com.example.Oboe.Service.FlashCardService;
    import com.example.Oboe.Util.JwtUtil;
    import com.example.Oboe.annotation.PremiumOnly;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.UUID;

    @RestController
    @RequestMapping("/api/flashcards")
    public class FlashCardController {

        @Autowired
        private FlashCardService flashCardService;

        @Autowired
        private JwtUtil jwtUtil;

        // Tạo flashcard
        @PostMapping
        public ResponseEntity<?> createFlashCard(@RequestBody FlashCardDto dto,
                                                 @AuthenticationPrincipal(expression = "userID") UUID userId) {
            FlashCards created = flashCardService.createFlashCard(dto, userId);
            return ResponseEntity.ok(created);
        }

        // Lấy danh sách flashcard của user, có phân trang
        @GetMapping
        public ResponseEntity<?> getUserFlashCards(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(required = false) String term,
                @AuthenticationPrincipal(expression = "userID") UUID userId
        ) {

            if (term != null && !term.isBlank()) {
                return ResponseEntity.ok(flashCardService.searchFlashCardsByTerm(userId, term, page, size));
            }

            return ResponseEntity.ok(flashCardService.getFlashCardsByUser(userId, page, size));
        }

        // Cập nhật flashcard
        @PutMapping("/{id}")
        public ResponseEntity<?> updateFlashCard(@PathVariable UUID id,
                                                 @RequestBody FlashCardDto dto,
                                                 @AuthenticationPrincipal(expression = "userID") UUID userId
        ) {
            FlashCards updated = flashCardService.updateFlashCard(id, dto, userId);
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(updated);
        }

        // Xoá flashcard
        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteFlashCard(@PathVariable UUID id,
                                                 @AuthenticationPrincipal(expression = "userID") UUID userId
        ) {
            boolean deleted = flashCardService.deleteFlashCard(id, userId);
            if (!deleted) return ResponseEntity.notFound().build();
            return ResponseEntity.ok().build();
        }

         @GetMapping("/latest")
         public ResponseEntity<?> getTop5LatestFlashCards(
                 @AuthenticationPrincipal(expression = "userID") UUID userId) {
             return ResponseEntity.ok(flashCardService.getTop5LatestFlashCards(userId));
         }


        @GetMapping("/all")
        public ResponseEntity<?> getAllFlashCards() {
            return ResponseEntity.ok(flashCardService.getAllFlashCards());
        }

        @GetMapping("/{id}")
        public ResponseEntity<?> getFlashcardById(@PathVariable("id") UUID id) {
            List<FlashCardDto> result = flashCardService.getFlashcardById(id);
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Flashcard không tồn tại với id: " + id);
            }
            return ResponseEntity.ok(result.get(0));
        }





    }



