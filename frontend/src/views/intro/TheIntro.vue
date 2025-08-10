<template>
  <div class="intro-page">
    <!-- Section 1: Hero -->
    <section class="ip-hero">
      <div class="ip-container">
        <h1 class="ip-hero-title scroll-reveal from-left delay-1" v-html="t('intro.heroTitle')"></h1>
        <p class="ip-hero-subtitle scroll-reveal from-right delay-2" v-html="t('intro.heroSubtitle')"></p>
        <router-link to="/login" class="ip-cta-button primary scroll-reveal from-bottom delay-2">{{ t('intro.startLearningFree') }}</router-link>
      </div>
    </section>

    <!-- Section 2: Key Benefits -->
    <section class="ip-benefits scroll-reveal from-bottom delay-2">
      <div class="ip-container">
        <div class="ip-benefit-card">
          <i class="fas fa-drafting-compass"></i>
          <h3>{{ t('intro.flexibleTools') }}</h3>
          <p>{{ t('intro.flexibleToolsDesc') }}</p>
        </div>
        <div class="ip-benefit-card">
          <i class="fas fa-layer-group"></i>
          <h3>{{ t('intro.clearRoadmap') }}</h3>
          <p>{{ t('intro.clearRoadmapDesc') }}</p>
        </div>
        <div class="ip-benefit-card">
          <i class="fas fa-users"></i>
          <h3>{{ t('intro.supportCommunity') }}</h3>
          <p>{{ t('intro.supportCommunityDesc') }}</p>
        </div>
      </div>
    </section>

    <!-- Section 3: Feature Deep Dive 1 -->
    <section class="ip-feature scroll-reveal">
      <div class="ip-container ip-feature-container">
        <div class="ip-feature-text scroll-reveal from-left delay-1">
          <h2 class="ip-feature-title ">{{ t('intro.buildKnowledge') }}</h2>
          <p>{{ t('intro.buildKnowledgeDesc') }}</p>
          <router-link to="/create/flashcard" class="ip-feature-link">{{ t('intro.startCreating') }} <i
              class="fas fa-arrow-right"></i></router-link>
        </div>
          <TheCard class="ip-feature-card scroll-reveal from-right delay-2" :slides="slides" :width="cardWidth" :height="cardHeight" :autoplay="true" :pagination="{ clickable: true }"  />
      </div>
    </section>

    <!-- Section 4: Feature Deep Dive 2 (Alt Layout) -->
    <section class="ip-feature alt-layout scroll-reveal">
      <div class="ip-container ip-feature-container">
        <div class="ip-feature-text scroll-reveal from-right delay-1">
          <h2 class="ip-feature-title ">{{ t('intro.practiceRemember') }}</h2>
          <p>{{ t('intro.practiceRememberDesc') }}</p>
          <router-link to="/library" class="ip-feature-link">{{ t('intro.viewHistory') }} <i
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
          <h2 class="ip-feature-title">{{ t('intro.connectExchange') }}</h2>
          <p>{{ t('intro.connectExchangeDesc') }}</p>
          <router-link to="/forum" class="ip-feature-link">{{ t('intro.exploreForum') }} <i
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
        <h2 class="ip-final-cta-title">{{ t('intro.readyToBreakthrough') }}</h2>
        <p>{{ t('intro.joinCommunity') }}</p>
        <router-link to="/register" class="ip-cta-button secondary">{{ t('intro.joinNow') }}</router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ImagePaths } from '@/assets/img/imagePaths';
import { onMounted, ref, computed, onUnmounted } from 'vue';
import { initScrollReveal } from '@/assets/js/common';
import TheCard from '@/components/layout/card/TheCard.vue';
import { useStore } from 'vuex'; // Import useStore to access Vuex state
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const store = useStore(); // Access Vuex store
const slides = store.getters['cart/slides']; // Get slides from the Vuex store

// Responsive card dimensions
const windowWidth = ref(window.innerWidth);

const cardWidth = computed(() => {
  return windowWidth.value > 768 ? 400 : 250;
});

const cardHeight = computed(() => {
  return windowWidth.value > 768 ? 300 : 350;
});

// Handle window resize
const handleResize = () => {
  windowWidth.value = window.innerWidth;
};

onMounted(() => {
  initScrollReveal();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
});
</script>

<style lang="scss" scoped>
@use '@/views/intro/TheIntro.scss';
</style>
