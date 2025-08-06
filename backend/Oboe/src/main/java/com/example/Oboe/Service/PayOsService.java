package com.example.Oboe.Service;

import com.example.Oboe.Config.PayOsConfig;
import com.example.Oboe.Entity.AccountType;
import com.example.Oboe.Entity.Payment;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.PaymentRepository;
import com.example.Oboe.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.WebhookData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PayOsService {

    private final PayOS payOS;
    private final PayOsConfig config;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public PayOsService(PayOS payOS,
                        PayOsConfig config,
                        PaymentRepository paymentRepository,
                        UserRepository userRepository) {
        this.payOS = payOS;
        this.config = config;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    public CheckoutResponseData createPayment(int amount, String itemName, UUID userId) throws Exception {
        int fixedAmount = 99000;

        ItemData item = ItemData.builder()
                .name(itemName)
                .quantity(1)
                .price(fixedAmount)
                .build();

        long tempOrderCode = System.currentTimeMillis();

        PaymentData paymentData = PaymentData.builder()
                .orderCode(tempOrderCode)
                .amount(fixedAmount)
                .description("Pay Oboeru " + tempOrderCode)
                .returnUrl(config.getReturnUrl())
                .cancelUrl(config.getCancelUrl())
                .items(List.of(item))
                .build();

        CheckoutResponseData response = payOS.createPaymentLink(paymentData);

        // Save to DB
        Payment payment = new Payment();
        payment.setOrderCode(tempOrderCode);
        payment.setAmount(fixedAmount);
        payment.setUser(userRepository.findById(userId).orElse(null));
        payment.setStatus("PENDING");
        paymentRepository.save(payment);

        return response;
    }

    public WebhookData handleWebhook(String rawJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        vn.payos.type.Webhook webhook = mapper.readValue(rawJson, vn.payos.type.Webhook.class);
        WebhookData data = payOS.verifyPaymentWebhookData(webhook);
//        WebhookData data = webhook.getData(); // Bỏ verify chữ ký khi test

        long orderCode = data.getOrderCode();
        String code = data.getCode();

        Payment payment = paymentRepository.findByOrderCode(orderCode);
        if (payment != null) {
            String status;

            switch (code) {
                case "00" -> {
                    status = "SUCCESS";
                    payment.setPaidAt(LocalDateTime.now());
                    User user = payment.getUser();
                    if (user.getAccountType() != AccountType.PREMIUM) {
                        user.setAccountType(AccountType.PREMIUM);

                        userRepository.save(user);
                    }
                }
                case "09" -> status = "CANCELLED";
                default -> status = "FAILED";
            }


            payment.setStatus(status);
            paymentRepository.save(payment);
        }

        return data;
    }

    public void cancelPayment(long orderCode, String reason) throws Exception {
        payOS.cancelPaymentLink(orderCode, reason);
    }

    public Object getPaymentInfo(long orderCode) throws Exception {
        return payOS.getPaymentLinkInformation(orderCode);
    }
}
