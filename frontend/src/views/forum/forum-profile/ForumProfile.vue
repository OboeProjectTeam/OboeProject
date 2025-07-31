<template>
  <div class="forum-profile-container">
    <div class="breadcrumb">
      <!-- Dynamic breadcrumb based on source -->
      <template v-if="route.query.fromSource === 'messages'">
        <!-- From Messages: Messenger > Hồ sơ -->
        <router-link to="/messages">Messenger</router-link>
        <i class="fas fa-chevron-right separator"></i>
        <span>Hồ sơ</span>
      </template>
      <template v-else-if="route.query.fromPostId">
        <!-- From Forum Post: Diễn đàn > Chi tiết bài viết > Hồ sơ -->
        <router-link to="/forum">Diễn đàn</router-link>
        <i class="fas fa-chevron-right separator"></i>
        <router-link :to="`/forum/post/${route.query.fromPostId}`">Chi tiết bài viết</router-link>
        <i class="fas fa-chevron-right separator"></i>
        <span>Hồ sơ </span>
      </template>
      <template v-else>
        <!-- Default from Forum: Diễn đàn > Hồ sơ -->
        <router-link to="/forum">Diễn đàn</router-link>
        <i class="fas fa-chevron-right separator"></i>
        <span>Hồ sơ</span>
      </template>
    </div>
    
    <!-- Loading state -->
    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <p>Đang tải hồ sơ...</p>
    </div>
    
    <!-- Error state -->
    <div v-else-if="error" class="error-container">
      <div class="error-message">
        <i class="fas fa-exclamation-triangle"></i>
        <p>{{ error }}</p>
        <button class="btn btn-primary" @click="loadUserProfile">Thử lại</button>
      </div>
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
import ProfileDetail from '@/components/layout/forum/profile/ProfileDetail.vue';
import profileApi from '@/api/modules/profileApi';

const route = useRoute();
const router = useRouter();
const store = useStore();

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

// Load user profile by username or userId
const loadUserProfile = async () => {
  if (!username.value) return;
  
  loading.value = true;
  error.value = null;
  
  try {
    // For current user, we can get more detailed info
    if (isMyProfile.value) {
      const profileData = await profileApi.getProfile();
      user.value = {
        ...profileData,
        username: profileData.userName || profileData.username,
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
        user.value = {
          ...profileData,
          username: profileData.userName || profileData.username,
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

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  
  .loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #e91e63;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 1rem;
  }
  
  p {
    color: #666;
    font-size: 1.1rem;
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-container {
  display: flex;
  justify-content: center;
  padding: 3rem;
  
  .error-message {
    text-align: center;
    background: #fff;
    padding: 2rem;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    max-width: 400px;
    
    i {
      font-size: 3rem;
      color: #f44336;
      margin-bottom: 1rem;
    }
    
    p {
      color: #666;
      margin: 1rem 0;
      font-size: 1.1rem;
    }
    
    .btn {
      margin-top: 1rem;
    }
  }
}
</style>