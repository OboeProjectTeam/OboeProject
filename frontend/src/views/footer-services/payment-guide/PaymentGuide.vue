<template>
  <div class="payment-guide-page">
    <div class="payment-guide-container">
      <div class="payment-guide-header">
        <h1>{{ t('paymentGuide.title') }}</h1>
        <p class="last-updated">{{ t('paymentGuide.lastUpdated') }} {{ lastUpdated }}</p>
      </div>

      <div class="payment-guide-content">
        <section class="guide-section">
          <h2>{{ t('paymentGuide.servicePackages.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('paymentGuide.servicePackages.free.title') }}</h3>
            <ul>
              <li>{{ t('paymentGuide.servicePackages.free.features.basicFeatures') }}</li>
              <li>{{ t('paymentGuide.servicePackages.free.features.flashcards') }}</li>
              <li>{{ t('paymentGuide.servicePackages.free.features.forum') }}</li>
              <li>{{ t('paymentGuide.servicePackages.free.features.dictionary') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('paymentGuide.servicePackages.premium.title') }}</h3>
            <ul>
              <li>{{ t('paymentGuide.servicePackages.premium.features.allFree') }}</li>
              <li>{{ t('paymentGuide.servicePackages.premium.features.unlimitedFlashcards') }}</li>
              <li>{{ t('paymentGuide.servicePackages.premium.features.jlptTests') }}</li>
              <li>{{ t('paymentGuide.servicePackages.premium.features.analytics') }}</li>
              <li>{{ t('paymentGuide.servicePackages.premium.features.prioritySupport') }}</li>
              <li>{{ t('paymentGuide.servicePackages.premium.features.noAds') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('paymentGuide.paymentMethods.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('paymentGuide.paymentMethods.cards.title') }}</h3>
            <p>{{ t('paymentGuide.paymentMethods.cards.description') }}</p>
            <ul>
              <li>{{ t('paymentGuide.paymentMethods.cards.types.visa') }}</li>
              <li>{{ t('paymentGuide.paymentMethods.cards.types.mastercard') }}</li>
              <li>{{ t('paymentGuide.paymentMethods.cards.types.jcb') }}</li>
              <li>{{ t('paymentGuide.paymentMethods.cards.types.atmCard') }}</li>
            </ul>
          </div>
          <div class="sub-section">
            <h3>{{ t('paymentGuide.paymentMethods.eWallets.title') }}</h3>
            <ul>
              <li>{{ t('paymentGuide.paymentMethods.eWallets.types.momo') }}</li>
              <li>{{ t('paymentGuide.paymentMethods.eWallets.types.paypal') }}</li>
            </ul>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('paymentGuide.paymentProcess.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('paymentGuide.paymentProcess.selectPackage.title') }}</h3>
            <ol>
              <li>{{ t('paymentGuide.paymentProcess.selectPackage.steps.login') }}</li>
              <li>{{ t('paymentGuide.paymentProcess.selectPackage.steps.upgrade') }}</li>
              <li>{{ t('paymentGuide.paymentProcess.selectPackage.steps.selectPremium') }}</li>
              <li>{{ t('paymentGuide.paymentProcess.selectPackage.steps.continue') }}</li>
            </ol>
          </div>
          <div class="sub-section">
            <h3>{{ t('paymentGuide.paymentProcess.makePayment.title') }}</h3>
            <ol>
              <li>{{ t('paymentGuide.paymentProcess.makePayment.steps.selectMethod') }}</li>
              <li>{{ t('paymentGuide.paymentProcess.makePayment.steps.fillInfo') }}</li>
              <li>{{ t('paymentGuide.paymentProcess.makePayment.steps.confirm') }}</li>
              <li>{{ t('paymentGuide.paymentProcess.makePayment.steps.wait') }}</li>
            </ol>
          </div>
        </section>

        <section class="guide-section">
          <h2>{{ t('paymentGuide.refundPolicy.title') }}</h2>
          <p>{{ t('paymentGuide.refundPolicy.description') }}</p>
        </section>

        <section class="guide-section">
          <h2>{{ t('paymentGuide.faq.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('paymentGuide.faq.payment.title') }}</h3>
            <div class="faq-item">
              <p class="question">{{ t('paymentGuide.faq.payment.changeMethod.question') }}</p>
              <p class="answer">{{ t('paymentGuide.faq.payment.changeMethod.answer') }}</p>
            </div>
            <div class="faq-item">
              <p class="question">{{ t('paymentGuide.faq.payment.noEmail.question') }}</p>
              <p class="answer">{{ t('paymentGuide.faq.payment.noEmail.answer') }}</p>
            </div>
          </div>
          <div class="sub-section">
            <h3>{{ t('paymentGuide.faq.premium.title') }}</h3>
            <div class="faq-item">
              <p class="question">{{ t('paymentGuide.faq.premium.whenUpgrade.question') }}</p>
              <p class="answer">{{ t('paymentGuide.faq.premium.whenUpgrade.answer') }}</p>
            </div>
            <div class="faq-item">
              <p class="question">{{ t('paymentGuide.faq.premium.shareAccount.question') }}</p>
              <p class="answer">{{ t('paymentGuide.faq.premium.shareAccount.answer') }}</p>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script>
import { useI18n } from 'vue-i18n'

export default {
  name: 'PaymentGuide',
  setup() {
    const { t } = useI18n()
    return { t }
  },
  data() {
    return {
      lastUpdated: '15/12/2024'
    }
  }
}
</script>

<style lang="scss" scoped>
@use '@/views/footer-services/payment-guide/PaymentGuide.scss';
</style>