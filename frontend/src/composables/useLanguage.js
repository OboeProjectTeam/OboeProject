import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const currentLocale = ref(localStorage.getItem('locale') || 'vi')

export function useLanguage() {
  const { locale, t } = useI18n()

  const availableLanguages = [
    { code: 'vi', name: 'Tiếng Việt', flag: '🇻🇳' },
    { code: 'en', name: 'English', flag: '🇺🇸' },
    { code: 'ja', name: '日本語', flag: '🇯🇵' }
  ]

  const currentLanguage = computed(() => {
    return availableLanguages.find(lang => lang.code === currentLocale.value) || availableLanguages[0]
  })

  const setLanguage = (langCode) => {
    currentLocale.value = langCode
    locale.value = langCode
    localStorage.setItem('locale', langCode)
    
    // Cập nhật document lang attribute
    document.documentElement.lang = langCode
  }

  const getLanguageName = (code) => {
    const lang = availableLanguages.find(lang => lang.code === code)
    return lang ? lang.name : code
  }

  return {
    currentLocale,
    currentLanguage,
    availableLanguages,
    setLanguage,
    getLanguageName,
    t
  }
}