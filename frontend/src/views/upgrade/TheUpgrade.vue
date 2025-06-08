<template>
  <div class="upgrade-page-container">
    <div class="page-header">
      <h1 class="main-title">Trở thành thành viên Oboe Pro</h1>
      <p class="subtitle">Mở khóa tiềm năng sáng tạo vô hạn và trải nghiệm học tập đỉnh cao.</p>
    </div>

    <div class="pricing-table">
      <!-- Free Plan -->
      <div class="plan-card">
        <div class="card-header">
          <h2 class="plan-name">Miễn phí</h2>
          <p class="plan-price">0<span class="price-unit">đ</span></p>
          <p class="plan-description">Các tính năng cơ bản để bắt đầu hành trình của .</p>
        </div>
        <div class="card-body">
          <ul class="features-list">
            <li><i class="fas fa-check-circle feature-icon"></i> Truy cập tất cả tài liệu</li>
            <li><i class="fas fa-check-circle feature-icon"></i> Tạo học liệu cơ bản</li>
            <li><i class="fas fa-check-circle feature-icon"></i> Hỗ trợ qua email</li>
            <li class="disabled"><i class="fas fa-times-circle feature-icon"></i> Dịch tự động bằng AI</li>
            <li class="disabled"><i class="fas fa-times-circle feature-icon"></i> Tạo học liệu bằng AI</li>
            <li class="disabled"><i class="fas fa-times-circle feature-icon"></i> Phân tích đánh giá và lời khuyên</li>
          </ul>
        </div>
        <div class="card-footer">
          <button class="btn btn-secondary" disabled>Gói hiện tại của </button>
        </div>
      </div>

      <!-- OboePro Plan -->
      <div class="plan-card pro-plan recommended">
        <div class="recommended-badge">
          <i class="fas fa-star"></i> Đề xuất
        </div>
        <div class="card-header">
          <h2 class="plan-name">Oboe Pro</h2>
          <p class="plan-price">99.000<span class="price-unit">đ</span><span class="price-term">/tháng</span></p>
          <p class="plan-description">Toàn bộ tính năng cho trải nghiệm không giới hạn.</p>
        </div>
        <div class="card-body">
          <ul class="features-list">
            <li><i class="fas fa-check-circle feature-icon pro-feature"></i> Truy cập không giới hạn</li>
            <li><i class="fas fa-check-circle feature-icon pro-feature"></i> Tạo học liệu bằng AI</li>
            <li><i class="fas fa-check-circle feature-icon pro-feature"></i> Hỗ trợ ưu tiên 24/7</li>
            <li><i class="fas fa-check-circle feature-icon pro-feature"></i> AI Dịch tự động</li>
            <li><i class="fas fa-check-circle feature-icon pro-feature"></i> AI đánh giá và lời khuyên quiz</li>
            <li><i class="fas fa-check-circle feature-icon pro-feature"></i> Truy cập sớm các tính năng mới</li>
          </ul>
        </div>
        <div class="card-footer">
          <button class="btn btn-primary btn-upgrade" @click="goToPayment">Chọn Oboe Pro</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const goToPayment = () => {
  router.push('/payment');
};
</script>

<style lang="scss" scoped>
@use '@/assets/css/index.scss' as *;
@use 'sass:color';

.upgrade-page-container {
  min-height: calc(100vh - 140px);
  font-family: $font-family-regular;
}

.page-header {
  text-align: center;
  margin-bottom: 50px;
  .main-title {
    font-size: 3rem;
    font-family: $font-family-bold;
    color: #212529;
    margin: 0;
  }
  .subtitle {
    font-size: 1.25rem;
    color: #6c757d;
    max-width: 600px;
    margin: 10px auto 0;
  }
}

.pricing-table {
  display: flex;
  justify-content: center;
  align-items: stretch;
  gap: 30px;
  flex-wrap: wrap;
}

.plan-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px -10px rgba(0,0,0,0.1);
  width: 100%;
  max-width: 380px;
  display: flex;
  flex-direction: column;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid #dee2e6;
  position: relative;
  overflow: hidden;

  &:hover {
    transform: translateY(-10px);
    box-shadow: 0 15px 40px -10px rgba(0,0,0,0.15);
  }

  &.pro-plan {
    border-color: transparent;
    border-width: 2px;
    border-image: linear-gradient(45deg, $primary-color, color.adjust($primary-color, $hue: -20deg, $saturation: 10%)) 1;
  }
}

.recommended-badge {
  position: absolute;
  top: 15px;
  right: -40px;
  transform: rotate(45deg);
  background: $primary-color;
  color: white;
  padding: 5px 40px;
  font-size: 12px;
  font-weight: 700;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
  .fa-star {
    margin-right: 5px;
  }
}

.card-header {
  padding: 30px;
  text-align: center;
  border-bottom: 1px solid #e9ecef;
  .plan-name {
    font-size: 1.75rem;
    font-family: $font-family-bold;
    margin: 0 0 10px;
  }
  .plan-price {
    font-size: 2.5rem;
    font-family: $font-family-bold;
    color: $primary-color;
    margin: 0;
    .price-unit {
      font-size: 1.5rem;
      vertical-align: super;
      margin-left: 2px;
    }
    .price-term {
      font-size: 1rem;
      color: #6c757d;
      font-weight: 500;
      margin-left: 5px;
    }
  }
  .plan-description {
    color: #6c757d;
    margin: 10px 0 0;
  }
}

.pro-plan .card-header .plan-price {
  color: $primary-color;
}

.plan-card:not(.pro-plan) .card-header .plan-price {
  color: #343a40;
}

.card-body {
  padding: 30px;
  flex-grow: 1;
  .features-list {
    list-style: none;
    padding: 0;
    margin: 0;
    li {
      display: flex;
      align-items: center;
      margin-bottom: 15px;
      color: #212529;
      font-size: 1rem;
      .feature-icon {
        margin-right: 12px;
        font-size: 1.2rem;
        color: #28a745;
      }
      &.disabled {
        color: #adb5bd;
        text-decoration: line-through;
        .feature-icon {
          color: #adb5bd;
        }
      }
      .pro-feature {
        color: $primary-color;
      }
    }
  }
}

.card-footer {
  padding: 30px;
  border-top: 1px solid #e9ecef;
  text-align: center;
}

.btn {
  padding: 15px 25px;
  border-radius: 8px;
  border: none;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
  font-size: 1.1rem;

  &.btn-primary {
    background-color: $primary-color;
    color: white;
    box-shadow: 0 4px 15px rgba(220, 53, 69, 0.2);
    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 7px 20px rgba(220, 53, 69, 0.3);
      background-color: color.adjust($primary-color, $lightness: -5%);
    }
  }
  &.btn-secondary {
    background-color: #e9ecef;
    color: #6c757d;
    cursor: not-allowed;
  }
}

@media (max-width: 768px) {
  .pricing-table {
    flex-direction: column;
    align-items: center;
  }
  .page-header .main-title {
    font-size: 2.5rem;
  }
}
</style> 