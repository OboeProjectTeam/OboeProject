<template>
  <div class="terms-page">
    <div class="terms-container">
      <div class="terms-header">
        <h1>{{ t('terms.title') }}</h1>
        <p class="last-updated">{{ t('terms.lastUpdated') }}</p>
      </div>

      <div class="terms-content">
        <section class="terms-section">
          <h2>{{ t('terms.section1Title') }}</h2>
          <p>{{ t('terms.section1Description1') }}</p>
          <p>{{ t('terms.section1Description2') }}</p>
        </section>

        <section class="terms-section">
          <h2>{{ t('terms.section2Title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('terms.section2_1Title') }}</h3>
            <p>{{ t('terms.section2_1Description') }}</p>
          </div>
          <div class="sub-section">
            <h3>{{ t('terms.section2_2Title') }}</h3>
            <p>{{ t('terms.section2_2Description') }}</p>
            <ul>
              <li>{{ t('terms.security1') }}</li>
              <li>{{ t('terms.security2') }}</li>
              <li>{{ t('terms.security3') }}</li>
            </ul>
          </div>
        </section>

        <section class="terms-section">
          <h2>{{ t('terms.section3Title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('terms.section3_1Title') }}</h3>
            <ul>
              <li>{{ t('terms.service1') }}</li>
              <li>{{ t('terms.service2') }}</li>
              <li>{{ t('terms.service3') }}</li>
              <li>{{ t('terms.service4') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('terms.section3_2Title') }}</h3>
            <ul>
              <li>{{ t('terms.community1') }}</li>
              <li>{{ t('terms.community2') }}</li>
              <li>{{ t('terms.community3') }}</li>
            </ul>
          </div>
        </section>

        <section class="terms-section">
          <h2>{{ t('terms.section4Title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('terms.section4_1Title') }}</h3>
            <ul>
              <li>{{ t('terms.userRight1') }}</li>
              <li>{{ t('terms.userRight2') }}</li>
              <li>{{ t('terms.userRight3') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('terms.section4_2Title') }}</h3>
            <ul>
              <li>{{ t('terms.userResponsibility1') }}</li>
              <li>{{ t('terms.userResponsibility2') }}</li>
              <li>{{ t('terms.userResponsibility3') }}</li>
            </ul>
          </div>
        </section>

        <section class="terms-section">
          <h2>{{ t('terms.section5Title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('terms.section5_1Title') }}</h3>
            <ul>
              <li>{{ t('terms.plan1') }}</li>
              <li>{{ t('terms.plan2') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('terms.section5_2Title') }}</h3>
            <ul>
              <li>{{ t('terms.payment1') }}</li>
              <li>{{ t('terms.payment2') }}</li>
              <li>{{ t('terms.payment3') }}</li>
            </ul>
          </div>
        </section>

        <section class="terms-section">
          <h2>{{ t('terms.section6Title') }}</h2>
          <p>{{ t('terms.section6Description') }}</p>
          <ul>
            <li>{{ t('terms.dataSecurity1') }}</li>
            <li>{{ t('terms.dataSecurity2') }}</li>
            <li>{{ t('terms.dataSecurity3') }}</li>
          </ul>
        </section>

        <section class="terms-section">
          <h2>{{ t('terms.section7Title') }}</h2>
          <ul>
            <li>{{ t('terms.other1') }}</li>
            <li>{{ t('terms.other2') }}</li>
            <li>{{ t('terms.other3') }}</li>
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
@use '@/views/footer-services/terms/TheTerms.scss';
</style>