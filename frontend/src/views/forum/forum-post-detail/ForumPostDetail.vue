<template>
  <div class="post-detail-container" @click="closeAllMenus">
    <!-- Loading State -->
    <div v-if="isLoading" class="loading-state">
      <div class="spinner"></div>
      <p>{{ t('forumPostDetail.loading') }}</p>
    </div>

    <!-- Error State -->
    <div v-else-if="!isPostLoaded" class="error-state">
      <p>{{ t('forumPostDetail.notFound') }}</p>
      <button class="btn btn-primary" @click="goBackToForum">{{ t('forumPostDetail.backToForum') }}</button>
    </div>

    <!-- Content State -->
    <template v-else>
      <!-- Breadcrumb -->
      <div class="breadcrumb">
        <a href="#" @click.prevent="goBackToForum">{{ t('forumPostDetail.forum') }}</a>
        <i class="fas fa-chevron-right separator"></i>
        <span>{{ t('forumPostDetail.postDetail') }}</span>
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
                  {{ t('forumPostDetail.deletePost') }}
                </button>
                <button class="menu-item" @click="handleMenuItemClick('update')">
                  <i class="fas fa-edit"></i>
                  {{ t('forumPostDetail.editPost') }}
                </button>
              </template>
              <!-- Options for other users -->
              <template v-else>
                <button class="menu-item" @click="handleMenuItemClick('report')">
                  <i class="fas fa-flag"></i>
                  {{ t('forumPostDetail.reportPost') }}
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

      <!-- AI Sensei Reply Box -->
      <div class="ai-sensei-section" v-if="aiSenseiReply || aiSenseiLoading || aiSenseiError">
        <div class="ai-sensei-card">
          <div class="ai-sensei-header">
            <div class="ai-sensei-avatar">
               <img :src="ImagePaths.avatar.sensei" alt="Oboe Sensei" class="sensei-avatar">
            </div>
            <div class="ai-sensei-info">
              <h3 class="sensei-name">
                <i class="fas fa-robot"></i>
                Oboe Sensei
              </h3>
              <span class="sensei-badge">{{ t('forumPostDetail.aiTeacher') }}</span>
            </div>
          </div>
          
          <!-- Loading State -->
          <div v-if="aiSenseiLoading" class="ai-sensei-loading">
            <div class="spinner"></div>
            <p>Oboe Sensei đang suy nghĩ...</p>
          </div>
          
          <!-- Error State -->
          <div v-else-if="aiSenseiError" class="ai-sensei-error">
            <i class="fas fa-exclamation-triangle"></i>
            <p>{{ aiSenseiError }}</p>
          </div>
          
          <!-- Reply Content -->
          <div v-else-if="aiSenseiReply" class="ai-sensei-content">
            <div class="ai-sensei-reply">
              <p>{{ aiSenseiReply.reply }}</p>
            </div>
            <div class="ai-sensei-meta">
              <i class="fas fa-clock"></i>
              <span>{{ formatTimeAgo(new Date(aiSenseiReply.commentAt)) }}</span>
            </div>
          </div>
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
          <p>{{ t('forumPostDetail.loadingComments') }}</p>
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
                     :placeholder="t('forumPostDetail.replyTo', { username: reply.author.username })"
                     :disabled="isSubmittingReply[reply.id]"
                   ></textarea>
                   <div class="reply-form-actions">
                     <button @click="replyingTo = null; replyContent[reply.id] = ''" class="btn btn-secondary cancel-reply-btn">{{ t('forumPostDetail.cancel') }}</button>
                     <button 
                       @click="submitReply(reply.id)"
                       class="btn btn-primary submit-reply-btn"
                       :disabled="isSubmittingReply[reply.id] || !replyContent[reply.id]?.trim()"
                     >
                       <span v-if="isSubmittingReply[reply.id]">
                         <i class="fas fa-spinner fa-spin"></i> {{ t('forumPostDetail.sending') }}
                       </span>
                       <span v-else>
                         {{ t('forumPostDetail.sendReply') }}
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
                <i class="fas fa-spinner fa-spin"></i> {{ t('forumPostDetail.loading') }}
              </span>
              <span v-else>
                <i class="fas fa-comment-dots"></i> {{ t('forumPostDetail.loadMoreComments') }}
              </span>
            </button>
          </div>
          
          
       </div>

       <!-- Add Reply Form -->
      <div class="add-reply-card" v-if="currentUser">
        <h3 class="add-reply-header">{{ t('forumPostDetail.joinDiscussion') }}</h3>
        <div class="reply-form">
          <textarea 
            v-model="newCommentContent"
            class="reply-textarea" 
            :placeholder="t('forumPostDetail.writeComment')"
            :disabled="isSubmittingComment"
            rows="4"
          ></textarea>
          <button 
            @click="submitNewComment"
            class="btn btn-primary submit-reply-btn"
            :disabled="isSubmittingComment || !newCommentContent.trim()"
          >
            <span v-if="isSubmittingComment">
              <i class="fas fa-spinner fa-spin"></i> {{ t('forumPostDetail.sending') }}
            </span>
            <span v-else>
              <i class="fas fa-paper-plane"></i> {{ t('forumPostDetail.sendReply') }}
            </span>
          </button>
        </div>
      </div>
      
      <!-- Login prompt for non-authenticated users -->
      <div class="login-prompt" v-else>
        <p>{{ t('forumPostDetail.loginRequired') }}</p>
        <button @click="router.push('/login')" class="btn btn-primary">
          {{ t('forumPostDetail.login') }}
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
      :title="t('forumPostDetail.deleteConfirmTitle')"
      :message="t('forumPostDetail.deleteConfirmMessage')"
      :confirmText="t('forumPostDetail.delete')"
      @confirm="handleDeletePost"
      @cancel="showDeleteConfirm = false"
    />

    <ThePopup
      v-if="showLockConfirm"
      :title="isCommentsLocked ? t('forumPostDetail.unlockComments') : t('forumPostDetail.lockComments')"
      :message="isCommentsLocked ? t('forumPostDetail.unlockConfirmMessage') : t('forumPostDetail.lockConfirmMessage')"
      :confirmText="isCommentsLocked ? t('forumPostDetail.unlock') : t('forumPostDetail.lock')"
      @confirm="handleToggleComments"
      @cancel="showLockConfirm = false"
    />


    <!-- Report Confirmation Popup -->
    <ThePopup
      v-if="showReportConfirm"
      :title="t('forumPostDetail.reportConfirmTitle')"
      :message="t('forumPostDetail.reportConfirmMessage')"
      :confirmText="t('forumPostDetail.submitReport')"
      @confirm="handleSubmitReport"
      @cancel="showReportConfirm = false"
      style="z-index: 10000;"
    />

    <!-- Report Success Animation -->
    <div v-if="showReportSuccessAnimation" class="report-success-animation">
      <div class="success-content">
        <div class="success-icon">
          <i class="fas fa-check-circle"></i>
        </div>
        <div class="success-text">
          <h3>{{ t('forumPostDetail.reportSuccess') }}</h3>
          <p>{{ t('forumPostDetail.reportSuccessMessage') }}</p>
        </div>
      </div>
    </div>

    <!-- Report Dialog -->
    <div class="report-dialog" v-if="showReportDialog">
      <div class="report-dialog-content">
        <div class="dialog-header">
          <h3>{{ t('forumPostDetail.reportPostTitle') }}</h3>
          <button class="close-btn" @click="closeReportDialog">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <form @submit.prevent="validateAndConfirm" class="report-form">
          <div class="form-group">
            <label>{{ t('forumPostDetail.violationType') }}</label>
            <select v-model="reportData.type" required>
              <option value="">{{ t('forumPostDetail.selectViolationType') }}</option>
              <option value="spam">{{ t('forumPostDetail.spam') }}</option>
              <option value="inappropriate">{{ t('forumPostDetail.inappropriate') }}</option>
              <option value="harassment">{{ t('forumPostDetail.harassment') }}</option>
              <option value="copyright">{{ t('forumPostDetail.copyright') }}</option>
              <option value="violence">{{ t('forumPostDetail.violence') }}</option>
              <option value="hate_speech">{{ t('forumPostDetail.hateSpeech') }}</option>
              <option value="fake_news">{{ t('forumPostDetail.fakeNews') }}</option>
              <option value="other">{{ t('forumPostDetail.other') }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>{{ t('forumPostDetail.detailedDescription') }}</label>
            <textarea 
              v-model="reportData.reason"
              rows="4"
              :placeholder="t('forumPostDetail.reportPlaceholder')"
              required
            ></textarea>
          </div>

          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="closeReportDialog">
              {{ t('forumPostDetail.cancel') }}
            </button>
            <button type="submit" class="btn-submit">
              {{ t('forumPostDetail.continue') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ImagePaths } from '@/assets/img/imagePaths';
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useStore } from 'vuex';
import { useI18n } from 'vue-i18n';
import UserProfileCard from '@/components/layout/forum/profilecard/UserProfileCard.vue';
import { useFloating, autoUpdate, offset } from '@floating-ui/vue';
import ThePopup from '@/components/common/popup/ThePopup.vue';
import blogApi from '@/api/modules/blogApi';
import commentApi from '@/api/modules/commentApi';
import reportApi from '@/api/modules/reportApi';
import aiRepApi from '@/api/modules/aiRepApi';

const router = useRouter();
const route = useRoute();
const store = useStore();
const { t } = useI18n();
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

// AI Sensei reply state
const aiSenseiReply = ref(null);
const aiSenseiLoading = ref(false);
const aiSenseiError = ref(null);

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
    error.value = err.message || 'Không thể tải bài viết';
    blogPost.value = null;
  } finally {
    isLoading.value = false;
  }
};

// Fetch AI Sensei reply
const fetchAiSenseiReply = async (blogId) => {
  try {
    aiSenseiLoading.value = true;
    aiSenseiError.value = null;
    
    const response = await aiRepApi.replyToBlog(blogId);
    
    // API trả về format: {id, blogId, content, createdAt}
    aiSenseiReply.value = {
      id: response.id,
      reply: response.content, // content từ API -> reply cho UI
      commentAt: response.createdAt, // createdAt từ API -> commentAt cho UI
      blogId: response.blogId
    };
    
  } catch (err) {
    aiSenseiError.value = err.message || 'Không thể tải phản hồi từ Oboe Sensei';
    aiSenseiReply.value = null;
  } finally {
    aiSenseiLoading.value = false;
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
    
    // Check if there are more comments to load
    // Method 1: Using totalPages
    const hasMoreByPages = commentsPage.value + 1 < totalPages;
    
    // Method 2: Using totalElements (fallback)
    const hasMoreByElements = comments.value.length < totalElements;
    
    hasMoreComments.value = hasMoreByPages || hasMoreByElements;

    
    // Update post stats with total comment count
    if (blogPost.value && totalElements !== undefined) {
      blogPost.value.stats.replies = totalElements;
    }
    
  } catch (err) {
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
  if (interval > 1) return t('forum.timeAgo.years', { count: Math.floor(interval) });
  interval = seconds / 2592000;
  if (interval > 1) return t('forum.timeAgo.months', { count: Math.floor(interval) });
  interval = seconds / 86400;
  if (interval > 1) return t('forum.timeAgo.days', { count: Math.floor(interval) });
  interval = seconds / 3600;
  if (interval > 1) return t('forum.timeAgo.hours', { count: Math.floor(interval) });
  interval = seconds / 60;
  if (interval > 1) return t('forum.timeAgo.minutes', { count: Math.floor(interval) });
  return t('forum.timeAgo.seconds');
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
    
    // Load blog post first
    await fetchBlogPost(route.params.id);
    
    // Only fetch AI reply and comments if blog post loaded successfully
    if (blogPost.value) {
      await fetchAiSenseiReply(route.params.id);
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
const showReportSuccessAnimation = ref(false);

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

// Helper function to get report title based on type
const getReportTitle = (type) => {
  const reportTitles = {
    'spam': 'Spam / Quảng cáo',
    'inappropriate': 'Nội dung không phù hợp',
    'harassment': 'Quấy rối / Xúc phạm',
    'copyright': 'Vi phạm bản quyền',
    'violence': 'Bạo lực / Nguy hiểm',
    'hate_speech': 'Phát ngôn thù ghét',
    'fake_news': 'Thông tin sai lệch',
    'other': 'Khác'
  };
  return reportTitles[type] || 'Báo cáo vi phạm';
};

// Function to show success animation
const showReportSuccessAnimationFunc = () => {
  showReportSuccessAnimation.value = true;
  setTimeout(() => {
    showReportSuccessAnimation.value = false;
  }, 3000); // Hide animation after 3 seconds
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
      // Prepare report data for API
      const reportDto = {
        title: getReportTitle(reportData.value.type),
        content: reportData.value.reason,
        blogId: blogPost.value.id
      };

      // Call the reportApi.create method
      await reportApi.create(reportDto);

      // Show success animation
      showReportSuccessAnimationFunc();
      // Show success message
      store.dispatch('showMessage', {
        type: 'success',
        text: 'Cảm ơn bạn đã báo cáo. Chúng tôi sẽ xem xét và xử lý sớm nhất.'
      });
      // Close all report related dialogs after a short delay to show animation
      setTimeout(() => {
        closeReportDialog();
      }, 1500);
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
    store.dispatch('showMessage', {
      type: 'error',
      text: 'Có lỗi xảy ra khi cập nhật trạng thái bình luận'
    });
  }
  showLockConfirm.value = false;
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
</style>