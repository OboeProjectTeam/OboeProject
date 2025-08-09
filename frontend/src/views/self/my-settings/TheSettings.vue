<template>
  <div class="settings-page-container">
    <div class="settings-sidebar">
      <h2 class="sidebar-title">{{ t('settings.title') }}</h2>
      <ul class="nav-menu">
        <li>
          <a href="#account" 
             class="nav-link" 
             :class="{ active: currentSection === 'account' }"
             @click="setCurrentSection('account', $event)">
            <i class="fas fa-rocket icon"></i>{{ t('settings.upgrade') }}
          </a>
        </li>
        <li>
          <a href="#appearance" 
             class="nav-link" 
             :class="{ active: currentSection === 'appearance' }"
             @click="setCurrentSection('appearance', $event)">
            <i class="fas fa-paint-brush icon"></i>{{ t('settings.appearance') }}
          </a>
        </li>
        <li>
          <a href="#privacy" 
             class="nav-link" 
             :class="{ active: currentSection === 'privacy' }"
             @click="setCurrentSection('privacy', $event)">
            <i class="fas fa-shield-alt icon"></i>{{ t('settings.privacy') }}
          </a>
        </li>
      </ul>
    </div>

    <div class="settings-content">
      <!-- Account Upgrade Section -->
      <div id="account" class="settings-card">
        <h3 class="card-title">{{ t('settings.accountUpgrade') }}</h3>
        <div class="upgrade-promo">
          <div class="promo-icon">
            <i class="fas fa-star"></i>
          </div>
          <div class="promo-text">
            <h4>{{ t('settings.unlockFeatures') }}</h4>
          </div>
          <button class="btn btn-upgrade" @click="goToUpgrade">{{ t('settings.upgradeNow') }}</button>
        </div>
      </div>

      <!-- Appearance Section -->
      <div id="appearance" class="settings-card">
        <h3 class="card-title">{{ t('settings.appearance') }}</h3>
        <!-- Dark Mode -->
        <div class="setting-item">
          <div class="item-info">
            <label>{{ t('settings.darkMode') }}</label>
            <p class="item-description">{{ t('settings.darkModeDesc') }}</p>
          </div>
          <div class="item-control">
            <label class="switch">
              <input type="checkbox" v-model="isDark">
              <span class="slider round"></span>
            </label>
          </div>
        </div>
        <!-- Language -->
        <div class="setting-item">
          <div class="item-info">
            <label>{{ t('settings.language') }}</label>
            <p class="item-description">{{ t('settings.languageDesc') }}</p>
          </div>
          <div class="item-control">
            <div class="language-select-wrapper">
              <i class="fas fa-globe select-icon"></i>
              <select class="language-select" v-model="selectedLanguage" @change="handleLanguageChange">
                <option v-for="lang in availableLanguages" :key="lang.code" :value="lang.code">
                  {{ lang.flag }} {{ t(`languages.${lang.code}`) }}
                </option>
              </select>
            </div>
          </div>
        </div>
      </div>

      <!-- Privacy Section -->
      <div id="privacy" class="settings-card">
        <h3 class="card-title">{{ t('settings.privacy') }}</h3>
        <!-- Change Password -->
        <div class="setting-item-divider"></div>
        <div class="setting-item">
          <div class="item-info">
            <label>{{ t('settings.changePassword') }}</label>
            <p class="item-description" v-if="!isGoogleUser">{{ t('settings.changePasswordDesc') }}</p>
            <p class="item-description" v-else>{{ t('settings.changePasswordGoogleDesc') }}</p>
          </div>
          <div class="item-control">
            <button v-if="!isChangingPassword && !isGoogleUser" class="btn btn-secondary" @click="isChangingPassword = true">{{ t('settings.changePassword') }}</button>
            <button v-else-if="isGoogleUser" class="btn btn-secondary" disabled>{{ t('settings.changePassword') }}</button>
            <div v-else class="password-fields">
              <input v-model="oldPassword" type="password" :placeholder="t('settings.oldPassword')" class="password-input">
              <input v-model="newPassword" type="password" :placeholder="t('settings.newPassword')" class="password-input">
              <input v-model="confirmPassword" type="password" :placeholder="t('settings.confirmPassword')" class="password-input">
              <div class="password-buttons">
                <button class="btn btn-secondary" @click="cancelPasswordChange">{{ t('common.cancel') }}</button>
                <button class="btn btn-primary" @click="handleChangePassword" :disabled="isLoading">
                  {{ isLoading ? t('settings.processing') : t('settings.saveChanges') }}
                </button>
              </div>
              <div v-if="passwordError" class="error-message">{{ passwordError }}</div>
              <div v-if="passwordSuccess" class="success-message">{{ passwordSuccess }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { useDarkMode } from '@/composables/useDarkMode';
import { useLanguage } from '@/composables/useLanguage';
import api from '@/api';

const router = useRouter();
const store = useStore();
const { isDark } = useDarkMode();
const { t, availableLanguages, currentLocale, setLanguage } = useLanguage();
const isChangingPassword = ref(false);
const currentSection = ref('account');

// Kiểm tra user có đăng nhập bằng Google không
const isGoogleUser = computed(() => store.getters['auth/isGoogleUser']);

// Language management
const selectedLanguage = ref(currentLocale.value);

const handleLanguageChange = () => {
  setLanguage(selectedLanguage.value);
};

const oldPassword = ref('');
const newPassword = ref('');
const confirmPassword = ref('');
const isLoading = ref(false);
const passwordError = ref('');
const passwordSuccess = ref('');

const setCurrentSection = (section, event) => {
  if (event) {
    event.preventDefault(); // Prevent default hash navigation
  }
  currentSection.value = section;
  
  // Find the target element and content container
  const targetElement = document.getElementById(section);
  const contentContainer = document.querySelector('.settings-content');
  
  if (targetElement && contentContainer) {
    // Get the relative position of the target element within the content container
    const containerRect = contentContainer.getBoundingClientRect();
    const targetRect = targetElement.getBoundingClientRect();
    const scrollOffset = targetRect.top - containerRect.top + contentContainer.scrollTop - 20; // 20px offset from top

    // Smooth scroll the content container
    contentContainer.scrollTo({
      top: scrollOffset,
      behavior: "smooth"
    });
  }

  // Update URL hash without triggering scroll
  window.history.pushState(null, '', `#${section}`);
};

// Handle hash changes in URL
const handleHashChange = () => {
  const hash = window.location.hash.slice(1);
  if (hash) {
    currentSection.value = hash;
    setCurrentSection(hash);
  }
};

onMounted(() => {
  // Set initial section from URL hash if exists
  if (window.location.hash) {
    const hash = window.location.hash.slice(1);
    currentSection.value = hash;
    // Small delay to ensure elements are rendered
    setTimeout(() => setCurrentSection(hash), 100);
  }
  window.addEventListener('hashchange', handleHashChange);
});

onUnmounted(() => {
  window.removeEventListener('hashchange', handleHashChange);
});

const goToUpgrade = () => {
  router.push('/upgrade');
};

const cancelPasswordChange = () => {
  isChangingPassword.value = false;
  oldPassword.value = '';
  newPassword.value = '';
  confirmPassword.value = '';
  passwordError.value = '';
  passwordSuccess.value = '';
};

const validatePasswords = () => {
  if (!oldPassword.value.trim()) {
    passwordError.value = t('passwordErrors.oldPasswordRequired');
    return false;
  }
  
  if (!newPassword.value.trim()) {
    passwordError.value = t('passwordErrors.newPasswordRequired');
    return false;
  }
  
  if (newPassword.value.length < 6) {
    passwordError.value = t('passwordErrors.passwordTooShort');
    return false;
  }
  
  if (newPassword.value !== confirmPassword.value) {
    passwordError.value = t('passwordErrors.passwordMismatch');
    return false;
  }
  
  if (oldPassword.value === newPassword.value) {
    passwordError.value = t('passwordErrors.samePassword');
    return false;
  }
  
  return true;
};

const handleChangePassword = async () => {
  passwordError.value = '';
  passwordSuccess.value = '';
  
  if (!validatePasswords()) {
    return;
  }
  
  isLoading.value = true;
  
  try {
    const passwordData = {
      oldPassword: oldPassword.value,
      newPassword: newPassword.value
    };
    
    await api.auth.changePassword(passwordData);
    
    passwordSuccess.value = t('passwordErrors.changePasswordSuccess');
    
    // Reset form sau 2 giây
    setTimeout(() => {
      cancelPasswordChange();
    }, 2000);
    
  } catch (error) {
    passwordError.value = error.message || t('passwordErrors.changePasswordError');
  } finally {
    isLoading.value = false;
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/self/my-settings/TheSettings.scss';
</style>