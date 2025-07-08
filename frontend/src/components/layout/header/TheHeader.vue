<template>
  <div class="header">
    <div class="header_container">
      <!-- Desktop View -->
      <div v-if="!isMobile" class="desktop-view">
        <div class="header__left">
          <TheLogo />
          <router-link to="/" class="nav-link">Trang Chủ</router-link>
          <router-link to="/forum" class="nav-link">Diễn Đàn</router-link>
        </div>

        <div class="header__searchbar">
          <TheSearchbar :placeholder="placeholder" />
          <ul class="options__list">
            <li v-for="(item, index) in options" :key="index" 
              class="option-item" 
              :class="{ active: activeIndex === index }" 
              @click="setActive(index)">
              {{ item }}
            </li>
          </ul>
        </div>

        <div class="header__right">
          <template v-if="!isAuthenticated">
            <router-link to="/register">
              <MsButton radius="10px">Đăng Ký</MsButton>
            </router-link>
            <router-link to="/login">
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
            <div class="notification-icon" 
              @click="toggleNotifications"
              ref="notificationIconRef">
              <i class="fas fa-bell"></i>
              <span v-if="unreadNotifications" class="notification-badge">{{ unreadNotifications }}</span>
              <div v-if="showNotifications" 
                class="notifications-menu"
                ref="notificationsRef">
                <div class="notifications-header">
                  <span>Thông Báo</span>
                  <button @click="markAllAsRead" class="mark-read-btn">Đánh dấu đã đọc</button>
                </div>
                <div class="notifications-list">
                  <div v-if="notifications.length === 0" class="no-notifications">
                    Không có thông báo mới
                  </div>
                  <div v-else v-for="(notification, index) in notifications" 
                    :key="index" 
                    class="notification-item"
                    :class="{ 'unread': !notification.read }"
                    @click="handleNotificationClick(notification)">
                    <div class="notification-icon-wrapper" v-if="!notification.user">
                      <i :class="[
                        'fas',
                        {
                          'fa-envelope': notification.type === 'message',
                          'fa-comment': notification.type === 'comment',
                          'fa-trophy': notification.type === 'achievement',
                          'fa-forum': notification.type === 'forum'
                        }
                      ]"></i>
                    </div>
                    <img 
                      v-else 
                      :src="notification.user.avatar" 
                      :alt="notification.user.name"
                      class="notification-avatar"
                    />
                    <div class="notification-details">
                      <div class="notification-content">{{ notification.content }}</div>
                      <div class="notification-time">{{ notification.time }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="user-profile" @click="toggleUserMenu">
              <img :src="currentUser?.photoURL || 'https://ui-avatars.com/api/?name=' + (currentUser?.displayName || 'User')" 
                alt="User Avatar" 
                class="user-avatar" />
              <div v-if="showUserMenu" class="user-menu">
                <div class="user-info">
                  <img :src="currentUser?.photoURL || 'https://ui-avatars.com/api/?name=' + (currentUser?.displayName || 'User')" 
                    alt="User Avatar" 
                    class="menu-avatar" />
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

      <!-- Mobile View -->
      <div v-else class="mobile-view" :class="{ 'header-collapsed': !isHeaderExpanded }">
        <!-- Mobile Header -->
        <div class="mobile-header">
          <div class="mobile-menu-toggle" @click="toggleMobileMenu">
            <i class="fas fa-bars"></i>
          </div>
          <div class="mobile-auth">
            <template v-if="!isAuthenticated">
              <router-link to="/register">
                <MsButton radius="10px" size="small">Đăng Ký</MsButton>
              </router-link>
              <router-link to="/login">
                <MsButton radius="10px" size="small">Đăng Nhập</MsButton>
              </router-link>
            </template>
            <template v-else>
              <div class="notification-icon" @click="toggleNotifications">
                <i class="fas fa-bell"></i>
                <span v-if="unreadNotifications" class="notification-badge">{{ unreadNotifications }}</span>
                <div v-if="showNotifications" class="notifications-menu">
                  <div class="notifications-header">
                    <span>Thông Báo</span>
                    <button @click="markAllAsRead" class="mark-read-btn">Đánh dấu đã đọc</button>
                  </div>
                  <div class="notifications-list">
                    <div v-if="notifications.length === 0" class="no-notifications">
                      Không có thông báo mới
                    </div>
                    <div v-else v-for="(notification, index) in notifications" 
                      :key="index" 
                      class="notification-item"
                      :class="{ 'unread': !notification.read }"
                      @click="handleNotificationClick(notification)">
                      <div class="notification-icon-wrapper" v-if="!notification.user">
                        <i :class="[
                          'fas',
                          {
                            'fa-envelope': notification.type === 'message',
                            'fa-comment': notification.type === 'comment',
                            'fa-trophy': notification.type === 'achievement',
                            'fa-forum': notification.type === 'forum'
                          }
                        ]"></i>
                      </div>
                      <img 
                        v-else 
                        :src="notification.user.avatar" 
                        :alt="notification.user.name"
                        class="notification-avatar"
                      />
                      <div class="notification-details">
                        <div class="notification-content">{{ notification.content }}</div>
                        <div class="notification-time">{{ notification.time }}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="user-profile" @click="toggleUserMenu">
                <img :src="currentUser?.photoURL || 'https://ui-avatars.com/api/?name=' + (currentUser?.displayName || 'User')" 
                  alt="User Avatar" 
                  class="user-avatar" />
                <div v-if="showUserMenu" class="user-menu">
                  <div class="user-info">
                    <img :src="currentUser?.photoURL || 'https://ui-avatars.com/api/?name=' + (currentUser?.displayName || 'User')" 
                      alt="User Avatar" 
                      class="menu-avatar" />
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

        <!-- Mobile Search and Options -->
        <div class="mobile-collapsible">
          <div class="mobile-searchbar">
            <TheSearchbar :placeholder="placeholder" />
            <ul class="options__list">
              <li v-for="(item, index) in options" :key="index" 
                class="option-item" 
                :class="{ active: activeIndex === index }" 
                @click="setActive(index)">
                {{ item }}
              </li>
            </ul>
          </div>
        </div>

        <!-- Dark Overlay -->
        <div v-if="showMobileMenu" class="mobile-overlay" @click="closeMobileMenu"></div>

        <!-- Mobile Menu -->
        <div v-if="showMobileMenu" class="mobile-menu">
          <div class="mobile-menu-header">
            <div class="mobile-menu-toggle" @click="closeMobileMenu">
              <i class="fas fa-bars"></i>
            </div>
            <TheLogo 
              :img-width="40"
              :svg-width="70"
              :svg-height="60"
              :font-size="24"
              :text-x="20"
              :text-y="45"
              class="mobile-logo" 
            />
          </div>
          <nav class="mobile-nav">
            <router-link to="/" class="menu-item" @click="closeMobileMenu">
              <i class="fas fa-home"></i>
              <span>Trang Chủ</span>
            </router-link>
            <router-link to="/forum" class="menu-item" @click="closeMobileMenu">
              <i class="fas fa-comments"></i>
              <span>Diễn Đàn</span>
            </router-link>
            <router-link to="/library" class="menu-item" @click="closeMobileMenu">
              <i class="fas fa-book-reader"></i>
              <span>Thư Viện</span>
            </router-link>
            <router-link to="/create/flashcard" class="menu-item" @click="closeMobileMenu">
              <i class="fas fa-layer-group"></i>
              <span>Tạo Học Liệu</span>
            </router-link>
            <router-link to="/create/quiz" class="menu-item" @click="closeMobileMenu">
              <i class="fas fa-question-circle"></i>
              <span>Tạo Quiz</span>
            </router-link>
            <router-link to="/upgrade" class="menu-item upgrade-item" @click="closeMobileMenu">
              <i class="fas fa-crown"></i>
              <span>Nâng cấp tài khoản</span>
            </router-link>
          </nav>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, toRefs, computed, ref, onMounted, onUnmounted } from 'vue'
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
  showCreateMenu: false,
  showMobileMenu: false,
  showNotifications: false,
  isHeaderExpanded: true,
  lastScrollTop: 0
})

const isMobile = ref(false)

const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
}

const handleScroll = () => {
  if (!isMobile.value) return;
  
  const currentScrollTop = window.pageYOffset || document.documentElement.scrollTop;
  
  // Determine scroll direction
  if (currentScrollTop > state.lastScrollTop && currentScrollTop > 50) {
    // Scrolling down & past threshold
    state.isHeaderExpanded = false;
  } else {
    // Scrolling up or at top
    state.isHeaderExpanded = true;
  }
  
  state.lastScrollTop = currentScrollTop;
}

const notificationsRef = ref(null)
const notificationIconRef = ref(null)

const handleClickOutside = (event) => {
  if (state.showNotifications && 
      notificationsRef.value && 
      notificationIconRef.value && 
      !notificationsRef.value.contains(event.target) &&
      !notificationIconRef.value.contains(event.target)) {
    state.showNotifications = false
  }
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  window.addEventListener('scroll', handleScroll)
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
  window.removeEventListener('scroll', handleScroll)
  document.removeEventListener('click', handleClickOutside)
})

const options = computed(() => store.getters['header/options'])
const isAuthenticated = computed(() => store.getters['auth/isAuthenticated'])
const currentUser = computed(() => store.getters['auth/currentUser'])

const notifications = ref([
  {
    id: 1,
    content: 'Bạn có tin nhắn mới từ Nguyễn Văn A',
    time: '2 phút trước',
    read: false,
    type: 'message',
    user: {
      name: 'Nguyễn Văn A',
      avatar: 'https://ui-avatars.com/api/?name=Nguyen+Van+A&background=random'
    }
  },
  {
    id: 2,
    content: 'Bạn có tin nhắn mới từ Nguyễn Văn B',
    time: '2 phút trước',
    read: false,
    type: 'message',
    user: {
      name: 'Nguyễn Văn B',
      avatar: 'https://ui-avatars.com/api/?name=Nguyen+Van+B&background=random'
    }
  },
  {
    id: 3,
    content: 'Nguyễn Văn C đã bình luận về bài viết của bạn',
    time: '1 giờ trước',
    read: false,
    type: 'comment',
    user: {
      name: 'Nguyễn Văn C',
      avatar: 'https://ui-avatars.com/api/?name=Nguyen+Van+C&background=random'
    }
  },
  {
    id: 4,
    content: 'Đã có người trả lời bình luận của bạn',
    time: '1 ngày trước',
    read: true,
    type: 'forum',
    user: {
      name: 'Trần Thị D',
      avatar: 'https://ui-avatars.com/api/?name=Tran+Thi+D&background=random'
    }
  },
  {
    id: 5,
    content: 'Nguyễn Văn D đã bình luận về bài viết của bạn',
    time: '1 giờ trước',
    read: false,
    type: 'comment',
    user: {
      name: 'Nguyễn Văn C',
      avatar: 'https://ui-avatars.com/api/?name=Nguyen+Van+C&background=random'
    }
  },
  {
    id: 6,
    content: 'Đã có người trả lời bình luận của bạn',
    time: '1 ngày trước',
    read: true,
    type: 'forum',
    user: {
      name: 'Trần Thị D',
      avatar: 'https://ui-avatars.com/api/?name=Tran+Thi+D&background=random'
    }
  },
  {
    id: 7,
    content: 'Nguyễn Văn E đã bình luận về bài viết của bạn',
    time: '1 giờ trước',
    read: false,
    type: 'comment',
    user: {
      name: 'Nguyễn Văn D',
      avatar: 'https://ui-avatars.com/api/?name=Nguyen+Van+D&background=random'
    }
  },
  {
    id: 8,
    content: 'Đã có người trả lời bình luận của bạn',
    time: '1 ngày trước',
    read: true,
    type: 'forum',
    user: {
      name: 'Trần Thị D',
      avatar: 'https://ui-avatars.com/api/?name=Tran+Thi+D&background=random'
    }
  }
])

const unreadNotifications = computed(() => {
  return notifications.value.filter(n => !n.read).length
})

const toggleMobileMenu = () => {
  state.showMobileMenu = !state.showMobileMenu
  if (state.showMobileMenu) {
    state.showUserMenu = false
    state.showCreateMenu = false
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = 'auto'
  }
}

const closeMobileMenu = () => {
  state.showMobileMenu = false
  document.body.style.overflow = 'auto'
}

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

const toggleNotifications = () => {
  state.showNotifications = !state.showNotifications
  if (state.showNotifications) {
    state.showUserMenu = false
    state.showCreateMenu = false
  }
}

const markAllAsRead = () => {
  notifications.value = notifications.value.map(notification => ({
    ...notification,
    read: true
  }))
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

const handleNotificationClick = (notification) => {
  notification.read = true
  
  switch(notification.type) {
    case 'message':
      router.push('/messages')
      break
    case 'forum':
      router.push('/forum')
      break
    case 'comment':
      router.push('/forum')
      break
    case 'achievement':
      router.push('/profile')
      break
    default:
      break
  }
  
  state.showNotifications = false
}

const { activeIndex, placeholder, showUserMenu, showCreateMenu, showMobileMenu, showNotifications, isHeaderExpanded } = toRefs(state)
</script>

<style lang="scss" scoped>
@use '@/components/layout/header/TheHeader.scss';
</style>
