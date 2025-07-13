<template>
  <div class="comment-section">
    <h2>Bình luận & Góp ý</h2>
    
    <!-- Form thêm bình luận -->
    <div class="comment-form">
      <textarea
        v-model="newComment"
        placeholder="Nhập bình luận của ..."
        rows="3"
      ></textarea>
      <button @click="submitComment" :disabled="!newComment.trim()">
        Gửi bình luận
      </button>
    </div>

    <!-- Danh sách bình luận -->
    <div class="comments-list">
      <div v-if="comments.length === 0" class="no-comments">
        Chưa có bình luận nào. Hãy là người đầu tiên bình luận!
      </div>
      <div v-else v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-header">
          <div class="user-info">
            <img :src="comment.userAvatar || '/default-avatar.png'" alt="User avatar" class="avatar">
            <span class="username">{{ comment.username }}</span>
          </div>
          <span class="timestamp">{{ formatDate(comment.timestamp) }}</span>
        </div>
        <p class="comment-content">{{ comment.content }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useStore } from 'vuex'

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

const store = useStore()
const newComment = ref('')

// Lấy comments từ store
const comments = computed(() => 
  store.getters['search/getCommentsByTypeAndId'](props.type, props.itemId)
)

// Hàm format ngày tháng
const formatDate = (timestamp) => {
  const date = new Date(timestamp)
  return new Intl.DateTimeFormat('vi-VN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

// Hàm gửi bình luận
const submitComment = async () => {
  if (!newComment.value.trim()) return

  const comment = {
    id: Date.now(),
    content: newComment.value,
    userId: store.state.user?.id || 'anonymous',
    username: store.state.user?.username || 'Người dùng ẩn danh',
    userAvatar: store.state.user?.avatar,
    timestamp: new Date().toISOString()
  }

  try {
    // Thêm comment vào store
    await store.dispatch('search/addComment', {
      type: props.type,
      itemId: props.itemId,
      comment
    })
    // Reset form
    newComment.value = ''
  } catch (error) {
    console.error('Lỗi khi thêm bình luận:', error)
  }
}
</script>

<style lang="scss" scoped>
@use "@/components/layout/comment/CommentSection.scss";
</style> 