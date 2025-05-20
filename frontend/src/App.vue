<template>
  <div class="app-container">
    <TheHeader />
    <div class="router-view-wrapper">
      <router-view />
    </div>
    <TheFooter />
  </div>
</template>

<script setup>
import TheFooter from './components/layout/footer/TheFooter.vue';
import TheHeader from './components/layout/header/TheHeader.vue'
import '@fortawesome/fontawesome-free/css/all.min.css'
</script>

<style lang="scss">

.app-container {
  position: relative;
}

.router-view-wrapper {
  margin-top: 140px; 
  padding:20px 80px;
}
</style>
