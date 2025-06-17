<template>
  <!-- Admin Layout -->
  <router-view v-if="isAdminRoute" />

  <!-- Main App Layout -->
  <div v-else class="app-container">
    <TheHeader />
    <OboeProButton v-if="!isAuthRoute" @click="goToUpgrade" />
    <FlashcardList v-if="!isAuthRoute" />
    <div class="router-view-wrapper">
      <router-view @send-message="openChatBox" />
    </div>
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
import { auth } from '@/firebase'
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
  // Listen for auth state changes
  auth.onAuthStateChanged((user) => {
    if (user) {
      const userData = {
        displayName: user.displayName,
        email: user.email,
        photoURL: user.photoURL,
        uid: user.uid
      }
      store.dispatch('auth/setUser', userData)
    } else {
      store.dispatch('auth/setUser', null)
    }
  })

  // Add event listener for send-message events
  router.afterEach((to) => {
    to.meta.emit = (event, ...args) => {
      if (event === 'send-message') {
        openChatBox(...args);
      }
    };
  });
})

function openChatBox(user) {
  chatBoxUser.value = user
  chatBoxVisible.value = true
}

function closeChatBox() {
  chatBoxVisible.value = false
  chatBoxUser.value = null
}
</script>

<style lang="scss">
.app-container {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.router-view-wrapper {
  margin-top: 140px;
  padding: 20px 165px;
  flex: 1;
}

/* Ensure chat box appears above other elements */
.chatbox {
  z-index: 9999;
}
</style>
