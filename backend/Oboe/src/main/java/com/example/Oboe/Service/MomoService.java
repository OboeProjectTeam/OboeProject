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

    private static final String AMOUNT = "2000";

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

    public Map<String, String> createPayment(UUID userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String orderId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        String orderInfo = "Nâng cấp Premium cho người dùng: " + user.getUserName();
        String extraData = userId.toString(); // ✅ truyền đúng extraData vào rawData

        String rawData = buildRawData(orderId, requestId, orderInfo, extraData);
        String signature = HmacUtil.signSHA256(rawData, secretKey);

        System.out.println("==== TẠO CHỮ KÝ MOMO ====");
        System.out.println("RawData: " + rawData);
        System.out.println("Signature: " + signature);

        JSONObject payload = new JSONObject();
        payload.put("partnerCode", partnerCode);
        payload.put("accessKey", accessKey);
        payload.put("requestId", requestId);
        payload.put("amount", AMOUNT);
        payload.put("orderId", orderId);
        payload.put("orderInfo", orderInfo);
        payload.put("redirectUrl", returnUrl);
        payload.put("ipnUrl", notifyUrl);
        payload.put("extraData", extraData); // ✅ khớp với rawData
        payload.put("requestType", "captureWallet");
        payload.put("signature", signature);

        HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.toString().getBytes());
        }

        String response = new BufferedReader(new InputStreamReader(conn.getInputStream()))
                .lines().collect(Collectors.joining("\n"));

        JSONObject jsonResponse = new JSONObject(response);

        return Map.of(
                "payUrl", jsonResponse.getString("payUrl"),
                "orderId", orderId,
                "requestId", requestId
        );
    }

    private String buildRawData(String orderId, String requestId, String orderInfo, String extraData) {
        return "accessKey=" + accessKey +
                "&amount=" + AMOUNT +
                "&extraData=" + extraData +
                "&ipnUrl=" + notifyUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + returnUrl +
                "&requestId=" + requestId +
                "&requestType=captureWallet";
    }


    public void handleMomoCallback(Map<String, String> payload) {
        String resultCode = payload.get("resultCode");
        String amount = payload.get("amount");
        String extraData = payload.get("extraData");

        if ("0".equals(resultCode) && extraData != null && !extraData.isBlank()) {
            try {
                UUID userId = UUID.fromString(extraData);

                userRepository.findById(userId).ifPresent(user -> {
                    user.setAccountType(AccountType.PREMIUM);
                    userRepository.save(user);

                    Payment payment = new Payment();
                    payment.setAmount(Integer.parseInt(amount));
                    payment.setStatus("SUCCESS");
                    payment.setUser(user);
                    paymentRepository.save(payment);
                });

            } catch (IllegalArgumentException e) {
                System.err.println("Invalid UUID in extraData: " + extraData);
            }
        } else {
            System.err.println("Giao dịch thất bại hoặc thiếu thông tin callback.");
        }
    }
}
