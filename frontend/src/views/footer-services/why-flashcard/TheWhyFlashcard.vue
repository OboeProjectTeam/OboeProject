<template>
  <div class="why-flashcard-page">
    <div class="why-flashcard-container">
      <div class="why-flashcard-header">
        <h1>Tại Sao Nên Học Với Flashcard?</h1>
        <p class="last-updated">Cập nhật lần cuối: {{ lastUpdated }}</p>
      </div>

      <div class="why-flashcard-content">
        <section class="guide-section">
          <h2>1. Khoa Học Về Phương Pháp Học Flashcard</h2>
          <div class="sub-section">
            <h3>1.1. Nguyên Lý Spaced Repetition</h3>
            <p>Spaced Repetition (SR) là phương pháp học tập dựa trên khoảng thời gian tối ưu giữa các lần ôn tập, giúp:</p>
            <ul>
              <li>Tăng khả năng ghi nhớ dài hạn</li>
              <li>Giảm thời gian học tập không cần thiết</li>
              <li>Duy trì kiến thức hiệu quả hơn</li>
              <li>Tối ưu hóa quá trình học tập</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>1.2. Hiệu Ứng Testing</h3>
            <p>Testing Effect là hiện tượng tâm lý học cho thấy việc kiểm tra kiến thức thường xuyên sẽ:</p>
            <ul>
              <li>Củng cố trí nhớ tốt hơn so với đọc lại</li>
              <li>Tạo liên kết não bộ bền vững</li>
              <li>Giúp nhận biết được lỗ hổng kiến thức</li>
              <li>Tăng khả năng áp dụng kiến thức vào thực tế</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>2. Lợi Ích Của Flashcard Trong Học Tiếng Nhật</h2>
          <div class="sub-section">
            <h3>2.1. Học Từ Vựng Hiệu Quả</h3>
            <ul>
              <li>Dễ dàng ghi nhớ từ mới thông qua hình ảnh và ví dụ</li>
              <li>Học cách sử dụng từ trong nhiều ngữ cảnh khác nhau</li>
              <li>Tăng vốn từ vựng một cách có hệ thống</li>
              <li>Ôn tập nhanh chóng mọi lúc mọi nơi</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>2.2. Luyện Tập Kanji</h3>
            <ul>
              <li>Học các thành phần bộ thủ một cách trực quan</li>
              <li>Ghi nhớ các cách đọc On-kun hiệu quả</li>
              <li>Liên kết nghĩa với hình dạng chữ Kanji</li>
              <li>Luyện tập viết Kanji thông qua stroke order</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>3. Tính Năng Flashcard Trên Oboe</h2>
          <div class="sub-section">
            <h3>3.1. Hệ Thống Thông Minh</h3>
            <ul>
              <li>Tự động điều chỉnh thời gian ôn tập dựa trên hiệu suất học tập</li>
              <li>Phân tích và theo dõi tiến độ học tập chi tiết</li>
              <li>Gợi ý các từ vựng và Kanji liên quan</li>
              <li>Tích hợp phát âm chuẩn từ người bản xứ</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>3.2. Tùy Chỉnh Linh Hoạt</h3>
            <ul>
              <li>Tạo bộ thẻ riêng theo chủ đề mong muốn</li>
              <li>Thêm hình ảnh, âm thanh và ghi chú</li>
              <li>Chia sẻ bộ thẻ với cộng đồng</li>
              <li>Nhập/Xuất dữ liệu dễ dàng</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>4. Cách Học Hiệu Quả Với Flashcard</h2>
          <div class="sub-section">
            <h3>4.1. Thiết Lập Thói Quen</h3>
            <ul>
              <li>Học đều đặn mỗi ngày, tránh học dồn</li>
              <li>Chia nhỏ bài học thành các phần dễ tiếp thu</li>
              <li>Kết hợp với các phương pháp học khác</li>
              <li>Tạo môi trường học tập không bị phân tâm</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>4.2. Kỹ Thuật Học Tập</h3>
            <ul>
              <li>Sử dụng phương pháp Active Recall (gợi nhớ chủ động)</li>
              <li>Tạo liên kết với kiến thức đã biết</li>
              <li>Học theo nhóm từ vựng liên quan</li>
              <li>Áp dụng ngay vào thực hành</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>5. Kết Quả Thực Tế</h2>
          <div class="sub-section">
            <h3>5.1. Thống Kê Từ Người Dùng</h3>
            <ul>
              <li>90% người dùng cải thiện điểm JLPT sau 3 tháng sử dụng</li>
              <li>Trung bình ghi nhớ 30 từ mới mỗi ngày</li>
              <li>Tỷ lệ duy trì học tập cao hơn 75% so với phương pháp truyền thống</li>
              <li>Tiết kiệm 50% thời gian học tập</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>5.2. Phản Hồi Từ Người Học</h3>
            <div class="testimonial">
              <p class="quote">"Tôi đã vượt qua N2 sau 6 tháng học với Oboe Flashcard. Phương pháp này thực sự hiệu quả!"</p>
              <p class="author">- Nguyễn Văn A, Đã đạt N2</p>
            </div>
            <div class="testimonial">
              <p class="quote">"Từ một người mất gốc tiếng Nhật, giờ tôi có thể tự tin giao tiếp với người bản xứ."</p>
              <p class="author">- Trần Thị B, Du học sinh</p>
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
.why-flashcard-page {
  min-height: 100vh;

  .why-flashcard-container {
    max-width: 900px;
    margin: 0 auto;
    background-color: white;
    padding: 40px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .why-flashcard-header {
    text-align: center;
    margin-bottom: 40px;
    padding-bottom: 20px;
    border-bottom: 2px solid #eee;

    h1 {
      font-size: 32px;
      color: #333;
      margin-bottom: 10px;
    }

    .last-updated {
      color: #666;
      font-size: 14px;
    }
  }

  .why-flashcard-content {
    .guide-section {
      margin-bottom: 40px;

      h2 {
        color: #2c3e50;
        font-size: 24px;
        margin-bottom: 20px;
        padding-bottom: 10px;
        border-bottom: 1px solid #eee;
      }

      .sub-section {
        margin-bottom: 24px;

        h3 {
          color: #34495e;
          font-size: 20px;
          margin-bottom: 16px;
        }

        p {
          color: #444;
          line-height: 1.6;
          margin-bottom: 16px;
        }

        ul {
          list-style-type: disc;
          padding-left: 20px;
          margin-bottom: 16px;

          li {
            color: #444;
            line-height: 1.6;
            margin-bottom: 8px;
          }
        }

        .testimonial {
          margin-bottom: 20px;
          padding: 16px;
          background-color: #f8f9fa;
          border-radius: 8px;

          .quote {
            color: #2c3e50;
            font-style: italic;
            margin-bottom: 8px;
          }

          .author {
            color: #666;
            font-size: 14px;
            text-align: right;
          }
        }
      }
    }
  }
}

// Responsive styles
@media (max-width: 768px) {
  .why-flashcard-page {
    .why-flashcard-container {
      padding: 20px;
    }

    .why-flashcard-header {
      h1 {
        font-size: 28px;
      }
    }

    .why-flashcard-content {
      .guide-section {
        h2 {
          font-size: 22px;
        }

        .sub-section h3 {
          font-size: 18px;
        }
      }
    }
  }
}
</style> 