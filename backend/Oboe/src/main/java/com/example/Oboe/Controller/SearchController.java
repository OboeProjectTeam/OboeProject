package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.*;
import com.example.Oboe.Repository.CardItemRepository;
import com.example.Oboe.Repository.FlashCardRepository;
import com.example.Oboe.Repository.QuizzesRepository;
import com.example.Oboe.Repository.UserRepository;
import com.example.Oboe.Service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private FlashCardRepository flashCardRepository;

    @Autowired
    private QuizzesRepository quizzesRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CardItemRepository cardItemRepository;

    // Search không phân loại (keyword ra flashcard + quiz + user)
    @GetMapping("/searchkeyword")
    public ResponseEntity<?> suggest(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String type // flashcard | quiz | user | null (mặc định lấy hết)
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> response = new HashMap<>();

        if (type == null || type.equalsIgnoreCase("flashcard")) {
            Page<FlashcardSearchResultDTO> flashcardPage = flashCardRepository.searchFlashcardsByKeyword(keyword, pageable);

            // Gọi repository để lấy cardItems theo từng flashcard
            List<FlashcardSearchResultDTO> enrichedFlashcards = flashcardPage.getContent().stream()
                    .peek(dto -> {
                        List<CardItemDto> items = cardItemRepository.findAllByFlashCardId(dto.getFlashcardId());
                        dto.setCardItems(items);
                    })
                    .collect(Collectors.toList());

            Map<String, Object> flashcardMap = Map.of(
                    "content", enrichedFlashcards,
                    "page", flashcardPage.getNumber(),
                    "size", flashcardPage.getSize(),
                    "totalElements", flashcardPage.getTotalElements(),
                    "totalPages", flashcardPage.getTotalPages(),
                    "last", flashcardPage.isLast()
            );
            response.put("flashcards", flashcardMap);
        }

        if (type == null || type.equalsIgnoreCase("quiz")) {
            Page<QuizSearchResultDTO> quizPage = quizzesRepository.searchQuizzesByKeyword(keyword, pageable);
            Map<String, Object> quizMap = Map.of(
                    "content", quizPage.getContent(),
                    "page", quizPage.getNumber(),
                    "size", quizPage.getSize(),
                    "totalElements", quizPage.getTotalElements(),
                    "totalPages", quizPage.getTotalPages(),
                    "last", quizPage.isLast()
            );
            response.put("quizzes", quizMap);
        }

        if (type == null || type.equalsIgnoreCase("user")) {
            Page<UserSearchResultDTO> userPage = userRepository.searchUsersWithFlashcardCount(keyword, pageable);
            Map<String, Object> userMap = Map.of(
                    "content", userPage.getContent(),
                    "page", userPage.getNumber(),
                    "size", userPage.getSize(),
                    "totalElements", userPage.getTotalElements(),
                    "totalPages", userPage.getTotalPages(),
                    "last", userPage.isLast()
            );
            response.put("users", userMap);
        }

        return ResponseEntity.ok(response);
    }




    // Search theo keyword + type
    @GetMapping("/by-type")
    public List<Map<String, String>> searchByType(
            @RequestParam("keyword") String keyword,
            @RequestParam("type") String type
    ) {
        return searchService.searchByType(keyword, type);
    }
    @GetMapping
    public List<Map<String, String>> search(
            @RequestParam("keyword") String keyword,
            @RequestParam("type") String type
    ) {
        return searchService.searchByType(keyword, type);
    }
    // Gợi ý tất cả loại (nếu không phân loại)
    @GetMapping("/suggest")
    public List<Map<String, String>> suggest(@RequestParam("keyword") String keyword) {
        return searchService.suggestAllTypes(keyword);
    }
}