<template>
    <div class="the-register" :style="`background-image: url(${loginBg})`" >
      <FormAuthen :isRegister="true"/>
    </div>
  </template>
  
<script setup>
  import loginBg from '@/assets/img/login.jpg'
  import '@/views/auth/register/TheRegister.scss';
  import FormAuthen from '@/components/layout/form-login/FormAuthen.vue';
</script>


  