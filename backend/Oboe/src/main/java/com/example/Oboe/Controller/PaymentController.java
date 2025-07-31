package com.example.Oboe.Controller;
import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.DTOs.CustomWebhookDTO;
import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.UserRepository;
import org.springframework.security.core.Authentication;
import vn.payos.type.Webhook;
import com.example.Oboe.Service.MomoService;
import com.example.Oboe.Service.PayOsService;
import com.example.Oboe.DTOs.CustomWebhookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private PayOsService payOsService;
    @Autowired
    private UserRepository userRepository;
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

    @PostMapping("/payos")
    public ResponseEntity<?> payWithPayOS(@RequestParam(required = false) Integer amount,
                                          Authentication authentication) {
        try {
            CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();

            UUID userId = customUser.getUserID();
            String username = customUser.getUsername();
            AuthProvider provider = customUser.getAuthProvider();

            System.out.println("🔐 Username: " + username);
            System.out.println("🔐 Provider: " + provider);
            System.out.println("🔐 UserID: " + userId);

            String itemName = "Thanh toán Oboeru";

            var result = payOsService.createPayment(99000, itemName, userId); // fixed amount

            if (result == null) {
                return ResponseEntity.status(500).body(Map.of(
                        "error", "Lỗi tạo thanh toán PayOS",
                        "message", "Kết quả trả về từ PayOS là null"
                ));
            }

            // Debug kết quả trả về
            System.out.println("✅ Checkout URL: " + result.getCheckoutUrl());
            System.out.println("✅ Order Code: " + result.getOrderCode());
            System.out.println("✅ Expired At: " + result.getExpiredAt());

            String qrUrl = "https://chart.googleapis.com/chart?cht=qr&chs=300x300&chl=" +
                    URLEncoder.encode(result.getCheckoutUrl() != null ? result.getCheckoutUrl() : "", StandardCharsets.UTF_8);

            // Dùng HashMap để tránh lỗi khi có value null
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("checkoutUrl", result.getCheckoutUrl());
            response.put("orderCode", result.getOrderCode());
            response.put("expiredAt", result.getExpiredAt());
            response.put("qrUrl", qrUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Lỗi tạo thanh toán PayOS");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }




    @PostMapping("/payos-notify")
    public ResponseEntity<String> handlePayOsCallback(@RequestBody CustomWebhookDTO webhookBody) throws Exception {
        payOsService.handleWebhook(webhookBody);
        return ResponseEntity.ok("success");
    }

}
