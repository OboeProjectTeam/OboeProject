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
  </div>
</template>

<script setup>
import TheFooter from '@/components/layout/footer/TheFooter.vue'
import TheHeader from '@/components/layout/header/TheHeader.vue'
import FlashcardList from '@/components/layout/flashcard-list/FlashcardList.vue'
import OboeProButton from '@/components/layout/pro-button/OboeProButton.vue'
import '@fortawesome/fontawesome-free/css/all.min.css'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import ChatBox from '@/components/layout/chat-box/ChatBox.vue'

const route = useRoute()
const store = useStore()
const router = useRouter()

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
onMounted(() => {
  router.afterEach((to) => {
    to.meta.emit = (event, ...args) => {
      if (event === 'send-message') {
        openChatBox(...args);
      }
    };
  });
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

  // @media (min-width: $breakpoint-xl) {
  //   padding: 20px 40px;
  // }

  // @media (max-width: $breakpoint-lg) {
  //   margin-top: 160px;
  //   padding: 20px 30px;
  // }

  // @media (max-width: $breakpoint-md) {
  //   margin-top: 160px;
  //   padding: 15px 20px;
  // }

  // @media (max-width: $breakpoint-sm) {
  //   margin-top: 160px;
  //   padding: 15px;
  // }
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