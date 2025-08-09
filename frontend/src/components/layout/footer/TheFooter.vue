<template>
  <div class="footer">
    <div class="footer-bot flex">
      <div class="footer-infor">
        <TheLogo class="footer-logo" />
        <ul>
          <li class="flex p-hover" v-for="(item, index) in listInfor" :key="index">
            <div class="mr-10 pb-25 footer-inforicon">
              <i :class="item.icon" class="w-100"></i>
            </div>
            <p class="footer-infortext">{{ item.title }}</p>
          </li>
        </ul>
        <ul class="flex social-links">
          <li class="mr-10" v-for="(item, index) in listCNC" :key="index">
            <a :href="item.src" target="_blank" rel="noopener noreferrer">
              <i :class="item.icon" class="w-100"></i>
            </a>
          </li>
        </ul>
      </div>
      <div class="footer-listservies-hr">
        <ul class="flex">
          <li class="mr-10 listservies-box" v-for="(items, outerIndex) in listServies" :key="outerIndex">
            <p class="listservies-title">{{ items.title }}</p>
            <div class="listservies-hr"></div>
            <ul>
              <li :class="[
                'listservies-detail',
                {
                  cur: outerIndex !== listServies.length - 1,
                  'p-hover': outerIndex !== listServies.length - 1
                }
              ]" v-for="(item, innerIndex) in items.service" :key="innerIndex">
                <router-link v-if="item.link" :to="item.link">
                  <p>{{ item.title }}</p>
                </router-link>
                <p v-else>{{ item.title }}</p>
              </li>
            </ul>
            <MsButton 
              v-if="outerIndex === listServies.length - 1" 
              class="button-hover footer-comment-btn" 
              height="40px" 
              @click="handleFeedbackClick"
            >
              {{ t('footer.sendFeedback') }}
            </MsButton>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import TheLogo from '@/components/layout/logo/TheLogo.vue';
import MsButton from '@/components/common/button/MsButton.vue';

const store = useStore();
const router = useRouter();
const { t } = useI18n();

// Use computed properties for reactive i18n data
const listInfor = computed(() => [
  {
    icon: "fas fa-mobile-screen-button",
    title: t('footer.contactInfo.phone')
  },
  {
    icon: "far fa-envelope",
    title: t('footer.contactInfo.email')
  },
  {
    icon: "far fa-map",
    title: t('footer.contactInfo.address')
  },
  {
    icon: "far fa-clock",
    title: t('footer.contactInfo.workingHours')
  }
]);

// Get social links from store (no translation needed)
const listCNC = store.getters['footer/ListCNC'];

const listServies = computed(() => [
  {
    title: t('footer.services.customerService.title'),
    service: [
      { title: t('footer.services.customerService.termsOfService'), link: "/footer-services" },
      { title: t('footer.services.customerService.privacyPolicy'), link: "/footer-services/privacy" },
      { title: t('footer.services.customerService.studyGuide'), link: "/footer-services/study-guide" },
      { title: t('footer.services.customerService.paymentGuide'), link: "/footer-services/payment-guide" }
    ]
  },
  {
    title: t('footer.services.usageTips.title'),
    service: [
      { title: t('footer.services.usageTips.whyFlashcard'), link: "/footer-services/why-flashcard" },
      { title: t('footer.services.usageTips.writingGuide'), link: "/footer-services/writing-guide" },
      { title: t('footer.services.usageTips.japaneseBenefits'), link: "/footer-services/benefits-japanese" },
      { title: t('footer.services.usageTips.kanjiGuide'), link: "/footer-services/kanji-guide" }
    ]
  },
  {
    title: t('footer.services.generalInfo.title'),
    service: [
      { title: t('footer.services.generalInfo.japanNews'), link: "/footer-services/japan-news" },
      { title: t('footer.services.generalInfo.upgradeAccount'), link: "/upgrade" },
      { title: t('footer.services.generalInfo.contact'), link: "/footer-services/contact" }
    ]
  },
  {
    title: t('footer.services.feedback.title'),
    service: [
      {
        title: t('footer.services.feedback.message1'),
        link: ""
      },
      {
        title: t('footer.services.feedback.message2'),
        link: ""
      }
    ]
  }
]);

const handleFeedbackClick = () => {
  router.push('/footer-services/contact');
};
</script>

<style lang="scss" scoped>
@use '@/components/layout/footer/TheFooter.scss';
</style>
