import { ref, watch } from 'vue';

const isDark = ref(localStorage.getItem('darkMode') === 'true');

watch(isDark, (val) => {
  localStorage.setItem('darkMode', val);
  document.body.classList.toggle('dark-mode', val);
}, { immediate: true });

export function useDarkMode() {
  return { isDark };
}