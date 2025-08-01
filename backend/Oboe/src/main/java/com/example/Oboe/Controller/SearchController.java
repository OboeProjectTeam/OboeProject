package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.*;
import com.example.Oboe.Repository.FlashCardRepository;
import com.example.Oboe.Repository.QuizzesRepository;
import com.example.Oboe.Repository.UserRepository;
import com.example.Oboe.Service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    // Search không phân loại (keyword ra flashcard + quiz + user)
    @GetMapping("/suggest")
    public ResponseEntity<SearchResultWrapperDTO> suggest(@RequestParam String keyword) {
        List<FlashcardSearchResultDTO> flashcards = flashCardRepository.searchFlashcardsByKeyword(keyword);
        List<QuizSearchResultDTO> quizzes = quizzesRepository.searchQuizzesByKeyword(keyword);
        List<UserSearchResultDTO> users = userRepository.searchUsersWithFlashcardCount(keyword);

        SearchResultWrapperDTO result = new SearchResultWrapperDTO();
        result.setFlashcards(flashcards);
        result.setQuizzes(quizzes);
        result.setUsers(users);

        return ResponseEntity.ok(result);
    }

    // Search theo keyword + type (ví dụ: type=user, type=quiz, type=flashcard)
    @GetMapping("/by-type")
    public List<Map<String, String>> searchByType(
            @RequestParam("keyword") String keyword,
            @RequestParam("type") String type
    ) {
        return searchService.searchByType(keyword, type);
    }
}
