<script setup>
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { onMounted } from 'vue'

const router = useRouter()
const store = useStore()



onMounted(() => {
  setTimeout(async () => {
    const hash = window.location.hash.substring(1);
    const params = new URLSearchParams(hash);

    const token = params.get('token');
    const provider = params.get('provider');






    if (token) {
      try {
        // Decode JWT để kiểm tra payload
        if (token.includes('.')) {
          const payload = token.split('.')[1];
          const decodedPayload = JSON.parse(atob(payload));

        }

        localStorage.setItem('token', token);

        
        await store.dispatch('auth/fetchCurrentUser', { token, provider });

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
