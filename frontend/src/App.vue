<template>
  <!-- Admin Layout -->
  <router-view v-if="isAdminRoute" />

  <!-- Main App Layout -->
  <div v-else class="app-container">
    <TheHeader />
    <OboeProButton v-if="!isAuthRoute" @click="goToUpgrade" class="oboe-pro-button" />
    <FlashcardList v-if="!isAuthRoute" />
    <main class="main-content">
      <router-view @send-message="openChatBox" />
    </main>
    <TheFooter />
    <ChatBox
      v-if="chatBoxVisible"
      :user="chatBoxUser"
      :visible="chatBoxVisible"
      @close="closeChatBox"
    />
    
    <!-- Language Selection Popup for first-time users -->
    <LanguageSelectionPopup
      v-if="showLanguagePopup"
      @language-selected="handleLanguageSelected"
      @skip="handleSkipLanguageSelection"
    />
  </div>
</template>

<script setup>
import TheFooter from '@/components/layout/footer/TheFooter.vue'
import TheHeader from '@/components/layout/header/TheHeader.vue'
import FlashcardList from '@/components/layout/flashcard-list/FlashcardList.vue'
import OboeProButton from '@/components/layout/pro-button/OboeProButton.vue'
import '@fortawesome/fontawesome-free/css/all.min.css'
import { computed, onMounted, ref, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import ChatBox from '@/components/layout/chat-box/ChatBox.vue'
import LanguageSelectionPopup from '@/components/common/popup/LanguageSelectionPopup.vue'
import { useLanguage } from '@/composables/useLanguage'

const route = useRoute()
const store = useStore()
const router = useRouter()
const { setLanguage } = useLanguage()

const isAuthRoute = computed(() => {
  return ['/login', '/register','/intro'].includes(route.path)
})

const isAdminRoute = computed(() => {
  return route.path.startsWith('/admin')
})

const goToUpgrade = () => {
  router.push('/upgrade');
};

const chatBoxUser = ref(null)
const chatBoxVisible = ref(false)

// Language popup state
const showLanguagePopup = ref(false)

// Watch for first login state
const isFirstLogin = computed(() => store.getters['auth/isFirstLogin'])
const isAuthenticated = computed(() => store.getters['auth/isAuthenticated'])

// Function to check and show language popup
const checkAndShowLanguagePopup = async () => {
  await nextTick()
  
  // Thêm delay nhỏ để đảm bảo tất cả state đã được cập nhật
  setTimeout(() => {
    const hasSeenLanguagePopup = localStorage.getItem('hasSeenLanguagePopup') === 'true'
    
    console.log('Checking language popup:', {
      isAuthenticated: isAuthenticated.value,
      isFirstLogin: isFirstLogin.value,
      isAuthRoute: isAuthRoute.value,
      isAdminRoute: isAdminRoute.value,
      currentRoute: route.path,
      showLanguagePopup: showLanguagePopup.value,
      hasSeenLanguagePopup
    })
    
    // Chỉ hiển thị popup nếu:
    // 1. Đã đăng nhập
    // 2. Là lần đầu đăng nhập
    // 3. Không phải trang auth hoặc admin
    // 4. Chưa từng xem popup ngôn ngữ
    // 5. Popup chưa được hiển thị
    if (isAuthenticated.value && 
        isFirstLogin.value && 
        !isAuthRoute.value && 
        !isAdminRoute.value && 
        !hasSeenLanguagePopup &&
        !showLanguagePopup.value) {
      console.log('Showing language popup')
      showLanguagePopup.value = true
    }
  }, 100)
}

watch([isFirstLogin, isAuthenticated], async ([firstLogin, authenticated]) => {
  if (authenticated && firstLogin && !isAuthRoute.value && !isAdminRoute.value) {
    await nextTick()
    showLanguagePopup.value = true
  }
}, { immediate: true })

// Watch route changes to check popup
watch(route, async () => {
  await checkAndShowLanguagePopup()
})

// Watch specifically for authentication changes
watch(isAuthenticated, (newVal) => {
  if (newVal) {
    // Delay để đảm bảo route đã được cập nhật
    setTimeout(() => {
      checkAndShowLanguagePopup()
    }, 200)
  }
})

onMounted(async () => {
  router.afterEach((to) => {
    to.meta.emit = (event, ...args) => {
      if (event === 'send-message') {
        openChatBox(...args);
      }
    };
  });
  
  // Kiểm tra và hiển thị popup ngôn ngữ khi component được mount
  await checkAndShowLanguagePopup()
})

function openChatBox(user) {
  // Handle different user data formats
  const chatUser = {
    id: user.user_id || user.userId || user.id,
    userId: user.user_id || user.userId || user.id,
    name: user.fullName || user.name || user.userName,
    username: user.userName || user.username,
    fullName: user.fullName || user.name,
    avatar: user.avatarUrl || user.avatar || user.avatarUrlReceiver
  }
  chatBoxUser.value = chatUser
  chatBoxVisible.value = true
}

function closeChatBox() {
  chatBoxVisible.value = false
  chatBoxUser.value = null
}

// Language popup handlers
function handleLanguageSelected(language) {
  console.log('Language selected:', language)
  // Set the selected language
  setLanguage(language)
  // Hide popup and reset first login state
  showLanguagePopup.value = false
  store.commit('auth/SET_FIRST_LOGIN', false)
  // Lưu trạng thái đã xem popup ngôn ngữ
  localStorage.setItem('hasSeenLanguagePopup', 'true')
}

function handleSkipLanguageSelection() {
  console.log('Language selection skipped')
  // Just hide popup and reset first login state
  showLanguagePopup.value = false
  store.commit('auth/SET_FIRST_LOGIN', false)
  // Lưu trạng thái đã xem popup ngôn ngữ
  localStorage.setItem('hasSeenLanguagePopup', 'true')
}
</script>

<style lang="scss">
@use '@/assets/css/index.scss' as *;

// Breakpoints
$breakpoint-sm: 576px;
$breakpoint-md: 768px;
$breakpoint-lg: 992px;
$breakpoint-xl: 1200px;

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  min-height: 100vh;
  overflow-x: hidden;
}

.app-container {
  position: relative;
  min-height: 100vh;
  width: 100%;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
}

.main-content {
  margin-top: 130px;
  padding: 20px;
  flex: 1;
  width: 100%;
  min-height: 400px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;

  @media (min-width: $breakpoint-xl) {
    padding: 20px 40px;
  }

  @media (max-width: $breakpoint-lg) {
    margin-top: 160px;
    padding: 20px 30px;
  }

  @media (max-width: $breakpoint-md) {
    margin-top: 160px;
    padding: 15px 20px;
  }

  @media (max-width: $breakpoint-sm) {
    margin-top: 160px;
    padding: 15px;
  }
}

/* Ensure chat box appears above other elements */
.chatbox {
  z-index: 9999;
}

/* Global styles for better mobile experience */
img {
  max-width: 100%;
  height: auto;
}

button {
  touch-action: manipulation;
}

/* Hide scrollbar but allow scrolling */
.hide-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
  
  &::-webkit-scrollbar {
    display: none;
  }
}

/* Prevent text size adjustment on orientation change */
html {
  -webkit-text-size-adjust: 100%;
}

.oboe-pro-button {
  @media (max-width: 768px) {
    display: none !important;
  }
}

</style>