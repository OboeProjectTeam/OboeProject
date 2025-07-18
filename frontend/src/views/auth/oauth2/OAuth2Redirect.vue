<template>
    <div class="redirecting">
      Đang xác thực tài khoản...
    </div>
  </template>
  
  <script setup>
  import { onMounted } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  
  const route = useRoute()
  const router = useRouter()
  
  onMounted(() => {
    const token = route.query.token
    const provider = route.query.provider
  
    if (token) {
      if (window.opener) {
        // Gửi message với token và provider
        window.opener.postMessage({ token, provider }, window.location.origin)
        console.log('Sent token to parent window')
        
        // Đóng popup sau 1 giây
        setTimeout(() => {
          window.close()
        }, 1000)
      } else {
        // Nếu mở trong tab thường → xử lý như cũ
        localStorage.setItem('token', token)
        localStorage.setItem('provider', provider)
        router.push('/')
      }
    } else {
      // Nếu không có token → quay lại login
      router.push('/login')
    }
  })
  </script>
  