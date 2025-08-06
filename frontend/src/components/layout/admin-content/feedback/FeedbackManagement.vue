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
          <option value="">Tất cả chủ đề</option>
          <option value="feature">Góp ý</option>
          <option value="bug">Hỗ trợ kỹ thuật</option>
          <option value="other">Thanh toán</option>
        </select>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Đang tải dữ liệu phản hồi...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-state">
      <div class="error-message">
        <i class="fas fa-exclamation-triangle"></i>
        <p>{{ error }}</p>
        <button @click="fetchFeedback" class="btn-retry">
          <i class="fas fa-redo"></i>
          Thử lại
        </button>
      </div>
    </div>

    <div v-else class="feedback-list">
      <!-- Empty State -->
      <div v-if="filteredFeedback.length === 0" class="empty-state">
        <div class="empty-message">
          <i class="fas fa-inbox"></i>
          <p v-if="searchQuery || statusFilter || categoryFilter">
            Không tìm thấy phản hồi nào phù hợp với bộ lọc.
          </p>
          <p v-else>Chưa có phản hồi nào.</p>
        </div>
      </div>

      <div 
        v-for="feedback in paginatedFeedback" 
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
          
          <div class="feedback-header-right">
            <span class="feedback-date">{{ formatDate(feedback.createdAt) }}</span>
            <div class="feedback-actions">
              <button 
                v-if="feedback.status === 'new'"
                @click="startProcessing(feedback)"
                class="btn-action btn-process"
                title="Bắt đầu xử lý"
              >
                <i class="fas fa-play"></i>
                <span>Bắt đầu xử lý</span>
              </button>
              
              <button 
                v-if="feedback.status === 'in_progress'"
                @click="resolveFeedback(feedback)"
                class="btn-action btn-resolve"
                title="Đã xử lý"
              >
                <i class="fas fa-check"></i>
                <span>Đã xử lý</span>
              </button>
              
              <button 
                v-if="feedback.status !== 'closed'"
                @click="closeFeedback(feedback)"
                class="btn-action btn-close"
                title="Đóng phản hồi"
              >
                <i class="fas fa-times"></i>
                <span>Đóng</span>
              </button>
            </div>
          </div>
        </div>

        <div class="feedback-content">
          <div class="user-info">
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
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="!loading && !error && filteredFeedback.length > 0" class="pagination-container">
      <div class="pagination-info">
        <span>
          Hiển thị {{ (currentPage - 1) * itemsPerPage + 1 }} - 
          {{ Math.min(currentPage * itemsPerPage, filteredFeedback.length) }} 
          trong tổng số {{ filteredFeedback.length }} phản hồi
        </span>
      </div>
      
      <div class="pagination" v-if="totalPages > 1">
        <button 
          class="pagination-btn"
          :disabled="currentPage === 1"
          @click="currentPage = 1"
          title="Trang đầu"
        >
          <i class="fas fa-angle-double-left"></i>
        </button>
        
        <button 
          class="pagination-btn"
          :disabled="currentPage === 1"
          @click="currentPage--"
          title="Trang trước"
        >
          <i class="fas fa-chevron-left"></i>
        </button>
        
        <div class="pagination-pages">
           <template v-for="page in visiblePages" :key="page">
             <span v-if="page === '...'" class="pagination-ellipsis">...</span>
             <button
               v-else
               class="pagination-btn page-number"
               :class="{ active: page === currentPage }"
               @click="currentPage = page"
             >
               {{ page }}
             </button>
           </template>
         </div>
        
        <button 
          class="pagination-btn"
          :disabled="currentPage === totalPages"
          @click="currentPage++"
          title="Trang sau"
        >
          <i class="fas fa-chevron-right"></i>
        </button>
        
        <button 
          class="pagination-btn"
          :disabled="currentPage === totalPages"
          @click="currentPage = totalPages"
          title="Trang cuối"
        >
          <i class="fas fa-angle-double-right"></i>
        </button>
      </div>
    </div>


  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import feedbackApi from '@/api/modules/feedbackApi';

// Data
const feedbackList = ref([]);
const loading = ref(false);
const error = ref(null);

const searchQuery = ref('');
const statusFilter = ref('');
const categoryFilter = ref('');
const currentPage = ref(1);
const itemsPerPage = 10;


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

const paginatedFeedback = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredFeedback.value.slice(start, end);
});

const visiblePages = computed(() => {
  const pages = [];
  const total = totalPages.value;
  const current = currentPage.value;
  
  if (total <= 7) {
    // Hiển thị tất cả trang nếu ít hơn hoặc bằng 7 trang
    for (let i = 1; i <= total; i++) {
      pages.push(i);
    }
  } else {
    // Hiển thị trang đầu
    pages.push(1);
    
    if (current > 4) {
      pages.push('...');
    }
    
    // Hiển thị các trang xung quanh trang hiện tại
    const start = Math.max(2, current - 1);
    const end = Math.min(total - 1, current + 1);
    
    for (let i = start; i <= end; i++) {
      if (!pages.includes(i)) {
        pages.push(i);
      }
    }
    
    if (current < total - 3) {
      pages.push('...');
    }
    
    // Hiển thị trang cuối
    if (!pages.includes(total)) {
      pages.push(total);
    }
  }
  
  return pages;
});

// API Methods
const fetchFeedback = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    const response = await feedbackApi.getAll();
    
    // Map dữ liệu từ API vào định dạng component
    feedbackList.value = response.map(feedback => ({
      id: feedback.feedbackId,
      title: feedback.title,
      message: feedback.content,
      category: mapTopicToCategory(feedback.topic),
      status: 'new', // Mặc định là new vì API không có status
      createdAt: feedback.createdAt,
      user: {
        id: feedback.feedbackId, // Sử dụng feedbackId làm user id tạm thời
        name: feedback.fullName,
        email: feedback.email
      }
    }));
  } catch (err) {
    error.value = 'Không thể tải dữ liệu phản hồi. Vui lòng thử lại.';
    console.error('Error fetching feedback:', err);
  } finally {
    loading.value = false;
  }
};

// Helper function để map topic từ API sang category
const mapTopicToCategory = (topic) => {
  const topicMap = {
    'Góp ý': 'feature',
    'billing': 'other',
    'support': 'bug',
    'Hỗ trợ kỹ thuật': 'bug'
  };
  return topicMap[topic] || 'other';
};

// Methods
const getCategoryName = (category) => {
  const categories = {
    feature: 'Góp ý',
    bug: 'Hỗ trợ kỹ thuật',
    other: 'Thanh toán'
  };
  return categories[category] || 'Khác';
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



// Watchers - Reset trang khi filter thay đổi
watch([searchQuery, statusFilter, categoryFilter], () => {
  currentPage.value = 1;
});

// Lifecycle
onMounted(() => {
  fetchFeedback();
});
</script>

<style lang="scss" scoped>
@use '@/components/layout/admin-content/feedback/FeedbackManagement.scss';
</style>