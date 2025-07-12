<template>
  <div class="payment-guide-page">
    <div class="payment-guide-container">
      <div class="payment-guide-header">
        <h1>Hướng Dẫn Thanh Toán</h1>
        <p class="last-updated">Cập nhật lần cuối: {{ lastUpdated }}</p>
      </div>

      <div class="payment-guide-content">
        <section class="guide-section">
          <h2>1. Gói Dịch Vụ</h2>
          <div class="sub-section">
            <h3>1.1. Oboe Free</h3>
            <ul>
              <li>Truy cập các tính năng cơ bản</li>
              <li>Học với bộ thẻ flashcard có sẵn</li>
              <li>Tham gia diễn đàn cộng đồng</li>
              <li>Tra cứu từ điển cơ bản</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>1.2. Oboe Premium</h3>
            <ul>
              <li>Tất cả tính năng của gói Free</li>
              <li>Tạo không giới hạn bộ thẻ flashcard</li>
              <li>Truy cập toàn bộ ngân hàng đề thi JLPT</li>
              <li>Công cụ phân tích học tập nâng cao</li>
              <li>Ưu tiên hỗ trợ kỹ thuật</li>
              <li>Không hiển thị quảng cáo</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>2. Phương Thức Thanh Toán</h2>
          <div class="sub-section">
            <h3>2.1. Thanh Toán Qua Thẻ</h3>
            <p>Chúng tôi chấp nhận các loại thẻ sau:</p>
            <ul>
              <li>Thẻ Visa</li>
              <li>Thẻ Mastercard</li>
              <li>Thẻ JCB</li>
              <li>Thẻ ATM nội địa (có đăng ký Internet Banking)</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>2.2. Ví Điện Tử</h3>
            <ul>
              <li>Momo</li>
              <li>PayPal (cho thanh toán quốc tế)</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>3. Quy Trình Thanh Toán</h2>
          <div class="sub-section">
            <h3>3.1. Chọn Gói Dịch Vụ</h3>
            <ol>
              <li>Đăng nhập vào tài khoản Oboe</li>
              <li>Truy cập trang "Nâng cấp tài khoản"</li>
              <li>Chọn gói Premium</li>
              <li>Nhấn "Tiếp tục" để chuyển đến bước thanh toán</li>
            </ol>
          </div>
          <div class="sub-section">
            <h3>3.2. Thực Hiện Thanh Toán</h3>
            <ol>
              <li>Chọn phương thức thanh toán mong muốn</li>
              <li>Điền thông tin thanh toán theo yêu cầu</li>
              <li>Xác nhận và hoàn tất giao dịch</li>
              <li>Đợi xác nhận từ hệ thống</li>
            </ol>
          </div>
        </section>

        <section class="guide-section">
          <h2>4. Chính Sách Hoàn Tiền</h2>
          <p>Không hoàn tiền dưới mọi hình thức</p>
        </section>

        <section class="guide-section">
          <h2>5. Câu Hỏi Thường Gặp</h2>
          <div class="sub-section">
            <h3>5.1. Về Thanh Toán</h3>
            <div class="faq-item">
              <p class="question">Tôi có thể đổi phương thức thanh toán sau khi đã bắt đầu giao dịch không?</p>
              <p class="answer">Có, bạn có thể quay lại bước chọn phương thức thanh toán bất cứ lúc nào trước khi hoàn tất giao dịch.</p>
            </div>
            <div class="faq-item">
              <p class="question">Tôi không nhận được email xác nhận thanh toán?</p>
              <p class="answer">Email xác nhận thường được gửi trong vòng 5 phút. Vui lòng kiểm tra hộp thư spam hoặc liên hệ hỗ trợ nếu chưa nhận được.</p>
            </div>
          </div>
          <div class="sub-section">
            <h3>5.2. Về Tài Khoản Premium</h3>
            <div class="faq-item">
              <p class="question">Khi nào tài khoản được nâng cấp lên Premium?</p>
              <p class="answer">Tài khoản sẽ được nâng cấp ngay lập tức sau khi thanh toán thành công.</p>
            </div>
            <div class="faq-item">
              <p class="question">Tôi có thể chia sẻ tài khoản Premium với người khác không?</p>
              <p class="answer">Không, mỗi tài khoản Premium chỉ được sử dụng cho một người dùng.</p>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const lastUpdated = ref('15/03/2024');
</script>

<style lang="scss" scoped>
@use '@/views/footer-services/payment-guide/PaymentGuide.scss';
</style> 