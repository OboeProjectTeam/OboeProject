<template>
    <div class="login" :style="`background-image: url(${loginBg})`">
      <FormAuthen />
    </div>
  </template>
  
<script setup>
  import loginBg from './../../assets/img/login.jpg'
  import './TheLoginCss.css'
import FormAuthen from '../../components/form-login/FormAuthen.vue';

</script>


  