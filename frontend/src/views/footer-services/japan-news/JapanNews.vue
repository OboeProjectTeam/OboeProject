<template>
  <div class="japan-news-page">
    <div class="japan-news-container">
      <div class="japan-news-header">
        <h1>{{ t('japanNews.title') }}</h1>
        <p class="last-updated">{{ t('japanNews.lastUpdated') }} {{ lastUpdated }}</p>
      </div>

      <div class="japan-news-content">
        <section class="news-section featured-news">
          <h2>{{ t('japanNews.featuredNews.title') }}</h2>
          <div class="featured-news-grid">
            <div class="featured-news-item" v-for="(news, index) in featuredNews" :key="index">
              <div class="news-image">
                <img :src="news.image" :alt="news.title">
              </div>
              <div class="news-content">
                <span class="news-category">{{ news.category }}</span>
                <h3>{{ news.title }}</h3>
                <p class="news-excerpt">{{ news.excerpt }}</p>
                <div class="news-meta">
                  <span class="news-date">{{ news.date }}</span>
                  <span class="news-source">{{ news.source }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="news-section market-trends">
          <h2>{{ t('japanNews.marketTrends.title') }}</h2>
          <div class="market-trends-grid">
            <div class="trend-item" v-for="(trend, index) in marketTrends" :key="index">
              <div class="trend-icon">
                <i :class="trend.icon"></i>
              </div>
              <div class="trend-content">
                <h3>{{ trend.title }}</h3>
                <p>{{ trend.description }}</p>
                <div class="trend-stats">
                  <span class="trend-value">{{ trend.value }}</span>
                  <span class="trend-change" :class="trend.changeType">
                    <i :class="trend.changeIcon"></i>
                    {{ trend.change }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="news-section job-opportunities">
          <h2>{{ t('japanNews.jobOpportunities.title') }}</h2>
          <div class="job-categories">
            <div class="job-category" v-for="(category, index) in jobCategories" :key="index">
              <h3>{{ category.title }}</h3>
              <ul class="job-list">
                <li v-for="(job, jobIndex) in category.jobs" :key="jobIndex">
                  <div class="job-header">
                    <span class="job-title">{{ job.title }}</span>
                    <span class="job-salary">{{ job.salary }}</span>
                  </div>
                  <div class="job-details">
                    <span class="job-location">
                      <i class="fas fa-map-marker-alt"></i>
                      {{ job.location }}
                    </span>
                    <span class="job-company">
                      <i class="fas fa-building"></i>
                      {{ job.company }}
                    </span>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </section>

        <section class="news-section study-abroad">
          <h2>{{ t('japanNews.studyAbroad.title') }}</h2>
          <div class="education-updates">
            <div class="update-item" v-for="(update, index) in educationUpdates" :key="index">
              <div class="update-icon">
                <i :class="update.icon"></i>
              </div>
              <div class="update-content">
                <h3>{{ update.title }}</h3>
                <p>{{ update.description }}</p>
                <ul class="update-details">
                  <li v-for="(detail, detailIndex) in update.details" :key="detailIndex">
                    {{ detail }}
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </section>

        <section class="news-section cultural-exchange">
          <h2>{{ t('japanNews.culturalExchange.title') }}</h2>
          <div class="events-grid">
            <div class="event-item" v-for="(event, index) in culturalEvents" :key="index">
              <div class="event-image">
                <img :src="event.image" :alt="event.title">
              </div>
              <div class="event-content">
                <span class="event-date">{{ event.date }}</span>
                <h3>{{ event.title }}</h3>
                <p>{{ event.description }}</p>
                <div class="event-location">
                  <i class="fas fa-map-marker-alt"></i>
                  {{ event.location }}
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ImagePaths } from '@/assets/img/imagePaths';
import { ref, computed } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const lastUpdated = ref('15/03/2024');

const featuredNews = computed(() => [
  {
    image: ImagePaths.footer.japanVisa,
    category: t('japanNews.featuredNews.economy'),
    title: t('japanNews.featuredNews.visaNews.title'),
    excerpt: t('japanNews.featuredNews.visaNews.excerpt'),
    date: '15/03/2024',
    source: t('japanNews.featuredNews.visaNews.source')
  },
  {
    image: ImagePaths.footer.japanAi,
    category: t('japanNews.featuredNews.technology'),
    title: t('japanNews.featuredNews.aiNews.title'),
    excerpt: t('japanNews.featuredNews.aiNews.excerpt'),
    date: '14/03/2024',
    source: t('japanNews.featuredNews.aiNews.source')
  }
]);

const marketTrends = computed(() => [
  {
    icon: 'fas fa-chart-line',
    title: t('japanNews.marketTrends.gdpGrowth.title'),
    description: t('japanNews.marketTrends.gdpGrowth.description'),
    value: '2.8%',
    change: '+0.5%',
    changeType: 'positive',
    changeIcon: 'fas fa-arrow-up'
  },
  {
    icon: 'fas fa-yen-sign',
    title: t('japanNews.marketTrends.yenRate.title'),
    description: t('japanNews.marketTrends.yenRate.description'),
    value: '148.25',
    change: '-0.3%',
    changeType: 'negative',
    changeIcon: 'fas fa-arrow-down'
  }
]);

const jobCategories = computed(() => [
  {
    title: t('japanNews.jobOpportunities.it.title'),
    jobs: [
      {
        title: t('japanNews.jobOpportunities.it.softwareEngineer'),
        salary: '¥4.5M - ¥7M/năm',
        location: t('japanNews.jobOpportunities.it.location'),
        company: t('japanNews.jobOpportunities.it.company1')
      },
      {
        title: t('japanNews.jobOpportunities.it.frontendDeveloper'),
        salary: '¥4M - ¥6M/năm',
        location: t('japanNews.jobOpportunities.it.locationOsaka'),
        company: t('japanNews.jobOpportunities.it.company2')
      }
    ]
  },
  {
    title: t('japanNews.jobOpportunities.manufacturing.title'),
    jobs: [
      {
        title: t('japanNews.jobOpportunities.manufacturing.mechanicalEngineer'),
        salary: '¥3.8M - ¥5.5M/năm',
        location: t('japanNews.jobOpportunities.manufacturing.locationNagoya'),
        company: t('japanNews.jobOpportunities.manufacturing.company1')
      },
      {
        title: t('japanNews.jobOpportunities.manufacturing.electronicsEngineer'),
        salary: '¥4M - ¥6M/năm',
        location: t('japanNews.jobOpportunities.manufacturing.locationYokohama'),
        company: t('japanNews.jobOpportunities.manufacturing.company2')
      }
    ]
  }
]);

const educationUpdates = computed(() => [
  {
    icon: 'fas fa-graduation-cap',
    title: t('japanNews.studyAbroad.mextScholarship.title'),
    description: t('japanNews.studyAbroad.mextScholarship.description'),
    details: [
      t('japanNews.studyAbroad.mextScholarship.deadline'),
      t('japanNews.studyAbroad.mextScholarship.value'),
      t('japanNews.studyAbroad.mextScholarship.flight'),
      t('japanNews.studyAbroad.mextScholarship.tuition')
    ]
  },
  {
    icon: 'fas fa-school',
    title: t('japanNews.studyAbroad.ejuExam.title'),
    description: t('japanNews.studyAbroad.ejuExam.description'),
    details: [
      t('japanNews.studyAbroad.ejuExam.examDate'),
      t('japanNews.studyAbroad.ejuExam.registerDeadline'),
      t('japanNews.studyAbroad.ejuExam.location'),
      t('japanNews.studyAbroad.ejuExam.fee')
    ]
  }
]);

const culturalEvents = computed(() => [
  {
    image: ImagePaths.footer.japanSakura,
    date: '20/04/2024',
    title: t('japanNews.culturalExchange.sakuraFestival.title'),
    description: t('japanNews.culturalExchange.sakuraFestival.description'),
    location: t('japanNews.culturalExchange.sakuraFestival.location')
  },
  {
    image: ImagePaths.footer.japanCulture,
    date: '15/05/2024',
    title: t('japanNews.culturalExchange.culturalExhibition.title'),
    description: t('japanNews.culturalExchange.culturalExhibition.description'),
    location: t('japanNews.culturalExchange.culturalExhibition.location')
  }
]);
</script>

<style lang="scss" scoped>
@use '@/views/footer-services/japan-news/JapanNews.scss';
</style>