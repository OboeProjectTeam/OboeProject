package com.example.Oboe.Service;

import com.example.Oboe.Entity.*;
import com.example.Oboe.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchService {

    @Autowired
    private KanjiRepository kanjiRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private GrammarRepository grammarRepository;

    @Autowired
    private SampleSentenceRepository sampleSentenceRepository;

    @Autowired
    private ReadingRepository readingRepository;

    public List<Kanji> searchKanji(String keyword) {
        return kanjiRepository.searchKanji(keyword);
    }

    public List<Vocabulary> searchVocabulary(String keyword) {
        return vocabularyRepository.searchVocabulary(keyword);
    }

    public List<Grammar> searchGrammar(String keyword) {
        return grammarRepository.searchGrammar(keyword);
    }

    public List<SampleSentence> searchSentences(String keyword) {
        return sampleSentenceRepository.searchByVietnameseMeaning(keyword);
    }

    /**
     * Tìm tất cả kết quả liên quan đến từ khoá keyword, bao gồm tra từ, nghĩa hoặc cách đọc (romaji)
     */
    public Map<String, List<Map<String, Object>>> searchByKeyword(String keyword) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        // --- Vocabulary ---
        List<Map<String, Object>> vocabResults = new ArrayList<>();
        for (Vocabulary v : vocabularyRepository.searchVocabulary(keyword)) {
            Map<String, Object> map = new HashMap<>();
            map.put("vocabulary", v);
            map.put("readings", readingRepository.findByOwnerTypeAndOwnerId("vocabulary", v.getVocalbId()));
            vocabResults.add(map);
        }
        result.put("vocabulary", vocabResults);

        // --- Kanji ---
        List<Map<String, Object>> kanjiResults = new ArrayList<>();
        for (Kanji k : kanjiRepository.searchKanji(keyword)) {
            Map<String, Object> map = new HashMap<>();
            map.put("kanji", k);
            map.put("readings", readingRepository.findByOwnerTypeAndOwnerId("kanji", k.getKanjiId()));
            kanjiResults.add(map);
        }
        result.put("kanji", kanjiResults);

        // --- Grammar ---
        List<Map<String, Object>> grammarResults = new ArrayList<>();
        for (Grammar g : grammarRepository.searchGrammar(keyword)) {
            Map<String, Object> map = new HashMap<>();
            map.put("grammar", g);
            map.put("readings", readingRepository.findByOwnerTypeAndOwnerId("gramma", g.getGrammaID()));
            grammarResults.add(map);
        }
        result.put("grammar", grammarResults);

        // --- Reading (e.g. bagen, dekimashita, etc.) ---
        List<Reading> readings = readingRepository.searchReadingsByText(keyword);
        for (Reading r : readings) {
            String ownerType = r.getOwnerType();
            UUID ownerId = r.getOwnerId();

            Map<String, Object> item = new HashMap<>();
            item.put("reading", r);

            switch (ownerType) {
                case "vocabulary" -> vocabularyRepository.findById(ownerId).ifPresent(v -> {
                    item.put("vocabulary", v);
                    result.computeIfAbsent("vocabulary", k -> new ArrayList<>()).add(item);
                });
                case "kanji" -> kanjiRepository.findById(ownerId).ifPresent(k -> {
                    item.put("kanji", k);
                    result.computeIfAbsent("kanji", k2 -> new ArrayList<>()).add(item);
                });
                case "gramma" -> grammarRepository.findById(ownerId).ifPresent(g -> {
                    item.put("grammar", g);
                    result.computeIfAbsent("grammar", k2 -> new ArrayList<>()).add(item);
                });
            }
        }

        return result;
    }
}
