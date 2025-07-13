<template>
    <div class="login" :style="`background-image: url(${ImagePaths.login.loginBackground})`">
      <FormAuthen />
    </div>
  </template>
  
<script setup>

  import { ImagePaths } from '@/assets/img/imagePaths';
  
  import '@/views/auth/login/TheLogin.scss';
  import FormAuthen from '@/components/layout/form-login/FormAuthen.vue';
</script>



  