package com.example.Oboe.Controller;

import com.example.Oboe.Entity.*;
import com.example.Oboe.Service.SearchService;
import com.example.Oboe.Repository.ReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/search")
@CrossOrigin("*")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private ReadingRepository readingRepository;

    @GetMapping
    public Map<String, Object> search(@RequestParam("keyword") String keyword,
                                      @RequestParam("type") String type) {
        Map<String, Object> result = new HashMap<>();

        switch (type.toLowerCase()) {
            case "vocabulary" -> {
                List<Map<String, Object>> vocabResults = new ArrayList<>();
                for (Vocabulary v : searchService.searchVocabulary(keyword)) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("vocabulary", v);
                    item.put("readings", readingRepository.findByOwnerTypeAndOwnerId("vocabulary", v.getVocalbId()));
                    vocabResults.add(item);
                }
                result.put("vocabulary", vocabResults);
            }

            case "grammar" -> {
                List<Map<String, Object>> grammarResults = new ArrayList<>();
                for (Grammar g : searchService.searchGrammar(keyword)) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("grammar", g);
                    item.put("readings", readingRepository.findByOwnerTypeAndOwnerId("gramma", g.getGrammaID()));
                    grammarResults.add(item);
                }
                result.put("grammar", grammarResults);
            }

            case "sentence" -> {
                result.put("sentence", searchService.searchSentences(keyword));
            }

            case "kanji" -> {
                List<Map<String, Object>> kanjiResults = new ArrayList<>();
                for (Kanji k : searchService.searchKanji(keyword)) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("kanji", k);
                    item.put("readings", readingRepository.findByOwnerTypeAndOwnerId("kanji", k.getKanjiId()));
                    kanjiResults.add(item);
                }
                result.put("kanji", kanjiResults);
            }

            default -> result.put("error", "Loại tìm kiếm không hợp lệ: " + type);
        }

        return result;
    }
}
