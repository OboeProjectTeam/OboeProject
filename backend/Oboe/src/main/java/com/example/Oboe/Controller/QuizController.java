package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.QuizDTO;
import com.example.Oboe.Service.QuizzesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizzesService quizzesService;

    @GetMapping
    public List<QuizDTO> getAll() {
        return quizzesService.getAll();
    }
    @GetMapping("/{id}")
    public QuizDTO getById(@PathVariable UUID id) {
        return quizzesService.getById(id);
    }
    @PostMapping
    public QuizDTO create(@RequestBody QuizDTO quizDTO) {
        return quizzesService.create(quizDTO);
    }
    @PutMapping("/{id}")
    public QuizDTO update(@PathVariable UUID id, @RequestBody QuizDTO quizDTO) {
        return quizzesService.update(id, quizDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        quizzesService.delete(id);
        return ResponseEntity.ok(" Xóa Quizzes  thành công");
    }


}
