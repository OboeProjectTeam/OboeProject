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
        <input v-model="password" type="password" class="styled-input__input" />
        <div class="styled-input__placeholder">
          <span class="styled-input__placeholder-text">Mật Khẩu</span>
        </div>
        <div class="styled-input__circle"></div>
      </div>

      <div class="flex-jsb grap-20">
        <div v-if="isRegister" class="styled-input" :style="{ 'margin-top': '20px', 'margin-bottom': '20px' }">
          <input v-model="lastname" type="text" class="styled-input__input" />
          <div class="styled-input__placeholder">
            <span class="styled-input__placeholder-text">Họ</span>
          </div>
          <div class="styled-input__circle"></div>
        </div>

        <div v-if="isRegister" class="styled-input" :style="{ 'margin-top': '20px', 'margin-bottom': '20px' }">
          <input v-model="firstname" type="text" class="styled-input__input" />
          <div class="styled-input__placeholder">
            <span class="styled-input__placeholder-text">Tên</span>
          </div>
          <div class="styled-input__circle"></div>
        </div>
      </div>

      <div v-if="!isRegister">
        <MCheckbox v-model="remember">
          <span style="color: #888888;font-size: 10px;">Ghi nhớ tài khoản</span>
        </MCheckbox>
      </div>
      <div v-if="isRegister">
        <MCheckbox v-model="remember">
          <span style="color: #888888;font-size: 10px; width: 100%;">
            Tôi chấp nhận
            <router-link to="/dieu-khoan-dich-vu"  target="_blank">
              Điều khoản dịch vụ
            </router-link>
            và
            <router-link to="/quyen-rieng-tu"  target="_blank">
              Chính sách quyền riêng tư
            </router-link>
            của Oboe
          </span>
        </MCheckbox>
      </div>
      <button type="button" class="styled-button" @click="submitForm">
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

      <div v-if="!isRegister">
        <div id="firebaseui-auth-container"></div>
        <div id="loader">Loading...</div>
      </div>

    </div>
  </form>
</template>



<script setup>
import { ref, onMounted, nextTick } from 'vue'
import '@/components/layout/form-login/FormAuthen.scss'
import MCheckbox from '@/components/common/checkbox/MCheckbox.vue'
import * as firebaseui from 'firebaseui'
import 'firebaseui/dist/firebaseui.css'
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { firebase, auth } from '@/firebase.js'

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
const dob = ref('');
const address = ref('');
const user = ref(null);

const uiConfig = {
  signInFlow: 'popup',
  signInOptions: [
    firebase.auth.GoogleAuthProvider.PROVIDER_ID,
    firebase.auth.FacebookAuthProvider.PROVIDER_ID,
  ],
  callbacks: {
    signInSuccessWithAuthResult: function (authResult) {
      const userData = {
        displayName: authResult.user.displayName,
        email: authResult.user.email,
        photoURL: authResult.user.photoURL,
        uid: authResult.user.uid
      };
      store.dispatch('auth/setUser', userData);
      router.push('/');
      return false;
    },
    uiShown: function () {
      document.getElementById('loader').style.display = 'none';
      loginTranslate();
    }
  }
}

// Initialize the FirebaseUI Widget using Firebase.
const ui = firebaseui.auth.AuthUI.getInstance() || new firebaseui.auth.AuthUI(firebase.auth());

const submitForm = () => {
  console.log('Username:', username.value)
  console.log('Password:', password.value)
  router.push('/');
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
// Delay nhỏ để chắc chắn DOM render xong

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

    if (!props.isRegister) {
      ui.start('#firebaseui-auth-container', uiConfig)
    }

    initAnimatedPlaceholders()
    setupInputFocusAnimations()
    runStartupTransitions()
  } catch (error) {
    console.error(' Error in FormAuthen onMounted:', error)
  }
})
</script>