<template>
  <div class="study-guide-page">
    <div class="study-guide-container">
      <div class="study-guide-header">
        <h1>Hướng Dẫn Tự Học</h1>
        <p class="last-updated">Cập nhật lần cuối: {{ lastUpdated }}</p>
      </div>

      <div class="study-guide-content">
        <section class="guide-section">
          <h2>1. Lộ trình học tiếng Nhật</h2>
          <div class="sub-section">
            <h3>1.1. Cấp độ cơ bản (N5-N4)</h3>
            <ul>
              <li>Học bảng chữ Hiragana và Katakana</li>
              <li>Ngữ pháp cơ bản N5-N4</li>
              <li>Từ vựng thông dụng (khoảng 1500 từ)</li>
              <li>Kanji cơ bản (khoảng 300 chữ)</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>1.2. Cấp độ trung cấp (N3)</h3>
            <ul>
              <li>Ngữ pháp trung cấp</li>
              <li>Từ vựng mở rộng (thêm 3000 từ)</li>
              <li>Kanji trung cấp (thêm 600 chữ)</li>
              <li>Kỹ năng đọc hiểu và nghe hiểu</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>2. Phương pháp học hiệu quả</h2>
          <div class="sub-section">
            <h3>2.1. Học với Flashcard</h3>
            <p>Oboe cung cấp hệ thống flashcard thông minh giúp bạn:</p>
            <ul>
              <li>Học từ vựng và kanji một cách có hệ thống</li>
              <li>Ôn tập theo phương pháp spaced repetition</li>
              <li>Theo dõi tiến độ học tập</li>
              <li>Tùy chỉnh bộ thẻ theo nhu cầu</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>2.2. Luyện tập với Quiz</h3>
            <p>Kiểm tra kiến thức thường xuyên thông qua:</p>
            <ul>
              <li>Bài kiểm tra từ vựng</li>
              <li>Bài tập ngữ pháp</li>
              <li>Luyện đọc hiểu</li>
              <li>Luyện nghe hiểu</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>3. Tài nguyên học tập</h2>
          <div class="sub-section">
            <h3>3.1. Tài liệu học tập</h3>
            <ul>
              <li>Giáo trình tiếng Nhật cơ bản</li>
              <li>Bài tập ngữ pháp theo cấp độ</li>
              <li>Tài liệu luyện thi JLPT</li>
              <li>Tài liệu hội thoại thực tế</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>3.2. Công cụ hỗ trợ</h3>
            <ul>
              <li>Từ điển tích hợp</li>
              <li>Công cụ tra cứu kanji</li>
              <li>Phần mềm luyện phát âm</li>
              <li>Ứng dụng mobile học mọi lúc</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>4. Lời khuyên cho người mới bắt đầu</h2>
          <div class="sub-section">
            <h3>4.1. Thiết lập mục tiêu</h3>
            <ul>
              <li>Xác định mục tiêu học tập rõ ràng</li>
              <li>Lập kế hoạch học tập phù hợp</li>
              <li>Đặt mục tiêu ngắn hạn và dài hạn</li>
              <li>Theo dõi tiến độ thường xuyên</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>4.2. Duy trì động lực</h3>
            <ul>
              <li>Học đều đặn mỗi ngày</li>
              <li>Tham gia cộng đồng học tập</li>
              <li>Thực hành với người bản xứ</li>
              <li>Tận dụng các tài nguyên giải trí (anime, manga)</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>5. Hỗ trợ từ Oboe</h2>
          <div class="sub-section">
            <h3>5.1. Hỗ trợ học tập</h3>
            <ul>
              <li>Tư vấn lộ trình học tập cá nhân</li>
              <li>Giải đáp thắc mắc từ chuyên gia</li>
              <li>Cập nhật tài liệu thường xuyên</li>
              <li>Thống kê và báo cáo tiến độ</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>5.2. Cộng đồng học tập</h3>
            <ul>
              <li>Diễn đàn trao đổi kiến thức</li>
              <li>Nhóm học tập theo cấp độ</li>
              <li>Chia sẻ kinh nghiệm học tập</li>
              <li>Kết nối với người học khác</li>
            </ul>
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
@use '@/views/footer-services/study-guide/StudyGuide.scss';
</style> 