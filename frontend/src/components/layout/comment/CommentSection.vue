<template>
  <div class="comment-section">
    <h2>Bình luận & Góp ý</h2>
    
    <!-- Form thêm bình luận -->
    <div class="comment-form">
      <textarea
        v-model="newComment"
        placeholder="Nhập bình luận của bạn..."
        rows="3"
      ></textarea>
      <button @click="submitComment" :disabled="!newComment.trim() || isSubmitting">
        <i v-if="isSubmitting" class="fas fa-spinner"></i>
        <i v-else class="fas fa-paper-plane"></i>
        {{ isSubmitting ? 'Đang gửi...' : 'Gửi bình luận' }}
      </button>
    </div>

    <!-- Danh sách bình luận -->
    <div class="comments-list">
      <div v-if="isLoading" class="loading">
        Đang tải bình luận...
      </div>
      <div v-else-if="comments.length === 0" class="no-comments">
        Chưa có bình luận nào. Hãy là người đầu tiên bình luận!
      </div>
      <div v-else>
        <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
          <div class="comment-header">
            <div class="user-info">
              <img :src="comment.avatarUrl || '/default-avatar.png'" alt="User avatar" class="avatar">
              <span class="username">{{ comment.userName || 'Người dùng ẩn danh' }}</span>
            </div>
            <span class="timestamp">{{ formatDate(comment.createdAt) }}</span>
          </div>
          <p class="comment-content">{{ comment.content }}</p>
          
          <!-- Replies -->
          <div v-if="comment.replies && comment.replies.length > 0" class="replies">
            <div v-for="reply in comment.replies" :key="reply.commentId" class="reply-item">
              <div class="comment-header">
                <div class="user-info">
                  <img :src="reply.avatarUrl || '/default-avatar.png'" alt="User avatar" class="avatar">
                  <span class="username">{{ reply.userName || 'Người dùng ẩn danh' }}</span>
                </div>
                <span class="timestamp">{{ formatDate(reply.createdAt) }}</span>
              </div>
              <p class="comment-content">{{ reply.content }}</p>
            </div>
          </div>
        </div>
        
        <!-- Load more button -->
        <div v-if="hasMore" class="load-more">
          <button @click="loadMoreComments" :disabled="isLoadingMore">
            <i v-if="isLoadingMore" class="fas fa-spinner"></i>
            <i v-else class="fas fa-chevron-down"></i>
            {{ isLoadingMore ? 'Đang tải...' : 'Xem thêm bình luận' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import commentApi from '@/api/modules/commentApi'

const props = defineProps({
  type: {
    type: String,
    required: true,
    validator: (value) => ['word', 'kanji', 'grammar', 'sentence'].includes(value)
  },
  itemId: {
    type: [String, Number],
    required: true
  }
})

const newComment = ref('')
const comments = ref([])
const isLoading = ref(false)
const isSubmitting = ref(false)
const isLoadingMore = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const pageSize = ref(5)

// Computed property to check if there are more comments to load
const hasMore = computed(() => currentPage.value < totalPages.value - 1)

// Function to get team ID based on type and itemId
const getTeamId = () => {
  const teamId = `${props.type}_${props.itemId}`
  console.log('Generated teamId:', teamId) // Debug log
  console.log('Props type:', props.type, 'Props itemId:', props.itemId) // Debug log
  return teamId
}

// Function to format date
const formatDate = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return new Intl.DateTimeFormat('vi-VN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

// Function to load comments
const loadComments = async (page = 0, append = false) => {
  try {
    if (!append) {
      isLoading.value = true
    } else {
      isLoadingMore.value = true
    }
    
    console.log('Loading comments for itemId:', props.itemId) // Debug log
    console.log('Calling API with URL:', `/api/comments/${props.itemId}?page=${page}&size=${pageSize.value}`) // Debug log
    
    const response = await commentApi.getComments(props.itemId, page, pageSize.value)
    console.log('Comments API response:', response) // Debug log
    
    if (response && response.comments) {
      if (append) {
        comments.value = [...comments.value, ...response.comments]
      } else {
        comments.value = response.comments
      }
      
      currentPage.value = response.currentPage || page
      totalPages.value = Math.ceil((response.totalElements || 0) / pageSize.value)
    } else {
      console.log('No comments in response or invalid response structure')
      if (!append) {
        comments.value = []
      }
    }
  } catch (error) {
    console.error('Error loading comments:', error)
    console.error('Error details:', error.response?.data || error.message)
    if (!append) {
      comments.value = []
    }
  } finally {
    isLoading.value = false
    isLoadingMore.value = false
  }
}

// Function to load more comments
const loadMoreComments = () => {
  if (hasMore.value && !isLoadingMore.value) {
    loadComments(currentPage.value + 1, true)
  }
}

// Function to submit comment
const submitComment = async () => {
  if (!newComment.value.trim() || isSubmitting.value) return

  try {
    isSubmitting.value = true
    
    const commentData = {
      content: newComment.value.trim(),
      title: 'Bình luận' // Add default title
    }
    
    console.log('Submitting comment for itemId:', props.itemId) // Debug log
    await commentApi.createComment(props.itemId, commentData)
    
    // Reset form
    newComment.value = ''
    
    // Reload comments to show the new one
    await loadComments(0, false)
    
  } catch (error) {
    console.error('Error submitting comment:', error)
    alert('Có lỗi xảy ra khi gửi bình luận. Vui lòng thử lại.')
  } finally {
    isSubmitting.value = false
  }
}

// Load comments on component mount
onMounted(() => {
  loadComments()
})
</script>

<style lang="scss" scoped>
@use "@/components/layout/comment/CommentSection.scss";
</style>