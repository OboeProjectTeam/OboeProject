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
                <button class="menu-item" @click="handleMenuItemClick('update')">
                  <i class="fas fa-edit"></i>
                  Sửa bài viết
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
          <i class="fas fa-tags"></i>
          <span v-for="tag in postTags" :key="tag" class="tag">{{ tag }}</span>
        </div>
      </div>

             <!-- Replies Section -->
       <div class="replies-section">
         <h2 class="replies-header">
           {{ postStats.replies }} Trả lời
           <span v-if="comments.length > 0 && postStats.replies > comments.length" class="showing-count">
             (hiển thị {{ comments.length }})
           </span>
         </h2>
        
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
             <div class="reply-item" :class="{ 'newly-created': reply.id === newlyCreatedCommentId }">
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
                   <div class="reply-item" :class="{ 'newly-created': nestedReply.id === newlyCreatedReplyId }">
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
                    </div>
                      </div>
                </div>
              </div>
            </transition>
          </div>
                 </div>
                     <!-- Load More Comments Button -->
           <div v-if="hasMoreComments || (comments.length > 0 && comments.length < postStats.replies)" class="load-more-comments">
            <button 
              @click="loadMoreComments"
              class="btn btn-outline-primary load-more-btn"
              :disabled="loadingMoreComments"
            >
              <span v-if="loadingMoreComments">
                <i class="fas fa-spinner fa-spin"></i> Đang tải...
              </span>
              <span v-else>
                <i class="fas fa-comment-dots"></i> Xem thêm bình luận
              </span>
            </button>
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
        :userId="activeUserCard.id" 
        :postId="blogPost?.id"
        :postTitle="blogPost?.title"
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

// Comments pagination state
const commentsPage = ref(0);
const commentsSize = ref(5); // Match backend default
const hasMoreComments = ref(false);
const loadingMoreComments = ref(false);

// Comment form state
const newCommentTitle = ref('');
const newCommentContent = ref('');
const isSubmittingComment = ref(false);

// Reply form state
const replyContent = ref({});
const isSubmittingReply = ref({});

// Highlight new comments
const newlyCreatedCommentId = ref(null);
const newlyCreatedReplyId = ref(null);

// API Functions
const fetchBlogPost = async (postId) => {
  try {
    isLoading.value = true;
    error.value = null;
    
    const response = await blogApi.getById(postId);

    
    blogPost.value = {
      id: response.id,
      title: response.title,
      content: response.content,
      author: {
        id: response.userId,
        username: response.author,
        avatar: response.avatarUrl
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

const fetchComments = async (postId, loadMore = false) => {
  try {
    if (!loadMore) {
      commentsLoading.value = true;
      commentsPage.value = 0; // Reset to first page
      comments.value = []; // Clear existing comments
    } else {
      loadingMoreComments.value = true;
    }
    

    
    // Use commentApi to get comments for the blog post with pagination
    const response = await commentApi.getComments(postId, commentsPage.value, commentsSize.value);


    console.log('Response structure:', {
      comments: response?.comments?.length || 0,
      totalPages: response?.totalPages,
      totalElements: response?.totalElements,
      currentPage: response?.currentPage,
      size: response?.size,
      numberOfElements: response?.numberOfElements
    });
    
    // Helper function to map a single comment with its nested replies
    const mapComment = (comment) => ({
      id: comment.commentId,
      author: {
        id: comment.userId,
        username: comment.userName || 'Anonymous',
        avatar: comment.avatarUrl
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
    
    if (loadMore) {
      // Append new comments to existing ones
      comments.value = [...comments.value, ...mappedComments];
    } else {
      // Replace comments (initial load)
      comments.value = mappedComments;
    }
    
    // Update pagination info
    const totalElements = response.totalElements || 0;
    let totalPages = response.totalPages || 0;
    
    // Fallback: calculate totalPages if not provided by API
    if (!totalPages && totalElements > 0) {
      totalPages = Math.ceil(totalElements / commentsSize.value);
    }
    
    console.log('Pagination info:', {
      currentPage: commentsPage.value,
      totalPages,
      totalElements,
      commentsLoaded: mappedComments.length,
      totalCommentsInState: comments.value.length,
      commentsSize: commentsSize.value
    });
    
    // Check if there are more comments to load
    // Method 1: Using totalPages
    const hasMoreByPages = commentsPage.value + 1 < totalPages;
    
    // Method 2: Using totalElements (fallback)
    const hasMoreByElements = comments.value.length < totalElements;
    
    console.log('hasMoreComments calculation:', {
      hasMoreByPages,
      hasMoreByElements,
      currentCommentsCount: comments.value.length,
      totalElements
    });
    
    hasMoreComments.value = hasMoreByPages || hasMoreByElements;

    
    // Update post stats with total comment count
    if (blogPost.value && totalElements !== undefined) {
      blogPost.value.stats.replies = totalElements;
    }
    
  } catch (err) {
    console.error('Error fetching comments:', err);
    if (!loadMore) {
      comments.value = [];
    }
  } finally {
    commentsLoading.value = false;
    loadingMoreComments.value = false;
  }
};

// Load more comments
const loadMoreComments = async () => {
  if (!blogPost.value || loadingMoreComments.value) {
    return;
  }
  
  // Safety check: don't load if we already have all comments
  if (comments.value.length >= postStats.value.replies) {

    hasMoreComments.value = false;
    return;
  }
  
  commentsPage.value += 1;
  await fetchComments(blogPost.value.id, true);
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


    
    // Call API to create comment
    const newComment = await commentApi.createComment(blogPost.value.id, commentData);

    
    // Track newly created comment for highlighting
    newlyCreatedCommentId.value = newComment.commentId;
    
    // Reset form first
    newCommentTitle.value = '';
    newCommentContent.value = '';
    
    // Reload comments from beginning to see the new comment
    // This ensures we get the latest data and proper pagination
    await fetchComments(blogPost.value.id, false);
    
    // Remove highlight after 5 seconds
    setTimeout(() => {
      newlyCreatedCommentId.value = null;
    }, 5000);
    
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


    
    // Call API to create reply
    const newReply = await commentApi.replyComment(parentCommentId, replyData);

    
    // Track newly created reply for highlighting  
    newlyCreatedReplyId.value = newReply.commentId;
    
    // Clear reply form
    replyContent.value[parentCommentId] = '';
    replyingTo.value = null;
    
    // Reload comments to see the new reply and maintain proper pagination
    await fetchComments(blogPost.value.id, false);
    
    // Remove highlight after 5 seconds
    setTimeout(() => {
      newlyCreatedReplyId.value = null;
    }, 5000);
    
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
  
  // Debug logs for post ownership




  
  // Try different field combinations for current user ID
  const currentUserId = currentUser.value.userId || currentUser.value.user_id || currentUser.value.id;
  const postAuthorId = blogPost.value.author.id;
  



  
  if (currentUserId && postAuthorId && currentUserId === postAuthorId) {

    return true;
  }
  
  // Fallback: Username comparison
  const currentUsername = currentUser.value.username || currentUser.value.userName;
  const postAuthorUsername = blogPost.value.author.username;
  

  
  if (currentUsername && postAuthorUsername && currentUsername === postAuthorUsername) {

    return true;
  }
  


  return false;
});

// Use comments from API instead of mock data
const replies = computed(() => comments.value);

// Load post data
onMounted(async () => {
  if (route.params.id) {
    // Clear any existing highlights when loading new post
    newlyCreatedCommentId.value = null;
    newlyCreatedReplyId.value = null;
    
    await fetchBlogPost(route.params.id);
    // Load comments after blog post is loaded
    if (blogPost.value) {
      await fetchComments(route.params.id, false);
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
    case 'update':
    case 'edit':
      handleEditPost();
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

// Handle edit post
const handleEditPost = () => {
  if (blogPost.value) {
    // Redirect to edit page with post ID
    router.push(`/forum/edit/${blogPost.value.id}`);
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
  
  // Get current user from store
  const currentUser = store.getters['auth/currentUser'];
  
  // Check if clicking on own avatar - prevent showing popup
  if (currentUser && user) {
    // Try different field combinations for comparison
    const currentUserId = currentUser.userId || currentUser.user_id;
    const clickedUserId = user.id || user.userId || user.user_id;
    

















    
         // Primary comparison: ID-based
     if (currentUserId && clickedUserId && currentUserId === clickedUserId) {

       return; // Don't show popup for own avatar
     }
     
     // Fallback comparison: Username-based
     const currentUsername = currentUser.username || currentUser.userName;
     const clickedUsername = user.username || user.userName;
     



     
     if (currentUsername && clickedUsername && currentUsername === clickedUsername) {

       return; // Don't show popup for own avatar
     }
     

  }
  
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

// Replies header styling
.replies-header {
  .showing-count {
    font-size: 14px;
    font-weight: 400;
    color: #6c757d;
    margin-left: 8px;
  }
}

// Load more comments styling
.load-more-comments {
  display: flex;
  justify-content: center;
  margin: 24px 0;
  
  .load-more-btn {
    padding: 12px 24px;
    font-size: 14px;
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.2s ease;
    min-width: 200px;
    
    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
    
    i {
      margin-right: 8px;
    }
    
    &:hover:not(:disabled) {
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 123, 255, 0.2);
    }
  }
}

// Newly created comment/reply highlight
.reply-item.newly-created {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.12), rgba(255, 142, 142, 0.06));
  border: 1px solid rgba(255, 107, 107, 0.25);
  border-radius: 12px;
  padding: 16px;
  margin: 8px 0;
  position: relative;
  animation: highlightPulse 4s ease-in-out;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.1);
  transition: all 0.3s ease-out;
  
  &::before {
    content: '';
    position: absolute;
    top: -1px;
    left: -1px;
    right: -1px;
    bottom: -1px;
    background: linear-gradient(45deg, rgba(255, 107, 107, 0.15), rgba(255, 142, 142, 0.08));
    border-radius: 12px;
    z-index: -1;
    animation: glowPulse 3s ease-in-out infinite alternate;
    opacity: 0.7;
  }
  
  &::after {
    content: '✨ Mới';
    position: absolute;
    top: 8px;
    right: 12px;
    background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
    color: white;
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 11px;
    font-weight: 600;
    letter-spacing: 0.5px;
    box-shadow: 0 2px 6px rgba(255, 107, 107, 0.3);
    animation: newBadgePulse 2s ease-in-out infinite alternate;
  }
  
  // Override the default reply-item padding since we're adding our own
  .reply-content-wrapper {
    margin-left: 0;
    padding-right: 60px; // Make space for "Mới" badge
  }
}

@keyframes highlightPulse {
  0% {
    background: rgba(255, 107, 107, 0.15);
    transform: scale(1.01);
  }
  50% {
    background: rgba(255, 107, 107, 0.08);
    transform: scale(1);
  }
  100% {
    background: rgba(255, 107, 107, 0.05);
    transform: scale(1);
  }
}

@keyframes glowPulse {
  0% {
    opacity: 0.3;
  }
  100% {
    opacity: 0.1;
  }
}

@keyframes newBadgePulse {
  0% {
    transform: scale(1);
    box-shadow: 0 2px 6px rgba(255, 107, 107, 0.3);
  }
  100% {
    transform: scale(1.05);
    box-shadow: 0 3px 8px rgba(255, 107, 107, 0.4);
  }
}
</style> 