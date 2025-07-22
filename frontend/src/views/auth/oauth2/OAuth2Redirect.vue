<script setup>
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { onMounted } from 'vue'

const router = useRouter()
const store = useStore()

console.log(" Vào OAuth2Redirect.vue");

onMounted(() => {
  setTimeout(async () => {
    const hash = window.location.hash.substring(1);
    const params = new URLSearchParams(hash);

    const token = params.get('token');
    const provider = params.get('provider');

    console.log('Redirect hash:', hash);
    console.log('Token:', token);

    if (token) {
      try {
        localStorage.setItem('token', token);
        await store.dispatch('auth/fetchCurrentUser', { token, provider });
        router.replace('/');
      } catch (e) {
        console.error('OAuth2 Redirect Error:', e);
        router.replace('/login');
      }
    } else {
      router.replace('/login');
    }
  }, 100); // thử delay 100ms
});
</script>

<template>
  <div style="text-align: center; margin-top: 50px;">
    <p>Đang đăng nhập bằng OAuth2...</p>
  </div>
</template>
