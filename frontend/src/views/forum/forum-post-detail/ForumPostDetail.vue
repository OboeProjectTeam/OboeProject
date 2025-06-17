<template>
  <div class="post-detail-container" @click="closeAllMenus">
    <!-- Loading State -->
    <div v-if="isLoading" class="loading-state">
      <div class="spinner"></div>
      <p>Đang tải bài viết...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="!isPostLoaded" class="error-state">
      <p>Không tìm thấy bài viết hoặc đã có lỗi xảy ra.</p>
      <button class="btn btn-primary" @click="goBackToForum">Quay lại diễn đàn</button>
    </div>

    <!-- Content State -->
    <template v-else>
      <!-- Breadcrumb -->
      <div class="breadcrumb">
        <a href="#" @click.prevent="goBackToForum">Diễn đàn</a>
        <i class="fas fa-chevron-right separator"></i>
        <span>Chi tiết bài viết</span>
      </div>

      <!-- Post Header -->
      <div class="post-header">
        <h1 class="post-title">{{ postTitle }}</h1>
        <div class="post-meta-wrapper">
          <div class="post-meta" v-if="postAuthor">
            <img 
              :src="postAuthor.avatar" 
              :alt="postAuthor.username" 
              class="author-avatar"
              @click.stop="toggleUserCard($event, postAuthor)"
            >
            <div class="meta-info">
              <span class="author-name" @click.stop="toggleUserCard($event, postAuthor)">
                {{ postAuthor.username }}
              </span>
              <span class="post-time">Đăng {{ postAuthor.stats?.posted || 'vừa xong' }}</span>
            </div>
          </div>
          
          <!-- Three Dots Menu -->
          <div class="post-actions">
            <button class="btn-menu" @click.stop="togglePostMenu">
              <i class="fas fa-ellipsis-h"></i>
            </button>
            <!-- Dropdown Menu -->
            <div v-if="showPostMenu" class="post-menu-dropdown" @click.stop>
              <!-- Options for post owner -->
              <template v-if="isPostOwner">
                <button class="menu-item" @click="handleMenuItemClick('delete')">
                  <i class="fas fa-trash"></i>
                  Xóa bài viết
                </button>
                <button class="menu-item" @click="handleMenuItemClick('toggle-lock')">
                  <i class="fas" :class="isCommentsLocked ? 'fa-lock-open' : 'fa-lock'"></i>
                  {{ isCommentsLocked ? 'Mở khóa bình luận' : 'Khóa bình luận' }}
                </button>
              </template>
              <!-- Options for other users -->
              <template v-else>
                <button class="menu-item" @click="handleMenuItemClick('report')">
                  <i class="fas fa-flag"></i>
                  Báo cáo bài viết
                </button>
                <button class="menu-item" @click="handleMenuItemClick('hide')">
                  <i class="fas fa-eye-slash"></i>
                  Ẩn bài viết
                </button>
              </template>
            </div>
          </div>
        </div>
      </div>

      <!-- Post Content -->
      <div class="post-content-card">
        <div v-html="postContent"></div>
        
        <!-- Tags -->
        <div v-if="postTags.length > 0" class="post-tags">
          <span v-for="tag in postTags" :key="tag" class="tag">{{ tag }}</span>
        </div>
      </div>

      <!-- Replies Section -->
      <div class="replies-section">
        <h2 class="replies-header">{{ postStats.replies }} Trả lời</h2>
        
        <div class="reply-list">
          <!-- Reply Thread -->
          <div v-for="reply in replies" :key="reply.id" class="reply-thread">
            
            <!-- Main Reply -->
            <div class="reply-item">
              <img 
                :src="reply.author.avatar" 
                :alt="reply.author.username" 
                class="author-avatar"
                @click.stop="toggleUserCard($event, reply.author)"
              >
              <div class="reply-content-wrapper">
                <div class="reply-meta">
                  <span class="author-name" @click.stop="toggleUserCard($event, reply.author)">{{ reply.author.username }}</span>
                  <span class="post-time">{{ reply.time }}</span>
                </div>
                <div class="reply-content">
                  <p>{{ reply.text }}</p>
                </div>
                <div class="reply-actions">
                  <button @click="toggleReplyForm(reply.id)" class="btn-link">
                    <i class="fas fa-reply"></i> Trả lời
                  </button>
                </div>
              </div>
            </div>

            <!-- Reply Form (for main reply) -->
            <transition name="slide-fade">
              <div v-if="replyingTo === reply.id" class="nested-reply-form">
                <div class="reply-form">
                  <textarea class="reply-textarea" :placeholder="`Viết trả lời cho ${reply.author.username}...`"></textarea>
                  <div class="reply-form-actions">
                    <button class="btn btn-primary submit-reply-btn">Gửi</button>
                    <button @click="replyingTo = null" class="btn btn-secondary cancel-reply-btn">Hủy</button>
                  </div>
                </div>
              </div>
            </transition>

            <!-- Nested Replies -->
            <div class="nested-replies" v-if="reply.replies && reply.replies.length > 0">
              <div v-for="nestedReply in reply.replies" :key="nestedReply.id" class="reply-thread is-nested">
                <div class="reply-item">
                  <img 
                    :src="nestedReply.author.avatar" 
                    :alt="nestedReply.author.username" 
                    class="author-avatar"
                    @click.stop="toggleUserCard($event, nestedReply.author)"
                  >
                  <div class="reply-content-wrapper">
                    <div class="reply-meta">
                      <span class="author-name" @click.stop="toggleUserCard($event, nestedReply.author)">{{ nestedReply.author.username }}</span>
                      <span class="post-time">{{ nestedReply.time }}</span>
                    </div>
                    <div class="reply-content">
                      <p>{{ nestedReply.text }}</p>
                    </div>
                    <div class="reply-actions">
                      <button @click="toggleReplyForm(nestedReply.id)" class="btn-link">
                        <i class="fas fa-reply"></i> Trả lời
                      </button>
                    </div>
                  </div>
                </div>

                <!-- Reply Form (for nested reply) -->
                <transition name="slide-fade">
                  <div v-if="replyingTo === nestedReply.id" class="nested-reply-form">
                    <div class="reply-form">
                      <textarea class="reply-textarea" :placeholder="`Viết trả lời cho ${nestedReply.author.username}...`"></textarea>
                      <div class="reply-form-actions">
                        <button class="btn btn-primary submit-reply-btn">Gửi</button>
                        <button @click="replyingTo = null" class="btn btn-secondary cancel-reply-btn">Hủy</button>
                      </div>
                    </div>
                  </div>
                </transition>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Add Reply Form -->
      <div class="add-reply-card">
        <h3 class="add-reply-header">Tham gia thảo luận</h3>
        <div class="reply-form">
          <textarea class="reply-textarea" placeholder="Viết bình luận của bạn..."></textarea>
          <button class="btn btn-primary submit-reply-btn">Gửi trả lời</button>
        </div>
      </div>
    </template>

    <!-- User Profile Card -->
    <Teleport to="body">
      <UserProfileCard 
        ref="floatingEl"
        v-if="activeUserCard" 
        :user="activeUserCard" 
        :style="floatingStyles"
        @send-message="handleSendMessage"
      />
    </Teleport>

    <!-- Popup components -->
    <ThePopup
      v-if="showDeleteConfirm"
      title="Xóa bài viết"
      message="Bạn có chắc chắn muốn xóa bài viết này không? Hành động này không thể hoàn tác."
      confirmText="Xóa"
      @confirm="handleDeletePost"
      @cancel="showDeleteConfirm = false"
    />

    <ThePopup
      v-if="showLockConfirm"
      :title="isCommentsLocked ? 'Mở khóa bình luận' : 'Khóa bình luận'"
      :message="isCommentsLocked ? 'Bạn có chắc chắn muốn mở khóa bình luận cho bài viết này?' : 'Bạn có chắc chắn muốn khóa bình luận cho bài viết này?'"
      :confirmText="isCommentsLocked ? 'Mở khóa' : 'Khóa'"
      @confirm="handleToggleComments"
      @cancel="showLockConfirm = false"
    />

    <ThePopup
      v-if="showHideConfirm"
      title="Ẩn bài viết"
      message="Bạn có chắc chắn muốn ẩn bài viết này khỏi feed của bạn?"
      confirmText="Ẩn"
      @confirm="handleHidePost"
      @cancel="showHideConfirm = false"
    />

    <!-- Report Confirmation Popup -->
    <ThePopup
      v-if="showReportConfirm"
      title="Xác nhận báo cáo"
      message="Bạn có chắc chắn muốn gửi báo cáo này?"
      confirmText="Gửi báo cáo"
      @confirm="handleSubmitReport"
      @cancel="showReportConfirm = false"
    />

    <!-- Report Dialog -->
    <div class="report-dialog" v-if="showReportDialog">
      <div class="report-dialog-content">
        <div class="dialog-header">
          <h3>Báo cáo bài viết</h3>
          <button class="close-btn" @click="closeReportDialog">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <form @submit.prevent="validateAndConfirm" class="report-form">
          <div class="form-group">
            <label>Loại vi phạm</label>
            <select v-model="reportData.type" required>
              <option value="">Chọn loại vi phạm</option>
              <option value="spam">Spam / Quảng cáo</option>
              <option value="inappropriate">Nội dung không phù hợp</option>
              <option value="harassment">Quấy rối / Xúc phạm</option>
              <option value="copyright">Vi phạm bản quyền</option>
              <option value="violence">Bạo lực / Nguy hiểm</option>
              <option value="hate_speech">Phát ngôn thù ghét</option>
              <option value="fake_news">Thông tin sai lệch</option>
              <option value="other">Khác</option>
            </select>
          </div>

          <div class="form-group" v-if="reportData.type === 'inappropriate'">
            <label>Chi tiết nội dung không phù hợp</label>
            <select v-model="reportData.subType">
              <option value="adult">Nội dung người lớn</option>
              <option value="offensive">Từ ngữ khiếm nhã</option>
              <option value="sensitive">Nội dung nhạy cảm</option>
              <option value="graphic">Hình ảnh phản cảm</option>
            </select>
          </div>

          <div class="form-group">
            <label>Mô tả chi tiết</label>
            <textarea 
              v-model="reportData.reason"
              rows="4"
              placeholder="Vui lòng mô tả chi tiết lý do báo cáo..."
              required
            ></textarea>
          </div>

          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="closeReportDialog">
              Hủy
            </button>
            <button type="submit" class="btn-submit">
              Tiếp tục
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useStore } from 'vuex';
import UserProfileCard from '@/components/layout/forum/profilecard/UserProfileCard.vue';
import { useFloating, autoUpdate, offset } from '@floating-ui/vue';
import ThePopup from '@/components/common/popup/ThePopup.vue';

const router = useRouter();
const route = useRoute();
const store = useStore();
const replyingTo = ref(null);
const activeUserCard = ref(null);
const showPostMenu = ref(false);
const isCommentsLocked = ref(false);
const isLoading = ref(true);

// Floating UI setup
const referenceEl = ref(null);
const floatingEl = ref(null);
const { floatingStyles } = useFloating(referenceEl, floatingEl, {
  placement: 'bottom-start',
  whileElementsMounted: autoUpdate,
  middleware: [offset({ mainAxis: -10, crossAxis: 25 })]
});

// Get post data from store with default values
const currentPost = computed(() => store.getters['forum/getCurrentPost']);

// Demo current user for testing post ownership
const currentUser = ref({
  uid: 'current-user-123' // This should match the ID in CreateForumPost.vue
});

// Computed properties for post data
const postAuthor = computed(() => currentPost.value.author);
const postTitle = computed(() => currentPost.value.title);
const postContent = computed(() => currentPost.value.content);
const postCategory = computed(() => currentPost.value.category || 'Chi tiết bài viết');
const postTags = computed(() => currentPost.value.tags);
const postStats = computed(() => currentPost.value.stats);

// Check if post is loaded and has real data
const isPostLoaded = computed(() => currentPost.value.id !== null);

// Check if current user is post owner
const isPostOwner = computed(() => {
  return currentUser.value?.uid === postAuthor.value?.id;
});

// Load post data
onMounted(async () => {
  isLoading.value = true;
  try {
    if (route.params.id) {
      await store.dispatch('forum/getPost', route.params.id);
    }
  } catch (error) {
    console.error('Error loading post:', error);
  } finally {
    isLoading.value = false;
  }
});

// Menu actions
const togglePostMenu = () => {
  showPostMenu.value = !showPostMenu.value;
};

const closeAllMenus = () => {
  showPostMenu.value = false;
  activeUserCard.value = null;
};

// Control variables for popups
const showDeleteConfirm = ref(false);
const showLockConfirm = ref(false);
const showHideConfirm = ref(false);
const showReportDialog = ref(false);
const showReportConfirm = ref(false);

const reportData = ref({
  type: '',
  subType: '',
  reason: ''
});

// Update handleMenuItemClick
const handleMenuItemClick = (action) => {
  showPostMenu.value = false; // Close menu first
  switch (action) {
    case 'delete':
      showDeleteConfirm.value = true;
      break;
    case 'toggle-lock':
      showLockConfirm.value = true;
      break;
    case 'report':
      showReportDialog.value = true;
      break;
    case 'hide':
      showHideConfirm.value = true;
      break;
  }
};

const closeReportDialog = () => {
  showReportDialog.value = false;
  showReportConfirm.value = false;
  reportData.value = {
    type: '',
    subType: '',
    reason: ''
  };
};

const validateAndConfirm = () => {
  // Validate form
  if (!reportData.value.type || !reportData.value.reason) {
    store.dispatch('notification/show', {
      type: 'error',
      message: 'Vui lòng điền đầy đủ thông tin báo cáo'
    });
    return;
  }

  // If type is inappropriate, require subType
  if (reportData.value.type === 'inappropriate' && !reportData.value.subType) {
    store.dispatch('notification/show', {
      type: 'error',
      message: 'Vui lòng chọn chi tiết loại nội dung không phù hợp'
    });
    return;
  }

  // Show confirmation popup
  showReportConfirm.value = true;
};

const handleSubmitReport = async () => {
  try {
    // Here you would send the report to your backend
    await store.dispatch('forum/submitReport', {
      postId: currentPost.value.id,
      ...reportData.value
    });
    
    // Show success message
    store.dispatch('notification/show', {
      type: 'success',
      message: 'Cảm ơn bạn đã báo cáo. Chúng tôi sẽ xem xét và xử lý sớm nhất.'
    });
    
    // Close all report related dialogs
    closeReportDialog();
  } catch (error) {
    console.error('Error submitting report:', error);
    store.dispatch('notification/show', {
      type: 'error',
      message: 'Có lỗi xảy ra khi gửi báo cáo. Vui lòng thử lại sau.'
    });
  }
};

// Update action handlers to work with popups
const handleDeletePost = async () => {
  try {
    await store.dispatch('forum/deletePost', currentPost.value.id);
    router.push('/forum');
  } catch (error) {
    console.error('Error deleting post:', error);
  }
  showDeleteConfirm.value = false;
};

const handleToggleComments = async () => {
  try {
    await store.dispatch('forum/toggleLockComments', currentPost.value.id);
    isCommentsLocked.value = !isCommentsLocked.value;
  } catch (error) {
    console.error('Error toggling comments:', error);
    alert('Có lỗi xảy ra khi cập nhật trạng thái bình luận');
  }
  showLockConfirm.value = false;
};

const handleHidePost = () => {
  alert('Đã ẩn bài viết này khỏi feed của bạn');
  showHideConfirm.value = false;
};

const goBackToForum = () => {
  router.push('/forum');
};

const toggleUserCard = (event, user) => {
  if (!user) return;
  
  if (activeUserCard.value && activeUserCard.value.username === user.username) {
    activeUserCard.value = null;
    referenceEl.value = null;
  } else {
    referenceEl.value = event.currentTarget;
    activeUserCard.value = user;
  }
};

function handleSendMessage(user) {
  router.currentRoute.value.meta.emit?.('send-message', user);
}

const toggleReplyForm = (replyId) => {
  if (replyingTo.value === replyId) {
    replyingTo.value = null; // Close if already open for this reply
  } else {
    replyingTo.value = replyId; // Open for this reply
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/forum/forum-post-detail/ForumPostDetail.scss';
@use '@/assets/css/index.scss' ;
.post-meta-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.post-actions {
  position: relative;
}

.btn-menu {
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: #666;
  border-radius: 50%;
  
  &:hover {
    background-color: #f5f5f5;
  }
}

.post-menu-dropdown {
  position: absolute;
  right: 0;
  top: 100%;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  min-width: 180px;
  z-index: 1000;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 12px 16px;
  border: none;
  background: none;
  text-align: left;
  cursor: pointer;
  color: #333;
  
  &:hover {
    background-color: #f5f5f5;
  }
  
  i {
    width: 16px;
  }
}

.loading-state,
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  text-align: center;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid $primary-color;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-state {
  p {
    color: #666;
    margin-bottom: 16px;
  }
}

.report-dialog {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.report-dialog-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e2e8f0;
  
  h3 {
    margin: 0;
    font-size: 1.25rem;
    color: #2d3748;
  }
  
  .close-btn {
    background: none;
    border: none;
    color: #718096;
    cursor: pointer;
    padding: 8px;
    border-radius: 6px;
    
    &:hover {
      background: #f7fafc;
      color: #4a5568;
    }
  }
}

.report-form {
  padding: 20px;
  
  .form-group {
    margin-bottom: 20px;
    
    label {
      display: block;
      margin-bottom: 8px;
      font-weight: 500;
      color: #2d3748;
    }
    
    select, textarea {
      width: 100%;
      padding: 10px;
      border: 1px solid #e2e8f0;
      border-radius: 6px;
      font-size: 0.95rem;
      color: #4a5568;
      
      &:focus {
        outline: none;
        border-color: $primary-color;
        box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
      }
    }
    
    textarea {
      resize: vertical;
      min-height: 100px;
    }
  }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  
  button {
    padding: 10px 20px;
    border-radius: 6px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
    
    &.btn-cancel {
      background: #f7fafc;
      border: 1px solid #e2e8f0;
      color: #4a5568;
      
      &:hover {
        background: #edf2f7;
      }
    }
    
    &.btn-submit {
      background: $primary-color;
      border: none;
      color: white;
      
      &:hover {
        background: $hover-btn-color;
      }
    }
  }
}
</style> 