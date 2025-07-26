<template>
  <div class="user-profile-card" @mousedown.stop>
    <div class="card-content">
      <!-- Loading state -->
      <div v-if="loading" class="card-loading">
        <div class="loading-spinner"></div>
        <p>Đang tải thông tin...</p>
      </div>
      
      <!-- Error state -->
      <div v-else-if="error" class="card-error">
        <p>{{ error }}</p>
        <button class="btn btn-secondary" @click="loadUserProfile">Thử lại</button>
      </div>
      
      <!-- User profile content -->
      <div v-else-if="userProfile" class="card-body">
        <div class="card-header">
          <img :src="userProfile.avatarUrl" :alt="userProfile.fullName" class="profile-avatar">
          <div class="profile-info">
            <router-link :to="profileLink" class="username-link">
              <h2 class="username">{{ userProfile.fullName }}</h2>
            </router-link>
            <p class="email">{{ userProfile.userName }}</p>
            <div class="user-badges">
            </div>
          </div>
                     <div class="card-actions" v-if="!isCurrentUser">
             <button class="btn btn-primary" @click="$emit('send-message', userProfile)">
               <i class="fas fa-envelope"></i> Gửi tin nhắn
             </button>
           </div>
           <div class="card-actions" v-else>
             <div class="current-user-badge">
               <i class="fas fa-user"></i> Đây là bạn
             </div>
           </div>
        </div>
        
        <div class="card-details">
          <div class="location-info" v-if="userProfile.address">
            <i class="fas fa-map-marker-alt"></i> {{ userProfile.address }}
          </div>
          
          <div class="join-date" v-if="userProfile.create_at">
            <i class="fas fa-calendar-alt"></i> 
            Tham gia: {{ formatDate(userProfile.create_at) }}
          </div>
          
          <div class="birthday" v-if="userProfile.day_of_birth">
            <i class="fas fa-birthday-cake"></i> 
            Sinh nhật: {{ formatDate(userProfile.day_of_birth) }}
          </div>
        </div>
        
        <div class="user-stats">
          <div class="stat-item">
            <div class="stat-label">Bài viết</div>
            <div class="stat-value">{{ userProfile.blogCount || 0 }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">Bình luận</div>
            <div class="stat-value">{{ userProfile.commentCount || 0 }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">Học Liệu</div>
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
import profileApi from '@/api/modules/profileApi';

const store = useStore();

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
    error.value = 'Không thể tải thông tin người dùng';
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

.card-loading, .card-error {
  text-align: center;
  padding: 2rem;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #e91e63;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.card-details {
  margin: 1rem 0;
  
  > div {
    margin-bottom: 0.5rem;
    color: #666;
    font-size: 0.9rem;
    
    i {
      margin-right: 0.5rem;
      width: 16px;
    }
  }
}

.user-badges {
  margin-top: 0.5rem;
  
  .badge {
    display: inline-block;
    padding: 0.25rem 0.5rem;
    border-radius: 12px;
    font-size: 0.75rem;
    font-weight: 500;
    
    &.verified-badge {
      background-color: #e3f2fd;
      color: #1976d2;
      
      i {
        margin-right: 0.25rem;
      }
    }
  }
}

.email {
  color: #666;
  font-size: 0.9rem;
  margin: 0.25rem 0;
}

.user-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
  
  .stat-item {
    text-align: center;
    
    .stat-label {
      font-size: 0.8rem;
      color: #666;
      margin-bottom: 0.25rem;
    }
    
    .stat-value {
      font-size: 1.2rem;
      font-weight: 600;
      color: #e91e63;
    }
  }
}

.current-user-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #4caf50, #45a049);
  color: white;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
  box-shadow: 0 2px 6px rgba(76, 175, 80, 0.3);
  
  i {
    margin-right: 0.5rem;
    font-size: 0.8rem;
  }
}
</style> 