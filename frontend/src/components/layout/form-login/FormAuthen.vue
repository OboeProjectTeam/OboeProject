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
          <span class="styled-input__placeholder-text">Email</span>
        </div>
        <div class="styled-input__circle"></div>
      </div>

      <div class="styled-input" :style="{ 'margin-top': isRegister ? '20px' : '0px' }">
        <input v-model="password" type="password" class="styled-input__input" />
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
            <router-link to="/footer-services" target="_blank">Điều khoản dịch vụ</router-link>
            và
            <router-link to="/footer-services/privacy" target="_blank">Chính sách quyền riêng tư</router-link>
            của Oboe
          </span>
        </MCheckbox>
      </div>

      <button type="button" class="styled-button" @click="submitForm" :disabled="isLoading" :class="{ 'loading': isLoading }">
        <span class="styled-button__real-text-holder" v-if="!isLoading">
          <span class="styled-button__real-text">{{ isRegister ? 'Đăng ký' : 'Đăng nhập' }}</span>
          <span class="styled-button__moving-block face">
            <span class="styled-button__text-holder">{{ isRegister ? 'Đăng ký' : 'Đăng nhập' }}</span>
          </span>
          <span class="styled-button__moving-block back">
            <span class="styled-button__text-holder">{{ isRegister ? 'Đăng ký' : 'Đăng nhập' }}</span>
          </span>
        </span>
        <span v-if="isLoading" class="loading-text">
          <i class="fas fa-spinner fa-spin"></i> {{ isRegister ? 'Đang đăng ký...' : 'Đang đăng nhập...' }}
        </span>
      </button>

      <div v-if="!isRegister" class="divider">
        <span class="divider-text">Hoặc</span>
      </div>

       <div v-if="!isRegister">
        <div id="firebaseui-auth-container"></div>
        <div id="loader">Loading...</div>
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
import { firebase, auth } from '@/firebase.js'
import * as firebaseui from 'firebaseui'
import 'firebaseui/dist/firebaseui.css'
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

const validateForm = () => {
  const validationErrors = []
  
  // Validate username
  if (!username.value || username.value.trim() === '') {
    validationErrors.push('Email không được để trống')
  } else if (props.isRegister) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    const value = username.value.trim()
    
    if (!emailRegex.test(value)) {
      validationErrors.push('Email không đúng định dạng')
    }
  }
  
  // Validate password
  if (!password.value || password.value.trim() === '') {
    validationErrors.push('Mật khẩu không được để trống')
  } else if (props.isRegister && password.value.length < 6) {
    validationErrors.push('Mật khẩu phải có ít nhất 6 ký tự')
  }
  
  // Validate register fields
  if (props.isRegister) {
    if (!firstname.value || firstname.value.trim() === '') {
      validationErrors.push('Tên không được để trống')
    }
    if (!lastname.value || lastname.value.trim() === '') {
      validationErrors.push('Họ không được để trống')
    }
    if (!remember.value) {
      validationErrors.push('Bạn phải chấp nhận điều khoản dịch vụ')
    }
  }
  
  return validationErrors
}

const uiConfig = {
  signInFlow: 'popup',
  signInSuccessUrl: '',
  signInOptions: [
    {
      provider: firebase.auth.GoogleAuthProvider.PROVIDER_ID,
      customParameters: {
        prompt: 'select_account'
      }
    },
  ],
  callbacks: {
    signInSuccessWithAuthResult: async function (authResult) {
      try {
        const idToken = await authResult.user.getIdToken();
        const result = await api.auth.loginWithFirebase(idToken);

        if (!result || !result.token || !result.user) {
          throw new Error("Thiếu token hoặc user: " + JSON.stringify(result));
        }
        const { token, user } = result;
        
        // Sử dụng fetchCurrentUser action để xử lý first login
        await store.dispatch('auth/fetchCurrentUser', { token, user });
        
        // Kiểm tra role của user sau khi đăng nhập thành công
        if (user && user.role === 'ROLE_ADMIN') {
          router.push('/admin');
        } else {
          router.push('/');
        }
      } catch (error) {
        console.error(error);
        
        // Kiểm tra nếu lỗi là do user bị banned
        if (error.message && (error.message.includes('Tài khoản đã bị khóa') || error.message.includes('User is banned'))) {
          showDialog('🚫 Tài khoản của bạn đã bị cấm\n\nVui lòng liên hệ quản trị viên để được hỗ trợ.', 'error');
        } else if (error.message && error.message.includes('Firebase authentication failed')) {
          // Xử lý các lỗi Firebase khác
          let errorMsg = 'Đăng nhập Firebase thất bại';
          if (error.message.includes('Tài khoản đã bị khóa') || error.message.includes('User is banned')) {
            errorMsg = '🚫 Tài khoản của bạn đã bị cấm\n\nVui lòng liên hệ quản trị viên để được hỗ trợ.';
          } else if (error.message.includes('Invalid')) {
            errorMsg = 'Token Firebase không hợp lệ. Vui lòng thử lại.';
          } else if (error.message.includes('Expired')) {
            errorMsg = 'Phiên đăng nhập đã hết hạn. Vui lòng thử lại.';
          }
          showDialog(errorMsg, 'error');
        } else {
          // Lỗi khác
          showDialog('Đăng nhập thất bại. Vui lòng thử lại.', 'error');
        }
      }
      return false;
    },
    uiShown: function () {
      const loader = document.getElementById('loader');
      if (loader) {
        loader.style.display = 'none';
      }
      loginTranslate();
    }
  }
}
const ui = firebaseui.auth.AuthUI.getInstance() || new firebaseui.auth.AuthUI(firebase.auth());
function loginTranslate() {
  const googleBtn = document.querySelector('.firebaseui-idp-google .firebaseui-idp-text');
  if (googleBtn) {
    googleBtn.textContent = 'Đăng nhập với Google';
  }

  const facebookBtn = document.querySelector('.firebaseui-idp-facebook .firebaseui-idp-text');
  if (facebookBtn) {
    facebookBtn.textContent = 'Đăng nhập với Facebook';
  }
}
const handlePopupConfirm = () => {
  showPopup.value = false
  if (success.value && props.isRegister) {
    router.push('/login')
  }
}

const submitForm = async () => {
    // Frontend validation
    const validationErrors = validateForm()
    if (validationErrors.length > 0) {
      // Hiển thị lỗi theo từng dòng với bullet points
      const errorMessage = validationErrors.map(error => `• ${error}`).join('\n')
      showDialog(errorMessage, 'error')
      return
    }
  
  isLoading.value = true
  store.commit('auth/CLEAR_AUTH')

  try {
    if (props.isRegister) {
      await store.dispatch('auth/signup', {
        userName: username.value.trim(),
        passWord: password.value,
        firstName: firstname.value.trim(),
        lastName: lastname.value.trim(),
        authProvider: 'EMAIL',
      })
      showDialog('Đăng ký thành công! Vui lòng kiểm tra email để xác minh.', 'success')
      resetForm()
      success.value = true 

    } else {
      await store.dispatch('auth/login', {
        userName: username.value.trim(),
        passWord: password.value,
      })

      // Kiểm tra role của user sau khi đăng nhập thành công
      const user = store.getters['auth/currentUser'];
      if (user && user.role === 'ROLE_ADMIN') {
        router.push('/admin');
      } else {
        router.push('/');
      }
    }
  } catch (err) {
    // Xử lý các message lỗi cụ thể từ backend
    let errorMessage = err.message || (props.isRegister ? 'Đăng ký thất bại' : 'Đăng nhập thất bại')
    
    // Mapping các message lỗi từ backend sang tiếng Việt nếu cần
    if (errorMessage.includes('Username is required')) {
      errorMessage = 'Email không được để trống'
    } else if (errorMessage.includes('Password is required')) {
      errorMessage = 'Mật khẩu không được để trống'
    } else if (errorMessage.includes('User not found')) {
      errorMessage = 'Tài khoản không tồn tại'
    } else if (errorMessage.includes('Invalid credentials')) {
      errorMessage = 'Email hoặc mật khẩu không chính xác'
    } else if (errorMessage.includes('Please verify your email')) {
      errorMessage = 'Vui lòng xác minh email trước khi đăng nhập'
    } else if (errorMessage.includes('Verification email sent')) {
      errorMessage = 'Email xác minh đã được gửi. Vui lòng kiểm tra hộp thư của bạn.'
    } else if (errorMessage.includes('Tài khoản đã tồn tại')) {
      errorMessage = 'Tài khoản đã tồn tại với email này'
    } else if (errorMessage.includes('Nhiều tài khoản trùng username')) {
      errorMessage = 'Có nhiều tài khoản với thông tin này. Vui lòng liên hệ hỗ trợ.'
    } else if (errorMessage.includes('Hãy đăng nhập bằng')) {
      // Giữ nguyên message này vì đã có sẵn tiếng Việt
    } else if (errorMessage.includes('Không thể kết nối')) {
      errorMessage = 'Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng.'
    } else if (errorMessage.includes('Lỗi máy chủ')) {
      errorMessage = 'Lỗi máy chủ. Vui lòng thử lại sau.'
    } else if (errorMessage.includes('timeout') || errorMessage.includes('TIMEOUT')) {
      errorMessage = 'Kết nối quá chậm. Vui lòng thử lại.'
    } else if (errorMessage.includes('Network Error') || errorMessage.includes('ERR_NETWORK')) {
      errorMessage = 'Lỗi kết nối mạng. Vui lòng kiểm tra internet.'
    } else if (errorMessage.includes('ECONNREFUSED') || errorMessage.includes('ERR_CONNECTION_REFUSED')) {
      errorMessage = 'Không thể kết nối đến máy chủ. Vui lòng thử lại sau.'
    }
    
    showDialog(errorMessage, 'error')
  } finally {
    isLoading.value = false
  }
}
const resetForm = () => {
  username.value = ''
  password.value = ''
  firstname.value = ''
  lastname.value = ''
  remember.value = false
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
function observeAndRemoveFirebaseSnackbar() {
  const observer = new MutationObserver(() => {
    const snackbar = document.querySelector('.mdl-snackbar, .firebaseui-snackbar, .firebaseui-container .mdl-snackbar__text');
    if (snackbar && snackbar.parentNode) {
      snackbar.parentNode.remove();
    }
  });

  observer.observe(document.body, {
    childList: true,
    subtree: true,
  });

  // Dừng quan sát sau 5 giây để tránh leak
  setTimeout(() => {
    observer.disconnect();
  }, 5000);
}

onMounted(async () => {
  try {
    await nextTick()
     if (!props.isRegister) {
      ui.start('#firebaseui-auth-container', uiConfig)
      observeAndRemoveFirebaseSnackbar();
    }
    initAnimatedPlaceholders()
    setupInputFocusAnimations()
    runStartupTransitions()
  } catch (error) {
    console.error('Error in FormAuthen onMounted:', error)
  }
})
</script>
