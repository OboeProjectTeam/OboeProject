package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.AnsweredQuestionDTO;
import com.example.Oboe.DTOs.QuestionDTO;
import com.example.Oboe.Entity.CardItem;
import com.example.Oboe.Repository.CardItemRepository;
import com.example.Oboe.Service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.Oboe.DTOs.UserAnswerAIDTO;



import java.util.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private CardItemRepository cardItemRepository;

    @Autowired
    private GeminiService geminiService;

    @GetMapping("/generate-question/{cardItemId}")
    public List<QuestionDTO> generateQuestion(@PathVariable UUID cardItemId) {
        CardItem cardItem = cardItemRepository.findById(cardItemId).orElse(null);
        if (cardItem == null) {
            throw new RuntimeException("CardItem not found");
        }
        String prompt = buildPrompt(cardItem);
        return geminiService.generateQuestion(prompt);
    }

    @GetMapping("/generate-random-question")
    public List<QuestionDTO> generateRandomQuestion() {
        List<CardItem> allCardItems = cardItemRepository.findAll();
        if (allCardItems.isEmpty()) {
            throw new RuntimeException("No CardItems found in database");
        }
        Random random = new Random();
        CardItem randomCardItem = allCardItems.get(random.nextInt(allCardItems.size()));
        String prompt = buildPrompt(randomCardItem);
        return geminiService.generateQuestion(prompt);
    }

    private String buildPrompt(CardItem cardItem) {
        return """
            Tạo ra đúng 10 câu hỏi trắc nghiệm tiếng Nhật dựa trên từ vựng sau:

            - Từ vựng: "%s"
            - Nghĩa tiếng Việt: "%s"

            Yêu cầu:
            1. Mỗi câu hỏi có 4 lựa chọn .
            2. Chỉ 1 đáp án đúng.
            3. Trả về định dạng JSON như sau:

            [
                {
                    "question": "Câu hỏi",
                    "choices": [
                        "lựa chọn A",
                        "lựa chọn B",
                        "lựa chọn C",
                        "lựa chọn D"
                    ],
                    "answer": "Đáp án đúng"
                }
            ]

            Không thêm giải thích hay văn bản nào ngoài JSON.
            """.formatted(cardItem.getWord(), cardItem.getMeaning());
    }

    @PostMapping("/evaluate")
    public String evaluateAnswers(@RequestBody UserAnswerAIDTO request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("""
        Bạn là một giáo viên chấm bài thi tiếng Nhật.
        Hãy dựa vào từng câu hỏi, đáp án đúng, và câu trả lời của người dùng để:
        - Đưa ra điểm tổng kết theo thang điểm 100.
        - Phân tích rõ câu nào đúng, câu nào sai.
        - Đưa ra nhận xét tổng thể cho toàn bài làm.
       
        Dưới đây là danh sách câu hỏi và câu trả lời:
        """);

        int index = 1;
        for (AnsweredQuestionDTO q : request.getAnswers()) {
            prompt.append(String.format("""
            Câu %d:
            - Câu hỏi: %s
            - Phương án: %s
            - Đáp án đúng: %s
            - Câu trả lời của người dùng: %s

            """, index++, q.getQuestionName(), q.getOptions(), q.getCorrectAnswer(), q.getUserAnswer()));
        }

        prompt.append("""
                Hãy trả kết quả theo định dạng:
                {
                  "score": 85,
                  "results": [
                    {
                      "question": "Câu hỏi 1",
                      "correct": true,
                      "feedback": "Bạn trả lời đúng"
                    },
                    ...
                  ],
                  "comment": "Hãy viết phần nhận xét tổng thể theo phong cách thân thiện, rõ ràng và mang tính hỗ trợ học tập. Nội dung cần có:
                  1. Đánh giá trình độ hiện tại (ví dụ: bạn đang ở khoảng N5 hoặc đầu N4).
                  2. Nhận xét các phần làm tốt (ví dụ: bạn làm tốt phần từ vựng chủ đề trường học).
                  3. Phân tích các lỗi sai chính, ví dụ: sai mẫu ngữ pháp như ～ている hoặc hiểu sai nghĩa của từ đồng âm.
                  4. Gợi ý cụ thể để cải thiện, ví dụ:
                     - 'Bạn nên ôn lại mẫu ngữ pháp N5 như ～ませんか, ～ましょう bằng cách làm bài tập trong sách Minna no Nihongo Bài 5 đến Bài 8.'
                     - 'Bạn có thể luyện nghe các đoạn hội thoại ngắn về chủ đề gia đình để cải thiện khả năng phản xạ.'
                  5. Động viên hoặc khen ngợi, ví dụ: 'Tiến bộ rất tốt! Chỉ cần luyện tập thêm một chút là bạn có thể đạt N4. Hãy tiếp tục cố gắng nhé!'"
        }
        Không thêm dấu ``` hoặc định dạng markdown nào. Chỉ JSON thuần.
        """);

        try {
            String response = geminiService.generateTextFromPrompt(prompt.toString());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Lỗi khi đánh giá câu trả lời\"}";
        }
    }

}
