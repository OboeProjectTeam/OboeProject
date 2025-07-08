<template>
  <div class="intro-page">
    <!-- Section 1: Hero -->
    <section class="ip-hero">
      <div class="ip-container">
        <h1 class="ip-hero-title scroll-reveal from-left delay-1">Chinh Phục Tiếng Nhật<br>Theo Cách Của Bạn</h1>
        <p class="ip-hero-subtitle scroll-reveal from-right delay-2">Oboe là không gian học tập được tạo ra cho bạn, bởi
          chính bạn. <br>Nơi mọi công cụ đều được thiết kế để phục vụ lộ trình của riêng bạn.</p>
        <router-link to="/login" class="ip-cta-button primary scroll-reveal from-bottom delay-2">Bắt Đầu Học Miễn
          Phí</router-link>
      </div>
    </section>

    <!-- Section 2: Key Benefits -->
    <section class="ip-benefits scroll-reveal from-bottom delay-2">
      <div class="ip-container">
        <div class="ip-benefit-card">
          <i class="fas fa-drafting-compass"></i>
          <h3>Công Cụ Linh Hoạt</h3>
          <p>Tự do tạo thẻ học, bài kiểm tra và sắp xếp thư viện theo cách bạn muốn.</p>
        </div>
        <div class="ip-benefit-card">
          <i class="fas fa-layer-group"></i>
          <h3>Lộ Trình Rõ Ràng</h3>
          <p>Theo dõi tiến độ, xem lại lịch sử và đánh dấu yêu thích để không bỏ lỡ kiến thức.</p>
        </div>
        <div class="ip-benefit-card">
          <i class="fas fa-users"></i>
          <h3>Cộng Đồng Hỗ Trợ</h3>
          <p>Kết nối, chia sẻ học liệu và học hỏi kinh nghiệm từ hàng ngàn người dùng khác.</p>
        </div>
      </div>
    </section>

    <!-- Section 3: Feature Deep Dive 1 -->
    <section class="ip-feature scroll-reveal">
      <div class="ip-container ip-feature-container">
        <div class="ip-feature-text scroll-reveal from-left delay-1">
          <h2 class="ip-feature-title ">Tạo Dựng Tri Thức</h2>
          <p>Bắt đầu từ những viên gạch nhỏ nhất. Với Oboe, bạn có thể dễ dàng tạo ra các bộ thẻ ghi nhớ (flashcards)
            cho từ vựng, kanji, hoặc các mẫu ngữ pháp. Biến kiến thức khô khan thành những học liệu sinh động, luôn sẵn
            sàng để bạn ôn tập.</p>
          <router-link to="/create/flashcard" class="ip-feature-link">Bắt đầu tạo <i
              class="fas fa-arrow-right"></i></router-link>
        </div>
          <TheCard class="ip-feature-card scroll-reveal from-right delay-2" :slides="slides" :width="400" :height="300" :autoplay="true" :pagination="{ clickable: true }"  />
      </div>
    </section>

    <!-- Section 4: Feature Deep Dive 2 (Alt Layout) -->
    <section class="ip-feature alt-layout scroll-reveal">
      <div class="ip-container ip-feature-container">
        <div class="ip-feature-text scroll-reveal from-right delay-1">
          <h2 class="ip-feature-title ">Thực Hành & Ghi Nhớ</h2>
          <p>Học phải đi đôi với hành. Chuyển đổi bất kỳ bộ thẻ nào thành một bài kiểm tra đa dạng chỉ với vài cú nhấp
            chuột. Mọi kết quả đều được lưu lại, giúp bạn nhìn thấy rõ sự tiến bộ của mình qua từng ngày.</p>
          <router-link to="/library" class="ip-feature-link">Xem lại lịch sử <i
              class="fas fa-arrow-right"></i></router-link>
        </div>
        <div class="ip-feature-image-placeholder scroll-reveal from-left delay-2">
          <img class="img-test-flashcard" :src="ImagePaths.intro.testFlashcard" alt="test-flashcard">
        </div>
      </div>
    </section>
    <section class="ip-feature scroll-reveal">
      <div class="ip-container ip-feature-container">
        <div class="ip-feature-text scroll-reveal from-left delay-1">
          <h2 class="ip-feature-title">Kết Nối & Giao Lưu</h2>
          <p>Tham gia Diễn Đàn để trao đổi kinh nghiệm, hỏi đáp, chia sẻ tài liệu và kết nối với cộng đồng học tiếng
            Nhật năng động. Cùng nhau học hỏi và phát triển!</p>
          <router-link to="/forum" class="ip-feature-link">Khám phá Diễn Đàn <i
              class="fas fa-arrow-right"></i></router-link>
        </div>
        <div class="ip-feature-image-placeholder scroll-reveal from-right delay-2">
          <img class="img-forum" :src="ImagePaths.intro.forum" alt="forum">
        </div>
      </div>
    </section>
    <!-- Section 5: Final CTA -->
    <section class="ip-final-cta scroll-reveal from-bottom delay-2">
      <div class="ip-container">
        <h2 class="ip-final-cta-title">Sẵn Sàng Để Bứt Phá?</h2>
        <p>Tham gia cộng đồng Oboe ngay hôm nay và trải nghiệm một phương pháp học hoàn toàn mới.</p>
        <router-link to="/register" class="ip-cta-button secondary">Tham Gia Ngay</router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ImagePaths } from '@/assets/img/imagePaths';
import { onMounted } from 'vue';
import { initScrollReveal } from '@/assets/js/common';
import TheCard from '@/components/layout/card/TheCard.vue';
import { useStore } from 'vuex'; // Import useStore to access Vuex state
const store = useStore(); // Access Vuex store
const slides = store.getters['cart/slides']; // Get slides from the Vuex store


onMounted(() => {
  initScrollReveal();
});
</script>

<style lang="scss" scoped>
@use '@/views/intro/TheIntro.scss';
</style>
