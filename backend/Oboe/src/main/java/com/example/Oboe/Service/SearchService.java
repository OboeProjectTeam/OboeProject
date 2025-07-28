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

    // Gợi ý tất cả các loại (dành cho /suggest)
    public List<Map<String, String>> suggestAllTypes(String keyword) {
        List<Map<String, String>> suggestions = new ArrayList<>();

        suggestions.addAll(searchByType(keyword, "vocabulary"));
        suggestions.addAll(searchByType(keyword, "kanji"));
        suggestions.addAll(searchByType(keyword, "grammar"));
        suggestions.addAll(searchByType(keyword, "sentence"));

        return suggestions;
    }

    // Tìm kiếm theo keyword và type cụ thể (dành cho /api/search?keyword=...&type=...)
    public List<Map<String, String>> searchByType(String keyword, String type) {
        List<Map<String, String>> suggestions = new ArrayList<>();

        switch (type.toLowerCase()) {
            case "vocabulary":
                for (Vocabulary v : vocabularyRepository.searchVocabulary(keyword)) {
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "vocabulary");
                    item.put("id", v.getVocalbId().toString());
                    item.put("word", v.getWords());
                    item.put("reading", v.getVietnamesePronunciation());
                    item.put("meaning", v.getMeanning());
                    suggestions.add(item);
                }
                break;

            case "kanji":
                for (Kanji k : kanjiRepository.searchKanji(keyword)) {
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "kanji");
                    item.put("id", k.getKanjiId().toString());
                    item.put("word", k.getCharacter_name());
                    item.put("reading", k.getVietnamesePronunciation());
                    item.put("meaning", k.getMeaning());
                    suggestions.add(item);
                }
                break;

            case "grammar":
                for (Grammar g : grammarRepository.searchGrammar(keyword)) {
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "grammar");
                    item.put("id:",g.getGrammaID().toString());
                    item.put("word", g.getStructure());
                    item.put("reading", g.getVietnamesePronunciation());
                    item.put("meaning", g.getExplanation());
                    suggestions.add(item);
                }
                break;

            case "sentence":
                for (SampleSentence s : sampleSentenceRepository.searchByVietnameseMeaning(keyword)) {
                    Map<String, String> item = new HashMap<>();
                    item.put("type", "sentence");
                    item.put("id:",s.getSample_sentence_id().toString());
                    item.put("word", s.getJapaneseText());
                    item.put("reading", s.getVietnamesePronunciation());
                    item.put("meaning", s.getVietnameseMeaning());
                    suggestions.add(item);
                }
                break;

            default:
                throw new IllegalArgumentException("Type không hợp lệ: " + type);
        }

        return suggestions;
    }
}
