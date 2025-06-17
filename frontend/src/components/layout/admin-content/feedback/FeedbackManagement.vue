<template>
  <div class="feedback-management">
    <div class="filters">
      <div class="search-box">
        <i class="fas fa-search"></i>
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="Tìm kiếm phản hồi..."
          @input="handleSearch"
        >
      </div>
      
      <div class="filter-options">
        <select v-model="statusFilter">
          <option value="">Tất cả trạng thái</option>
          <option value="new">Mới</option>
          <option value="in_progress">Đang xử lý</option>
          <option value="resolved">Đã xử lý</option>
          <option value="closed">Đã đóng</option>
        </select>
        
        <select v-model="categoryFilter">
          <option value="">Tất cả danh mục</option>
          <option value="bug">Lỗi kỹ thuật</option>
          <option value="feature">Đề xuất tính năng</option>
          <option value="content">Nội dung</option>
          <option value="other">Khác</option>
        </select>
      </div>
    </div>

    <div class="feedback-list">
      <div 
        v-for="feedback in filteredFeedback" 
        :key="feedback.id"
        class="feedback-item"
        :class="feedback.status"
      >
        <div class="feedback-header">
          <div class="feedback-meta">
            <span class="feedback-id">#{{ feedback.id }}</span>
            <span class="category-badge" :class="feedback.category">
              {{ getCategoryName(feedback.category) }}
            </span>
            <span class="status-badge" :class="feedback.status">
              {{ getStatusName(feedback.status) }}
            </span>
          </div>
          <div class="feedback-date">
            {{ formatDate(feedback.createdAt) }}
          </div>
        </div>

        <div class="feedback-content">
          <div class="user-info">
            <img :src="feedback.user.avatar" :alt="feedback.user.name">
            <div>
              <span class="user-name">{{ feedback.user.name }}</span>
              <span class="user-email">{{ feedback.user.email }}</span>
            </div>
          </div>

          <div class="feedback-message">
            <h4>{{ feedback.title }}</h4>
            <p>{{ feedback.message }}</p>
          </div>
        </div>

        <div class="feedback-actions">
          <div class="status-actions">
            <button 
              v-if="feedback.status === 'new'"
              class="btn-start"
              @click="startProcessing(feedback)"
            >
              <i class="fas fa-play"></i>
              Bắt đầu xử lý
            </button>
            <button 
              v-if="feedback.status === 'in_progress'"
              class="btn-resolve"
              @click="resolveFeedback(feedback)"
            >
              <i class="fas fa-check"></i>
              Đánh dấu đã xử lý
            </button>
            <button 
              v-if="['new', 'in_progress'].includes(feedback.status)"
              class="btn-close"
              @click="closeFeedback(feedback)"
            >
              <i class="fas fa-times"></i>
              Đóng phản hồi
            </button>
          </div>
          
          <button 
            class="btn-reply"
            @click="replyToFeedback(feedback)"
          >
            <i class="fas fa-reply"></i>
            Trả lời
          </button>
        </div>

        <!-- Feedback Replies -->
        <div class="feedback-replies" v-if="feedback.replies?.length">
          <div 
            v-for="reply in feedback.replies" 
            :key="reply.id"
            class="reply-item"
          >
            <div class="reply-header">
              <div class="user-info">
                <img :src="reply.user.avatar" :alt="reply.user.name">
                <div>
                  <span class="user-name">{{ reply.user.name }}</span>
                  <span class="user-role" v-if="reply.user.role === 'admin'">Admin</span>
                </div>
              </div>
              <span class="reply-date">{{ formatDate(reply.createdAt) }}</span>
            </div>
            <div class="reply-content">
              {{ reply.message }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="pagination">
      <button 
        :disabled="currentPage === 1"
        @click="currentPage--"
      >
        <i class="fas fa-chevron-left"></i>
      </button>
      <span>Trang {{ currentPage }} / {{ totalPages }}</span>
      <button 
        :disabled="currentPage === totalPages"
        @click="currentPage++"
      >
        <i class="fas fa-chevron-right"></i>
      </button>
    </div>

    <!-- Reply Modal -->
    <div class="modal" v-if="showReplyModal">
      <div class="modal-content">
        <h3>Trả lời phản hồi</h3>
        <form @submit.prevent="submitReply">
          <div class="form-group">
            <label>Tin nhắn</label>
            <textarea 
              v-model="replyMessage"
              placeholder="Nhập nội dung trả lời..."
              rows="4"
            ></textarea>
          </div>
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="showReplyModal = false">
              Hủy
            </button>
            <button type="submit" class="btn-save">
              Gửi trả lời
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

// Mock data - replace with API calls
const feedbackList = ref([
  {
    id: 1,
    title: 'Lỗi không thể đăng nhập',
    message: 'Tôi không thể đăng nhập vào hệ thống sau khi cập nhật mật khẩu mới.',
    category: 'bug',
    status: 'new',
    createdAt: '2024-03-15T10:30:00',
    user: {
      id: 1,
      name: 'John Doe',
      email: 'john@example.com',
      avatar: 'https://i.pravatar.cc/150?u=john'
    },
    replies: [
      {
        id: 1,
        message: 'Cảm ơn bạn đã báo cáo. Chúng tôi sẽ kiểm tra và phản hồi sớm.',
        createdAt: '2024-03-15T11:00:00',
        user: {
          id: 2,
          name: 'Admin User',
          role: 'admin',
          avatar: 'https://i.pravatar.cc/150?u=admin'
        }
      }
    ]
  },
  // Add more mock feedback...
]);

const searchQuery = ref('');
const statusFilter = ref('');
const categoryFilter = ref('');
const currentPage = ref(1);
const itemsPerPage = 10;
const showReplyModal = ref(false);
const replyMessage = ref('');
const selectedFeedback = ref(null);

// Computed properties
const filteredFeedback = computed(() => {
  let result = feedbackList.value;
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(feedback => 
      feedback.title.toLowerCase().includes(query) ||
      feedback.message.toLowerCase().includes(query) ||
      feedback.user.name.toLowerCase().includes(query)
    );
  }
  
  if (statusFilter.value) {
    result = result.filter(feedback => feedback.status === statusFilter.value);
  }
  
  if (categoryFilter.value) {
    result = result.filter(feedback => feedback.category === categoryFilter.value);
  }
  
  return result;
});

const totalPages = computed(() => 
  Math.ceil(filteredFeedback.value.length / itemsPerPage)
);

// Methods
const getCategoryName = (category) => {
  const categories = {
    bug: 'Lỗi kỹ thuật',
    feature: 'Đề xuất tính năng',
    content: 'Nội dung',
    other: 'Khác'
  };
  return categories[category] || category;
};

const getStatusName = (status) => {
  const statuses = {
    new: 'Mới',
    in_progress: 'Đang xử lý',
    resolved: 'Đã xử lý',
    closed: 'Đã đóng'
  };
  return statuses[status] || status;
};

const formatDate = (date) => {
  return new Date(date).toLocaleString('vi-VN');
};

const handleSearch = () => {
  currentPage.value = 1;
};

const startProcessing = (feedback) => {
  const index = feedbackList.value.findIndex(f => f.id === feedback.id);
  if (index !== -1) {
    feedbackList.value[index] = {
      ...feedback,
      status: 'in_progress'
    };
  }
};

const resolveFeedback = (feedback) => {
  const index = feedbackList.value.findIndex(f => f.id === feedback.id);
  if (index !== -1) {
    feedbackList.value[index] = {
      ...feedback,
      status: 'resolved'
    };
  }
};

const closeFeedback = (feedback) => {
  if (confirm('Bạn có chắc chắn muốn đóng phản hồi này?')) {
    const index = feedbackList.value.findIndex(f => f.id === feedback.id);
    if (index !== -1) {
      feedbackList.value[index] = {
        ...feedback,
        status: 'closed'
      };
    }
  }
};

const replyToFeedback = (feedback) => {
  selectedFeedback.value = feedback;
  showReplyModal.value = true;
};

const submitReply = () => {
  if (selectedFeedback.value && replyMessage.value.trim()) {
    const index = feedbackList.value.findIndex(f => f.id === selectedFeedback.value.id);
    if (index !== -1) {
      const newReply = {
        id: Date.now(),
        message: replyMessage.value,
        createdAt: new Date().toISOString(),
        user: {
          id: 999, // Replace with actual admin ID
          name: 'Admin User',
          role: 'admin',
          avatar: 'https://i.pravatar.cc/150?u=admin'
        }
      };
      
      feedbackList.value[index].replies = [
        ...(feedbackList.value[index].replies || []),
        newReply
      ];
    }
    
    showReplyModal.value = false;
    replyMessage.value = '';
    selectedFeedback.value = null;
  }
};
</script>

<style lang="scss" scoped>
@use '@/components/layout/admin-content/feedback/FeedbackManagement.scss';
</style> 