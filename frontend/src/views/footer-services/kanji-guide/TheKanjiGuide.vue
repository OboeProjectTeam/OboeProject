<template>
  <div class="kanji-guide-page">
    <div class="kanji-guide-container">
      <div class="kanji-guide-header">
        <h1>Cách Nhận Diện Chữ Hán (Kanji)</h1>
        <p class="last-updated">Cập nhật lần cuối: {{ lastUpdated }}</p>
      </div>

      <div class="kanji-guide-content">
        <section class="guide-section">
          <h2>1. Cấu Trúc Chữ Hán</h2>
          <div class="sub-section">
            <h3>1.1. Bộ Thủ (部首 - Bushu)</h3>
            <p>Bộ thủ là thành phần cơ bản nhất của chữ Hán, giúp phân loại và gợi ý nghĩa của chữ.</p>
            <div class="kanji-example-box">
              <div class="kanji-item">
                <p class="kanji">氵</p>
                <p class="meaning">Bộ thủy (nước)</p>
                <p class="examples">Ví dụ: 海 (biển), 泳 (bơi), 洗 (rửa)</p>
              </div>
              <div class="kanji-item">
                <p class="kanji">木</p>
                <p class="meaning">Bộ mộc (cây)</p>
                <p class="examples">Ví dụ: 林 (rừng), 森 (rừng rậm), 板 (ván)</p>
              </div>
              <div class="kanji-item">
                <p class="kanji">火</p>
                <p class="meaning">Bộ hỏa (lửa)</p>
                <p class="examples">Ví dụ: 炎 (ngọn lửa), 焼 (đốt), 煮 (nấu)</p>
              </div>
            </div>
          </div>
          <div class="sub-section">
            <h3>1.2. Cách Kết Hợp</h3>
            <ul>
              <li>Kết hợp ngang: 林 = 木 + 木 (nhiều cây = rừng)</li>
              <li>Kết hợp dọc: 早 = 日 + 十 (mặt trời + mười = sớm)</li>
              <li>Kết hợp bao quanh: 国 (quốc gia)</li>
              <li>Kết hợp phức tạp: 館 (building/hall)</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>2. Quy Tắc Viết Chữ Hán</h2>
          <div class="sub-section">
            <h3>2.1. Nguyên Tắc Cơ Bản</h3>
            <ul>
              <li>Từ trái sang phải</li>
              <li>Từ trên xuống dưới</li>
              <li>Từ ngoài vào trong</li>
              <li>Nét ngang trước nét dọc</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>2.2. Stroke Order</h3>
            <div class="stroke-order-box">
              <div class="stroke-example">
                <p class="kanji">木</p>
                <ol>
                  <li>Nét ngang trên cùng</li>
                  <li>Nét dọc giữa</li>
                  <li>Nét chéo trái</li>
                  <li>Nét chéo phải</li>
                </ol>
              </div>
              <div class="stroke-example">
                <p class="kanji">水</p>
                <ol>
                  <li>Nét chấm bên trái</li>
                  <li>Nét chấm bên phải</li>
                  <li>Nét ngang</li>
                  <li>Nét dọc</li>
                  <li>Nét gập khúc</li>
                </ol>
              </div>
            </div>
          </div>
        </section>

        <section class="guide-section">
          <h2>3. Cách Đọc Chữ Hán</h2>
          <div class="sub-section">
            <h3>3.1. Âm On (音読み)</h3>
            <p>Cách đọc theo âm Hán-Nhật, thường dùng trong từ ghép:</p>
            <div class="reading-example">
              <div class="kanji-reading">
                <span class="kanji">山</span>
                <span class="reading">サン (san)</span>
                <span class="example">富士山 (Fujisan - núi Phú Sĩ)</span>
              </div>
              <div class="kanji-reading">
                <span class="kanji">水</span>
                <span class="reading">スイ (sui)</span>
                <span class="example">水曜日 (Suiyoubi - thứ Tư)</span>
              </div>
            </div>
          </div>
          <div class="sub-section">
            <h3>3.2. Âm Kun (訓読み)</h3>
            <p>Cách đọc theo âm Nhật, thường dùng khi chữ đứng một mình:</p>
            <div class="reading-example">
              <div class="kanji-reading">
                <span class="kanji">山</span>
                <span class="reading">やま (yama)</span>
                <span class="example">山 (yama - núi)</span>
              </div>
              <div class="kanji-reading">
                <span class="kanji">水</span>
                <span class="reading">みず (mizu)</span>
                <span class="example">水 (mizu - nước)</span>
              </div>
            </div>
          </div>
        </section>

        <section class="guide-section">
          <h2>4. Phương Pháp Học Hiệu Quả</h2>
          <div class="sub-section">
            <h3>4.1. Học Theo Bộ Thủ</h3>
            <ul>
              <li>Tập trung học các bộ thủ phổ biến trước</li>
              <li>Nhóm các chữ Hán có cùng bộ thủ</li>
              <li>Tìm hiểu ý nghĩa và liên hệ với bộ thủ</li>
              <li>Sử dụng flashcard để ghi nhớ bộ thủ</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>4.2. Luyện Tập Thường Xuyên</h3>
            <ul>
              <li>Viết chữ theo stroke order chuẩn</li>
              <li>Đọc văn bản tiếng Nhật hàng ngày</li>
              <li>Sử dụng ứng dụng học Kanji</li>
              <li>Làm bài tập và kiểm tra định kỳ</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>5. Mẹo Nhận Diện Nhanh</h2>
          <div class="sub-section">
            <h3>5.1. Phân Tích Hình Dạng</h3>
            <div class="tips-box">
              <div class="tip-item">
                <h4>Tìm Bộ Thủ</h4>
                <p>Xác định bộ thủ chính để đoán nghĩa gốc</p>
              </div>
              <div class="tip-item">
                <h4>Đếm Số Nét</h4>
                <p>Đếm số nét để tìm kiếm trong từ điển</p>
              </div>
              <div class="tip-item">
                <h4>Nhận Dạng Mẫu</h4>
                <p>Tìm các mẫu lặp lại trong chữ Hán</p>
              </div>
            </div>
          </div>
          <div class="sub-section">
            <h3>5.2. Sử Dụng Công Cụ Hỗ Trợ</h3>
            <ul>
              <li>Từ điển Kanji điện tử</li>
              <li>Ứng dụng nhận dạng chữ viết</li>
              <li>Công cụ tìm kiếm theo bộ thủ</li>
              <li>Phần mềm luyện viết stroke order</li>
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
.kanji-guide-page {
  min-height: 100vh;

  .kanji-guide-container {
    max-width: 900px;
    margin: 0 auto;
    background-color: white;
    padding: 40px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .kanji-guide-header {
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

  .kanji-guide-content {
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

        .kanji-example-box {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 20px;
          margin: 20px 0;

          .kanji-item {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            text-align: center;

            .kanji {
              font-size: 36px;
              color: #2c3e50;
              margin-bottom: 10px;
            }

            .meaning {
              font-weight: 600;
              color: #34495e;
              margin-bottom: 8px;
            }

            .examples {
              font-size: 14px;
              color: #666;
            }
          }
        }

        .stroke-order-box {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          gap: 20px;
          margin: 20px 0;

          .stroke-example {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;

            .kanji {
              font-size: 48px;
              color: #2c3e50;
              text-align: center;
              margin-bottom: 16px;
            }

            ol {
              padding-left: 20px;

              li {
                color: #444;
                line-height: 1.6;
                margin-bottom: 8px;
              }
            }
          }
        }

        .reading-example {
          .kanji-reading {
            background-color: #f8f9fa;
            padding: 16px;
            border-radius: 8px;
            margin-bottom: 12px;
            display: flex;
            align-items: center;
            gap: 16px;

            .kanji {
              font-size: 24px;
              color: #2c3e50;
            }

            .reading {
              color: #34495e;
              font-weight: 600;
            }

            .example {
              color: #666;
              font-style: italic;
            }
          }
        }

        .tips-box {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 20px;
          margin: 20px 0;

          .tip-item {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            text-align: center;

            h4 {
              color: #2c3e50;
              font-size: 18px;
              margin-bottom: 10px;
            }

            p {
              color: #666;
              font-size: 14px;
              line-height: 1.4;
            }
          }
        }
      }
    }
  }
}

// Responsive styles
@media (max-width: 768px) {
  .kanji-guide-page {
    .kanji-guide-container {
      padding: 20px;
    }

    .kanji-guide-header {
      h1 {
        font-size: 28px;
      }
    }

    .kanji-guide-content {
      .guide-section {
        h2 {
          font-size: 22px;
        }

        .sub-section {
          h3 {
            font-size: 18px;
          }

          .kanji-example-box {
            grid-template-columns: 1fr;
          }

          .stroke-order-box {
            grid-template-columns: 1fr;
          }

          .tips-box {
            grid-template-columns: 1fr;
          }

          .reading-example {
            .kanji-reading {
              flex-direction: column;
              text-align: center;
              gap: 8px;
            }
          }
        }
      }
    }
  }
}
</style> 