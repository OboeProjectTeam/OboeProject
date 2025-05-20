<template>
    <div class="the-register" :style="`background-image: url(${loginBg})`" >
      <FormAuthen :isRegister="true"/>
    </div>
  </template>
  
<script setup>
  import loginBg from './../../assets/img/login.jpg'
  import './TheRegisterCss.css'
import FormAuthen from '../../components/form-login/FormAuthen.vue';

</script>


  