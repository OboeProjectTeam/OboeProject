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
    console.log('Token length:', token ? token.length : 'null');
    console.log('Token first 50 chars:', token ? token.substring(0, 50) + '...' : 'null');
    console.log('Provider:', provider);

    if (token) {
      try {
        // Decode JWT để kiểm tra payload
        if (token.includes('.')) {
          const payload = token.split('.')[1];
          const decodedPayload = JSON.parse(atob(payload));
          console.log('JWT Payload:', decodedPayload);
        }

        localStorage.setItem('token', token);
        console.log('Token saved to localStorage');
        
        await store.dispatch('auth/fetchCurrentUser', { token, provider });
        console.log('fetchCurrentUser completed successfully');
        router.replace('/');
      } catch (e) {
        console.error('OAuth2 Redirect Error:', e);
        console.error('Error details:', e.response?.data || e.message);
        router.replace('/login');
      }
    } else {
      console.error('No token received in OAuth2 redirect');
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
