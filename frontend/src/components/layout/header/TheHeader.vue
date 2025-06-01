<template>
  <div class="header">
    <div class="header_container flex-jsb">
      <div class="header__left flex">
        <TheLogo />
        <router-link to="/intro" class="size-17 cur p-hover">Giới Thiệu</router-link>
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
import MsButton from '../../button/MsButton.vue'
import TheSearchbar from '../../searchbar/TheSearchbar.vue'
import TheLogo from '../../logo/TheLogo.vue'
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
.user-profile {
  position: relative;
  cursor: pointer;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #E94560;
}
.header__right{
  gap:28px !important
}
.user-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 10px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  width: 280px;
  z-index: 1000;
}

.user-info {
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 600;
  color: #333;
}

.user-email {
  font-size: 0.9em;
  color: #666;
}

.menu-divider {
  height: 1px;
  background: #eee;
  margin: 8px 0;
}

.menu-items {
  padding: 8px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  color: #333;
  text-decoration: none;
  transition: background-color 0.2s;

  i {
    width: 20px;
    color: #666;
  }

  &:hover {
    background-color: #f5f5f5;
  }
}

.create-button {
  position: relative;
  margin-right: 16px;
}

.create-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background-color: $btn-primary;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.2s;

  &:hover {
    background-color: #d13651;
  }

  i {
    font-size: 14px;
  }
}

.create-menu {
  position: absolute;
  top: calc(100% + 5px);
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  width: 200px;
  z-index: 1000;
  
  .menu-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    color: #333;
    text-decoration: none;
    transition: background-color 0.2s;

    i {
      width: 20px;
      color: #666;
    }

    &:hover {
      background-color: #f5f5f5;
    }
  }
}

.study-sets-button {
  margin-right: 16px;
  text-decoration: none;
}

.study-sets-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background-color: #f8f9fa;
  border: 1px solid #ddd;
  border-radius: 8px;
  color: #666;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;

  &:hover {
    background-color: #e9ecef;
    border-color: #adb5bd;
  }

  i {
    font-size: 14px;
  }
}
</style>

<style scoped>
@import './TheHeaderCss.scss';
</style>
