<template>
  <div class="profile-page" v-if="user">
    <div class="profile-header bd-form">
        <div class="avatar-container">
                     <img :src="avatarPreview || editableUser.avatarUrl || editableUser.avatar || 'https://ui-avatars.com/api/?name=' + (editableUser.userName || editableUser.username || 'User')" :alt="editableUser.userName || editableUser.username" class="profile-avatar-large">
          <div v-if="isEditing" class="avatar-upload-overlay">
            <label for="avatar-upload" class="avatar-upload-label">
              <i class="fas fa-camera"></i>
              <span>{{ t('profile.changePhoto') }}</span>
            </label>
            <input 
              type="file" 
              id="avatar-upload" 
              @change="handleAvatarChange" 
              accept="image/*"
              class="avatar-upload-input"
            >
          </div>
        </div>
        <div class="profile-main-info">
                     <h1 class="username-large">{{ editableUser.userName || editableUser.username }}</h1>
          
          <p v-if="!isEditing" class="full-name-large">{{ editableUser.fullName }}</p>
          <input v-else type="text" v-model="editableUser.fullName" class="form-control mb-2" maxlength="50">

          <p v-if="!isEditing" class="title-large" >{{ editableUser.title }}</p>
        </div>
        <div class="profile-actions">
          <div v-if="props.isMyProfile">
            <template v-if="!isEditing">
              <button @click="startEditing" class="btn btn-primary"><i class="fas fa-pencil-alt"></i> {{ t('profile.editProfile') }}</button>
            </template>
            <template v-else>
              <button @click="saveProfile" class="btn btn-success"><i class="fas fa-save"></i> {{ t('common.save') }}</button>
              <button @click="cancelEditing" class="btn btn-secondary"><i class="fas fa-times"></i> {{ t('common.cancel') }}</button>
            </template>
          </div>
          <template v-else>
            <button class="btn btn-primary" @click="handleSendMessage"><i class="fas fa-envelope"></i> {{ t('profile.sendMessage') }}</button>
          </template>
        </div>
    </div>
    <!-- Basic personal info for all users -->
    <div class="personal-info-widget bd-form">
      <h3>{{ t('profile.personalInfo') }}</h3>
      <ul class="personal-info-list">
        <!-- Date of birth - editable for own profile -->
        <li v-if="editableUser.day_of_birth || props.isMyProfile">
          <i class="fas fa-birthday-cake"></i>
          <span v-if="!isEditing || !props.isMyProfile">{{ formatDate(editableUser.day_of_birth) || t('profile.notUpdated') }}</span>
          <input v-else type="date" v-model="editableUser.day_of_birth" class="form-control">
        </li>
        
        <!-- Email - always shown, not editable -->
        <li v-if="editableUser.email">
          <i class="fas fa-envelope"></i>
          <span>{{ editableUser.email }}</span>
        </li>
        
        <!-- Address - editable for own profile -->
        <li v-if="editableUser.address || props.isMyProfile">
          <i class="fas fa-map-marker-alt"></i>
          <span v-if="!isEditing || !props.isMyProfile">{{ editableUser.address || t('profile.notUpdated') }}</span>
          <input v-else type="text" v-model="editableUser.address" :placeholder="t('profile.address')" class="form-control" maxlength="100">
        </li>
        
        <!-- Phone - shown for all users, editable for own profile -->
        <li v-if="editableUser.phone || props.isMyProfile">
          <i class="fas fa-phone"></i>
          <span v-if="!isEditing || !props.isMyProfile">{{ editableUser.phone || t('profile.notUpdated') }}</span>
          <input v-else type="text" v-model="editableUser.phone" :placeholder="t('profile.phone')" class="form-control" maxlength="20">
        </li>
      </ul>
    </div>

    <div class="profile-body ">
      <div class="profile-sidebar">
        <div class="sidebar-widget bd-form">
          <h3>{{ t('profile.bio') }}</h3>
            <p v-if="!isEditing" class="bio-large">{{ editableUser.bio }}</p>
            <textarea v-else v-model="editableUser.bio" class="form-control" rows="5" maxlength="500"></textarea>
          
          <ul>
             <li>
                <i class="fas fa-globe"></i>
                <template v-if="!isEditing">
                  <a :href="editableUser.websiteUrl" target="_blank">{{ editableUser.website }}</a>
                </template>
                <input v-else type="text" v-model="editableUser.website" :placeholder="t('profile.website')" class="form-control" maxlength="100">
            </li>
            <li><i class="fas fa-calendar-alt"></i> {{ t('profile.joined') }} {{ editableUser.stats.joined }}</li>
          </ul>
        </div>
                  <div class="sidebar-widget bd-form">
            <h3>{{ t('profile.statistics') }}</h3>
            <div v-if="statsLoading && props.isMyProfile" class="stats-loading">
              <p>{{ t('profile.loadingStats') }}</p>
            </div>
            <ul v-else class="stats-list">
              <li><span>{{ t('profile.postsCount') }}</span> <strong>{{ userStats?.blogCount ?? editableUser.stats?.topics ?? 0 }}</strong></li>
              <li><span>{{ t('profile.repliesCount') }}</span> <strong>{{ userStats?.commentCount ?? editableUser.stats?.solutions ?? 0 }}</strong></li>
              <li><span>{{ t('profile.studyMaterials') }}</span> <strong>{{ userStats?.flashCard ?? editableUser.stats?.learning_materials ?? 0 }}</strong></li>
            </ul>
          </div>
      </div>
      <div class="profile-main-content bd-form">
        <div class="activity-tabs">
          <button class="tab-button" :class="{ active: currentTab === 'all' }" @click="handleTabClick('all')">{{ t('profile.allActivities') }}</button>
          <button class="tab-button" :class="{ active: currentTab === 'post' }" @click="handleTabClick('post')">{{ t('profile.posts') }}</button>
          <button class="tab-button" :class="{ active: currentTab === 'reply' }" @click="handleTabClick('reply')">{{ t('profile.replies') }}</button>
          <button class="tab-button" :class="{ active: currentTab === 'material' }" @click="handleTabClick('material')">{{ t('profile.studyMaterials') }}</button>
        </div>

                  <div v-if="activitiesLoading && currentTab === 'all'" class="activities-loading">
            <p>{{ t('profile.loadingActivities') }}</p>
          </div>
          <div v-else-if="blogsLoading && currentTab === 'post'" class="activities-loading">
            <p>{{ t('profile.loadingPosts') }}</p>
          </div>
          <div v-else-if="commentsLoading && currentTab === 'reply'" class="activities-loading">
            <p>{{ t('profile.loadingComments') }}</p>
          </div>
          <div v-else-if="flashcardsLoading && currentTab === 'material'" class="activities-loading">
            <p>{{ t('profile.loadingFlashcards') }}</p>
          </div>
          <ul v-else class="activity-list">
            <li v-for="activity in paginatedActivities" :key="activity.id" class="activity-item">
              <div class="activity-icon">
                <i v-if="activity.type === 'post'" class="fas fa-file-alt" :title="t('profile.newPost')"></i>
                <i v-if="activity.type === 'reply'" class="fas fa-reply" :title="t('profile.newReply')"></i>
                <i v-if="activity.type === 'material'" class="fas fa-book" :title="t('profile.studyMaterial')"></i>
              </div>
            <div class="activity-content">
              <div v-if="activity.type === 'post'">
                <div class="activity-title">
                   <router-link :to="activity.url">{{ t('profile.createdPost') }}: {{ activity.title }}</router-link>
                </div>
                 <div class="activity-meta">{{ t('profile.inCategory') }} {{ activity.topic }} • {{ activity.timestamp }}</div>
              </div>
              <div v-if="activity.type === 'reply'">
                 <div class="activity-title">
                   <router-link :to="activity.url">{{ t('profile.commentedIn') }}: {{ activity.postTitle }}</router-link>
                </div>
                <div class="activity-snippet">"{{ activity.content_snippet }}"</div>
                 <div class="activity-meta">{{ t('profile.at') }} {{ activity.timestamp }}</div>
              </div>
              <div v-if="activity.type === 'material'">
                <div class="activity-title">
                   <a @click.prevent="handleFlashcardClick(activity)" style="cursor: pointer; text-decoration: none; display: block; padding: 5px;">{{ t('profile.createdMaterial') }} {{ activity.title }}</a>
                </div>
                <div class="activity-snippet" v-if="activity.content_snippet">"{{ activity.content_snippet }}"</div>
              </div>
            </div>
          </li>
            <li v-if="paginatedActivities.length === 0" class="activity-item">
                <p v-if="currentTab === 'all'">{{ t('profile.noActivities') }}</p>
                <p v-else-if="currentTab === 'post'">{{ t('profile.noPosts') }}</p>
                <p v-else-if="currentTab === 'reply'">{{ t('profile.noReplies') }}</p>
                <p v-else-if="currentTab === 'material'">{{ t('profile.noMaterials') }}</p>
                <p v-else>{{ t('profile.noData') }}</p>
            </li>
          </ul>
        <div class="pagination-controls" v-if="totalPages > 1">
          <button @click="prevPage" :disabled="currentPage === 1" class="btn btn-secondary">{{ t('common.previous') }}</button>
          <span class="page-info">{{ t('common.page') }} {{ currentPage }} / {{ totalPages }}</span>
          <button @click="nextPage" :disabled="currentPage === totalPages" class="btn btn-secondary">{{ t('common.next') }}</button>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="loading">
    {{ t('profile.loadingProfile') }}
  </div>
</template>

<script setup>
  import { ref, computed, watch, onMounted } from 'vue';
  import { useStore } from 'vuex';
  import { useRouter } from 'vue-router';
  import { useI18n } from 'vue-i18n';
  import authApi from '@/api/modules/authApi';
  import blogApi from '@/api/modules/blogApi';
  import commentApi from '@/api/modules/commentApi';
  import flashcardApi from '@/api/modules/flashcardApi';
  import statisticsApi from '@/api/modules/statisticsApi';
  import profileApi from '@/api/modules/profileApi';
  import { handleApiError } from '@/api/apiUtils';

  const props = defineProps({
  user: {
      type: Object,
      required: true,
  },
  isMyProfile: {
      type: Boolean,
      default: false,
  },
  });

const router = useRouter();
const emit = defineEmits(['save-profile', 'send-message']);
const store = useStore();
const { t } = useI18n();

const isEditing = ref(false);
const editableUser = ref(JSON.parse(JSON.stringify(props.user)));

// Debug: Log initial user data


  const avatarPreview = ref(null);
  
  // Reactive variables
  const currentTab = ref('all');
  const currentPage = ref(1);
  const itemsPerPage = ref(10);
  const totalElements = ref(0);
  const totalPages = ref(0);
  
  // Loading states
  const activitiesLoading = ref(false);
  const blogsLoading = ref(false);
  const commentsLoading = ref(false);
  const flashcardsLoading = ref(false);
  const statsLoading = ref(false);
  
  // Data arrays - store all data and separate by type
  const allActivities = ref([]);
  const userActivities = ref([]);
  const userBlogs = ref([]);
  const userComments = ref([]);
  const userFlashcards = ref([]);
  const userStats = ref(null);
  
  // Pagination for each tab
  const tabPagination = ref({
    all: { page: 1, totalPages: 0, totalElements: 0 },
    post: { page: 1, totalPages: 0, totalElements: 0 },
    reply: { page: 1, totalPages: 0, totalElements: 0 },
    material: { page: 1, totalPages: 0, totalElements: 0 }
  });

// Only additional fields that can be edited (phone for now, since day_of_birth, email, address are already shown above)
// Additional editable fields for own profile only (currently empty since phone is handled separately)
const editableFields = [
  // Future additional fields can be added here
];

// Format date helper for display
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};

watch(() => props.user, (newUser) => {
  if (!isEditing.value) {
    editableUser.value = JSON.parse(JSON.stringify(newUser));
  }
}, { deep: true });

// Watch store user changes to update local state
const storeUser = computed(() => store.getters['auth/currentUser']);
watch(storeUser, (newStoreUser) => {
  if (props.isMyProfile && newStoreUser && !isEditing.value) {
    // Update local editableUser with store changes (like avatar updates)
    editableUser.value = { 
      ...editableUser.value, 
      ...newStoreUser,
      // Ensure both avatar properties are synced
      avatar: newStoreUser.avatarUrl || newStoreUser.avatar,
      avatarUrl: newStoreUser.avatarUrl || newStoreUser.avatar
    };
  }
}, { deep: true });

function startEditing() {
  isEditing.value = true;
}

async function handleAvatarChange(event) {
  const file = event.target.files[0];
  if (file) {
    try {
      // Upload avatar immediately when file is selected
      const response = await authApi.uploadAvatar(file);
      
      // Update preview with the returned URL
      avatarPreview.value = response.avatarUrl;
      
      // Update editable user avatar (both properties for compatibility)
      editableUser.value.avatarUrl = response.avatarUrl;
      editableUser.value.avatar = response.avatarUrl;
      
      // Update Vuex store with new avatar
      await store.dispatch('auth/updateUserAvatar', response.avatarUrl);
      
      // Show success message
      store.dispatch('showMessage', {
        type: 'success',
        text: t('profile.avatarUpdated')
      });
    } catch (error) {
      store.dispatch('showMessage', {
        type: 'error',
        text: t('profile.avatarUploadError') + ': ' + error.message
      });
      // Reset file input
      event.target.value = '';
    }
  }
}

async function saveProfile() {
  try {
    // Since avatar is uploaded separately, we only need to save other profile data
    const userData = { ...editableUser.value };
    
    // Call API to update profile
    await store.dispatch('updateProfile', userData);
    
    // Show success message
    store.dispatch('showMessage', {
      type: 'success',
      text: t('profile.profileUpdated')
    });
    
    isEditing.value = false;
  } catch (error) {
    store.dispatch('showMessage', {
      type: 'error',
      text: t('profile.profileUpdateError') + ': ' + error.message
    });
  }
}

function cancelEditing() {
  isEditing.value = false;
  editableUser.value = JSON.parse(JSON.stringify(props.user));
  avatarPreview.value = null;
}



  async function handleTabClick(tab) {
    currentTab.value = tab;
    
    // Update current page from tab-specific pagination
    currentPage.value = tabPagination.value[tab].page;
    totalElements.value = tabPagination.value[tab].totalElements;
    totalPages.value = tabPagination.value[tab].totalPages;
    
    // If no data loaded yet, load all activities
    if (allActivities.value.length === 0) {
      await loadAllActivities();
    }
  }

  const filteredActivities = computed(() => {
    const currentTabData = tabPagination.value[currentTab.value];
    const startIndex = (currentTabData.page - 1) * itemsPerPage.value;
    const endIndex = startIndex + itemsPerPage.value;
    
    if (currentTab.value === 'all') {
      return allActivities.value.slice(startIndex, endIndex);
    } else if (currentTab.value === 'post') {
      const posts = allActivities.value.filter(activity => activity.type === 'post');
      return posts.slice(startIndex, endIndex);
    } else if (currentTab.value === 'reply') {
      const replies = allActivities.value.filter(activity => activity.type === 'reply');
      return replies.slice(startIndex, endIndex);
    } else if (currentTab.value === 'material') {
      const materials = allActivities.value.filter(activity => activity.type === 'material');
      return materials.slice(startIndex, endIndex);
    }
    return [];
  });

// Use server-side pagination data
const paginatedActivities = computed(() => {
  return filteredActivities.value;
});

async function nextPage() {
  const currentTabData = tabPagination.value[currentTab.value];
  if (currentTabData.page < currentTabData.totalPages) {
    tabPagination.value[currentTab.value].page++;
    currentPage.value = tabPagination.value[currentTab.value].page;
  }
}

async function prevPage() {
  const currentTabData = tabPagination.value[currentTab.value];
  if (currentTabData.page > 1) {
    tabPagination.value[currentTab.value].page--;
    currentPage.value = tabPagination.value[currentTab.value].page;
  }
}

  watch(currentTab, () => {
    currentPage.value = 1;
  });

  // Load all user activities at once and categorize them
  async function loadAllActivities() {
    try {
      activitiesLoading.value = true;
      
      let allData = [];
      let currentPage = 0;
      let hasMoreData = true;
      
      // Load all pages of data
      while (hasMoreData) {
        let response;
        if (props.isMyProfile) {
          // For current user, use statistics API
          response = await statisticsApi.getUserActivity(currentPage, 50); // Larger page size
        } else {
          // For other users, use profile API with user ID
          const userId = props.user.user_id || props.user.userId;
          if (!userId) {
            throw new Error('User ID not found');
          }
          response = await profileApi.getUserActivities(userId, currentPage, 50);
        }
        
        if (response.content && response.content.length > 0) {
          allData = [...allData, ...response.content];
          currentPage++;
          hasMoreData = currentPage < response.totalPages;
        } else {
          hasMoreData = false;
        }
      }
      
      // Map all activities to the expected format
      const mappedActivities = await Promise.all(allData.map(async (item) => {
        const { type, data } = item;
        
        // Handle different activity types
        if (type === 'blog') {
          return {
            type: 'post',
            id: data.id,
            title: data.title,
            postTitle: data.title,
            content_snippet: data.content ? data.content.substring(0, 150) + '...' : '',
            topic: data.topics || data.tags || t('profile.topic'),
            timestamp: new Date(data.createdAt).toLocaleString('vi-VN'),
            url: `/forum/post/${data.id}`
          };
        } else if (type === 'comment') {
          // For comments, try to get the actual blog title
          let blogTitle = data.title || data.blogTitle || data.postTitle || t('profile.post');
          
          // If we have a referenceId, try to fetch the blog title
          const postId = data.referenceId || data.blogId || data.postId;
          if (postId) {
            try {
              const blogResponse = await blogApi.getById(postId);
              blogTitle = blogResponse.title || blogTitle;
            } catch (error) {
              console.warn(`Failed to fetch blog title for postId ${postId}:`, error);
              // Keep the original title if API call fails
            }
          }
          
          return {
            type: 'reply',
            id: data.commentId,
            title: `${t('profile.commentedIn')} "${blogTitle}"`,
            postTitle: blogTitle,
            content_snippet: data.content ? data.content.substring(0, 150) + '...' : '',
            topic: t('profile.comment'),
            timestamp: new Date(data.createdAt).toLocaleString('vi-VN'),
            url: `/forum/post/${postId}#comment-${data.commentId}`
          };
          
        } else if (type === 'flashcard') {
          // Construct URL with query parameters for FlashcardLearn
          const queryParams = new URLSearchParams({
            source: 'library',
            title: data.term || data.title || 'Flashcard',
            description: data.description || t('profile.flashcardSet'),
            setId: data.flashcardID || data.quizzesID || data.id,
            creatorName: data.creatorName || data.userName || props.user.userName || t('common.user'),
            creatorAvatar: data.creatorAvatar || data.avatarUrl || props.user.avatarUrl || props.user.avatar || 'https://ui-avatars.com/api/?name=User',
            createdAt: data.createdAt || new Date().toISOString()
          });
          
          const flashcardUrl = `/flashcard/learn?${queryParams.toString()}`;
          
          return {
            type: 'material',
            id: data.flashcardID || data.quizzesID,
            title: data.term,
            postTitle: data.term,
            content_snippet: data.description ? data.description.substring(0, 150) + '...' : '',
            topic: t('profile.studyMaterial'),
            timestamp: data.createdAt ? new Date(data.createdAt).toLocaleString('vi-VN') : t('common.unknown'),
            url: flashcardUrl,
            // Preserve cardItems for handleFlashcardClick
            cardItems: data.cardItems || []
          };
        } else {
          // Fallback for unknown types
          return {
            type: type,
            id: data.id || data.commentId || data.quizzesID,
            title: data.title || t('profile.activity'),
            postTitle: data.title || t('profile.activity'),
            content_snippet: data.description || data.content ? (data.description || data.content).substring(0, 150) + '...' : '',
            topic: t('profile.activity'),
            timestamp: data.createdAt ? new Date(data.createdAt).toLocaleString('vi-VN') : t('common.unknown'),
            url: '#'
          };
        }
      }));
      
      // Store all activities
      allActivities.value = mappedActivities;
      
      // Calculate pagination for each tab
      const posts = mappedActivities.filter(activity => activity.type === 'post');
      const replies = mappedActivities.filter(activity => activity.type === 'reply');
      const materials = mappedActivities.filter(activity => activity.type === 'material');
      
      // Update tab pagination
      tabPagination.value.all.totalElements = mappedActivities.length;
      tabPagination.value.all.totalPages = Math.ceil(mappedActivities.length / itemsPerPage.value);
      
      tabPagination.value.post.totalElements = posts.length;
      tabPagination.value.post.totalPages = Math.ceil(posts.length / itemsPerPage.value);
      
      tabPagination.value.reply.totalElements = replies.length;
      tabPagination.value.reply.totalPages = Math.ceil(replies.length / itemsPerPage.value);
      
      tabPagination.value.material.totalElements = materials.length;
      tabPagination.value.material.totalPages = Math.ceil(materials.length / itemsPerPage.value);
      
      // Update current tab pagination info
      const currentTabData = tabPagination.value[currentTab.value];
      totalElements.value = currentTabData.totalElements;
      totalPages.value = currentTabData.totalPages;
      
    } catch (error) {
      console.error('Failed to load all activities:', error);
      store.dispatch('showMessage', {
        type: 'error',
        text: t('profile.loadActivitiesError') + ': ' + error.message
      });
      allActivities.value = [];
      // Reset all pagination
      Object.keys(tabPagination.value).forEach(tab => {
        tabPagination.value[tab] = { page: 1, totalPages: 0, totalElements: 0 };
      });
    } finally {
      activitiesLoading.value = false;
    }
  }

  // Load user statistics
  async function loadUserStats() {
    // Only load detailed stats for own profile
    if (!props.isMyProfile) return;
    
    try {
      statsLoading.value = true;
      const stats = await statisticsApi.getUserStats();
      userStats.value = stats;
    } catch (error) {
      console.error('Failed to load user statistics:', error);
      userStats.value = null;
    } finally {
      statsLoading.value = false;
    }
  }

  // Load data when component mounts and when user changes
  onMounted(() => {
    // Load stats only for own profile
    if (props.isMyProfile) {
      loadUserStats();
    }
    
    // Load all activities
    loadAllActivities();
  });

  watch(() => props.user, () => {
    // Load stats only for own profile
    if (props.isMyProfile) {
      loadUserStats();
    }
    
    // Reset pagination and reload all activities
    Object.keys(tabPagination.value).forEach(tab => {
      tabPagination.value[tab] = { page: 1, totalPages: 0, totalElements: 0 };
    });
    currentPage.value = 1;
    
    // Load all activities
    loadAllActivities();
  });

  function handleSendMessage() {
    // Prepare user data for ChatBox
    const chatUser = {
      user_id: props.user.user_id || props.user.userId,
      userId: props.user.user_id || props.user.userId,
      id: props.user.user_id || props.user.userId,
      userName: props.user.userName || props.user.username,
      fullName: props.user.fullName,
      avatarUrl: props.user.avatarUrl || props.user.avatar,
      avatar: props.user.avatarUrl || props.user.avatar
    }
    // Emit to parent to open ChatBox
    emit('send-message', chatUser)
  }

  // Handle flashcard click - call API to get detailed data and navigate to FlashcardLearn
  async function handleFlashcardClick(activity) {
    try {
      // Extract flashcard ID from the activity URL or use activity id
      let flashcardId = null;
      
      if (activity.url && activity.url.includes('?')) {
        const urlParams = new URLSearchParams(activity.url.split('?')[1]);
        flashcardId = urlParams.get('setId') || urlParams.get('id');
      }
      
      // Fallback to activity id if no flashcardId in URL
      if (!flashcardId) {
        flashcardId = activity.id;
      }
      
      if (!flashcardId) {
        console.error('No flashcardId found for flashcard');
        store.dispatch('showMessage', {
          type: 'error',
          text: t('profile.flashcardIdError')
        });
        return;
      }
      
      // Call API to get detailed flashcard data
      const flashcardData = await flashcardApi.getById(flashcardId);
      
      if (!flashcardData || !flashcardData.cardItems || flashcardData.cardItems.length === 0) {
        console.error('No cardItems in flashcard data:', flashcardData);
        store.dispatch('showMessage', {
          type: 'error',
          text: t('profile.flashcardNoContent')
        });
        return;
      }
      
      // Convert API response to learning items format for FlashcardLearn
      const learningItems = flashcardData.cardItems.map(item => ({
        type: 'word',
        kanji: item.word || '',
        kana: '',
        meaning: item.meaning || '',
        content: item.word || '',
        backcontent: item.meaning || '',
        front: item.word || '',
        back: item.meaning || ''
      }));
      
      // Save to store for FlashcardLearn to use
      await store.dispatch('flashcard/setLearningItems', learningItems);
      
      // Navigate to FlashcardLearn with query parameters
      const navigationQuery = {
        source: 'profile',
        title: flashcardData.term || activity.title || 'Flashcard',
        description: flashcardData.description || `${t('profile.studyMaterialWith')} ${flashcardData.cardItems.length} ${t('profile.terms')}`,
        setId: flashcardData.flashcardID || flashcardId,
        creatorName: props.user.userName || t('common.user'),
        creatorAvatar: props.user.avatarUrl || props.user.avatar || 'https://ui-avatars.com/api/?name=User',
        createdAt: new Date().toISOString()
      };
      router.push({
        path: '/flashcard/learn',
        query: navigationQuery
      });
    } catch (error) {
      // Use handleApiError to get proper error message
      const errorMessage = handleApiError(error);
      
      store.dispatch('showMessage', {
        type: 'error',
        text: errorMessage
      });
    }
  }
</script>
  <style lang="scss" scoped>
  @use '@/components/layout/forum/profile/ProfileDetail.scss';
  </style>