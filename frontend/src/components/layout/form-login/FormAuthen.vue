<template>
  <form class="form__login" :style="{ width: formWidth }">
    <div class="form__cover"></div>
    <div class="form__loader">
      <div class="spinner active">
        <svg class="spinner__circular" viewBox="25 25 50 50">
          <circle class="spinner__path" cx="50" cy="50" r="20" fill="none" stroke-width="4" stroke-miterlimit="10" />
        </svg>
      </div>
    </div>

    <div class="form__content">
      <h1>{{ isRegister ? 'Đăng Ký' : 'Đăng Nhập' }}</h1>

      <div class="styled-input" :style="{ 'margin-top': isRegister ? '10px' : '0px' }">
        <input v-model="username" type="text" class="styled-input__input" />
        <div class="styled-input__placeholder">
          <span class="styled-input__placeholder-text">Email / Số Điện Thoại</span>
        </div>
        <div class="styled-input__circle"></div>
      </div>

      <div class="styled-input" :style="{ 'margin-top': isRegister ? '20px' : '0px' }">
        <input v-model="password" type="password" class="styled-input__input" :disabled="isLoading" />
        <div class="styled-input__placeholder">
          <span class="styled-input__placeholder-text">Mật Khẩu</span>
        </div>
        <div class="styled-input__circle"></div>
      </div>

      <div class="flex-jsb grap-20">
        <div v-if="isRegister" class="styled-input" :style="{ 'margin-top': '20px', 'margin-bottom': '20px' }">
          <input v-model="lastname" type="text" class="styled-input__input" :disabled="isLoading" />
          <div class="styled-input__placeholder">
            <span class="styled-input__placeholder-text">Họ</span>
          </div>
          <div class="styled-input__circle"></div>
        </div>

        <div v-if="isRegister" class="styled-input" :style="{ 'margin-top': '20px', 'margin-bottom': '20px' }">
          <input v-model="firstname" type="text" class="styled-input__input" :disabled="isLoading" />
          <div class="styled-input__placeholder">
            <span class="styled-input__placeholder-text">Tên</span>
          </div>
          <div class="styled-input__circle"></div>
        </div>
      </div>

      <div v-if="isRegister">
        <MCheckbox v-model="remember" :disabled="isLoading">
          <span style="color: #888888;font-size: 10px; width: 100%;">
            Tôi chấp nhận
            <router-link to="/dieu-khoan-dich-vu" target="_blank">Điều khoản dịch vụ</router-link>
            và
            <router-link to="/quyen-rieng-tu" target="_blank">Chính sách quyền riêng tư</router-link>
            của Oboe
          </span>
        </MCheckbox>
      </div>

      <button type="button" class="styled-button" @click="submitForm" :disabled="isLoading">
        <span class="styled-button__real-text-holder">
          <span class="styled-button__real-text">{{ isRegister ? 'Đăng ký' : 'Đăng nhập' }}</span>
          <span class="styled-button__moving-block face">
            <span class="styled-button__text-holder">{{ isRegister ? 'Đăng ký' : 'Đăng nhập' }}</span>
          </span>
          <span class="styled-button__moving-block back">
            <span class="styled-button__text-holder">{{ isRegister ? 'Đăng ký' : 'Đăng nhập' }}</span>
          </span>
        </span>
      </button>

      <div v-if="!isRegister" class="divider">
        <span class="divider-text">Hoặc</span>
      </div>

      <div v-if="!isRegister" class="social-login">
        <button type="button" class="social-button google" @click="handleGoogleLogin">
          <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" alt="Google" />
          <span>Đăng nhập với Google</span>
        </button>
        <button type="button" class="social-button facebook" @click="handleFacebookLogin">
          <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/facebook.svg" alt="Facebook" />
          <span>Đăng nhập với Facebook</span>
        </button>
      </div>
    </div>
  </form>
  <!-- Popup thông báo -->
  <ConfirmDialog
      v-if="showPopup"
      :title="popupTitle"
      :message="popupMessage"
      confirmText="OK"
      @confirm="handlePopupConfirm"
      :showCancel="false" 
    />
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import ConfirmDialog from '@/components/common/popup/ThePopup.vue'
import '@/components/layout/form-login/FormAuthen.scss'
import MCheckbox from '@/components/common/checkbox/MCheckbox.vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import api from '@/api'

const props = defineProps({
  isRegister: { type: Boolean, default: false },
  formWidth: { type: String, default: '400px' }
})

const router = useRouter()
const store = useStore()

const username = ref('')
const password = ref('')
const remember = ref(false)
const lastname = ref('')
const firstname = ref('')
const isLoading = ref(false)

// popup dialog
const showPopup = ref(false)
const popupMessage = ref('')
const popupTitle = ref('Thông báo')
const success = ref (false)

const showDialog = (message, type = 'success') => {
  popupTitle.value = type === 'success' ? '🎉 Thành công' : '❗ Thất bại'
  popupMessage.value = message
  showPopup.value = true
}

const handleGoogleLogin = () => {
  window.location.href = api.oauth.getGoogleAuthUrl()
}

const handleFacebookLogin = () => {
  window.location.href = api.oauth.getFacebookAuthUrl()
}
const handlePopupConfirm = () => {
  showPopup.value = false
  if (success.value && props.isRegister) {
    router.push('/login')
  }
}

const submitForm = async () => {
  isLoading.value = true
  store.commit('auth/CLEAR_AUTH')

  try {
    if (props.isRegister) {
      await store.dispatch('auth/signup', {
        userName: username.value,
        passWord: password.value,
        firstName: firstname.value,
        lastName: lastname.value,
        authProvider: 'EMAIL',
      })

      showDialog('Đăng ký thành công! Vui lòng kiểm tra email để xác minh.', 'success')
      resetForm()
      success.value = true 

    } else {
      await store.dispatch('auth/login', {
        userName: username.value,
        passWord: password.value,
      })

      router.push('/')
    }
  } catch (err) {
    showDialog(err.message || (props.isRegister ? 'Đăng ký thất bại' : 'Đăng nhập thất bại'), 'error')
  } finally {
    isLoading.value = false
  }
}
const resetForm = () => {
  username.value = ''
  password.value = ''
  firstname.value = ''
  lastname.value = ''
}

function placeholderAnimationIn(parent, action) {
  const act = action ? 'add' : 'remove'
  let letters = Array.from(parent.querySelectorAll('.letter'))
  if (!action) letters.reverse()

  letters.forEach((el, i) => {
    setTimeout(() => {
      const isFilled = parent.classList.contains('filled')
      if ((action && !isFilled) || (!action && isFilled)) return
      el.classList[act]('active')
    }, 50 * i)
  })
}

function initAnimatedPlaceholders() {
  const placeholders = document.querySelectorAll('.styled-input__placeholder-text')
  placeholders.forEach(el => {
    const value = el.innerText || '\u00A0'
    el.innerHTML = ''
    for (const char of value) {
      const span = document.createElement('span')
      span.className = 'letter'
      span.textContent = char === ' ' ? '\u00A0' : char
      el.appendChild(span)
    }
  })
}

function setupInputFocusAnimations() {
  const inputs = document.querySelectorAll('.styled-input__input')
  inputs.forEach(input => {
    const parent = input.parentNode
    input.addEventListener('focus', () => {
      parent.classList.add('filled')
      placeholderAnimationIn(parent, true)
    })
    input.addEventListener('blur', () => {
      if (input.value.length) return
      parent.classList.remove('filled')
      placeholderAnimationIn(parent, false)
    })
  })
}

function runStartupTransitions() {
  setTimeout(() => document.body.classList.add('on-start'), 100)
  setTimeout(() => document.body.classList.add('document-loaded'), 1800)
}

onMounted(async () => {
  try {
    await nextTick()
    initAnimatedPlaceholders()
    setupInputFocusAnimations()
    runStartupTransitions()
  } catch (error) {
    console.error('Error in FormAuthen onMounted:', error)
  }
})
</script>
