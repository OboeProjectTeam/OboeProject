package com.example.Oboe.Service;


import com.example.Oboe.Entity.AccountType;
import com.example.Oboe.Entity.Payment;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.PaymentRepository;
import com.example.Oboe.Repository.UserRepository;
import com.example.Oboe.Util.HmacUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MomoService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${momo.partnerCode}")
    private String partnerCode;

    @Value("${momo.accessKey}")
    private String accessKey;

    @Value("${momo.secretKey}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String endpoint;

    @Value("${momo.returnUrl}")
    private String returnUrl;

    @Value("${momo.notifyUrl}")
    private String notifyUrl;

    public String createPayment(UUID userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String orderId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        String amount = "2000";
        String orderInfo = "Nâng cấp Premium cho người dùng: " + user.getUserName();

        String rawData = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" +
                "&ipnUrl=" + notifyUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + returnUrl +
                "&requestId=" + requestId +
                "&requestType=captureWallet";

        String signature = HmacUtil.signSHA256(rawData, secretKey);

        JSONObject json = new JSONObject();
        json.put("partnerCode", partnerCode);
        json.put("accessKey", accessKey);
        json.put("requestId", requestId);
        json.put("amount", amount);
        json.put("orderId", orderId);
        json.put("orderInfo", orderInfo);
        json.put("redirectUrl", returnUrl);
        json.put("ipnUrl", notifyUrl);
        json.put("extraData", "");
        json.put("requestType", "captureWallet");
        json.put("signature", signature);

        HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.toString().getBytes());
        }

        String response = new BufferedReader(new InputStreamReader(conn.getInputStream()))
                .lines().collect(Collectors.joining("\n"));

        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getString("payUrl");
    }

    public void handleMomoCallback(Map<String, String> payload) {
        String orderId = payload.get("orderId");
        String resultCode = payload.get("resultCode");
        String amount = payload.get("amount");

        if ("0".equals(resultCode)) {
            // Thành công → xử lý logic upgrade
            // Mặc định ta tìm user có cùng orderId trong bảng Payment
            // Hoặc gắn orderId với user theo logic riêng nếu cần

            // Giả sử bạn gửi thêm userId trong extraData (có thể mở rộng)
            UUID userId = UUID.fromString(payload.get("extraData")); // cần validate trước

            userRepository.findById(userId).ifPresent(user -> {
                user.setAccountType(AccountType.PREMIUM);
                userRepository.save(user);

                Payment payment = new Payment();
                payment.setAmount(amount);
                payment.setStatus("SUCCESS");
                payment.setUser(user);
                paymentRepository.save(payment);
            });
        }
    }
}

