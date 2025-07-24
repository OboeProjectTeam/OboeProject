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
            Map<String, String> paymentResult = momoService.createPayment(userId);
            return ResponseEntity.ok(paymentResult); // Trả về full thông tin: payUrl, orderId, requestId
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Lỗi tạo thanh toán",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/momo-notify")
    public ResponseEntity<String> handleMomoCallback(@RequestBody Map<String, String> payload) {
        momoService.handleMomoCallback(payload);
        return ResponseEntity.ok("success");
    }
}
