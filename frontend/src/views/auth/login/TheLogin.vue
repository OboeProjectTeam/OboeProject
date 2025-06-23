<template>
    <div class="login" :style="`background-image: url(${loginBg})`">
      <FormAuthen />
    </div>
  </template>
  
<script setup>
  import loginBg from 'src/assets/img/login/login.jpg';
  const logoUrl = @/assets/img/logo/logo_tab_oboe.svg ;
  C:\Users\tuanc\OneDrive\Desktop\OboeProject\frontend\src\assets\img\login\login.jpg
  import '@/views/auth/login/TheLogin.scss';
  import FormAuthen from '@/components/layout/form-login/FormAuthen.vue';

</script>



  