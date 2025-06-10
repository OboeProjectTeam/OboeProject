<template>
    <div class="login" :style="`background-image: url(${loginBg})`">
      <FormAuthen />
    </div>
  </template>
  
<script setup>
  import loginBg from '@/assets/img/login.jpg'
  import '@/views/auth/login/TheLogin.scss';
  import FormAuthen from '@/components/layout/form-login/FormAuthen.vue';

</script>



  