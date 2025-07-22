<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { onMounted } from 'vue'

const router = useRouter()
const route = useRoute()
const store = useStore()

onMounted(async () => {
  const token = route.query.token

  if (token) {
    try {
      localStorage.setItem('token', token)

      await store.dispatch('auth/fetchCurrentUser', { token })

      router.replace('/')
    } catch (e) {
      console.error('OAuth2 Redirect Error:', e)
      router.replace('/login')
    }
  } else {
    router.replace('/login')
  }
})
</script>

<template>
  <div style="text-align: center; margin-top: 50px;">
    <p>Đang đăng nhập bằng OAuth2...</p>
  </div>
</template>
