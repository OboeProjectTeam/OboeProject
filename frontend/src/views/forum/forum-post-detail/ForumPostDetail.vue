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
              <span class="post-time">{{ postAuthor.stats?.posted || 'vừa xong' }}</span>
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
        <div class="post-content-text" v-html="postContent"></div>
        
        <!-- Tags -->
        <div v-if="postTags.length > 0" class="post-tags">
          <span v-for="tag in postTags" :key="tag" class="tag">{{ tag }}</span>
        </div>
      </div>

      <!-- Replies Section -->
      <div class="replies-section">
        <h2 class="replies-header">{{ postStats.replies }} Trả lời</h2>
        
        <!-- Comments Loading State -->
        <div v-if="commentsLoading" class="comments-loading">
          <div class="spinner"></div>
          <p>Đang tải bình luận...</p>
        </div>
        
        <!-- No Comments State -->
        <div v-else-if="comments.length === 0" class="no-comments">
          <p>Chưa có bình luận nào. Hãy là người đầu tiên bình luận!</p>
        </div>
        
        <div v-else class="reply-list">
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
                  <button 
                    v-if="reply.replies && reply.replies.length > 0" 
                    @click="toggleReplies(reply.id)" 
                    class="btn-link toggle-replies"
                  >
                    <i class="fas" :class="isRepliesShown(reply.id) ? 'fa-chevron-up' : 'fa-chevron-down'"></i>
                    {{ isRepliesShown(reply.id) ? 'Ẩn câu trả lời' : `Xem ${reply.replies.length} câu trả lời` }}
                  </button>
                </div>
              </div>
            </div>

                         <!-- Reply Form (for main reply) -->
             <transition name="slide-fade">
               <div v-if="replyingTo === reply.id" class="nested-reply-form">
                 <div class="reply-form">
                   <textarea 
                     v-model="replyContent[reply.id]"
                     class="reply-textarea" 
                     :placeholder="`Viết trả lời cho ${reply.author.username}...`"
                     :disabled="isSubmittingReply[reply.id]"
                   ></textarea>
                   <div class="reply-form-actions">
                     <button @click="replyingTo = null; replyContent[reply.id] = ''" class="btn btn-secondary cancel-reply-btn">Hủy</button>
                     <button 
                       @click="submitReply(reply.id)"
                       class="btn btn-primary submit-reply-btn"
                       :disabled="isSubmittingReply[reply.id] || !replyContent[reply.id]?.trim()"
                     >
                       <span v-if="isSubmittingReply[reply.id]">
                         <i class="fas fa-spinner fa-spin"></i> Đang gửi...
                       </span>
                       <span v-else>
                         Gửi trả lời
                       </span>
                     </button>
                   </div>
                 </div>
               </div>
             </transition>

            <!-- Nested Replies -->
            <transition name="slide-fade">
              <div v-if="reply.replies && reply.replies.length > 0 && isRepliesShown(reply.id)" class="nested-replies">
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
                       <!-- Không hiển thị nút trả lời cho comment cấp 2 -->
                    </div>
                                     </div>

                   <!-- Không có form trả lời cho comment cấp 2 -->
                </div>
              </div>
            </transition>
          </div>
        </div>
      </div>

      <!-- Add Reply Form -->
      <div class="add-reply-card" v-if="currentUser">
        <h3 class="add-reply-header">Tham gia thảo luận</h3>
        <div class="reply-form">
          <textarea 
            v-model="newCommentContent"
            class="reply-textarea" 
            placeholder="Viết bình luận của bạn..."
            :disabled="isSubmittingComment"
            rows="4"
          ></textarea>
          <button 
            @click="submitNewComment"
            class="btn btn-primary submit-reply-btn"
            :disabled="isSubmittingComment || !newCommentContent.trim()"
          >
            <span v-if="isSubmittingComment">
              <i class="fas fa-spinner fa-spin"></i> Đang gửi...
            </span>
            <span v-else>
              <i class="fas fa-paper-plane"></i> Gửi trả lời
            </span>
          </button>
        </div>
      </div>
      
      <!-- Login prompt for non-authenticated users -->
      <div class="login-prompt" v-else>
        <p>Bạn cần đăng nhập để có thể bình luận</p>
        <button @click="router.push('/login')" class="btn btn-primary">
          Đăng nhập
        </button>
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
import blogApi from '@/api/modules/blogApi';
import commentApi from '@/api/modules/commentApi';

const router = useRouter();
const route = useRoute();
const store = useStore();
const activeUserCard = ref(null);
const showPostMenu = ref(false);
const isCommentsLocked = ref(false);
const isLoading = ref(true);
const replyingTo = ref(null);

// API state
const blogPost = ref(null);
const comments = ref([]);
const error = ref(null);
const commentsLoading = ref(false);

// Comment form state
const newCommentTitle = ref('');
const newCommentContent = ref('');
const isSubmittingComment = ref(false);

// Reply form state
const replyContent = ref({});
const isSubmittingReply = ref({});

// API Functions
const fetchBlogPost = async (postId) => {
  try {
    isLoading.value = true;
    error.value = null;
    
    console.log('Fetching blog post:', postId);
    const response = await blogApi.getById(postId);
    console.log('Blog post response:', response);
    
    blogPost.value = {
      id: response.id,
      title: response.title,
      content: response.content,
      author: {
        id: response.userId,
        username: response.author,
        avatar: `https://ui-avatars.com/api/?name=${encodeURIComponent(response.author)}`
      },
      createdAt: response.createdAt,
      updatedAt: response.updatedAt,
      tags: response.tags ? response.tags.split(',').map(tag => tag.trim()) : [],
      topics: response.topics,
      stats: {
        replies: response.commentCount || 0
      }
    };
    
  } catch (err) {
    console.error('Error fetching blog post:', err);
    error.value = err.message || 'Không thể tải bài viết';
    blogPost.value = null;
  } finally {
    isLoading.value = false;
  }
};

const fetchComments = async (postId) => {
  try {
    commentsLoading.value = true;
    console.log('Fetching comments for post:', postId);
    
    // Use commentApi to get comments for the blog post
    const response = await commentApi.getComments(postId);
    console.log('Comments response:', response);
    
    // Helper function to map a single comment with its nested replies
    const mapComment = (comment) => ({
      id: comment.commentId,
      author: {
        id: comment.userId,
        username: comment.userName || 'Anonymous',
        avatar: `https://ui-avatars.com/api/?name=${encodeURIComponent(comment.userName || 'Anonymous')}`
      },
      time: formatTimeAgo(new Date(comment.createdAt)),
      text: comment.content,
      title: comment.title,
      // Map nested replies recursively
      replies: comment.replies ? comment.replies.map(reply => mapComment(reply)) : []
    });
    
    // Map comments to component format
    const commentsArray = response.comments || [];
    const mappedComments = commentsArray.map(comment => mapComment(comment));
    
    comments.value = mappedComments;
    
    // Update post stats with total comment count
    if (blogPost.value && response.totalElements !== undefined) {
      blogPost.value.stats.replies = response.totalElements;
    }
    
  } catch (err) {
    console.error('Error fetching comments:', err);
    comments.value = [];
  } finally {
    commentsLoading.value = false;
  }
};

// Submit new comment
const submitNewComment = async () => {
  if (!newCommentContent.value.trim()) {
    store.dispatch('showMessage', {
      type: 'error',
      text: 'Vui lòng nhập nội dung bình luận'
    });
    return;
  }

  if (!blogPost.value) {
    store.dispatch('showMessage', {
      type: 'error', 
      text: 'Không thể gửi bình luận lúc này'
    });
    return;
  }

  try {
    isSubmittingComment.value = true;
    
    // Prepare comment data
    const commentData = {
      title: newCommentTitle.value || 'Bình luận',
      content: newCommentContent.value.trim()
    };

    console.log('Submitting comment:', commentData);
    
    // Call API to create comment
    const newComment = await commentApi.createComment(blogPost.value.id, commentData);
    console.log('Comment created:', newComment);
    
    // Map new comment to component format
    const mappedNewComment = {
      id: newComment.commentId,
      author: {
        id: newComment.userId,
        username: newComment.userName || 'Anonymous',
        avatar: `https://ui-avatars.com/api/?name=${encodeURIComponent(newComment.userName || 'Anonymous')}`
      },
      time: formatTimeAgo(new Date(newComment.createdAt)),
      text: newComment.content,
      title: newComment.title,
      replies: []
    };
    
    // Add new comment to the beginning of the list
    comments.value.unshift(mappedNewComment);
    
    // Update comment count
    if (blogPost.value.stats) {
      blogPost.value.stats.replies = (blogPost.value.stats.replies || 0) + 1;
    }
    
    // Reset form
    newCommentTitle.value = '';
    newCommentContent.value = '';
    
    // Show success message
    store.dispatch('showMessage', {
      type: 'success',
      text: 'Bình luận đã được gửi thành công!'
    });
    
  } catch (err) {
    console.error('Error submitting comment:', err);
    store.dispatch('showMessage', {
      type: 'error',
      text: 'Có lỗi xảy ra khi gửi bình luận: ' + err.message
    });
  } finally {
    isSubmittingComment.value = false;
  }
};

// Submit reply to comment
const submitReply = async (parentCommentId) => {
  const replyText = replyContent.value[parentCommentId];
  
  if (!replyText || !replyText.trim()) {
    store.dispatch('showMessage', {
      type: 'error',
      text: 'Vui lòng nhập nội dung trả lời'
    });
    return;
  }

  try {
    isSubmittingReply.value[parentCommentId] = true;
    
    // Prepare reply data
    const replyData = {
      title: 'Trả lời',
      content: replyText.trim()
    };

    console.log('Submitting reply to comment:', parentCommentId, replyData);
    
    // Call API to create reply
    const newReply = await commentApi.replyComment(parentCommentId, replyData);
    console.log('Reply created:', newReply);
    
    // Map new reply to component format
    const mappedReply = {
      id: newReply.commentId,
      author: {
        id: newReply.userId,
        username: newReply.userName || 'Anonymous',
        avatar: `https://ui-avatars.com/api/?name=${encodeURIComponent(newReply.userName || 'Anonymous')}`
      },
      time: formatTimeAgo(new Date(newReply.createdAt)),
      text: newReply.content,
      title: newReply.title,
      replies: []
    };
    
    // Find parent comment and add reply to its replies
    const findAndAddReply = (commentsList, targetId, newReply) => {
      for (let comment of commentsList) {
        if (comment.id === targetId) {
          comment.replies.push(newReply);
          return true;
        }
        // Search in nested replies
        if (comment.replies && comment.replies.length > 0) {
          if (findAndAddReply(comment.replies, targetId, newReply)) {
            return true;
          }
        }
      }
      return false;
    };
    
    findAndAddReply(comments.value, parentCommentId, mappedReply);
    
    // Clear reply form
    replyContent.value[parentCommentId] = '';
    replyingTo.value = null;
    
    // Update total comment count
    if (blogPost.value.stats) {
      blogPost.value.stats.replies = (blogPost.value.stats.replies || 0) + 1;
    }
    
    // Show success message
    store.dispatch('showMessage', {
      type: 'success',
      text: 'Trả lời đã được gửi thành công!'
    });
    
  } catch (err) {
    console.error('Error submitting reply:', err);
    store.dispatch('showMessage', {
      type: 'error',
      text: 'Có lỗi xảy ra khi gửi trả lời: ' + err.message
    });
  } finally {
    isSubmittingReply.value[parentCommentId] = false;
  }
};

// Time formatting function
const formatTimeAgo = (date) => {
  const now = new Date();
  const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);
  let interval = seconds / 31536000;
  if (interval > 1) return Math.floor(interval) + " năm trước";
  interval = seconds / 2592000;
  if (interval > 1) return Math.floor(interval) + " tháng trước";
  interval = seconds / 86400;
  if (interval > 1) return Math.floor(interval) + " ngày trước";
  interval = seconds / 3600;
  if (interval > 1) return Math.floor(interval) + " giờ trước";
  interval = seconds / 60;
  if (interval > 1) return Math.floor(interval) + " phút trước";
  return "Vài giây trước";
};



// Floating UI setup
const referenceEl = ref(null);
const floatingEl = ref(null);
const { floatingStyles } = useFloating(referenceEl, floatingEl, {
  placement: 'bottom-start',
  whileElementsMounted: autoUpdate,
  middleware: [offset({ mainAxis: -10, crossAxis: 25 })]
});

// Get current user from store
const currentUser = computed(() => store.getters['auth/currentUser']);

// Computed properties for post data
const postAuthor = computed(() => {
  if (!blogPost.value) return null;
  
  return {
    ...blogPost.value.author,
    stats: {
      posted: formatTimeAgo(new Date(blogPost.value.createdAt))
    }
  };
});

const postTitle = computed(() => blogPost.value?.title || '');
const postContent = computed(() => blogPost.value?.content || '');
const postCategory = computed(() => blogPost.value?.topics || 'Chi tiết bài viết');
const postTags = computed(() => blogPost.value?.tags || []);
const postStats = computed(() => blogPost.value?.stats || { replies: 0 });

// Check if post is loaded and has real data
const isPostLoaded = computed(() => blogPost.value !== null && !error.value);

// Check if current user is post owner
const isPostOwner = computed(() => {
  if (!currentUser.value || !blogPost.value) return false;
  return currentUser.value.userId === blogPost.value.author.id;
});

// Use comments from API instead of mock data
const replies = computed(() => comments.value);

// Load post data
onMounted(async () => {
  if (route.params.id) {
    await fetchBlogPost(route.params.id);
    // Load comments after blog post is loaded
    if (blogPost.value) {
      await fetchComments(route.params.id);
    }
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
    if (blogPost.value) {
      // Here you would send the report to your backend
      // For now, just show success message since we don't have report API
      console.log('Report submitted for post:', blogPost.value.id, reportData.value);
      
      // Show success message
      store.dispatch('showMessage', {
        type: 'success',
        text: 'Cảm ơn bạn đã báo cáo. Chúng tôi sẽ xem xét và xử lý sớm nhất.'
      });
      
      // Close all report related dialogs
      closeReportDialog();
    }
  } catch (error) {
    console.error('Error submitting report:', error);
    store.dispatch('showMessage', {
      type: 'error',
      text: 'Có lỗi xảy ra khi gửi báo cáo. Vui lòng thử lại sau.'
    });
  }
};

// Update action handlers to work with popups
const handleDeletePost = async () => {
  try {
    if (blogPost.value) {
      await blogApi.delete(blogPost.value.id);
      store.dispatch('showMessage', {
        type: 'success',
        text: 'Bài viết đã được xóa thành công!'
      });
      router.push('/forum');
    }
  } catch (error) {
    console.error('Error deleting post:', error);
    store.dispatch('showMessage', {
      type: 'error',
      text: 'Không thể xóa bài viết: ' + error.message
    });
  }
  showDeleteConfirm.value = false;
};

const handleToggleComments = async () => {
  try {
    if (blogPost.value) {
      // For now, just toggle the local state since we don't have toggle API
      isCommentsLocked.value = !isCommentsLocked.value;
      store.dispatch('showMessage', {
        type: 'success',
        text: `Đã ${isCommentsLocked.value ? 'khóa' : 'mở khóa'} bình luận cho bài viết này.`
      });
    }
  } catch (error) {
    console.error('Error toggling comments:', error);
    store.dispatch('showMessage', {
      type: 'error',
      text: 'Có lỗi xảy ra khi cập nhật trạng thái bình luận'
    });
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
    // Clear reply content when closing
    replyContent.value[replyId] = '';
  } else {
    replyingTo.value = replyId; // Open for this reply
  }
};

const shownReplies = ref(new Set());

const toggleReplies = (replyId) => {
  if (shownReplies.value.has(replyId)) {
    shownReplies.value.delete(replyId);
  } else {
    shownReplies.value.add(replyId);
  }
};

const isRepliesShown = (replyId) => {
  return shownReplies.value.has(replyId);
};
</script>

<style lang="scss" scoped>
@use '@/views/forum/forum-post-detail/ForumPostDetail.scss';

// Additional styles for comments loading and empty states
.comments-loading, .no-comments {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
  color: #6c757d;
  
  .spinner {
    width: 24px;
    height: 24px;
    border: 3px solid #f3f3f3;
    border-top: 3px solid #007bff;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-right: 12px;
  }
  
  p {
    margin: 0;
    font-size: 16px;
  }
}

.no-comments {
  flex-direction: column;
  
  p {
    font-style: italic;
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

// Post content styling
.post-content-text {
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
  
  p {
    margin-bottom: 16px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

// Reply form styling
.reply-title-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  margin-bottom: 12px;
  font-family: inherit;
  transition: border-color 0.2s ease;
  
  &:focus {
    outline: none;
    border-color: #007bff;
    box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
  }
  
  &:disabled {
    background-color: #f8f9fa;
    color: #6c757d;
    cursor: not-allowed;
  }
  
  &::placeholder {
    color: #6c757d;
  }
}

.submit-reply-btn {
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
  
  .fa-spinner {
    margin-right: 8px;
  }
  
  .fa-paper-plane {
    margin-right: 8px;
  }
}

// Reply textarea styling
.reply-textarea {
  &:disabled {
    background-color: #f8f9fa;
    color: #6c757d;
    cursor: not-allowed;
    resize: vertical;
  }
}

// Login prompt styling
.login-prompt {
  background: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  margin-top: 20px;
  
  p {
    margin-bottom: 16px;
    color: #6c757d;
    font-size: 16px;
  }
  
  .btn {
    padding: 12px 24px;
    font-size: 14px;
    font-weight: 600;
  }
}
</style> 