<template>
  <div class="header">
    <div class="header_container">
      <!-- Desktop View -->
      <div v-if="!isMobile" class="desktop-view">
        <div class="header__left">
          <TheLogo />
          <router-link to="/" class="nav-link">{{ t('navigation.home') }}</router-link>
          <router-link to="/forum" class="nav-link">{{ t('navigation.forum') }}</router-link>
        </div>

        <div class="header__searchbar">
          <TheSearchbar :placeholder="state.placeholder" />
          <ul class="options__list">
            <li v-for="(item, index) in optionKeys" :key="index" 
              class="option-item" 
              :class="{ active: activeIndex === index }" 
              @click="setActive(index)">
              {{ t(`headerTabs.${item}`) }}
            </li>
          </ul>
        </div>

        <div class="header__right">
          <template v-if="!isAuthenticated">
            <router-link to="/register">
              <MsButton radius="10px">{{ t('auth.register') }}</MsButton>
            </router-link>
            <router-link to="/login">
              <MsButton radius="10px">{{ t('auth.login') }}</MsButton>
            </router-link>
          </template>
          <template v-else>
            <router-link to="/library" class="study-sets-button">
              <button class="study-sets-btn">
                <i class="fas fa-book-reader"></i>
                {{ t('navigation.library') }}
              </button>
            </router-link>
            <div class="create-button" @click="toggleCreateMenu">
              <button class="create-btn">
                <i class="fas fa-plus"></i>
                {{ t('common.add') }}
              </button>
              <div v-if="showCreateMenu" class="create-menu">
                <router-link to="/create/flashcard" class="menu-item">
                  <i class="fas fa-layer-group"></i>
                  {{ t('flashcard.create') }}
                </router-link>
                <router-link to="/create/quiz" class="menu-item">
                  <i class="fas fa-question-circle"></i>
                  {{ t('flashcard.test') }}
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
                  <span>{{ t('common.notifications') }}</span>
                  <button @click="markAllAsRead" class="mark-read-btn">{{ t('common.markAsRead') }}</button>
                </div>
                <div class="notifications-list">
                  <div v-if="notificationsLoading" class="loading-notifications">
                    <i class="fas fa-spinner fa-spin"></i>
                    {{ t('common.loading') }}
                  </div>
                  <div v-else-if="notifications.length === 0" class="no-notifications">
                    {{ t('common.noNotifications') }}
                  </div>
                  <div v-else v-for="(notification, index) in notifications" 
                    :key="notification.id || index" 
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
              <img :src="currentUser?.avatarUrl || currentUser?.photoURL || 'https://ui-avatars.com/api/?name=' + (currentUser?.displayName || currentUser?.userName || 'User')" 
                alt="User Avatar" 
                class="user-avatar" />
              <div v-if="showUserMenu" class="user-menu">
                <div class="user-info">
                  <img :src="currentUser?.avatarUrl || currentUser?.photoURL || 'https://ui-avatars.com/api/?name=' + (currentUser?.displayName || currentUser?.userName || 'User')" 
                    alt="User Avatar" 
                    class="menu-avatar" />
                  <div class="user-details">
                    <span class="user-name">{{ currentUser?.displayName || currentUser?.userName || 'User' }}</span>
                    <span class="user-email">{{ currentUser?.email }}</span>
                  </div>
                </div>
                <div class="menu-divider"></div>
                <div class="menu-items">
                  <router-link to="/profile" class="menu-item">
                    <i class="fas fa-user"></i>
                    {{ t('navigation.profile') }}
                  </router-link>
                  <div class="menu-item" @click="handleMessagesClick">
                    <i class="fas fa-envelope"></i>
                    {{ t('common.messages') }}
                  </div>
                  <router-link to="/settings" class="menu-item">
                    <i class="fas fa-cog"></i>
                    {{ t('navigation.settings') }}
                  </router-link>
                  <router-link v-if="isAdmin" to="/admin" class="menu-item">
                    <i class="fas fa-shield-alt"></i>
                    {{ t('navigation.admin') }}
                  </router-link>
                  <div class="menu-item" @click="handleLogout">
                    <i class="fas fa-sign-out-alt"></i>
                    {{ t('navigation.logout') }}
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
                <MsButton radius="10px" size="small">{{ t('auth.register') }}</MsButton>
              </router-link>
              <router-link to="/login">
                <MsButton radius="10px" size="small">{{ t('auth.login') }}</MsButton>
              </router-link>
            </template>
            <template v-else>
              <div class="notification-icon" @click="toggleNotifications">
                <i class="fas fa-bell"></i>
                <span v-if="unreadNotifications" class="notification-badge">{{ unreadNotifications }}</span>
                <div v-if="showNotifications" class="notifications-menu">
                  <div class="notifications-header">
                    <span>{{ t('common.notifications') }}</span>
                    <button @click="markAllAsRead" class="mark-read-btn">{{ t('common.markAsRead') }}</button>
                  </div>
                  <div class="notifications-list">
                    <div v-if="notificationsLoading" class="loading-notifications">
                      <i class="fas fa-spinner fa-spin"></i>
                      {{ t('common.loading') }}
                    </div>
                    <div v-else-if="notifications.length === 0" class="no-notifications">
                      {{ t('common.noNotifications') }}
                    </div>
                    <div v-else v-for="(notification, index) in notifications" 
                      :key="notification.id || index" 
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
                <img :src="currentUser?.avatarUrl || currentUser?.photoURL || 'https://ui-avatars.com/api/?name=' + (currentUser?.displayName || currentUser?.userName || 'User')" 
                  alt="User Avatar" 
                  class="user-avatar" />
                <div v-if="showUserMenu" class="user-menu">
                  <div class="user-info">
                    <img :src="currentUser?.avatarUrl || currentUser?.photoURL || 'https://ui-avatars.com/api/?name=' + (currentUser?.displayName || currentUser?.userName || 'User')" 
                      alt="User Avatar" 
                      class="menu-avatar" />
                    <div class="user-details">
                      <span class="user-name">{{ currentUser?.displayName || currentUser?.userName || 'User' }}</span>
                      <span class="user-email">{{ currentUser?.email }}</span>
                    </div>
                  </div>
                  <div class="menu-divider"></div>
                  <div class="menu-items">
                    <router-link to="/profile" class="menu-item">
                      <i class="fas fa-user"></i>
                      {{ t('navigation.profile') }}
                    </router-link>
                    <div class="menu-item" @click="handleMessagesClick">
                      <i class="fas fa-envelope"></i>
                      {{ t('common.messages') }}
                    </div>
                    <router-link to="/settings" class="menu-item">
                      <i class="fas fa-cog"></i>
                      {{ t('navigation.settings') }}
                    </router-link>
                    <router-link v-if="isAdmin" to="/admin" class="menu-item">
                      <i class="fas fa-shield-alt"></i>
                      {{ t('navigation.admin') }}
                    </router-link>
                    <div class="menu-item" @click="handleLogout">
                      <i class="fas fa-sign-out-alt"></i>
                      {{ t('navigation.logout') }}
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
            <TheSearchbar :placeholder="state.placeholder" />
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
              <span>{{ t('navigation.home') }}</span>
            </router-link>
            <router-link to="/forum" class="menu-item" @click="closeMobileMenu">
              <i class="fas fa-comments"></i>
              <span>{{ t('navigation.forum') }}</span>
            </router-link>
            <router-link to="/library" class="menu-item" @click="closeMobileMenu">
              <i class="fas fa-book-reader"></i>
              <span>{{ t('navigation.library') }}</span>
            </router-link>
            <router-link to="/create/flashcard" class="menu-item" @click="closeMobileMenu">
              <i class="fas fa-layer-group"></i>
              <span>{{ t('flashcard.create') }}</span>
            </router-link>
            <router-link to="/create/quiz" class="menu-item" @click="closeMobileMenu">
              <i class="fas fa-question-circle"></i>
              <span>{{ t('flashcard.test') }}</span>
            </router-link>
            <router-link to="/upgrade" class="menu-item upgrade-item" @click="closeMobileMenu">
              <i class="fas fa-crown"></i>
              <span>{{ t('upgrade.title') }}</span>
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
import { useI18n } from 'vue-i18n'
import MsButton from '@/components/common/button/MsButton.vue'
import TheSearchbar from '@/components/layout/searchbar/TheSearchbar.vue'
import TheLogo from '@/components/layout/logo/TheLogo.vue'
import api from '@/api'
import WebSocketService from '@/services/websocket'

const store = useStore()
const router = useRouter()
const { t } = useI18n()

const state = reactive({
  activeIndex: store.getters['header/activeIndex'],
  placeholder: computed(() => t('search.searchPlaceholder')),
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
  if (currentScrollTop > state.lastScrollTop && currentScrollTop > 50) {
    state.isHeaderExpanded = false;
  } else {
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

  if (isAuthenticated.value) {
    loadNotifications()
    WebSocketService.connect(currentUser.value?.userId || currentUser.value?.id)

    WebSocketService.onMessage((msg) => {
      console.log("[ Message Received]", msg);
    });

    WebSocketService.onNotification((noti) => {
      handleIncomingNotification(noti);
    });
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
  window.removeEventListener('scroll', handleScroll)
  document.removeEventListener('click', handleClickOutside)
})

const optionKeys = computed(() => store.getters['header/optionKeys'])
const isAuthenticated = computed(() => store.getters['auth/isAuthenticated'])
const currentUser = computed(() => store.getters['auth/currentUser'])
const isAdmin = computed(() => store.getters['auth/isAdmin'])

const notifications = ref([])
const notificationsLoading = ref(false)

const unreadNotifications = computed(() => {
  const count = notifications.value.filter(n => !n.read).length
  return count
})

const handleIncomingNotification = (data) => {
  // Chuyển key từ snake_case sang camelCase nếu cần
  const id = data.notifiId || data.notifId || data.id;
  const content = data.textNotification || data.text_notification || data.message;
  const updateAt = data.updateAt || data.update_at || new Date().toISOString();
  const read = data.read ?? false;

  const newNotification = {
    id,
    content,
    time: formatNotificationTime(updateAt),
    read,
    type: 'message'
  };

  notifications.value.unshift(newNotification);
};


const loadNotifications = async () => {
  try {
    notificationsLoading.value = true
    const response = await api.notification.getAll()
    const notificationsData = Array.isArray(response) ? response : (response.content || response.data || response)
    const mappedNotifications = (Array.isArray(notificationsData) ? notificationsData : []).map(notification => {
      return {
        id: notification.notifiId || notification.id,
        content: notification.textNotification || notification.content || notification.message,
        time: notification.updateAt ? formatNotificationTime(notification.updateAt) : 'Không rõ',
        read: notification.read || false,
        type: 'comment',
        user: null
      }
    })
    notifications.value = mappedNotifications
  } catch (error) {
    console.error('Failed to load notifications:', error)
    notifications.value = []
  } finally {
    notificationsLoading.value = false
  }
}

const formatNotificationTime = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)
  if (diffMins < 1) return 'Vừa xong'
  if (diffMins < 60) return `${diffMins} phút trước`
  if (diffHours < 24) return `${diffHours} giờ trước`
  if (diffDays < 7) return `${diffDays} ngày trước`
  return date.toLocaleDateString('vi-VN')
}

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
    loadNotifications()
  }
}

const markAllAsRead = async () => {
  try {
    await api.notification.markAllAsRead()
    notifications.value = notifications.value.map(notification => ({
      ...notification,
      read: true
    }))
  } catch (error) {
    console.error('Failed to mark notifications as read:', error)
    store.dispatch('message/showMessage', {
      type: 'error',
      text: 'Không thể đánh dấu thông báo đã đọc: ' + error.message
    })
  }
}

const handleMessagesClick = () => {
  state.showUserMenu = false
  router.push('/messages')
}

const handleLogout = async () => {
  try {
    await store.dispatch('auth/logout');
    router.push('/login');
  } catch (error) {
    console.error('Lỗi khi đăng xuất:', error);
  }
};

const setActive = (index) => {
  state.activeIndex = index
  store.commit('header/setActiveIndex', index)
  const optionKey = optionKeys.value[index]
  const optionText = t(`headerTabs.${optionKey}`)
  state.placeholder = t('search.searchPlaceholder')
}

const handleNotificationClick = (notification) => {
  notification.read = true
  switch(notification.type) {
    case 'message':
      router.push('/messages')
      break
    case 'forum':
    case 'comment':
      router.push('/messages')
      break
    case 'achievement':
      router.push('/messages')
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