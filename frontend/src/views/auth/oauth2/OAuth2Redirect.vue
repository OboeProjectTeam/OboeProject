<template>
    <div class="redirecting">
      Đang xác thực tài khoản...
    </div>
  </template>
  
  <script setup>
  import { onMounted } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  
  onMounted(() => {
    const route = useRoute()
    const router = useRouter()
    const token = route.query.token
    const provider = route.query.provider
  
    if (token) {
      // Lưu token vào localStorage (hoặc sessionStorage tùy bạn)
      localStorage.setItem('token', token)
      localStorage.setItem('provider', provider)
  
      // Chuyển về trang chính sau khi xác thực thành công
      router.push('/')
    } else {
      // Nếu không có token => quay về login
      router.push('/login')
    }
  })
  </script>
  