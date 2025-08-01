package com.example.Oboe.Service;

import com.example.Oboe.DTOs.*;
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
    private UserRepository userRepository;

    @Autowired
    private QuizzesRepository quizzesRepository;

    @Autowired
    private FlashCardRepository flashCardRepository;

    // Gợi ý tất cả các loại học liệu (cho /suggest)
    public List<Map<String, String>> suggestAllTypes(String keyword) {
        List<Map<String, String>> suggestions = new ArrayList<>();

        suggestions.addAll(searchByType(keyword, "vocabulary"));
        suggestions.addAll(searchByType(keyword, "kanji"));
        suggestions.addAll(searchByType(keyword, "grammar"));
        suggestions.addAll(searchByType(keyword, "sentence"));

        return suggestions;
    }

    // Tìm theo type cụ thể (học liệu)
    public List<Map<String, String>> searchByType(String keyword, String type) {
        List<Map<String, String>> suggestions = new ArrayList<>();

        switch (type.toLowerCase()) {
            case "vocabulary":
                for (Vocabulary v : vocabularyRepository.searchVocabulary(keyword)) {
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "vocabulary");
                    item.put("word", v.getWords());
                    item.put("reading", v.getVietnamesePronunciation());
                    item.put("meaning", v.getMeanning());
                    item.put("id", v.getVocalbId().toString());
                    suggestions.add(item);
                }
                break;

            case "kanji":
                for (Kanji k : kanjiRepository.searchKanji(keyword)) {
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "kanji");
                    item.put("word", k.getCharacter_name());
                    item.put("reading", k.getVietnamesePronunciation());
                    item.put("meaning", k.getMeaning());
                    item.put("id", k.getKanjiId().toString());
                    suggestions.add(item);
                }
                break;

            case "grammar":
                for (Grammar g : grammarRepository.searchGrammar(keyword)) {
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "grammar");
                    item.put("word", g.getStructure());
                    item.put("reading", g.getVietnamesePronunciation());
                    item.put("meaning", g.getExplanation());
                    item.put("id", g.getGrammaID().toString());
                    suggestions.add(item);
                }
                break;

            case "sentence":
                for (SampleSentence s : sampleSentenceRepository.searchByVietnameseMeaning(keyword)) {
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "sentence");
                    item.put("word", s.getJapaneseText());
                    item.put("reading", s.getVietnamesePronunciation());
                    item.put("meaning", s.getVietnameseMeaning());
                    item.put("id", s.getSample_sentence_id().toString());
                    suggestions.add(item);
                }
                break;

            default:
                throw new IllegalArgumentException("Type không hợp lệ: " + type);
        }

        return suggestions;
    }


}
