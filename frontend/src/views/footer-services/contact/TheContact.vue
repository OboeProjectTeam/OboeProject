<template>
  <div class="contact-page">
    <div class="contact-header">
      <h1 class="main-title">{{ t('contact.title') }}</h1>
      <p class="subtitle">{{ t('contact.subtitle') }}</p>
    </div>

    <div class="contact-content">
      <!-- Contact Information -->
      <div class="contact-info">
        <div class="info-card">
          <i class="fas fa-phone-alt"></i>
          <h3>{{ t('contact.phone') }}</h3>
          <p>0775751954</p>
          <p class="time">{{ t('contact.phoneTime') }}</p>
        </div>

        <div class="info-card">
          <i class="fas fa-envelope"></i>
          <h3>{{ t('contact.email') }}</h3>
          <p>bophanchamsoc@oboe.com</p>
          <p class="time">{{ t('contact.emailTime') }}</p>
        </div>

        <div class="info-card">
          <i class="fas fa-map-marker-alt"></i>
          <h3>{{ t('contact.address') }}</h3>
          <p>{{ t('contact.addressDetail') }}</p>
          <p class="time">{{ t('contact.addressNote') }}</p>
        </div>
      </div>

      <!-- Contact Form -->
      <div class="contact-form">
        <h2>{{ t('contact.formTitle') }}</h2>
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label for="name">{{ t('contact.nameLabel') }}</label>
            <input 
              type="text" 
              id="name" 
              v-model="formData.name"
              :placeholder="t('contact.namePlaceholder')"
              required
            >
          </div>

          <div class="form-group">
            <label for="email">{{ t('contact.emailLabel') }}</label>
            <input 
              type="email" 
              id="email" 
              v-model="formData.email"
              :placeholder="t('contact.emailPlaceholder')"
              required
            >
          </div>

          <div class="form-group">
            <label for="title">{{ t('contact.titleLabel') }}</label>
            <input 
              type="text" 
              id="title" 
              v-model="formData.title"
              :placeholder="t('contact.titlePlaceholder')"
              required
            >
          </div>

          <div class="form-group">
            <label for="subject">{{ t('contact.subjectLabel') }}</label>
            <select id="subject" v-model="formData.subject" required>
              <option value="">{{ t('contact.selectSubject') }}</option>
              <option value="support">{{ t('contact.technicalSupport') }}</option>
              <option value="billing">{{ t('contact.paymentIssue') }}</option>
              <option value="feedback">{{ t('contact.productFeedback') }}</option>
              <option value="other">{{ t('contact.other') }}</option>
            </select>
          </div>

          <div class="form-group">
            <label for="message">{{ t('contact.messageLabel') }}</label>
            <textarea 
              id="message" 
              v-model="formData.message"
              rows="5"
              :placeholder="t('contact.messagePlaceholder')"
              required
            ></textarea>
          </div>

          <button type="submit" class="submit-btn" :disabled="isSubmitting">
            {{ isSubmitting ? t('contact.sending') : t('contact.sendMessage') }}
          </button>
        </form>
      </div>
    </div>

    <!-- Social Media Links -->
    <div class="social-links">
      <h2>{{ t('contact.connectWithUs') }}</h2>
      <div class="social-icons">
        <a href="https://facebook.com" target="_blank" rel="noopener noreferrer">
          <i class="fab fa-facebook"></i>
        </a>
        <a href="https://twitter.com" target="_blank" rel="noopener noreferrer">
          <i class="fab fa-twitter"></i>
        </a>
        <a href="https://instagram.com" target="_blank" rel="noopener noreferrer">
          <i class="fab fa-instagram"></i>
        </a>
        <a href="https://youtube.com" target="_blank" rel="noopener noreferrer">
          <i class="fab fa-youtube"></i>
        </a>
      </div>
    </div>

    <!-- Success Popup -->
    <ThePopup
      v-if="showSuccessPopup"
      :title="t('contact.successTitle')"
      :message="t('contact.successMessage')"
      :confirm-text="t('common.ok')"
      :show-cancel="false"
      @confirm="closeSuccessPopup"
      @cancel="closeSuccessPopup"
    />

    <!-- Error Popup -->
    <ThePopup
      v-if="showErrorPopup"
      :title="t('contact.errorTitle')"
      :message="t('contact.errorMessage')"
      :confirm-text="t('common.ok')"
      :show-cancel="false"
      @confirm="closeErrorPopup"
      @cancel="closeErrorPopup"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import feedbackApi from '@/api/modules/feedbackApi';
import ThePopup from '@/components/common/popup/ThePopup.vue';

const { t } = useI18n();

const isSubmitting = ref(false);
const showSuccessPopup = ref(false);
const showErrorPopup = ref(false);

const formData = ref({
  name: '',
  email: '',
  subject: '',
  title: '',
  message: ''
});

const submitForm = async () => {
  try {
    isSubmitting.value = true;
    
    // Tạo payload theo format API
    const feedbackDto = {
      fullName: formData.value.name,
      email: formData.value.email,
      title: formData.value.title,
      topic: formData.value.subject,
      content: formData.value.message
    };
    
    // Gọi API gửi feedback
    const response = await feedbackApi.create(feedbackDto);
    // Reset form
    formData.value = {
      name: '',
      email: '',
      subject: '',
      title: '',
      message: ''
    };
    
    showSuccessPopup.value = true;
  } catch (error) {
    console.error('Error submitting form:', error);
    showErrorPopup.value = true;
  } finally {
    isSubmitting.value = false;
  }
};

const closeSuccessPopup = () => {
  showSuccessPopup.value = false;
};

const closeErrorPopup = () => {
  showErrorPopup.value = false;
};
</script>

<style lang="scss" scoped>
@use '@/views/footer-services/contact/TheContact.scss';
</style>