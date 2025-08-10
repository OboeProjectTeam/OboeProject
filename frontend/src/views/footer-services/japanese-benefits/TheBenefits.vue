<template>
  <div class="benefits-page">
    <div class="benefits-container">
      <div class="benefits-header">
        <h1>{{ t('japaneseBenefits.title') }}</h1>
        <p class="last-updated">{{ t('japaneseBenefits.lastUpdated') }} {{ lastUpdated }}</p>
      </div>

      <div class="benefits-content">
        <section class="guide-section">
          <h2>{{ t('japaneseBenefits.careerOpportunities.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('japaneseBenefits.careerOpportunities.japanMarket.title') }}</h3>
            <ul>
              <li>{{ t('japaneseBenefits.careerOpportunities.japanMarket.benefits.jobs') }}</li>
              <li>{{ t('japaneseBenefits.careerOpportunities.japanMarket.benefits.visa') }}</li>
              <li>{{ t('japaneseBenefits.careerOpportunities.japanMarket.benefits.environment') }}</li>
              <li>{{ t('japaneseBenefits.careerOpportunities.japanMarket.benefits.promotion') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('japaneseBenefits.careerOpportunities.vietnamMarket.title') }}</h3>
            <ul>
              <li>{{ t('japaneseBenefits.careerOpportunities.vietnamMarket.benefits.companies') }}</li>
              <li>{{ t('japaneseBenefits.careerOpportunities.vietnamMarket.benefits.translation') }}</li>
              <li>{{ t('japaneseBenefits.careerOpportunities.vietnamMarket.benefits.tourism') }}</li>
              <li>{{ t('japaneseBenefits.careerOpportunities.vietnamMarket.benefits.teaching') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('japaneseBenefits.education.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('japaneseBenefits.education.studyAbroad.title') }}</h3>
            <ul>
              <li>{{ t('japaneseBenefits.education.studyAbroad.benefits.tuition') }}</li>
              <li>{{ t('japaneseBenefits.education.studyAbroad.benefits.scholarships') }}</li>
              <li>{{ t('japaneseBenefits.education.studyAbroad.benefits.quality') }}</li>
              <li>{{ t('japaneseBenefits.education.studyAbroad.benefits.partTime') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('japaneseBenefits.education.research.title') }}</h3>
            <ul>
              <li>{{ t('japaneseBenefits.education.research.benefits.materials') }}</li>
              <li>{{ t('japaneseBenefits.education.research.benefits.projects') }}</li>
              <li>{{ t('japaneseBenefits.education.research.benefits.technology') }}</li>
              <li>{{ t('japaneseBenefits.education.research.benefits.exchange') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('japaneseBenefits.personalDevelopment.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('japaneseBenefits.personalDevelopment.thinking.title') }}</h3>
            <ul>
              <li>{{ t('japaneseBenefits.personalDevelopment.thinking.benefits.patience') }}</li>
              <li>{{ t('japaneseBenefits.personalDevelopment.thinking.benefits.memory') }}</li>
              <li>{{ t('japaneseBenefits.personalDevelopment.thinking.benefits.logic') }}</li>
              <li>{{ t('japaneseBenefits.personalDevelopment.thinking.benefits.problemSolving') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('japaneseBenefits.personalDevelopment.cultural.title') }}</h3>
            <ul>
              <li>{{ t('japaneseBenefits.personalDevelopment.cultural.benefits.understanding') }}</li>
              <li>{{ t('japaneseBenefits.personalDevelopment.cultural.benefits.workEthic') }}</li>
              <li>{{ t('japaneseBenefits.personalDevelopment.cultural.benefits.lifestyle') }}</li>
              <li>{{ t('japaneseBenefits.personalDevelopment.cultural.benefits.perspective') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('japaneseBenefits.lifeBenefits.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('japaneseBenefits.lifeBenefits.entertainment.title') }}</h3>
            <ul>
              <li>{{ t('japaneseBenefits.lifeBenefits.entertainment.benefits.anime') }}</li>
              <li>{{ t('japaneseBenefits.lifeBenefits.entertainment.benefits.literature') }}</li>
              <li>{{ t('japaneseBenefits.lifeBenefits.entertainment.benefits.events') }}</li>
              <li>{{ t('japaneseBenefits.lifeBenefits.entertainment.benefits.community') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('japaneseBenefits.lifeBenefits.travel.title') }}</h3>
            <ul>
              <li>{{ t('japaneseBenefits.lifeBenefits.travel.benefits.confidence') }}</li>
              <li>{{ t('japaneseBenefits.lifeBenefits.travel.benefits.cuisine') }}</li>
              <li>{{ t('japaneseBenefits.lifeBenefits.travel.benefits.communication') }}</li>
              <li>{{ t('japaneseBenefits.lifeBenefits.travel.benefits.festivals') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('japaneseBenefits.futureProspects.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('japaneseBenefits.futureProspects.marketTrends.title') }}</h3>
            <div class="stats-box">
              <div class="stat-item">
                <h4>{{ t('japaneseBenefits.futureProspects.marketTrends.stats.demand.title') }}</h4>
                <p class="stat-number">{{ t('japaneseBenefits.futureProspects.marketTrends.stats.demand.value') }}</p>
                <p class="stat-desc">{{ t('japaneseBenefits.futureProspects.marketTrends.stats.demand.description') }}</p>
              </div>
              <div class="stat-item">
                <h4>{{ t('japaneseBenefits.futureProspects.marketTrends.stats.salary.title') }}</h4>
                <p class="stat-number">{{ t('japaneseBenefits.futureProspects.marketTrends.stats.salary.value') }}</p>
                <p class="stat-desc">{{ t('japaneseBenefits.futureProspects.marketTrends.stats.salary.description') }}</p>
              </div>
              <div class="stat-item">
                <h4>{{ t('japaneseBenefits.futureProspects.marketTrends.stats.jobs.title') }}</h4>
                <p class="stat-number">{{ t('japaneseBenefits.futureProspects.marketTrends.stats.jobs.value') }}</p>
                <p class="stat-desc">{{ t('japaneseBenefits.futureProspects.marketTrends.stats.jobs.description') }}</p>
              </div>
            </div>
          </div>
          <div class="sub-section">
            <h3>{{ t('japaneseBenefits.futureProspects.personalGrowth.title') }}</h3>
            <div class="quote-box">
              <blockquote>
                {{ t('japaneseBenefits.futureProspects.personalGrowth.quote') }}
              </blockquote>
              <p class="quote-author">{{ t('japaneseBenefits.futureProspects.personalGrowth.author') }}</p>
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
@use '@/views/footer-services/japanese-benefits/TheBenefits.scss';
</style>