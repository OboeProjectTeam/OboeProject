<template>
  <div class="header">
    <div class="header_container flex-jsb">
      <div class="header__left flex">
        <TheLogo />
        <router-link to="/" class="size-17 cur p-hover">Trang Chủ</router-link>
        <router-link to="/forum" class="size-17 cur p-hover">Diễn Đàn</router-link>
      </div>
      <div class="header__searchbar">
        <TheSearchbar :placeholder="placeholder" />
        <ul class="options__list">
          <li v-for="(item, index) in options" :key="index" class="option-item p-hover"
            :class="{ active: activeIndex === index }" @click="setActive(index)">
            {{ item }}
          </li>
        </ul>
      </div>
      <div class="header__right flex-jsa">
        <template v-if="!isAuthenticated">
          <router-link to="/register" class="cur p-hover mr-20">
            <MsButton radius="10px">Đăng Ký</MsButton>
          </router-link>
          <router-link to="/login" class="cur p-hover">
            <MsButton radius="10px">Đăng Nhập</MsButton>
          </router-link>
        </template>
        <template v-else>
          <router-link to="/library" class="study-sets-button">
            <button class="study-sets-btn">
              <i class="fas fa-book-reader"></i>
              Thư Viện
            </button>
          </router-link>
          <div class="create-button" @click="toggleCreateMenu">
            <button class="create-btn">
              <i class="fas fa-plus"></i>
              Tạo
            </button>
            <div v-if="showCreateMenu" class="create-menu">
              <router-link to="/create/flashcard" class="menu-item">
                <i class="fas fa-layer-group"></i>
                Tạo Học Liệu
              </router-link>
              <router-link to="/create/quiz" class="menu-item">
                <i class="fas fa-question-circle"></i>
                Tạo Quiz
              </router-link>
            </div>
          </div>
          <div class="user-profile" @click="toggleUserMenu">
            <img :src="currentUser?.photoURL || 'https://ui-avatars.com/api/?name=' + (currentUser?.displayName || 'User')" alt="User Avatar" class="user-avatar" />
            <div v-if="showUserMenu" class="user-menu">
              <div class="user-info">
                <img :src="currentUser?.photoURL || 'https://ui-avatars.com/api/?name=' + (currentUser?.displayName || 'User')" alt="User Avatar" class="menu-avatar" />
                <div class="user-details">
                  <span class="user-name">{{ currentUser?.displayName || 'User' }}</span>
                  <span class="user-email">{{ currentUser?.email }}</span>
                </div>
              </div>
              <div class="menu-divider"></div>
              <div class="menu-items">
                <router-link to="/profile" class="menu-item">
                  <i class="fas fa-user"></i>
                  Hồ sơ
                </router-link>
                <router-link to="/messages" class="menu-item">
                  <i class="fas fa-envelope"></i>
                  Tin nhắn
                </router-link>
                <router-link to="/settings" class="menu-item">
                  <i class="fas fa-cog"></i>
                  Cài đặt
                </router-link>
                <div class="menu-item" @click="handleLogout">
                  <i class="fas fa-sign-out-alt"></i>
                  Đăng xuất
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, toRefs, computed, watch, ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import MsButton from '@/components/common/button/MsButton.vue'
import TheSearchbar from '@/components/layout/searchbar/TheSearchbar.vue'
import TheLogo from '@/components/layout/logo/TheLogo.vue'
import { auth } from '@/firebase'

const store = useStore()
const router = useRouter()

const state = reactive({
  activeIndex: store.getters['header/activeIndex'],
  placeholder: 'Tìm kiếm từ vựng',
  showUserMenu: false,
  showCreateMenu: false
})

const options = computed(() => store.getters['header/options'])
const isAuthenticated = computed(() => store.getters['auth/isAuthenticated'])
const currentUser = computed(() => store.getters['auth/currentUser'])

const toggleCreateMenu = () => {
  state.showCreateMenu = !state.showCreateMenu
  if (state.showCreateMenu) {
    state.showUserMenu = false
  }
}

const toggleUserMenu = () => {
  state.showUserMenu = !state.showUserMenu
  if (state.showUserMenu) {
    state.showCreateMenu = false
  }
}

const handleLogout = async () => {
  try {
    await auth.signOut()
    store.dispatch('auth/setUser', null)
    router.push('/login')
    state.showUserMenu = false
  } catch (error) {
    console.error('Logout error:', error)
  }
}

const setActive = (index) => {
  state.activeIndex = index
  store.commit('header/setActiveIndex', index)
  state.placeholder = 'Tìm kiếm ' + options.value[index].toLowerCase()
}

watch(() => state.activeIndex, (newIndex) => {
  state.placeholder = 'Tìm kiếm ' + options.value[newIndex].toLowerCase()
})

const { activeIndex, placeholder, showUserMenu, showCreateMenu } = toRefs(state)
</script>
<style lang="scss" scoped>
  @use '@/components/layout/header/TheHeader.scss';
</style>
