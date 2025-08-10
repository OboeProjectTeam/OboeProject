<template>
  <div class="upgrade-page-container">
    <div class="page-header scroll-reveal from-bottom delay-1">
      <h1 class="main-title">{{ $t('upgrade.becomeProMember') }}</h1>
      <p class="subtitle">{{ $t('upgrade.subtitle') }}</p>
    </div>

    <div class="pricing-table">
      <!-- Free Plan -->
      <div class="plan-card scroll-reveal from-left delay-2">
        <div class="card-header">
          <h2 class="plan-name">{{ $t('upgrade.pricing.free.name') }}</h2>
          <p class="plan-price">{{ $t('upgrade.pricing.free.price') }}<span class="price-unit">{{ $t('upgrade.pricing.free.currency') }}</span></p>
          <p class="plan-description">{{ $t('upgrade.pricing.free.description') }}</p>
        </div>
        <div class="card-body">
          <ul class="features-list">
            <li v-for="(feature, index) in freeFeatures" :key="index">
              <i class="fas fa-check-circle feature-icon"></i> {{ feature }}
            </li>
            <li v-for="(feature, index) in proFeaturesDisabled" :key="'disabled-'+index" class="disabled">
              <i class="fas fa-times-circle feature-icon"></i> {{ feature }}
            </li>
          </ul>
        </div>
        <div class="card-footer">
          <button class="btn btn-secondary" disabled>{{ $t('upgrade.currentPlan') }}</button>
        </div>
      </div>

      <!-- OboePro Plan -->
      <div class="plan-card pro-plan recommended scroll-reveal from-right delay-2">
        <div class="recommended-badge">
          <i class="fas fa-star"></i> {{ $t('upgrade.recommended') }}
        </div>
        <div class="card-header">
          <h2 class="plan-name">{{ $t('upgrade.pricing.pro.name') }}</h2>
          <p class="plan-price">{{ $t('upgrade.pricing.pro.price') }}<span class="price-unit">{{ $t('upgrade.pricing.pro.currency') }}</span><span class="price-term">{{ $t('upgrade.pricing.pro.period') }}</span></p>
          <p class="plan-description">{{ $t('upgrade.pricing.pro.description') }}</p>
        </div>
        <div class="card-body">
          <ul class="features-list">
            <li v-for="(feature, index) in proFeatures" :key="index">
              <i class="fas fa-check-circle feature-icon pro-feature"></i> {{ feature }}
            </li>
          </ul>
        </div>
        <div class="card-footer">
          <button class="btn btn-primary btn-upgrade" @click="goToPayment">{{ $t('upgrade.upgradeNow') }}</button>
        </div>
      </div>
    </div>

    <!-- New Features Section -->
    <div class="features-section">
      <h2 class="section-title scroll-reveal from-bottom delay-1">{{ $t('upgrade.featuresTitle') }}</h2>
      <div class="features-grid">
        <div class="feature-card scroll-reveal from-bottom delay-2">
          <div class="feature-icon">
            <i class="fas fa-robot"></i>
          </div>
          <h3>{{ $t('upgrade.aiAssistantTitle') }}</h3>
          <p>{{ $t('upgrade.aiAssistantDesc') }}</p>
        </div>

        <div class="feature-card scroll-reveal from-bottom delay-3">
          <div class="feature-icon">
            <i class="fas fa-chart-line"></i>
          </div>
          <h3>{{ $t('upgrade.learningAnalyticsTitle') }}</h3>
          <p>{{ $t('upgrade.learningAnalyticsDesc') }}</p>
        </div>

        <div class="feature-card scroll-reveal from-bottom delay-4">
          <div class="feature-icon">
            <i class="fas fa-brain"></i>
          </div>
          <h3>{{ $t('upgrade.smartLearningTitle') }}</h3>
          <p>{{ $t('upgrade.smartLearningDesc') }}</p>
        </div>

        <div class="feature-card scroll-reveal from-bottom delay-5">
          <div class="feature-icon">
            <i class="fas fa-book-reader"></i>
          </div>
          <h3>{{ $t('upgrade.advancedContentTitle') }}</h3>
          <p>{{ $t('upgrade.advancedContentDesc') }}</p>
        </div>
      </div>
    </div>

    <!-- Testimonials Section -->
    <div class="testimonials-section">
      <h2 class="section-title scroll-reveal from-bottom delay-1">{{ $t('upgrade.testimonialsTitle') }}</h2>
      <div class="testimonials-grid">
        <div class="testimonial-card scroll-reveal from-left delay-2">
          <div class="user-info">
            <img :src="ImagePaths.avatar.default" alt="User Avatar" class="user-avatar">
            <div class="user-details">
              <h4>{{ $t('upgrade.testimonial1Name') }}</h4>
              <p>{{ $t('upgrade.testimonial1Role') }}</p>
            </div>
          </div>
          <p class="testimonial-text">{{ $t('upgrade.testimonial1Text') }}</p>
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
            <img :src="ImagePaths.avatar.default" alt="User Avatar" class="user-avatar">
            <div class="user-details">
              <h4>{{ $t('upgrade.testimonial2Name') }}</h4>
              <p>{{ $t('upgrade.testimonial2Role') }}</p>
            </div>
          </div>
          <p class="testimonial-text">{{ $t('upgrade.testimonial2Text') }}</p>
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
      <h2 class="section-title scroll-reveal from-bottom delay-1">{{ $t('upgrade.faqTitle') }}</h2>
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
      <h2>{{ $t('upgrade.ctaTitle') }}</h2>
      <p>{{ $t('upgrade.ctaDescription') }}</p>
      <button class="btn btn-primary btn-large" @click="goToPayment">
        {{ $t('upgrade.startNow') }}
        <i class="fas fa-arrow-right"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ImagePaths } from '@/assets/img/imagePaths';
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { initScrollReveal } from '@/assets/js/common';

const router = useRouter();
const { t } = useI18n();

// Computed properties for features using i18n
const freeFeatures = computed(() => [
  t('upgrade.pricing.free.features.feature1'),
  t('upgrade.pricing.free.features.feature2'),
  t('upgrade.pricing.free.features.feature3')
]);

const proFeatures = computed(() => [
  t('upgrade.pricing.pro.features.feature1'),
  t('upgrade.pricing.pro.features.feature2'),
  t('upgrade.pricing.pro.features.feature3'),
  t('upgrade.pricing.pro.features.feature4'),
  t('upgrade.pricing.pro.features.feature5'),
  t('upgrade.pricing.pro.features.feature6')
]);

const proFeaturesDisabled = computed(() => proFeatures.value.slice(1));

// FAQ Items using i18n
const faqItems = computed(() => [
  {
    question: t('upgrade.faq.question1'),
    answer: t('upgrade.faq.answer1')
  },
  {
    question: t('upgrade.faq.question2'),
    answer: t('upgrade.faq.answer2')
  },
  {
    question: t('upgrade.faq.question3'),
    answer: t('upgrade.faq.answer3')
  },
  {
    question: t('upgrade.faq.question4'),
    answer: t('upgrade.faq.answer4')
  }
]);

const goToPayment = () => {
  router.push('/payment');
};

onMounted(() => {
  initScrollReveal();
});

const faqs = ref(faqItems.value.map(item => ({
  ...item,
  isOpen: false
})));


</script>

<style lang="scss" scoped>
@use '@/views/pay-fee/upgrade/TheUpgrade.scss';
</style>