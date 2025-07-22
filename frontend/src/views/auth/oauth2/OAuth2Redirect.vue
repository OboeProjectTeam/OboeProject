<!-- src/views/OAuth2Redirect.vue -->
<script setup>
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { onMounted } from 'vue'

const router = useRouter()
const store = useStore()

onMounted(() => {
  const urlParams = new URLSearchParams(window.location.search)
  const token = urlParams.get('token')
  const user = urlParams.get('user')

  if (token && user) {
    try {
      const parsedUser = JSON.parse(decodeURIComponent(user))
      store.commit('auth/SET_TOKEN', token)
      store.commit('auth/SET_USER', parsedUser)
      router.push('/')
    } catch (e) {
      alert('Xử lý OAuth2 thất bại!')
    }
  } else {
    alert('Không có token hoặc user được trả về!')
    router.push('/login')
  }
})
</script>

<template>
  <div>Đang xử lý đăng nhập OAuth2...</div>
</template>
