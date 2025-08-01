package com.example.Oboe.Controller;

import com.example.Oboe.DTOs.LearningMaterialDTO;
import com.example.Oboe.Service.LearningMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-materials")
public class LearningMaterialController {

    private final LearningMaterialService learningMaterialService;

    @Autowired
    public LearningMaterialController(LearningMaterialService learningMaterialService) {
        this.learningMaterialService = learningMaterialService;
    }

    // Lấy 3 học liệu từ 10 quiz đầu tiên (random trong 10)
    @GetMapping("/suggested")
    public ResponseEntity<List<LearningMaterialDTO>> getSuggestedMaterials() {
        List<LearningMaterialDTO> materials = learningMaterialService.getSuggestedMaterials();
        return ResponseEntity.ok(materials);
    }

    // Random 3 quiz từ toàn bộ danh sách (random bằng Java)
    @GetMapping("/random")
    public ResponseEntity<List<LearningMaterialDTO>> getRandomMaterials() {
        List<LearningMaterialDTO> materials = learningMaterialService.getSuggestedMaterialsRandom();
        return ResponseEntity.ok(materials);
    }

    // Random 3 quiz từ database (random bằng native query)
    @GetMapping("/random-db")
    public ResponseEntity<List<LearningMaterialDTO>> getRandomMaterialsFromDB() {
        List<LearningMaterialDTO> materials = learningMaterialService.getSuggestedMaterialsRandomFromDB();
        return ResponseEntity.ok(materials);
    }

}
