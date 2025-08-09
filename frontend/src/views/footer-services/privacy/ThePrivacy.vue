<template>
  <div class="privacy-page">
    <div class="privacy-container">
      <div class="privacy-header">
        <h1>{{ t('privacy.title') }}</h1>
        <p class="last-updated">{{ t('privacy.lastUpdated') }} {{ lastUpdated }}</p>
      </div>

      <div class="privacy-content">
        <section class="privacy-section">
          <h2>{{ t('privacy.section1Title') }}</h2>
          <p>{{ t('privacy.section1Description') }}</p>
        </section>

        <section class="privacy-section">
          <h2>{{ t('privacy.section2Title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('privacy.section2_1Title') }}</h3>
            <ul>
              <li>{{ t('privacy.personalInfo1') }}</li>
              <li>{{ t('privacy.personalInfo2') }}</li>
              <li>{{ t('privacy.personalInfo3') }}</li>
              <li>{{ t('privacy.personalInfo4') }}</li>
              <li>{{ t('privacy.personalInfo5') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('privacy.section2_2Title') }}</h3>
            <ul>
              <li>{{ t('privacy.learningInfo1') }}</li>
              <li>{{ t('privacy.learningInfo2') }}</li>
              <li>{{ t('privacy.learningInfo3') }}</li>
              <li>{{ t('privacy.learningInfo4') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('privacy.section2_3Title') }}</h3>
            <ul>
              <li>{{ t('privacy.interactionInfo1') }}</li>
              <li>{{ t('privacy.interactionInfo2') }}</li>
              <li>{{ t('privacy.interactionInfo3') }}</li>
            </ul>
          </div>
        </section>

        <section class="privacy-section">
          <h2>{{ t('privacy.section3Title') }}</h2>
          <ul>
            <li>{{ t('privacy.purpose1') }}</li>
            <li>{{ t('privacy.purpose2') }}</li>
            <li>{{ t('privacy.purpose3') }}</li>
            <li>{{ t('privacy.purpose4') }}</li>
            <li>{{ t('privacy.purpose5') }}</li>
            <li>{{ t('privacy.purpose6') }}</li>
          </ul>
        </section>

        <section class="privacy-section">
          <h2>{{ t('privacy.section4Title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('privacy.section4_1Title') }}</h3>
            <ul>
              <li>{{ t('privacy.security1') }}</li>
              <li>{{ t('privacy.security2') }}</li>
              <li>{{ t('privacy.security3') }}</li>
              <li>{{ t('privacy.security4') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('privacy.section4_2Title') }}</h3>
            <p>{{ t('privacy.section4_2Description') }}</p>
          </div>
        </section>

        <section class="privacy-section">
          <h2>{{ t('privacy.section5Title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('privacy.section5_1Title') }}</h3>
            <p>{{ t('privacy.section5_1Description') }}</p>
            <ul>
              <li>{{ t('privacy.sharing1') }}</li>
              <li>{{ t('privacy.sharing2') }}</li>
              <li>{{ t('privacy.sharing3') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('privacy.section5_2Title') }}</h3>
            <p>{{ t('privacy.section5_2Description') }}</p>
          </div>
        </section>

        <section class="privacy-section">
          <h2>{{ t('privacy.section6Title') }}</h2>
          <p>{{ t('privacy.section6Description') }}</p>
          <ul>
            <li>{{ t('privacy.userRight1') }}</li>
            <li>{{ t('privacy.userRight2') }}</li>
            <li>{{ t('privacy.userRight3') }}</li>
            <li>{{ t('privacy.userRight4') }}</li>
            <li>{{ t('privacy.userRight5') }}</li>
          </ul>
        </section>

        <section class="privacy-section">
          <h2>{{ t('privacy.section7Title') }}</h2>
          <p>{{ t('privacy.section7Description') }}</p>
          <ul>
            <li>{{ t('privacy.cookie1') }}</li>
            <li>{{ t('privacy.cookie2') }}</li>
            <li>{{ t('privacy.cookie3') }}</li>
            <li>{{ t('privacy.cookie4') }}</li>
          </ul>
        </section>

        <section class="privacy-section">
          <h2>{{ t('privacy.section8Title') }}</h2>
          <p>{{ t('privacy.section8Description') }}</p>
          <ul>
            <li>{{ t('privacy.contactEmail') }}</li>
            <li>{{ t('privacy.contactPhone') }}</li>
            <li>{{ t('privacy.contactAddress') }}</li>
          </ul>
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
@use '@/views/footer-services/privacy/ThePrivacy.scss';
</style>