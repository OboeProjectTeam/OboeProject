import { createI18n } from 'vue-i18n'
import vi from './locales/vi.json'
import en from './locales/en.json'
import ja from './locales/ja.json'

const messages = {
  vi,
  en,
  ja
}

// Lấy ngôn ngữ từ localStorage hoặc mặc định là tiếng Việt
const savedLocale = localStorage.getItem('locale') || 'vi'

const i18n = createI18n({
  legacy: false, // Sử dụng Composition API
  locale: savedLocale,
  fallbackLocale: 'vi',
  messages,
  globalInjection: true
})

export default i18n