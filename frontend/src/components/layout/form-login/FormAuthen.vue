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

      <div v-if="!isRegister">
        <MCheckbox v-model="remember" :disabled="isLoading">
          <span style="color: #888888;font-size: 10px;">Ghi nhớ tài khoản</span>
        </MCheckbox>
      </div>
      <div v-if="isRegister">
        <MCheckbox v-model="remember" :disabled="isLoading">
          <span style="color: #888888;font-size: 10px; width: 100%;">
            Tôi chấp nhận
            <router-link to="/dieu-khoan-dich-vu" target="_blank">
              Điều khoản dịch vụ
            </router-link>
            và
            <router-link to="/quyen-rieng-tu" target="_blank">
              Chính sách quyền riêng tư
            </router-link>
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
  <ThePopup
    v-if="showErrorPopup"
    title="Lỗi"
    :message="errorMessage"
    confirmText="Đóng"
    @confirm="closeErrorPopup"
    @cancel="closeErrorPopup"
  />
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import '@/components/layout/form-login/FormAuthen.scss'
import MCheckbox from '@/components/common/checkbox/MCheckbox.vue'
import ThePopup from '@/components/common/popup/ThePopup.vue'
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import authApi from '@/api/modules/authApi';
import oauthApi from '@/api/modules/oauthApi';

const props = defineProps({
  isRegister: {
    type: Boolean,
    default: false
  },
  formWidth: {
    type: String,
    default: '400px'
  }
});

const router = useRouter();
const store = useStore();

const username = ref('')
const password = ref('')
const remember = ref(false);
const lastname = ref('');
const firstname = ref('');
const errorMessage = ref('');
const showErrorPopup = ref(false);
const isLoading = ref(false);

const closeErrorPopup = () => {
  showErrorPopup.value = false;
  errorMessage.value = '';
}

const handleGoogleLogin = async () => {
  try {
    const googleAuthUrl = await oauthApi.getGoogleAuthUrl();
    console.log('Redirecting to Google auth URL:', googleAuthUrl);
    window.location.href = googleAuthUrl;
  } catch (error) {
    console.error('Google login error:', error);
    errorMessage.value = 'Đăng nhập thất bại. Vui lòng thử lại.';
    showErrorPopup.value = true;
  }
};

const handleFacebookLogin = async () => {
  try {
    const facebookAuthUrl = await oauthApi.getFacebookAuthUrl();
    console.log('Redirecting to Facebook auth URL:', facebookAuthUrl);
    window.location.href = facebookAuthUrl;
  } catch (error) {
    console.error('Facebook login error:', error);
    errorMessage.value = 'Đăng nhập thất bại. Vui lòng thử lại.';
    showErrorPopup.value = true;
  }
};

const submitForm = async () => {
  try {
    errorMessage.value = '';
    showErrorPopup.value = false;
    isLoading.value = true;

    if (props.isRegister) {
      // Validate form
      if (!username.value || !password.value || !lastname.value || !firstname.value) {
        errorMessage.value = 'Vui lòng điền đầy đủ thông tin';
        showErrorPopup.value = true;
        return;
      }

      if (!remember.value) {
        errorMessage.value = 'Vui lòng chấp nhận điều khoản dịch vụ';
        showErrorPopup.value = true;
        return;
      }

      // Call signup API
      const userData = {
        userName: username.value,
        passWord: password.value,
        firstName: firstname.value,
        lastName: lastname.value
      };

      const response = await authApi.signup(userData);
      // Show success message
      alert('Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.');
      // Redirect to login page
      router.push('/login');
    } else {
      // Handle login
      if (!username.value || !password.value) {
        errorMessage.value = 'Vui lòng nhập đầy đủ email/số điện thoại và mật khẩu';
        showErrorPopup.value = true;
        return;
      }

      try {
        const response = await authApi.login(username.value, password.value);
        if (response.token && response.user) {
          // Store token in localStorage
          localStorage.setItem('token', response.token);
          // Store token in Vuex
          store.dispatch('auth/setToken', response.token);
          // Store user info in Vuex
          store.dispatch('auth/setUser', response.user);
          // Show success message
          errorMessage.value = response.message || 'Đăng nhập thành công!';
          showErrorPopup.value = true;
          // After showing success message, wait a bit then redirect
          setTimeout(() => {
            router.push('/');
          }, 1000);
        } else {
          errorMessage.value = 'Đăng nhập thất bại. Vui lòng thử lại';
          showErrorPopup.value = true;
        }
      } catch (error) {
        if (error.message.includes('401')) {
          errorMessage.value = 'Tài khoản hoặc mật khẩu không chính xác';
        } else if (error.message.includes('403')) {
          errorMessage.value = 'Tài khoản của bạn chưa được xác thực. Vui lòng kiểm tra email để xác thực tài khoản';
        } else {
          errorMessage.value = error.message || 'Có lỗi xảy ra, vui lòng thử lại';
        }
        showErrorPopup.value = true;
      }
    }
  } catch (error) {
    errorMessage.value = error.message || 'Có lỗi xảy ra, vui lòng thử lại';
    showErrorPopup.value = true;
  } finally {
    isLoading.value = false;
  }
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
//Khởi tạo placeholder chữ động
function initAnimatedPlaceholders() {
  const placeholders = document.querySelectorAll('.styled-input__placeholder-text');
  placeholders.forEach(el => {
    const value = el.innerText || '\u00A0';
    el.innerHTML = '';
    for (const char of value) {
      const span = document.createElement('span');
      span.className = 'letter';
      // Nếu là khoảng trắng thì thay bằng non-breaking space
      span.textContent = char === ' ' ? '\u00A0' : char;
      el.appendChild(span);
    }
  });
}

// Chạy hiệu ứng cho các placeholder
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
// Chạy hiệu ứng khởi động giao diện
function runStartupTransitions() {
  setTimeout(() => document.body.classList.add('on-start'), 100);
  setTimeout(() => document.body.classList.add('document-loaded'), 1800);
}

onMounted(async () => {
  try {
    await nextTick()
    initAnimatedPlaceholders()
    setupInputFocusAnimations()
    runStartupTransitions()
  } catch (error) {
    console.error(' Error in FormAuthen onMounted:', error)
  }
})
</script>