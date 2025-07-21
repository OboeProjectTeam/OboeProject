package com.example.Oboe.Controller;

import com.example.Oboe.Service.MomoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private MomoService momoService;

    @PostMapping("/momo")
    public ResponseEntity<?> payWithMomo(@RequestParam UUID userId) {
        try {
            String payUrl = momoService.createPayment(userId);
            return ResponseEntity.ok(Map.of("payUrl", payUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi tạo thanh toán: " + e.getMessage());
        }
    }

    @PostMapping("/momo-notify")
    public ResponseEntity<String> handleMomoCallback(@RequestBody Map<String, String> payload) {
        momoService.handleMomoCallback(payload);
        return ResponseEntity.ok("success");
    }
}

