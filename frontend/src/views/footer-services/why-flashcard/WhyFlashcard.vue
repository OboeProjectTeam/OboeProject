<template>
  <div class="why-flashcard-page">
    <div class="why-flashcard-container">
      <div class="why-flashcard-header">
        <h1>{{ t('whyFlashcard.title') }}</h1>
        <p class="last-updated">{{ t('whyFlashcard.lastUpdated') }} {{ lastUpdated }}</p>
      </div>

      <div class="why-flashcard-content">
        <section class="guide-section">
          <h2>{{ t('whyFlashcard.scienceTitle') }}</h2>
          <div class="sub-section">
            <h3>{{ t('whyFlashcard.spacedRepetitionTitle') }}</h3>
            <p>{{ t('whyFlashcard.spacedRepetitionDesc') }}</p>
            <ul>
              <li>{{ t('whyFlashcard.spacedRepetitionBenefit1') }}</li>
              <li>{{ t('whyFlashcard.spacedRepetitionBenefit2') }}</li>
              <li>{{ t('whyFlashcard.spacedRepetitionBenefit3') }}</li>
              <li>{{ t('whyFlashcard.spacedRepetitionBenefit4') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('whyFlashcard.testingEffectTitle') }}</h3>
            <p>{{ t('whyFlashcard.testingEffectDesc') }}</p>
            <ul>
              <li>{{ t('whyFlashcard.testingEffectBenefit1') }}</li>
              <li>{{ t('whyFlashcard.testingEffectBenefit2') }}</li>
              <li>{{ t('whyFlashcard.testingEffectBenefit3') }}</li>
              <li>{{ t('whyFlashcard.testingEffectBenefit4') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('whyFlashcard.benefitsTitle') }}</h2>
          <div class="sub-section">
            <h3>{{ t('whyFlashcard.vocabularyTitle') }}</h3>
            <ul>
              <li>{{ t('whyFlashcard.vocabularyBenefit1') }}</li>
              <li>{{ t('whyFlashcard.vocabularyBenefit2') }}</li>
              <li>{{ t('whyFlashcard.vocabularyBenefit3') }}</li>
              <li>{{ t('whyFlashcard.vocabularyBenefit4') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('whyFlashcard.kanjiTitle') }}</h3>
            <ul>
              <li>{{ t('whyFlashcard.kanjiBenefit1') }}</li>
              <li>{{ t('whyFlashcard.kanjiBenefit2') }}</li>
              <li>{{ t('whyFlashcard.kanjiBenefit3') }}</li>
              <li>{{ t('whyFlashcard.kanjiBenefit4') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('whyFlashcard.featuresTitle') }}</h2>
          <div class="sub-section">
            <h3>{{ t('whyFlashcard.smartSystemTitle') }}</h3>
            <ul>
              <li>{{ t('whyFlashcard.smartSystemFeature1') }}</li>
              <li>{{ t('whyFlashcard.smartSystemFeature2') }}</li>
              <li>{{ t('whyFlashcard.smartSystemFeature3') }}</li>
              <li>{{ t('whyFlashcard.smartSystemFeature4') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('whyFlashcard.customizationTitle') }}</h3>
            <ul>
              <li>{{ t('whyFlashcard.customizationFeature1') }}</li>
              <li>{{ t('whyFlashcard.customizationFeature2') }}</li>
              <li>{{ t('whyFlashcard.customizationFeature3') }}</li>
              <li>{{ t('whyFlashcard.customizationFeature4') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('whyFlashcard.effectiveStudyTitle') }}</h2>
          <div class="sub-section">
            <h3>{{ t('whyFlashcard.habitsTitle') }}</h3>
            <ul>
              <li>{{ t('whyFlashcard.habitsAdvice1') }}</li>
              <li>{{ t('whyFlashcard.habitsAdvice2') }}</li>
              <li>{{ t('whyFlashcard.habitsAdvice3') }}</li>
              <li>{{ t('whyFlashcard.habitsAdvice4') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('whyFlashcard.techniquesTitle') }}</h3>
            <ul>
              <li>{{ t('whyFlashcard.techniquesAdvice1') }}</li>
              <li>{{ t('whyFlashcard.techniquesAdvice2') }}</li>
              <li>{{ t('whyFlashcard.techniquesAdvice3') }}</li>
              <li>{{ t('whyFlashcard.techniquesAdvice4') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('whyFlashcard.resultsTitle') }}</h2>
          <div class="sub-section">
            <h3>{{ t('whyFlashcard.statisticsTitle') }}</h3>
            <ul>
              <li>{{ t('whyFlashcard.statistic1') }}</li>
              <li>{{ t('whyFlashcard.statistic2') }}</li>
              <li>{{ t('whyFlashcard.statistic3') }}</li>
              <li>{{ t('whyFlashcard.statistic4') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('whyFlashcard.testimonialsTitle') }}</h3>
            <div class="testimonial">
              <p class="quote">{{ t('whyFlashcard.testimonial1Quote') }}</p>
              <p class="author">{{ t('whyFlashcard.testimonial1Author') }}</p>
            </div>
            <div class="testimonial">
              <p class="quote">{{ t('whyFlashcard.testimonial2Quote') }}</p>
              <p class="author">{{ t('whyFlashcard.testimonial2Author') }}</p>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const lastUpdated = ref('15/03/2024');
</script>

<style lang="scss" scoped>
@use '@/views/footer-services/why-flashcard/WhyFlashcard.scss';
</style>