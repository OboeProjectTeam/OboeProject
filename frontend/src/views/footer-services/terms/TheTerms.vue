<template>
  <div class="terms-page">
    <div class="terms-container">
      <div class="terms-header">
        <h1>Điều Khoản Dịch Vụ</h1>
        <p class="last-updated">Cập nhật lần cuối: {{ lastUpdated }}</p>
      </div>

      <div class="terms-content">
        <section class="terms-section">
          <h2>1. Giới thiệu</h2>
          <p>Chào mừng bạn đến với Oboe - Nền tảng học tiếng Nhật trực tuyến. Bằng việc truy cập và sử dụng dịch vụ của chúng tôi, bạn đồng ý tuân thủ các điều khoản và điều kiện này.</p>
          <p>Oboe cung cấp các công cụ và tài nguyên học tập tiếng Nhật hiệu quả, bao gồm hệ thống flashcard, bài kiểm tra, và cộng đồng học tập.</p>
        </section>

        <section class="terms-section">
          <h2>2. Tài khoản người dùng</h2>
          <div class="sub-section">
            <h3>2.1. Đăng ký tài khoản</h3>
            <p>Để sử dụng đầy đủ các tính năng của Oboe, bạn cần đăng ký tài khoản với thông tin chính xác và cập nhật.</p>
          </div>
          <div class="sub-section">
            <h3>2.2. Bảo mật tài khoản</h3>
            <p>Bạn có trách nhiệm:</p>
            <ul>
              <li>Bảo mật thông tin đăng nhập của mình</li>
              <li>Không chia sẻ tài khoản cho người khác</li>
              <li>Thông báo ngay cho chúng tôi nếu phát hiện việc sử dụng trái phép</li>
            </ul>
          </div>
        </section>

        <section class="terms-section">
          <h2>3. Dịch vụ của chúng tôi</h2>
          <div class="sub-section">
            <h3>3.1. Học tập</h3>
            <ul>
              <li>Hệ thống flashcard học từ vựng và kanji</li>
              <li>Bài kiểm tra và quiz đánh giá năng lực</li>
              <li>Tài liệu học tập và ngữ pháp</li>
              <li>Công cụ luyện tập và ôn tập</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>3.2. Cộng đồng</h3>
            <ul>
              <li>Diễn đàn thảo luận và trao đổi</li>
              <li>Chia sẻ tài liệu học tập</li>
              <li>Kết nối với người học khác</li>
            </ul>
          </div>
        </section>

        <section class="terms-section">
          <h2>4. Quyền và trách nhiệm</h2>
          <div class="sub-section">
            <h3>4.1. Quyền của người dùng</h3>
            <ul>
              <li>Truy cập và sử dụng các tính năng theo gói dịch vụ</li>
              <li>Nhận hỗ trợ kỹ thuật khi cần thiết</li>
              <li>Yêu cầu xóa tài khoản và dữ liệu cá nhân</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>4.2. Trách nhiệm của người dùng</h3>
            <ul>
              <li>Tuân thủ quy tắc cộng đồng</li>
              <li>Không chia sẻ nội dung vi phạm bản quyền</li>
              <li>Tôn trọng quyền riêng tư của người khác</li>
            </ul>
          </div>
        </section>

        <section class="terms-section">
          <h2>5. Gói dịch vụ và thanh toán</h2>
          <div class="sub-section">
            <h3>5.1. Các gói dịch vụ</h3>
            <ul>
              <li>Gói miễn phí: Truy cập các tính năng cơ bản</li>
              <li>Gói premium: Truy cập đầy đủ tính năng và nội dung độc quyền</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>5.2. Chính sách thanh toán</h3>
            <ul>
              <li>Thanh toán trước khi sử dụng gói premium</li>
              <li>Không hoàn tiền cho thời gian không sử dụng</li>
              <li>Tự động gia hạn theo chu kỳ đăng ký</li>
            </ul>
          </div>
        </section>

        <section class="terms-section">
          <h2>6. Bảo mật thông tin</h2>
          <p>Chúng tôi cam kết bảo vệ thông tin cá nhân của người dùng theo chính sách bảo mật của Oboe.</p>
          <ul>
            <li>Không chia sẻ thông tin cá nhân cho bên thứ ba</li>
            <li>Áp dụng các biện pháp bảo mật tiên tiến</li>
            <li>Tuân thủ các quy định về bảo vệ dữ liệu</li>
          </ul>
        </section>

        <section class="terms-section">
          <h2>7. Điều khoản khác</h2>
          <ul>
            <li>Chúng tôi có quyền thay đổi điều khoản mà không cần thông báo trước</li>
            <li>Mọi tranh chấp sẽ được giải quyết theo luật pháp Việt Nam</li>
            <li>Liên hệ bộ phận hỗ trợ khi có thắc mắc: bophanchamsoc@oboe.com</li>
          </ul>
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
@use '@/views/footer-services/terms/TheTerms.scss';
</style> 