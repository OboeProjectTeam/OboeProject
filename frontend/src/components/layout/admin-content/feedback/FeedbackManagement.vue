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
.feedback-management {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 20px;
}

.filters {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  gap: 20px;
}

.search-box {
  flex: 1;
  position: relative;
  
  i {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #718096;
  }
  
  input {
    width: 100%;
    padding: 10px 10px 10px 35px;
    border: 1px solid #e2e8f0;
    border-radius: 6px;
    font-size: 0.9rem;
    
    &:focus {
      outline: none;
      border-color: #4299e1;
    }
  }
}

.filter-options {
  display: flex;
  gap: 10px;
  
  select {
    padding: 8px 12px;
    border: 1px solid #e2e8f0;
    border-radius: 6px;
    background: #fff;
    font-size: 0.9rem;
    
    &:focus {
      outline: none;
      border-color: #4299e1;
    }
  }
}

.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feedback-item {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 20px;
  
  &.new {
    border-left: 4px solid #4299e1;
  }
  
  &.in_progress {
    border-left: 4px solid #ecc94b;
  }
  
  &.resolved {
    border-left: 4px solid #48bb78;
  }
  
  &.closed {
    border-left: 4px solid #718096;
  }
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.feedback-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  
  .feedback-id {
    font-weight: 600;
    color: #2d3748;
  }
}

.category-badge, .status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.85rem;
}

.category-badge {
  &.bug {
    background: #fff5f5;
    color: #c53030;
  }
  
  &.feature {
    background: #ebf8ff;
    color: #2b6cb0;
  }
  
  &.content {
    background: #f0fff4;
    color: #2f855a;
  }
  
  &.other {
    background: #f7fafc;
    color: #718096;
  }
}

.status-badge {
  &.new {
    background: #ebf8ff;
    color: #2b6cb0;
  }
  
  &.in_progress {
    background: #fefcbf;
    color: #975a16;
  }
  
  &.resolved {
    background: #f0fff4;
    color: #2f855a;
  }
  
  &.closed {
    background: #f7fafc;
    color: #718096;
  }
}

.feedback-date {
  color: #718096;
  font-size: 0.9rem;
}

.feedback-content {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  
  img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
  }
  
  div {
    display: flex;
    flex-direction: column;
    
    .user-name {
      font-weight: 500;
      color: #2d3748;
    }
    
    .user-email {
      color: #718096;
      font-size: 0.85rem;
    }
    
    .user-role {
      color: #4299e1;
      font-size: 0.85rem;
      font-weight: 500;
    }
  }
}

.feedback-message {
  flex: 1;
  padding: 16px;
  background: #fff;
  border-radius: 8px;

  h4 {
    margin: 0 0 8px 0;
    color: #2d3748;
    font-size: 16px;
  }

  p {
    margin: 0;
    color: #4a5568;
    line-height: 1.5;
  }
}

.feedback-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #e2e8f0;
}

.status-actions {
  display: flex;
  gap: 10px;
}

button {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
  
  &.btn-start {
    background: #4299e1;
    color: #fff;
    border: none;
    
    &:hover {
      background: #3182ce;
    }
  }
  
  &.btn-resolve {
    background: #48bb78;
    color: #fff;
    border: none;
    
    &:hover {
      background: #38a169;
    }
  }
  
  &.btn-close {
    background: none;
    border: 1px solid #e2e8f0;
    color: #718096;
    
    &:hover {
      background: #f7fafc;
    }
  }
  
  &.btn-reply {
    background: none;
    border: 1px solid #4299e1;
    color: #4299e1;
    
    &:hover {
      background: #ebf8ff;
    }
  }
}

.feedback-replies {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.reply-item {
  background: #f7fafc;
  border-radius: 6px;
  padding: 15px;
  margin-bottom: 10px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.reply-date {
  color: #718096;
  font-size: 0.85rem;
}

.reply-content {
  color: #4a5568;
  line-height: 1.5;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
  
  button {
    background: none;
    border: 1px solid #e2e8f0;
    padding: 8px 12px;
    border-radius: 6px;
    cursor: pointer;
    
    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
    
    &:not(:disabled):hover {
      background: #f7fafc;
    }
  }
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  
  .modal-content {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    width: 100%;
    max-width: 500px;
    
    h3 {
      margin: 0 0 20px;
    }
  }
}

.form-group {
  margin-bottom: 15px;
  
  label {
    display: block;
    margin-bottom: 5px;
    font-weight: 500;
  }
  
  textarea {
    width: 100%;
    padding: 8px 12px;
    border: 1px solid #e2e8f0;
    border-radius: 6px;
    font-size: 0.9rem;
    resize: vertical;
    min-height: 100px;
    
    &:focus {
      outline: none;
      border-color: #4299e1;
    }
  }
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
  
  button {
    padding: 8px 16px;
    border-radius: 6px;
    cursor: pointer;
    
    &.btn-cancel {
      background: none;
      border: 1px solid #e2e8f0;
      
      &:hover {
        background: #f7fafc;
      }
    }
    
    &.btn-save {
      background: #4299e1;
      color: #fff;
      border: none;
      
      &:hover {
        background: #3182ce;
      }
    }
  }
}
</style> 