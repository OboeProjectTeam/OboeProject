<template>
  <div class="home">
    <TheCard 
      :slides="slides" 
      :width="700" 
      :height="400" 
      :pagination="{ clickable: true }" 
      :canFlip="true" 
    />
  </div>
</template>

<script setup>
import { useStore } from 'vuex'; // Import useStore to access Vuex state
import TheCard from '../../components/card/TheCard.vue';

const store = useStore(); // Access Vuex store
const slides = store.getters['cart/slides']; // Get slides from the Vuex store
</script>

<style scoped>
@import url(./TheHomeCss.css);
</style>
