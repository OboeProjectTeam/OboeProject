package com.example.Oboe.Controller;

import com.example.Oboe.Service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    // Search theo keyword + type
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