<template>
  <div class="forum-profile-container">
    <div class="breadcrumb">
      <!-- Dynamic breadcrumb based on source -->
      <template v-if="route.query.fromSource === 'messages'">
        <!-- From Messages: Messenger > Hồ sơ -->
        <router-link to="/messages">{{ t('forumProfile.messenger') }}</router-link>
        <i class="fas fa-chevron-right separator"></i>
        <span>{{ t('forumProfile.profile') }}</span>
      </template>
      <template v-else-if="route.query.fromSource === 'search'">
        <!-- From Search: Trang chủ > Tìm kiếm > Hồ sơ -->
        <router-link to="/">{{ t('forumProfile.home') }}</router-link>
        <i class="fas fa-chevron-right separator"></i>
        <router-link :to="route.query.searchQuery ? `/search?q=${encodeURIComponent(route.query.searchQuery)}` : '/search'">{{ t('forumProfile.search') }}</router-link>
        <i class="fas fa-chevron-right separator"></i>
        <span>{{ t('forumProfile.profile') }}</span>
      </template>
      <template v-else-if="route.query.fromPostId">
        <!-- From Forum Post: Diễn đàn > Chi tiết bài viết > Hồ sơ -->
        <router-link to="/forum">{{ t('forumProfile.forum') }}</router-link>
        <i class="fas fa-chevron-right separator"></i>
        <router-link :to="`/forum/post/${route.query.fromPostId}`">{{ t('forumProfile.postDetail') }}</router-link>
        <i class="fas fa-chevron-right separator"></i>
        <span>{{ t('forumProfile.profile') }}</span>
      </template>
      <template v-else>
        <!-- Default from Forum: Diễn đàn > Hồ sơ -->
        <router-link to="/forum">{{ t('forumProfile.forum') }}</router-link>
        <i class="fas fa-chevron-right separator"></i>
        <span>{{ t('forumProfile.profile') }}</span>
      </template>
    </div>
    
    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>{{ t('forumProfile.loadingProfile') }}</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-container">
      <div class="error-icon">
        <i class="fas fa-exclamation-triangle"></i>
      </div>
      <h3>{{ error === 'User not found' ? t('forumProfile.userNotFound') : t('forumProfile.errorLoadingProfile') }}</h3>
      <p>{{ error === 'User not found' ? t('forumProfile.userNotFoundMessage') : t('forumProfile.errorLoadingProfileMessage') }}</p>
      <button @click="fetchUserProfile" class="retry-btn">
        <i class="fas fa-redo"></i>
        {{ t('forumProfile.tryAgain') }}
      </button>
    </div>
    
    <!-- Profile content -->
    <ProfileDetail 
      v-else-if="user" 
      :user="user" 
      :isMyProfile="isMyProfile"
      @send-message="handleSendMessage"
    /> 
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { useI18n } from 'vue-i18n';
import ProfileDetail from '@/components/layout/forum/profile/ProfileDetail.vue';
import profileApi from '@/api/modules/profileApi';

const route = useRoute();
const router = useRouter();
const store = useStore();
const { t } = useI18n();

const user = ref(null);
const loading = ref(false);
const error = ref(null);

const username = computed(() => route.params.username);
const currentUser = computed(() => store.getters['auth/currentUser']);

// Check if this is current user's profile
const isMyProfile = computed(() => {
  if (!currentUser.value || !username.value) return false;
  
  const currentUsername = currentUser.value.username || currentUser.value.userName;
  return currentUsername === username.value;
});

// Hàm helper để loại bỏ @gmail.com khỏi username
function removeGmailSuffix(username) {
  if (!username) return '';
  return username.replace(/@gmail\.com$/i, '');
}

// Load user profile by username or userId
const loadUserProfile = async () => {
  if (!username.value) return;
  
  loading.value = true;
  error.value = null;
  
  try {
    // For current user, we can get more detailed info
    if (isMyProfile.value) {
      const profileData = await profileApi.getProfile();
      const cleanUsername = removeGmailSuffix(profileData.userName || profileData.username);
      user.value = {
        ...profileData,
        username: cleanUsername,
        userName: cleanUsername,
        email: profileData.userName, // Giữ nguyên email đầy đủ
        stats: {
          joined: formatDate(profileData.create_at),
          learning_materials: profileData.flashCardCount || 0,
          topics: profileData.blogCount || 0,
          solutions: profileData.commentCount || 0
        }
      };
    } else {
      // For other users, get userId from query params
      const userId = route.query.userId;
      if (userId) {
        const profileData = await profileApi.getUserProfileById(userId);
        const cleanUsername = removeGmailSuffix(profileData.userName || profileData.username);
        user.value = {
          ...profileData,
          username: cleanUsername,
          userName: cleanUsername,
          email: profileData.userName, // Giữ nguyên email đầy đủ
          stats: {
            joined: formatDate(profileData.create_at),
            learning_materials: profileData.flashCardCount || 0,
            topics: profileData.blogCount || 0,
            solutions: profileData.commentCount || 0
          }
        };
      } else {
        throw new Error('Không tìm thấy thông tin người dùng');
      }
    }
  } catch (err) {
    console.error('Error loading user profile:', err);
    error.value = err.message || 'Không thể tải thông tin người dùng';
    user.value = null;
  } finally {
    loading.value = false;
  }
};

// Format date helper
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};

// Handle send message event
const handleSendMessage = (userData) => {
  // Navigate to messages with the selected user data
  router.push({
    name: 'messages',
    query: { 
      userId: userData.user_id || userData.userId || userData.id,
      userName: userData.userName || userData.username,
      fullName: userData.fullName || userData.userName || userData.username,
      avatarUrlReceiver: userData.avatarUrl || userData.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(userData.fullName || userData.userName || userData.username)}&background=random`
    }
  });
};

// Watch username and userId changes
watch([username, () => route.query.userId], () => {
  loadUserProfile();
}, { immediate: true });

// Load on mount
onMounted(() => {
  loadUserProfile();
});
</script>

<style lang="scss" scoped>
@use '@/views/forum/forum-profile/ForumProfile.scss';

</style>