package com.example.Oboe.Service;

import com.example.Oboe.Config.PayOsConfig;
import com.example.Oboe.DTOs.CustomWebhookDTO;
import com.example.Oboe.Entity.AccountType;
import com.example.Oboe.Entity.Payment;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.PaymentRepository;
import com.example.Oboe.Repository.UserRepository;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

import java.util.Optional;
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

    /**
     * Tạo link thanh toán cho người dùng cụ thể.
     */
    public CheckoutResponseData createPayment(int amount, String itemName, UUID userId) throws Exception {
        // Số tiền cố định
        int fixedAmount = 99000;

        // Tạo thông tin sản phẩm
        ItemData item = ItemData.builder()
                .name(itemName)
                .quantity(1)
                .price(fixedAmount)
                .build();

        // Tạo orderCode duy nhất bằng timestamp
        long tempOrderCode = System.currentTimeMillis();

        // Gói dữ liệu thanh toán
        PaymentData paymentData = PaymentData.builder()
                .orderCode(tempOrderCode)
                .amount(fixedAmount)
                .description("Pay Oboeru" + tempOrderCode)
                .returnUrl(config.getReturnUrl())
                .cancelUrl(config.getCancelUrl())
                .item(item)
                .build();

        // Gọi API PayOS tạo link thanh toán
        CheckoutResponseData response = payOS.createPaymentLink(paymentData);

        return response;
    }



    /**
     * Xử lý webhook từ PayOS sau khi thanh toán.
     */
    public WebhookData handleWebhook(Webhook webhookBody) throws Exception {
        WebhookData data = payOS.verifyPaymentWebhookData(webhookBody);

        long orderCode = data.getOrderCode();
        String status = data.getTransactionDateTime();

        Payment payment = paymentRepository.findByTransactionId(String.valueOf(orderCode));
        if (payment != null) {
            payment.setStatus(status.toUpperCase());
            paymentRepository.save(payment);

            if ("SUCCESS".equalsIgnoreCase(status)) {
                User user = payment.getUser();
                user.setAccountType(AccountType.PREMIUM);
                userRepository.save(user);
                System.out.println(" Người dùng " + user.getUserName() + " đã được nâng cấp lên PREMIUM.");
            }
        }

        return data;
    }


    /**
     * Hủy đơn thanh toán nếu cần.
     */
    public void cancelPayment(long orderCode, String reason) throws Exception {
        payOS.cancelPaymentLink(orderCode, reason);
    }

    /**
     * Lấy thông tin thanh toán đã tạo link.
     */
    public Object getPaymentInfo(long orderCode) throws Exception {
        return payOS.getPaymentLinkInformation(orderCode);
    }
    public void handleWebhook(CustomWebhookDTO webhookBody) {
        System.out.println("== Webhook PayOS ==");
        System.out.println("Code: " + webhookBody.getCode());
        System.out.println("OrderCode: " + webhookBody.getOrderCode());
        System.out.println("Amount: " + webhookBody.getAmount());
        System.out.println("Desc: " + webhookBody.getDesc());
        System.out.println("TransactionStatus: " + webhookBody.getTransactionStatus());
        System.out.println("Checksum (bỏ qua): " + webhookBody.getChecksum());

        Payment payment = paymentRepository.findByOrderCode(webhookBody.getOrderCode());

        if (payment != null) {
            payment.setStatus(webhookBody.getTransactionStatus());
            paymentRepository.save(payment);
            System.out.println("Đã cập nhật trạng thái đơn hàng.");
        } else {
            System.out.println("Không tìm thấy đơn hàng với orderCode: " + webhookBody.getOrderCode());
        }
    }
}
