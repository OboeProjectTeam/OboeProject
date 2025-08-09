<template>
  <div class="user-profile-card" @mousedown.stop>
    <div class="card-content">
      <!-- Loading state -->
      <div v-if="loading" class="card-loading">
        <div class="loading-spinner"></div>
        <p>{{ t('common.loadingInfo') }}</p>
      </div>
      
      <!-- Error state -->
      <div v-else-if="error" class="card-error">
        <p>{{ error }}</p>
        <button class="btn btn-secondary" @click="loadUserProfile">{{ t('common.retry') }}</button>
      </div>
      
      <!-- User profile content -->
      <div v-else-if="userProfile" class="card-body">
        <div class="card-header">
          <img :src="userProfile.avatarUrl" :alt="userProfile.fullName" class="profile-avatar">
          <div class="profile-info">
            <router-link :to="profileLink" class="username-link">
              <h2 class="username">{{ removeGmailSuffix(userProfile.fullName) }}</h2>
            </router-link>
            <p class="email">{{ removeGmailSuffix(userProfile.userName) }}</p>
            <div class="user-badges">
            </div>
          </div>
                     <div class="card-actions" v-if="!isCurrentUser">
             <button class="btn btn-primary" @click="$emit('send-message', userProfile)">
               <i class="fas fa-envelope"></i> {{ t('profile.sendMessage') }}
             </button>
           </div>
           <div class="card-actions" v-else>
             <div class="current-user-badge">
               <i class="fas fa-user"></i> {{ t('profile.thisIsYou') }}
             </div>
           </div>
        </div>
        
        <div class="card-details">
          <div class="location-info" v-if="userProfile.address">
            <i class="fas fa-map-marker-alt"></i> {{ userProfile.address }}
          </div>
          
          <div class="join-date" v-if="userProfile.create_at">
            <i class="fas fa-calendar-alt"></i> 
            {{ t('profile.joined') }}: {{ formatDate(userProfile.create_at) }}
          </div>
          
          <div class="birthday" v-if="userProfile.day_of_birth">
            <i class="fas fa-birthday-cake"></i> 
            {{ t('profile.birthday') }}: {{ formatDate(userProfile.day_of_birth) }}
          </div>
        </div>
        
        <div class="user-stats">
            <div class="stat-item">
              <div class="stat-label">{{ t('forum.posts') }}</div>
              <div class="stat-value">{{ userProfile.blogCount || 0 }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">{{ t('forum.comments') }}</div>
              <div class="stat-value">{{ userProfile.commentCount || 0 }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">{{ t('flashcard.materials') }}</div>
              <div class="stat-value">{{ userProfile.flashCardCount || 0 }}</div>
            </div>
          </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useI18n } from 'vue-i18n';
import profileApi from '@/api/modules/profileApi';

const store = useStore();
const { t } = useI18n();

const props = defineProps({
  userId: {
    type: String,
    required: true
  },
  postId: {
    type: [String, Number],
    default: null
  },
  postTitle: {
    type: String,
    default: null
  }
});

defineEmits(['send-message']);

// Reactive state
const userProfile = ref(null);
const loading = ref(false);
const error = ref(null);

// Load user profile function
const loadUserProfile = async () => {
  if (!props.userId) return;
  
  loading.value = true;
  error.value = null;
  
  try {
    const data = await profileApi.getUserProfileById(props.userId);
    userProfile.value = data;
  } catch (err) {
    error.value = t('profile.loadError');
    console.error('Error loading user profile:', err);
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

// Helper function to remove @gmail.com suffix
const removeGmailSuffix = (text) => {
  if (!text) return '';
  return text.replace(/@gmail\.com$/, '');
};

// Get current user from store
const currentUser = computed(() => store.getters['auth/currentUser']);

// Check if this is current user's profile
const isCurrentUser = computed(() => {
  if (!currentUser.value || !userProfile.value) return false;
  
  // Debug logs



  
  // Primary comparison: ID-based
  const currentUserId = currentUser.value.userId || currentUser.value.user_id || currentUser.value.id;
  const profileUserId = userProfile.value.user_id;
  

  
  if (currentUserId && profileUserId && currentUserId === profileUserId) {

    return true;
  }
  
  // Fallback comparison: Username-based
  const currentUsername = currentUser.value.username || currentUser.value.userName;
  const profileUsername = userProfile.value.userName;
  

  
  if (currentUsername && profileUsername && currentUsername === profileUsername) {

    return true;
  }
  

  return false;
});

// Profile link computed
const profileLink = computed(() => {
  if (!userProfile.value) return '#';
  
  let link = `/forum/u/${userProfile.value.userName}`;
  const queryParams = [];
  
  // Add userId to query params
  if (userProfile.value.user_id) {
    queryParams.push(`userId=${userProfile.value.user_id}`);
  }
  
  if (props.postId && props.postTitle) {
    queryParams.push(`fromPostId=${props.postId}`);
    queryParams.push(`fromPostTitle=${encodeURIComponent(props.postTitle)}`);
  }
  
  // Default fromSource is 'forum' since UserProfileCard is used in forum context
  queryParams.push(`fromSource=forum`);
  
  if (queryParams.length > 0) {
    link += `?${queryParams.join('&')}`;
  }
  
  return link;
});

// Watch userId changes
watch(() => props.userId, () => {
  loadUserProfile();
}, { immediate: true });

// Load on mount
onMounted(() => {
  loadUserProfile();
});
</script>

<style lang="scss" scoped>
@use '@/components/layout/forum/profilecard/UserProfileCard.scss';
</style>