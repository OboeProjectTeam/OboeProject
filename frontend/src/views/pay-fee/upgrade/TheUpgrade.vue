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
@use '@/views/pay-fee/upgrade/TheUpgrade.scss';
</style> 