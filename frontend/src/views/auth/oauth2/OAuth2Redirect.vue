<script setup>
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { onMounted, ref } from 'vue'

const router = useRouter()
const store = useStore()

// Debug variables
const isDev = ref(import.meta.env.DEV)
const currentUrl = ref(window.location.href)
const urlHash = ref(window.location.hash)
const urlSearch = ref(window.location.search)
const documentCookies = ref(document.cookie)



onMounted(() => {
  setTimeout(async () => {
    console.log('OAuth2Redirect: Starting token extraction...');
    
    // Cập nhật debug info
    currentUrl.value = window.location.href;
    urlHash.value = window.location.hash;
    documentCookies.value = document.cookie;
    
    let token = null;
    let provider = null;
    
    // Cách 1: Thử lấy token từ URL hash (cho trường hợp redirect với token trong URL)
    const hash = window.location.hash.substring(1);
    if (hash) {
      const hashParams = new URLSearchParams(hash);
      token = hashParams.get('token');
      provider = hashParams.get('provider');
      console.log('Token from URL hash:', token);
      console.log('Provider from URL hash:', provider);
    }
    
    // Cách 2: Lấy provider từ URL query params và token từ cookie
    if (!token) {
      console.log('No token in URL hash, checking query params and cookies...');
      
      // Lấy provider từ query params
      const urlParams = new URLSearchParams(window.location.search);
      provider = urlParams.get('provider');
      console.log('Provider from query params:', provider);
      
      // Function để lấy cookie
      function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
        return null;
      }
      
      token = getCookie('JWT_TOKEN');
      console.log('Token from cookie:', token);
      
      // Nếu có token từ cookie nhưng không có provider từ query, thử extract từ JWT
      if (token && !provider && token.includes('.')) {
        try {
          const payload = token.split('.')[1];
          const decodedPayload = JSON.parse(atob(payload));
          provider = decodedPayload.provider;
          console.log('Provider extracted from JWT payload:', provider);
        } catch (e) {
          console.error('Error decoding JWT:', e);
        }
      }
    }

    console.log('Final token:', token ? 'Found' : 'Not found');
    console.log('Final provider:', provider);

    if (token) {
      try {
        console.log('Processing token...');
        
        // Decode JWT để kiểm tra payload
        if (token.includes('.')) {
          const payload = token.split('.')[1];
          const decodedPayload = JSON.parse(atob(payload));
          console.log('JWT payload:', decodedPayload);
        }

        // Lưu token vào localStorage
        localStorage.setItem('token', token);
        if (provider) {
          localStorage.setItem('provider', provider);
        }

        console.log('Token saved to localStorage, fetching user profile...');
        
        // Fetch user profile
        await store.dispatch('auth/fetchCurrentUser', { token, provider });

        console.log('User profile fetched successfully, redirecting to home...');
        router.replace('/');
      } catch (e) {
        console.error('OAuth2 Redirect Error:', e);
        console.error('Error details:', e.response?.data || e.message);
        
        // Clear any stored tokens on error
        localStorage.removeItem('token');
        localStorage.removeItem('provider');
        
        router.replace('/login?error=oauth_failed');
      }
    } else {
      console.error('No token received in OAuth2 redirect (neither URL nor cookie)');
      router.replace('/login?error=no_token');
    }
  }, 100); // delay 100ms để đảm bảo DOM đã load
});
</script>

<template>
  <div style="text-align: center; margin-top: 50px; padding: 20px;">
    <h2>🔄 Đang xử lý đăng nhập OAuth2...</h2>
    <p>Vui lòng đợi trong giây lát...</p>
    
    <!-- Debug info (chỉ hiển thị trong development) -->
     <div v-if="isDev" style="margin-top: 30px; padding: 15px; background: #f5f5f5; border-radius: 8px; text-align: left; max-width: 600px; margin: 30px auto;">
       <h4>🔍 Debug Information:</h4>
       <p><strong>Current URL:</strong> {{ currentUrl }}</p>
       <p><strong>URL Hash:</strong> {{ urlHash || '(empty)' }}</p>
       <p><strong>URL Query:</strong> {{ urlSearch || '(empty)' }}</p>
       <p><strong>Cookies:</strong> {{ documentCookies || '(empty)' }}</p>
     </div>
  </div>
</template>
