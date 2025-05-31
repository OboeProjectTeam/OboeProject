<template>
  <div class="app-container">
    <TheHeader />
    <FlashcardList v-if="!isAuthRoute" />
    <div class="router-view-wrapper">
      <router-view />
    </div>
    <TheFooter />
  </div>
</template>

<script setup>
import TheFooter from './components/layout/footer/TheFooter.vue'
import TheHeader from './components/layout/header/TheHeader.vue'
import FlashcardList from '@/components/flashcard-list/FlashcardList.vue'
import '@fortawesome/fontawesome-free/css/all.min.css'
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const isAuthRoute = computed(() => {
  return ['/login', '/register'].includes(route.path)
})
</script>

<style lang="scss">
.app-container {
  position: relative;
}

.router-view-wrapper {
  margin-top: 140px;
  padding: 20px 80px;
}
</style>
