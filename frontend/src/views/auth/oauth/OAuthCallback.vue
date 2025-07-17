<template>
  <div class="oauth-callback">
    <div class="loading-spinner" v-if="isLoading">
      <div class="spinner"></div>
      <p>Đang xử lý đăng nhập...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useStore } from 'vuex';
import oauthApi from '@/api/modules/oauthApi';

const router = useRouter();
const route = useRoute();
const store = useStore();
const isLoading = ref(true);

onMounted(async () => {
  try {
    // Lấy token từ query params
    const token = route.query.token;
    
    if (!token) {
      throw new Error('Không tìm thấy token xác thực');
    }

    // Gọi API xử lý OAuth success
    const response = await oauthApi.handleOAuthSuccess(token);
    
    // Lưu token và thông tin user
    store.dispatch('auth/setToken', response.token);
    store.dispatch('auth/setUser', response.user);
    
    // Chuyển hướng về trang chủ
    router.push('/');
  } catch (error) {
    console.error('OAuth callback error:', error);
    // Chuyển hướng về trang login nếu có lỗi
    router.push('/login');
  } finally {
    isLoading.value = false;
  }
});
</script>

<style lang="scss" scoped>
.oauth-callback {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;

  .loading-spinner {
    text-align: center;

    .spinner {
      width: 50px;
      height: 50px;
      border: 3px solid #f3f3f3;
      border-top: 3px solid #3498db;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin: 0 auto 20px;
    }

    p {
      color: #666;
      font-size: 16px;
    }
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style> 