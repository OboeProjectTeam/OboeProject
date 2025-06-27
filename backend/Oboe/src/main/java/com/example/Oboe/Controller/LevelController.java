package com.example.Oboe.Controller;

import com.example.Oboe.Entity.Level;
import com.example.Oboe.Repository.LevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/level")
public class LevelController {
    @Autowired
    private LevelRepository levelRepository;

    @PostMapping
    public ResponseEntity<Level> createLevel(@RequestBody Level level) {
        return ResponseEntity.ok(levelRepository.save(level));
    }

    @GetMapping
    public List<Level> getAll() {
        return levelRepository.findAll();
    }
}
