<template>
  <div class="redirecting">
    Đang xác thực tài khoản...
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'

const route = useRoute()
const router = useRouter()
const store = useStore()

onMounted(async () => {
  try {
    const token = route.query.token
    const provider = route.query.provider

    if (!token) {
      console.error('No token received')
      router.push('/login?error=no_token')
      return
    }

    console.log('Received token from OAuth2:', { provider })

    // Store token in localStorage first
    localStorage.setItem('token', token)
    
    // Then update Vuex store and fetch user info
    await store.dispatch('auth/setToken', token)
    
    // Wait a bit to ensure all state updates are complete
    setTimeout(() => {
      console.log('Redirecting to home page...')
      window.location.href = '/'
    }, 1000)
  } catch (error) {
    console.error('Error in OAuth redirect:', error)
    router.push('/login?error=auth_failed')
  }
})
</script>

<style scoped>
.redirecting {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-size: 1.2rem;
  color: #666;
}
</style>
  