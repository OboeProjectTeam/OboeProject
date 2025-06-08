<template>
  <div class="app-container">
    <TheHeader />
    <OboeProButton v-if="!isAuthRoute" @click="goToUpgrade" />
    <FlashcardList v-if="!isAuthRoute" />
    <div class="router-view-wrapper">
      <router-view />
    </div>
    <TheFooter />
  </div>
</template>

<script setup>
import TheFooter from '@/components/layout/footer/TheFooter.vue'
import TheHeader from '@/components/layout/header/TheHeader.vue'
import FlashcardList from '@/components/layout/flashcard-list/FlashcardList.vue'
import OboeProButton from '@/components/layout/pro-button/OboeProButton.vue'
import '@fortawesome/fontawesome-free/css/all.min.css'
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { auth } from '@/firebase'

const route = useRoute()
const store = useStore()
const router = useRouter()

const isAuthRoute = computed(() => {
  return ['/login', '/register','/intro'].includes(route.path)
})

const goToUpgrade = () => {
  router.push('/upgrade');
};

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
})
</script>

<style lang="scss">
.app-container {
  position: relative;
}

.router-view-wrapper {
  margin-top: 140px;
  padding: 20px 165px;
}
</style>
