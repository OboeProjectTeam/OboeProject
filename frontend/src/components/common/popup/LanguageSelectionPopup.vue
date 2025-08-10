<template>
  <div class="language-popup-backdrop" @click.self="onSkip">
    <div class="language-popup">
      <div class="popup-header">
        <div class="popup-title">{{ $t('languageSelection.title') }}</div>
        <div class="popup-subtitle">{{ $t('languageSelection.subtitle') }}</div>
      </div>
      
      <div class="language-options">
        <div 
          v-for="language in languages" 
          :key="language.code"
          class="language-option"
          :class="{ 'selected': selectedLanguage === language.code }"
          @click="selectLanguage(language.code)"
        >
          <div class="language-flag">
            <span :class="`fi fi-${language.flagCode}`" class="flag-icon"></span>
          </div>
          <div class="language-info">
            <div class="language-name">{{ language.name }}</div>
            <div class="language-native">{{ language.nativeName }}</div>
          </div>
          <div class="language-check" v-if="selectedLanguage === language.code">
            <i class="fas fa-check"></i>
          </div>
        </div>
      </div>
      
      <div class="popup-actions">
        <button class="btn btn-secondary" @click="onSkip">
          {{ $t('languageSelection.skip') }}
        </button>
        <button 
          class="btn btn-primary" 
          @click="onConfirm"
          :disabled="!selectedLanguage"
        >
          {{ $t('languageSelection.confirm') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { locale } = useI18n()

const emit = defineEmits(['language-selected', 'skip'])

const selectedLanguage = ref(locale.value)

const languages = [
  {
    code: 'vi',
    name: 'Tiếng Việt',
    nativeName: 'Vietnamese',
    flagCode: 'vn' // Mã cờ Việt Nam cho flag-icons
  },
  {
    code: 'en',
    name: 'English',
    nativeName: 'English',
    flagCode: 'us' // Mã cờ Mỹ cho flag-icons
  },
  {
    code: 'ja',
    name: '日本語',
    nativeName: 'Japanese',
    flagCode: 'jp' // Mã cờ Nhật Bản cho flag-icons
  }
]

const selectLanguage = (code) => {
  selectedLanguage.value = code
}

const onConfirm = () => {
  emit('language-selected', selectedLanguage.value)
}

const onSkip = () => {
  emit('skip')
}
</script>

<style lang="scss" scoped>
@use '@/assets/css/index.scss';

.language-popup-backdrop {
  position: fixed;
  z-index: 3000;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
}

.language-popup {
  background: #fff;
  border-radius: 16px;
  min-width: 400px;
  max-width: 90vw;
  width: 500px;
  padding: 32px 24px 24px 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  gap: 24px;
  animation: popupSlideIn 0.3s ease-out;
}

@keyframes popupSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.popup-header {
  text-align: center;
  
  .popup-title {
    font-size: 1.5rem;
    font-weight: 700;
    color: #1a1a1a;
    margin-bottom: 8px;
  }
  
  .popup-subtitle {
    font-size: 1rem;
    color: #666;
    line-height: 1.5;
  }
}

.language-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.language-option {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    border-color: #dee2e6;
    background: #f8f9fa;
  }
  
  &.selected {
    border-color: $primary-color;
    background: rgba($primary-color, 0.05);
    
    .language-flag {
      background: rgba($primary-color, 0.1);
      border-color: $primary-color;
      transform: scale(1.05);
    }
  }
}

.language-flag {
  margin-right: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 36px;
  border-radius: 6px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  transition: all 0.2s ease;
  overflow: hidden;
  
  .flag-icon {
    width: 100%;
    height: 100%;
    border-radius: 4px;
    object-fit: cover;
  }
}

.language-info {
  flex: 1;
  
  .language-name {
    font-size: 1.1rem;
    font-weight: 600;
    color: #1a1a1a;
    margin-bottom: 2px;
  }
  
  .language-native {
    font-size: 0.9rem;
    color: #666;
  }
}

.language-check {
  color: $primary-color;
  font-size: 1.2rem;
  margin-left: 12px;
}

.popup-actions {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 8px;
}

.btn {
  padding: 12px 24px;
  border-radius: 8px;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s ease;
  flex: 1;
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.btn-primary {
  background: $primary-color;
  color: #fff;
  
  &:hover:not(:disabled) {
    filter: brightness(0.9);
    transform: translateY(-1px);
  }
}

.btn-secondary {
  background: #f1f3f5;
  color: #495057;
  
  &:hover {
    background: #e9ecef;
  }
}

@media (max-width: 768px) {
  .language-popup {
    min-width: 320px;
    width: 90vw;
    padding: 24px 20px 20px 20px;
  }
  
  .language-option {
    padding: 12px;
  }
  
  .language-flag {
    font-size: 2rem;
    margin-right: 12px;
    width: 40px;
    height: 30px;
  }
  
  .popup-actions {
    flex-direction: column;
  }
}
</style>