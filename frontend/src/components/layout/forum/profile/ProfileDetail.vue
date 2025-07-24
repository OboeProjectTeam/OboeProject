<template>
  <div class="profile-page" v-if="user">
    <div class="profile-header bd-form">
        <div class="avatar-container">
          <img :src="avatarPreview || editableUser.avatarUrl || editableUser.avatar || 'https://ui-avatars.com/api/?name=' + (editableUser.userName || editableUser.username || 'User')" :alt="editableUser.username" class="profile-avatar-large">
          <div v-if="isEditing" class="avatar-upload-overlay">
            <label for="avatar-upload" class="avatar-upload-label">
              <i class="fas fa-camera"></i>
              <span>Thay đổi ảnh</span>
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
          <h1 class="username-large">{{ editableUser.username }}</h1>
          
          <p v-if="!isEditing" class="full-name-large">{{ editableUser.fullName }}</p>
          <input v-else type="text" v-model="editableUser.fullName" class="form-control mb-2" maxlength="50">

          <p v-if="!isEditing" class="title-large" >{{ editableUser.title }}</p>
        </div>
        <div class="profile-actions">
          <div v-if="isMyProfile">
            <template v-if="!isEditing">
              <button @click="startEditing" class="btn btn-primary"><i class="fas fa-pencil-alt"></i> Chỉnh sửa hồ sơ</button>
            </template>
            <template v-else>
              <button @click="saveProfile" class="btn btn-success"><i class="fas fa-save"></i> Lưu thay đổi</button>
              <button @click="cancelEditing" class="btn btn-secondary"><i class="fas fa-times"></i> Hủy</button>
            </template>
          </div>
          <template v-else>
            <button class="btn btn-primary" @click="handleSendMessage"><i class="fas fa-envelope"></i> Gửi tin nhắn</button>
          </template>
        </div>
    </div>
    
    <div v-if="isMyProfile" class="profile-notification bd-form">
      <i class="fas fa-info-circle"></i>
      <span>7 ngày trải nghiệm các tính năng AI miễn phí sau khi cập nhật đầy đủ thông tin hồ sơ.</span>
    </div>

    <div v-if="isMyProfile" class="personal-info-widget bd-form">
      <h3>Thông tin cá nhân</h3>
      <ul class="personal-info-list">
         <li v-for="field in personalFields" :key="field.key">
          <i :class="field.icon"></i>
          <span v-if="!isEditing">{{ editableUser[field.key] || 'Chưa cập nhật' }}</span>
          <input v-else :type="field.type" v-model="editableUser[field.key]" :placeholder="field.placeholder" class="form-control" :maxlength="field.maxlength">
        </li>
      </ul>
    </div>

    <div class="profile-body ">
      <div class="profile-sidebar">
        <div class="sidebar-widget bd-form">
          <h3>Giới thiệu</h3>
            <p v-if="!isEditing" class="bio-large">{{ editableUser.bio }}</p>
            <textarea v-else v-model="editableUser.bio" class="form-control" rows="5" maxlength="500"></textarea>
          
          <ul>
             <li>
                <i class="fas fa-globe"></i>
                <template v-if="!isEditing">
                  <a :href="editableUser.websiteUrl" target="_blank">{{ editableUser.website }}</a>
                </template>
                <input v-else type="text" v-model="editableUser.website" placeholder="Website" class="form-control" maxlength="100">
            </li>
            <li><i class="fas fa-calendar-alt"></i> Tham gia {{ editableUser.stats.joined }}</li>
          </ul>
        </div>
                  <div class="sidebar-widget bd-form">
            <h3>Thống kê</h3>
            <div v-if="statsLoading && isMyProfile" class="stats-loading">
              <p>Đang tải thống kê...</p>
            </div>
            <ul v-else class="stats-list">
              <li><span>Chủ đề</span> <strong>{{ userStats?.blogCount ?? editableUser.stats?.topics ?? 0 }}</strong></li>
              <li><span>Bình luận</span> <strong>{{ userStats?.commentCount ?? editableUser.stats?.solutions ?? 0 }}</strong></li>
              <li><span>Học liệu</span> <strong>{{ userStats?.flashCard ?? editableUser.stats?.learning_materials ?? 0 }}</strong></li>
            </ul>
          </div>
      </div>
      <div class="profile-main-content bd-form">
        <div class="activity-tabs">
          <button class="tab-button" :class="{ active: currentTab === 'all' }" @click="handleTabClick('all')">Tất cả</button>
          <button class="tab-button" :class="{ active: currentTab === 'post' }" @click="handleTabClick('post')">Chủ đề</button>
          <button class="tab-button" :class="{ active: currentTab === 'reply' }" @click="handleTabClick('reply')">Bình luận</button>
          <button class="tab-button" :class="{ active: currentTab === 'material' }" @click="handleTabClick('material')">Học Liệu</button>
        </div>

                  <div v-if="activitiesLoading && currentTab === 'all'" class="activities-loading">
            <p>Đang tải hoạt động...</p>
          </div>
          <div v-else-if="blogsLoading && currentTab === 'post'" class="activities-loading">
            <p>Đang tải bài viết...</p>
          </div>
          <div v-else-if="commentsLoading && currentTab === 'reply'" class="activities-loading">
            <p>Đang tải bình luận...</p>
          </div>
          <div v-else-if="flashcardsLoading && currentTab === 'material'" class="activities-loading">
            <p>Đang tải flashcards...</p>
          </div>
          <ul v-else class="activity-list">
            <li v-for="activity in paginatedActivities" :key="activity.id" class="activity-item">
              <div class="activity-icon">
                <i v-if="activity.type === 'post'" class="fas fa-file-alt" title="Chủ đề mới"></i>
                <i v-if="activity.type === 'reply'" class="fas fa-reply" title="Bình luận mới"></i>
                <i v-if="activity.type === 'answer'" class="fas fa-check-square" title="Câu trả lời"></i>
                <i v-if="activity.type === 'material'" class="fas fa-book" title="Học liệu"></i>
                <i v-if="activity.type === 'quiz'" class="fas fa-question-circle" title="Bài kiểm tra"></i>
                <i v-if="activity.type === 'flashcard'" class="fas fa-layer-group" title="Flashcard"></i>
              </div>
            <div class="activity-content">
              <div v-if="activity.type === 'post'">
                <div class="activity-title">
                   <router-link :to="activity.url">Đã tạo chủ đề: {{ activity.title }}</router-link>
                </div>
                 <div class="activity-meta">Trong mục {{ activity.topic }} • {{ activity.timestamp }}</div>
              </div>
              <div v-if="activity.type === 'reply'">
                 <div class="activity-title">
                   <router-link :to="activity.url">Đã bình luận trong: {{ activity.postTitle }}</router-link>
                </div>
                <div class="activity-snippet">"{{ activity.content_snippet }}"</div>
                 <div class="activity-meta">vào lúc {{ activity.timestamp }}</div>
              </div>
              <div v-if="activity.type === 'answer'">
                <div class="activity-title">
                   <router-link :to="activity.url">Đã trả lời câu hỏi: {{ activity.postTitle }}</router-link>
                </div>
                <div class="activity-snippet">"{{ activity.content_snippet }}"</div>
                 <div class="activity-meta">vào lúc {{ activity.timestamp }}</div>
              </div>
              <div v-if="activity.type === 'quiz'">
                <div class="activity-title">
                   <router-link :to="activity.url">Đã tạo Học Liệu {{ activity.title }}</router-link>
                </div>
                <div class="activity-snippet" v-if="activity.content_snippet">"{{ activity.content_snippet }}"</div>
              </div>
            </div>
          </li>
                        <li v-if="paginatedActivities.length === 0" class="activity-item">
                <p v-if="currentTab === 'all'">Không có hoạt động nào để hiển thị.</p>
                <p v-else-if="currentTab === 'post'">Chưa có bài viết nào được tạo.</p>
                <p v-else-if="currentTab === 'reply'">Chưa có bình luận nào.</p>
                <p v-else-if="currentTab === 'material'">Chưa có flashcard nào được tạo.</p>
                <p v-else>Không có dữ liệu để hiển thị.</p>
            </li>
          </ul>
        <div class="pagination-controls" v-if="totalPages > 1">
          <button @click="prevPage" :disabled="currentPage === 1" class="btn btn-secondary">Trước</button>
          <span class="page-info">Trang {{ currentPage }} / {{ totalPages }}</span>
          <button @click="nextPage" :disabled="currentPage === totalPages" class="btn btn-secondary">Sau</button>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="loading">
    Đang tải hồ sơ...
  </div>
</template>

<script setup>
  import { ref, computed, watch, onMounted } from 'vue';
  import { useStore } from 'vuex';
  import { useRouter } from 'vue-router';
  import authApi from '@/api/modules/authApi';
  import blogApi from '@/api/modules/blogApi';
  import commentApi from '@/api/modules/commentApi';
  import flashcardApi from '@/api/modules/flashcardApi';
  import statisticsApi from '@/api/modules/statisticsApi';

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

const isEditing = ref(false);
const editableUser = ref(JSON.parse(JSON.stringify(props.user)));

// Debug: Log initial user data
console.log('ProfileDetail - Initial props.user:', props.user);
console.log('ProfileDetail - Initial editableUser:', editableUser.value);
  const avatarPreview = ref(null);
  const userBlogs = ref([]);
  const userComments = ref([]);
  const userFlashcards = ref([]);
  const userStats = ref(null);
  const statsLoading = ref(false);
  const userActivities = ref([]);
  const activitiesLoading = ref(false);
  const blogsLoading = ref(false);
  const commentsLoading = ref(false);
  const flashcardsLoading = ref(false);

const personalFields = [
  { key: 'day_of_birth', icon: 'fas fa-birthday-cake', placeholder: 'Ngày sinh', type: 'text', maxlength: 20 },
  { key: 'phone', icon: 'fas fa-phone', placeholder: 'Số điện thoại', type: 'text', maxlength: 20 },
  { key: 'email', icon: 'fas fa-envelope', placeholder: 'Email', type: 'email', maxlength: 100 },
  { key: 'address', icon: 'fas fa-map-pin', placeholder: 'Địa chỉ', type: 'text', maxlength: 150 },
];

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
        text: 'Avatar đã được cập nhật thành công!'
      });
    } catch (error) {
      store.dispatch('showMessage', {
        type: 'error',
        text: 'Không thể tải lên avatar: ' + error.message
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
      text: 'Hồ sơ đã được cập nhật thành công!'
    });
    
    isEditing.value = false;
  } catch (error) {
    store.dispatch('showMessage', {
      type: 'error',
      text: 'Không thể cập nhật hồ sơ: ' + error.message
    });
  }
}

function cancelEditing() {
  isEditing.value = false;
  editableUser.value = JSON.parse(JSON.stringify(props.user));
  avatarPreview.value = null;
}

const currentTab = ref('all');
const currentPage = ref(1);
const itemsPerPage = ref(10);

  async function handleTabClick(tab) {
    currentTab.value = tab;
    
    if (tab === 'all') {
      await loadUserActivities();
    } else if (tab === 'post') {
      await loadUserBlogs();
    } else if (tab === 'reply') {
      await loadUserComments();
    } else if (tab === 'material') {
      await loadUserFlashcards();
    }
  }

  const filteredActivities = computed(() => {
    if (currentTab.value === 'all') {
      return userActivities.value;
    }

    if (currentTab.value === 'post') {
      return userBlogs.value;
    }

    if (currentTab.value === 'reply') {
      return userComments.value;
    }

    if (currentTab.value === 'material') {
      return userFlashcards.value;
    }

    // For other tabs, use fallback data
    if (!props.user || !props.user.activities) {
      return [];
    }

    return props.user.activities.filter(activity => activity.type === currentTab.value);
  });

const totalPages = computed(() => {
  return Math.ceil(filteredActivities.value.length / itemsPerPage.value);
});

const paginatedActivities = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return filteredActivities.value.slice(start, end);
});

function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
}

function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
}

  watch(currentTab, () => {
    currentPage.value = 1;
  });

  // Load user statistics
  async function loadUserStats() {
    if (!props.isMyProfile) return; // Only load stats for own profile
    
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

  // Load user activities
  async function loadUserActivities(page = 0, size = 10) {
    if (!props.isMyProfile) return; // Only load activities for own profile
    
    try {
      activitiesLoading.value = true;
      console.log('Loading user activities...');
      const response = await statisticsApi.getUserActivity(page, size);
      console.log('User activities loaded:', response);
      
      // Map activities to the expected format based on new API structure
      const activities = response.content.map(item => {
        const { type, data } = item;
        
        // Handle different activity types
        if (type === 'blog') {
          return {
            type: 'post',
            id: data.id,
            title: data.title,
            postTitle: data.title,
            content_snippet: data.content ? data.content.substring(0, 150) + '...' : '',
            topic: data.topics || data.tags || 'Chủ đề',
            timestamp: new Date(data.createdAt).toLocaleString('vi-VN'),
            url: `/forum/post/${data.id}`
          };
        } else if (type === 'comment') {
          return {
            type: 'reply',
            id: data.commentId,
            title: data.title || 'Bình luận',
            postTitle: data.title || 'Bình luận',
            content_snippet: data.content ? data.content.substring(0, 150) + '...' : '',
            topic: 'Bình luận',
            timestamp: new Date(data.createdAt).toLocaleString('vi-VN'),
            url: `/forum/post/${data.referenceId}#comment-${data.commentId}`
          };
        } else if (type === 'flashcard') {
          return {
            type: 'quiz',
            id: data.quizzesID,
            title: data.term,
            postTitle: data.term,
            content_snippet: data.description ? data.description.substring(0, 150) + '...' : '',
            topic: 'Bài kiểm tra',
            timestamp: data.createdAt ? new Date(data.createdAt).toLocaleString('vi-VN') : 'Không rõ',
            url: `/learn/quiz/${data.quizzesID}`
          };
        } else {
          // Fallback for unknown types
          return {
            type: type,
            id: data.id || data.commentId || data.quizzesID,
            title: data.title || 'Hoạt động',
            postTitle: data.title || 'Hoạt động',
            content_snippet: data.description || data.content ? (data.description || data.content).substring(0, 150) + '...' : '',
            topic: 'Hoạt động',
            timestamp: data.createdAt ? new Date(data.createdAt).toLocaleString('vi-VN') : 'Không rõ',
            url: '#'
          };
        }
      });
      
      userActivities.value = activities;
    } catch (error) {
      console.error('Failed to load user activities:', error);
      store.dispatch('showMessage', {
        type: 'error',
        text: 'Không thể tải hoạt động: ' + error.message
      });
      userActivities.value = [];
    } finally {
      activitiesLoading.value = false;
    }
  }

  // Load user blogs
  async function loadUserBlogs(page = 0, size = 10) {
    if (!props.isMyProfile) return; // Only load blogs for own profile
    
    try {
      blogsLoading.value = true;
      console.log('Loading user blogs...');
      const response = await blogApi.getUserBlogs(page, size);
      console.log('User blogs loaded:', response);
      
      // Handle different response formats
      const blogs = response.content || response.data || response;
      
      // Map blogs to the expected format
      const mappedBlogs = (Array.isArray(blogs) ? blogs : []).map(blog => ({
        type: 'post',
        id: blog.blogId || blog.id,
        title: blog.title,
        postTitle: blog.title,
        content_snippet: blog.content ? blog.content.substring(0, 150) + '...' : '',
        topic: blog.topics || blog.category || 'Chủ đề',
        timestamp: new Date(blog.createdAt).toLocaleString('vi-VN'),
        url: `/blog/${blog.blogId || blog.id}`
      }));
      
      userBlogs.value = mappedBlogs;
    } catch (error) {
      console.error('Failed to load user blogs:', error);
      store.dispatch('showMessage', {
        type: 'error',
        text: 'Không thể tải bài viết: ' + error.message
      });
      userBlogs.value = [];
    } finally {
      blogsLoading.value = false;
    }
  }

  // Load user comments
  async function loadUserComments(page = 0, size = 10) {
    if (!props.isMyProfile) return; // Only load comments for own profile
    
    try {
      commentsLoading.value = true;
      console.log('Loading user comments...');
      const response = await commentApi.getUserComments(page, size);
      console.log('User comments loaded:', response);
      
      // Handle different response formats
      const comments = response.content || response.data || response;
      
      // Map comments to the expected format
      const mappedComments = (Array.isArray(comments) ? comments : []).map(comment => ({
        type: 'reply',
        id: comment.commentId || comment.id,
        title: comment.blogTitle || comment.postTitle || 'Bình luận',
        postTitle: comment.blogTitle || comment.postTitle || 'Bình luận',
        content_snippet: comment.content ? comment.content.substring(0, 150) + '...' : '',
        topic: 'Bình luận',
        timestamp: new Date(comment.createdAt).toLocaleString('vi-VN'),
        url: `/blog/${comment.blogId || comment.postId}#comment-${comment.commentId || comment.id}`
      }));
      
      userComments.value = mappedComments;
    } catch (error) {
      console.error('Failed to load user comments:', error);
      store.dispatch('showMessage', {
        type: 'error',
        text: 'Không thể tải bình luận: ' + error.message
      });
      userComments.value = [];
    } finally {
      commentsLoading.value = false;
    }
  }

  // Load user flashcards
  async function loadUserFlashcards(page = 0, size = 10) {
    if (!props.isMyProfile) return; // Only load flashcards for own profile
    
    try {
      flashcardsLoading.value = true;
      console.log('Loading user flashcards...');
      const response = await flashcardApi.getUserFlashcards(page, size);
      console.log('User flashcards loaded:', response);
      
      // Handle different response formats
      const flashcards = response.content || response.data || response;
      
      // Map flashcards to the expected format
      const mappedFlashcards = (Array.isArray(flashcards) ? flashcards : []).map(flashcard => ({
        type: 'quiz',
        id: flashcard.flashcardId || flashcard.id,
        title: flashcard.title || flashcard.term || 'Flashcard',
        postTitle: flashcard.title || flashcard.term || 'Flashcard',
        content_snippet: flashcard.description ? flashcard.description.substring(0, 150) + '...' : `${flashcard.cardCount || 0} thẻ học`,
        topic: flashcard.category || flashcard.subject || 'Học liệu',
        timestamp: new Date(flashcard.createdAt).toLocaleString('vi-VN'),
        url: `/flashcard/${flashcard.flashcardId || flashcard.id}`
      }));
      
      userFlashcards.value = mappedFlashcards;
    } catch (error) {
      console.error('Failed to load user flashcards:', error);
      store.dispatch('showMessage', {
        type: 'error',
        text: 'Không thể tải flashcards: ' + error.message
      });
      userFlashcards.value = [];
    } finally {
      flashcardsLoading.value = false;
    }
  }

  // Load stats when component mounts and when user changes
  onMounted(() => {
    if (props.isMyProfile) {
      loadUserStats();
      // Load data based on current tab
      if (currentTab.value === 'all') {
        loadUserActivities();
      } else if (currentTab.value === 'post') {
        loadUserBlogs();
      } else if (currentTab.value === 'reply') {
        loadUserComments();
      } else if (currentTab.value === 'material') {
        loadUserFlashcards();
      }
    }
  });

  watch(() => props.user, () => {
    if (props.isMyProfile) {
      loadUserStats();
      if (currentTab.value === 'all') {
        loadUserActivities();
      } else if (currentTab.value === 'post') {
        loadUserBlogs();
      } else if (currentTab.value === 'reply') {
        loadUserComments();
      } else if (currentTab.value === 'material') {
        loadUserFlashcards();
      }
    }
  });

  function handleSendMessage() {
    if (router.currentRoute.value.meta.emit) {
      router.currentRoute.value.meta.emit('send-message', props.user);
    }
  }

</script>

  <style lang="scss" scoped>
  @use '@/components/layout/forum/profile/ProfileDetail.scss';
  
  .stats-loading, .activities-loading {
    text-align: center;
    padding: 20px;
    color: #666;
    font-style: italic;
  }
  </style> 