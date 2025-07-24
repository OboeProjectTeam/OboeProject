package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.QuestionDTO;
import com.example.Oboe.Service.QuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
public class QuestionsController {

    @Autowired
    private QuestionsService questionsService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody List<QuestionDTO> dtoList) {
        List<QuestionDTO> created = questionsService.create(dtoList);
        Map<String, Object> response = new HashMap<>();
        response.put("thông báo", "thêm câu thành công");
        response.put("data",created);
        response.put("số lượng câu hỏi đã thêm",created.size());
        return ResponseEntity.ok(response);

    }
    //// API lấy danh sách câu hỏi theo quizId, có hỗ trợ phân trang
    @GetMapping("/by-quiz/{quizId}")
    public ResponseEntity<Map<String, Object>> getByQuizPaged(
            @PathVariable UUID quizId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QuestionDTO> pageResult = questionsService.getQuestionsByQuizId(quizId, pageable);

        /// Tạo map để cấu trúc lại dữ liệu phân trang trả về cho dữ liệu đẹp
        Map<String, Object> response = new HashMap<>();
        response.put("thông báo", "lấy ra toàn bộ câu hỏi thành công");
        response.put("content", pageResult.getContent());
        response.put("pageNumber", pageResult.getNumber());
        response.put("pageSize", pageResult.getSize());
        response.put("totalElements", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        response.put("last", pageResult.isLast());
        return ResponseEntity.ok(response);
    }

}
