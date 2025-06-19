<template>
  <div class="upgrade-page-container">
    <div class="page-header scroll-reveal from-bottom delay-1">
      <h1 class="main-title">Trở thành thành viên Oboe Pro</h1>
      <p class="subtitle">Mở khóa tiềm năng sáng tạo vô hạn và trải nghiệm học tập đỉnh cao.</p>
    </div>

    <div class="pricing-table">
      <!-- Free Plan -->
      <div class="plan-card scroll-reveal from-left delay-2">
        <div class="card-header">
          <h2 class="plan-name">{{ PRICING.FREE.NAME }}</h2>
          <p class="plan-price">{{ PRICING.FREE.PRICE }}<span class="price-unit">{{ PRICING.FREE.CURRENCY }}</span></p>
          <p class="plan-description">{{ PRICING.FREE.DESCRIPTION }}</p>
        </div>
        <div class="card-body">
          <ul class="features-list">
            <li v-for="(feature, index) in PRICING.FREE.FEATURES" :key="index">
              <i class="fas fa-check-circle feature-icon"></i> {{ feature }}
            </li>
            <li v-for="(feature, index) in PRICING.PRO.FEATURES.slice(1)" :key="'disabled-'+index" class="disabled">
              <i class="fas fa-times-circle feature-icon"></i> {{ feature }}
            </li>
          </ul>
        </div>
        <div class="card-footer">
          <button class="btn btn-secondary" disabled>Gói hiện tại của bạn</button>
        </div>
      </div>

      <!-- OboePro Plan -->
      <div class="plan-card pro-plan recommended scroll-reveal from-right delay-2">
        <div class="recommended-badge">
          <i class="fas fa-star"></i> Đề xuất
        </div>
        <div class="card-header">
          <h2 class="plan-name">{{ PRICING.PRO.NAME }}</h2>
          <p class="plan-price">{{ PRICING.PRO.PRICE }}<span class="price-unit">{{ PRICING.PRO.CURRENCY }}</span><span class="price-term">{{ PRICING.PRO.PERIOD }}</span></p>
          <p class="plan-description">{{ PRICING.PRO.DESCRIPTION }}</p>
        </div>
        <div class="card-body">
          <ul class="features-list">
            <li v-for="(feature, index) in PRICING.PRO.FEATURES" :key="index">
              <i class="fas fa-check-circle feature-icon pro-feature"></i> {{ feature }}
            </li>
          </ul>
        </div>
        <div class="card-footer">
          <button class="btn btn-primary btn-upgrade" @click="goToPayment">Nâng cấp ngay</button>
        </div>
      </div>
    </div>

    <!-- New Features Section -->
    <div class="features-section">
      <h2 class="section-title scroll-reveal from-bottom delay-1">Tính Năng Nổi Bật của Oboe Pro</h2>
      <div class="features-grid">
        <div class="feature-card scroll-reveal from-bottom delay-2">
          <div class="feature-icon">
            <i class="fas fa-robot"></i>
          </div>
          <h3>Trợ Lý AI Thông Minh</h3>
          <p>Nhận hướng dẫn học tập được cá nhân hóa dựa trên phong cách và tiến độ của bạn.</p>
        </div>

        <div class="feature-card scroll-reveal from-bottom delay-3">
          <div class="feature-icon">
            <i class="fas fa-chart-line"></i>
          </div>
          <h3>Phân Tích Học Tập</h3>
          <p>Theo dõi tiến độ với báo cáo chi tiết và nhận gợi ý cải thiện phù hợp.</p>
        </div>

        <div class="feature-card scroll-reveal from-bottom delay-4">
          <div class="feature-icon">
            <i class="fas fa-brain"></i>
          </div>
          <h3>Học Tập Thông Minh</h3>
          <p>Hệ thống tự động điều chỉnh độ khó và tần suất ôn tập dựa trên khả năng của bạn.</p>
        </div>

        <div class="feature-card scroll-reveal from-bottom delay-5">
          <div class="feature-icon">
            <i class="fas fa-book-reader"></i>
          </div>
          <h3>Nội Dung Nâng Cao</h3>
          <p>Truy cập thư viện từ vựng và ngữ pháp nâng cao, phù hợp với mọi trình độ.</p>
        </div>
      </div>
    </div>

    <!-- Testimonials Section -->
    <div class="testimonials-section">
      <h2 class="section-title scroll-reveal from-bottom delay-1">Người Dùng Nói Gì?</h2>
      <div class="testimonials-grid">
        <div class="testimonial-card scroll-reveal from-left delay-2">
          <div class="user-info">
            <img src="/src/assets/img/avatar/default-avatar.jpg" alt="User Avatar" class="user-avatar">
            <div class="user-details">
              <h4>Nguyễn Văn A</h4>
              <p>Học viên N3</p>
            </div>
          </div>
          <p class="testimonial-text">"OboePro giúp tôi cải thiện khả năng tiếng Nhật một cách đáng kể. Trợ lý AI thực sự hiểu được điểm yếu của tôi."</p>
          <div class="rating">
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
          </div>
        </div>

        <div class="testimonial-card scroll-reveal from-right delay-2">
          <div class="user-info">
            <img src="/src/assets/img/avatar/default-avatar.jpg" alt="User Avatar" class="user-avatar">
            <div class="user-details">
              <h4>Trần Thị B</h4>
              <p>Học viên N2</p>
            </div>
          </div>
          <p class="testimonial-text">"Báo cáo tiến độ chi tiết giúp tôi dễ dàng theo dõi sự tiến bộ và duy trì động lực học tập."</p>
          <div class="rating">
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- FAQ Section -->
    <div class="faq-section">
      <h2 class="section-title scroll-reveal from-bottom delay-1">Câu Hỏi Thường Gặp</h2>
      <div class="faq-container">
        <div class="faq-item scroll-reveal from-bottom delay-2" v-for="(faq, index) in faqs" :key="index">
          <div class="faq-question" @click="faq.isOpen = !faq.isOpen">
            <span>{{ faq.question }}</span>
            <i :class="['fas', faq.isOpen ? 'fa-chevron-up' : 'fa-chevron-down']"></i>
          </div>
          <div class="faq-answer" :class="{ 'open': faq.isOpen }">
            {{ faq.answer }}
          </div>
        </div>
      </div>
    </div>

    <!-- CTA Section -->
    <div class="cta-section scroll-reveal from-bottom delay-1">
      <h2>Sẵn sàng nâng cao trải nghiệm học tập?</h2>
      <p>Dùng OboePro và khám phá sức mạnh của AI trong việc học tiếng Nhật.</p>
      <button class="btn btn-primary btn-large" @click="goToPayment">
        Bắt đầu dùng ngay
        <i class="fas fa-arrow-right"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { initScrollReveal } from '@/assets/js/common';
import { FAQ_ITEMS, PRICING } from '@/assets/js/constants';

const router = useRouter();

const goToPayment = () => {
  router.push('/payment');
};

onMounted(() => {
  initScrollReveal();
});

const faqs = ref(FAQ_ITEMS.map(item => ({
  ...item,
  isOpen: false
})));
</script>

<style lang="scss" scoped>
@use '@/views/pay-fee/upgrade/TheUpgrade.scss';
</style> 