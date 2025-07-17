<template>
  <div class="oauth-redirect">
    <div class="loading">
      <div class="spinner">
        <div class="bounce1"></div>
        <div class="bounce2"></div>
        <div class="bounce3"></div>
      </div>
      <p>Đang xử lý đăng nhập...</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

onMounted(async () => {
  try {
    // Get token and user data from URL params
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');
    const userData = params.get('user');
    const provider = params.get('provider');

    if (!token) {
      throw new Error('No token received');
    }

    let user = null;
    try {
      user = userData ? JSON.parse(decodeURIComponent(userData)) : null;
    } catch (e) {
      console.error('Error parsing user data:', e);
    }

    // Send data back to parent window
    if (window.opener) {
      window.opener.postMessage(
        { token, user, provider },
        window.location.origin
      );
      // Close this popup window
      window.close();
    } else {
      // If no opener, redirect to home page
      window.location.href = '/';
    }
  } catch (error) {
    console.error('OAuth callback error:', error);
    // Handle error - maybe show error message
    if (window.opener) {
      window.opener.postMessage(
        { error: error.message },
        window.location.origin
      );
      window.close();
    } else {
      window.location.href = '/login?error=auth_failed';
    }
  }
});
</script>

<style scoped>
.oauth-redirect {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f5f5f5;
}

.loading {
  text-align: center;
}

.spinner {
  margin: 20px auto;
  width: 70px;
  text-align: center;
}

.spinner > div {
  width: 18px;
  height: 18px;
  background-color: #333;
  border-radius: 100%;
  display: inline-block;
  animation: bounce 1.4s infinite ease-in-out both;
  margin: 0 2px;
}

.spinner .bounce1 {
  animation-delay: -0.32s;
}

.spinner .bounce2 {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% { 
    transform: scale(0);
  } 
  40% { 
    transform: scale(1.0);
  }
}
</style>
  