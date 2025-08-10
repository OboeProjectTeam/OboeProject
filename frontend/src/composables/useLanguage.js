import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
//lấy ngôn ngữ đã lưu trong localStorage (nếu có), nếu không có thì mặc định là 'vi'
const currentLocale = ref(localStorage.getItem('locale') || 'vi')

//trả về tất cả logic và dữ liệu liên quan đến đa ngôn ngữ.
export function useLanguage() {

  //biến reactive điều khiển ngôn ngữ hiện tại.
  // hàm dịch văn bản (t('key')).
  const { locale, t } = useI18n()

  const availableLanguages = [
    { code: 'vi', name: 'Tiếng Việt', flag: '🇻🇳', flagCode: 'vn' },
    { code: 'en', name: 'English', flag: '🇺🇸', flagCode: 'us' },
    { code: 'ja', name: '日本語', flag: '🇯🇵', flagCode: 'jp' }
  ]

  const currentLanguage = computed(() => {
    return availableLanguages.find(lang => lang.code === currentLocale.value) || availableLanguages[0]
  })
  // Hàm thay đổi ngôn ngữ
  const setLanguage = (langCode) => {
    currentLocale.value = langCode
    locale.value = langCode
    localStorage.setItem('locale', langCode)
    
    // Cập nhật document lang attribute
    document.documentElement.lang = langCode
  }

  //hàm lấy tên ngôn ngữ theo mã.
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