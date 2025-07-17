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
        //  Nếu được mở bằng popup: gửi dữ liệu về cửa sổ cha
        window.opener.postMessage({
          token,
          provider
        }, window.location.origin)
  
        //  Đóng popup
        window.close()
      } else {
        //  Nếu mở trong tab thường → xử lý như cũ
        localStorage.setItem('token', token)
        localStorage.setItem('provider', provider)
        router.push('/')
      }
    } else {
      router.push('/login')
    }
  })
  </script>
  