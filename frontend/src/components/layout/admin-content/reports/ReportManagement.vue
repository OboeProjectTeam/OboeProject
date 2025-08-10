<template>
  <div class="report-management">
    <div class="filters">
      <div class="search-box">
        <i class="fas fa-search"></i>
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="Tìm kiếm báo cáo..."
          @input="handleSearch"
        >
      </div>
      
      <div class="filter-options">
        <select v-model="statusFilter">
          <option value="">Tất cả trạng thái</option>
          <option value="pending">Chờ xử lý (PENDING)</option>
          <option value="resolved">Đã xử lý (APPROVED)</option>
          <option value="rejected">Đã từ chối (REJECTED)</option>
        </select>
        
        <select v-model="typeFilter">
          <option value="">Tất cả loại</option>
          <option value="spam">Spam / Quảng cáo</option>
          <option value="inappropriate">Nội dung không phù hợp</option>
          <option value="harassment">Quấy rối / Xúc phạm</option>
          <option value="copyright">Vi phạm bản quyền</option>
          <option value="violence">Bạo lực / Nguy hiểm</option>
          <option value="hate_speech">Phát ngôn thù ghét</option>
          <option value="fake_news">Thông tin sai lệch</option>
          <option value="other">Khác</option>
        </select>

        <select v-model="severityFilter">
          <option value="">Tất cả mức độ</option>
          <option value="low">Thấp</option>
          <option value="medium">Trung bình</option>
          <option value="high">Cao</option>
          <option value="urgent">Khẩn cấp</option>
        </select>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Đang tải danh sách báo cáo...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-container">
      <div class="error-message">
        <i class="fas fa-exclamation-triangle"></i>
        <p>{{ error }}</p>
        <button @click="fetchReports" class="btn-retry">
          <i class="fas fa-redo"></i>
          Thử lại
        </button>
      </div>
    </div>

    <!-- Table Container -->
    <div v-else class="table-container">
      <table class="reports-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Bài viết</th>
            <th>Người báo cáo</th>
            <th>Loại</th>
            <th>Chi tiết</th>
            <th>Mức độ</th>
            <th>Trạng thái</th>
            <th>Ngày báo cáo</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="paginatedReports.length === 0">
            <td colspan="7" class="no-data">
              <div class="no-data-message">
                <i class="fas fa-inbox"></i>
                <p v-if="filteredReports.length === 0 && !loading">
                  {{ searchQuery || statusFilter || typeFilter || severityFilter 
                     ? 'Không tìm thấy báo cáo nào phù hợp với bộ lọc.' 
                     : 'Chưa có báo cáo nào.' }}
                </p>
                <p v-else-if="!loading">Không có dữ liệu cho trang này.</p>
              </div>
            </td>
          </tr>
          <tr v-for="report in paginatedReports" :key="report.id">
            <td>
              <span class="report-id" :title="report.id">
                {{ report.id.substring(0, 8) }}...
              </span>
            </td>
            <td class="post-info">
              <div class="post-preview">
                <h4>{{ report.post.title }}</h4>
                <p>{{ report.post.excerpt }}</p>
              </div>
              <div class="post-actions">
                <button class="btn-view" @click="viewPost(report.post)">
                  <i class="fas fa-external-link-alt"></i>
                  Xem bài viết
                </button>
              </div>
            </td>
            <td class="reporter-info-cell">
              <div class="reporter-info">
                <img :src="report.reporter.avatar" :alt="report.reporter.name">
                <div>
                  <span class="reporter-name">{{ report.reporter.name }}</span>
                  <span class="reporter-email">{{ report.reporter.email }}</span>
                </div>
              </div>
            </td>
            <td>
              <span class="type-badge" :class="report.type">
                {{ getReportTypeName(report.type) }}
              </span>
              <span v-if="report.subType" class="subtype-badge">
                {{ getReportSubTypeName(report.subType) }}
              </span>
            </td>
            <td class="report-reason">
              <div class="reason-text">{{ report.reason }}</div>
            </td>
            <td>
              <span class="severity-badge" :class="report.severity">
                {{ getSeverityName(report.severity) }}
              </span>
            </td>
            <td>
              <span class="status-badge" :class="report.status">
                {{ getStatusName(report.status) }}
              </span>
            </td>
            <td>
              <div class="date-info">
                <span class="date">{{ formatDate(report.reportDate) }}</span>
                <span class="time">{{ formatTime(report.reportDate) }}</span>
              </div>
            </td>
            <td>
              <div class="actions">
                <button 
                  v-if="report.status === 'pending'"
                  class="btn-resolve"
                  @click="resolveReport(report)"
                  title="Xử lý báo cáo"
                >
                  <i class="fas fa-check"></i>
                </button>
                <button 
                  v-if="report.status === 'pending'"
                  class="btn-reject"
                  @click="rejectReport(report)"
                  title="Từ chối báo cáo"
                >
                  <i class="fas fa-times"></i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div v-if="!loading && !error && totalItems > 0" class="pagination-container">
      <div class="pagination-info">
        <span>
          Hiển thị {{ ((currentPage - 1) * itemsPerPage) + 1 }} - 
          {{ Math.min(currentPage * itemsPerPage, totalItems) }} 
          trong tổng số {{ totalItems }} báo cáo
        </span>
        <select v-model="itemsPerPage" @change="handleItemsPerPageChange" class="items-per-page">
          <option :value="5">5 / trang</option>
          <option :value="10">10 / trang</option>
          <option :value="20">20 / trang</option>
          <option :value="50">50 / trang</option>
        </select>
      </div>
      
      <div class="pagination-controls">
        <button 
          :disabled="currentPage === 1"
          @click="goToPage(1)"
          class="pagination-btn"
          title="Trang đầu"
        >
          <i class="fas fa-angle-double-left"></i>
        </button>
        
        <button 
          :disabled="currentPage === 1"
          @click="currentPage--"
          class="pagination-btn"
          title="Trang trước"
        >
          <i class="fas fa-chevron-left"></i>
        </button>
        
        <div class="page-numbers">
          <button
            v-for="page in visiblePages"
            :key="page"
            @click="goToPage(page)"
            :class="['page-btn', { active: page === currentPage }]"
          >
            {{ page }}
          </button>
        </div>
        
        <button 
          :disabled="currentPage === totalPages"
          @click="currentPage++"
          class="pagination-btn"
          title="Trang sau"
        >
          <i class="fas fa-chevron-right"></i>
        </button>
        
        <button 
          :disabled="currentPage === totalPages"
          @click="goToPage(totalPages)"
          class="pagination-btn"
          title="Trang cuối"
        >
          <i class="fas fa-angle-double-right"></i>
        </button>
      </div>
      
      <div class="page-jump">
        <span>Đi đến trang:</span>
        <input 
          type="number" 
          :min="1" 
          :max="totalPages"
          v-model.number="jumpToPage"
          @keyup.enter="handlePageJump"
          class="page-input"
        >
        <button @click="handlePageJump" class="jump-btn">Đi</button>
      </div>
    </div>

    <!-- Resolve Report Modal -->
    <div class="modal" v-if="showResolveModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Xử lý báo cáo</h3>
          <button class="close-btn" @click="showResolveModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <form @submit.prevent="submitResolve" class="resolve-form">
          <div class="form-group">
            <label>Hành động</label>
            <select v-model="resolveData.action" required>
              <option value="warning">Gửi cảnh báo</option>
              <option value="delete">Xóa bài viết</option>
              <option value="perm_ban">Cấm vĩnh viễn</option>
            </select>
          </div>

          <div class="form-group">
            <label>Ghi chú cho người vi phạm</label>
            <textarea 
              v-model="resolveData.userNote"
              placeholder="Giải thích lý do xử lý cho người dùng..."
              rows="3"
              required
            ></textarea>
          </div>
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="showResolveModal = false">
              Hủy
            </button>
            <button type="submit" class="btn-save">
              Xác nhận
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Report History Modal -->
    <div class="modal" v-if="showHistoryModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Lịch sử báo cáo</h3>
          <button class="close-btn" @click="showHistoryModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="history-timeline">
          <div v-for="event in reportHistory" :key="event.id" class="timeline-item">
            <div class="timeline-icon" :class="event.type">
              <i :class="getTimelineIcon(event.type)"></i>
            </div>
            <div class="timeline-content">
              <div class="event-header">
                <span class="event-title">{{ event.title }}</span>
                <span class="event-date">{{ formatDate(event.date) }}</span>
              </div>
              <p class="event-description">{{ event.description }}</p>
              <div v-if="event.action" class="event-action">
                <span class="action-label">Hành động:</span>
                <span class="action-value">{{ event.action }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import reportApi from '@/api/modules/reportApi.js';

const router = useRouter();

// Filters
const searchQuery = ref('');
const statusFilter = ref('');
const typeFilter = ref('');
const severityFilter = ref('');
const currentPage = ref(1);
const itemsPerPage = ref(10);
const jumpToPage = ref(1);

// Data
const reports = ref([]);
const loading = ref(false);
const error = ref(null);

// Modals
const showResolveModal = ref(false);
const showHistoryModal = ref(false);
const resolveData = ref({
  severity: '',
  action: '',
  userNote: '',
  internalNote: ''
});
const selectedReport = ref(null);
const reportHistory = ref([]);

// Fetch reports from API
const fetchReports = async () => {
  try {
    loading.value = true;
    error.value = null;
    
    const data = await reportApi.getAllBlogReports();
    // Map API data to component format
    reports.value = data.map(report => ({
      id: report.reportId,
      post: {
        id: report.blogId,
        title: report.blogTitle,
        excerpt: report.blogContent ? report.blogContent.substring(0, 100) + '...' : 'Không có nội dung',
        author: {
          id: 'unknown',
          name: report.userName || 'Người dùng ẩn danh'
        }
      },
      reporter: {
        id: 'unknown',
        name: report.userName || 'Người báo cáo ẩn danh',
        email: 'unknown@example.com',
        avatar: report.avatarUrl || 'https://i.pravatar.cc/150?u=anonymous',
        reportCount: report.reportCount || 1
      },
      type: getTypeFromTitle(report.type), // Phân loại dựa trên title
      reason: report.content || 'Không có lý do',
      severity: mapReportSeverity(report.reportCount), // Tạo severity từ reportCount
      status: mapApiStatusToComponentStatus(report.status),
      reportDate: report.report_at + 'T00:00:00', // Thêm time để tạo datetime
      evidence: [],
      // Thêm các trường mới từ API
      authProvider: report.authProvider
    }));
    
  } catch (err) {
    console.error('Error fetching reports:', err);
    error.value = 'Không thể tải danh sách báo cáo. Vui lòng thử lại.';
  } finally {
    loading.value = false;
  }
};
const getTypeFromTitle = (title) => {
  const titleLower = title.toLowerCase();
  if (titleLower.includes('spam')) return 'spam';
  if (titleLower.includes('quấy rối')) return 'harassment';
  if (titleLower.includes('không phù hợp')) return 'inappropriate';
  if (titleLower.includes('vi phạm')) return 'copyright';
  return 'other';
};

const mapReportSeverity = (reportCount) => {
  // Sử dụng reportCount để xác định mức độ nghiêm trọng
  if (typeof reportCount === 'number') {
    if (reportCount >= 10) return 'urgent';
    if (reportCount >= 5) return 'high';
    if (reportCount >= 2) return 'medium';
    return 'low';
  }
  return 'low';
};

const mapApiStatusToComponentStatus = (apiStatus) => {
  switch (apiStatus) {
    case 'PENDING': return 'pending';
    case 'APPROVED': return 'resolved';
    case 'REJECTED': return 'rejected';
    default: return 'pending';
  }
};

// Initialize data on component mount
onMounted(() => {
  fetchReports();
});

// Watch for filter changes and reset to page 1
watch([searchQuery, statusFilter, typeFilter, severityFilter], () => {
  currentPage.value = 1;
  jumpToPage.value = 1;
});

// Computed
const filteredReports = computed(() => {
  let result = reports.value;
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(report => 
      report.post.title.toLowerCase().includes(query) ||
      report.reporter.name.toLowerCase().includes(query) ||
      report.reason.toLowerCase().includes(query)
    );
  }
  
  if (statusFilter.value) {
    result = result.filter(report => report.status === statusFilter.value);
  }
  
  if (typeFilter.value) {
    result = result.filter(report => report.type === typeFilter.value);
  }
  
  if (severityFilter.value) {
    result = result.filter(report => report.severity === severityFilter.value);
  }
  
  return result;
});

// Phân trang dữ liệu
const paginatedReports = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return filteredReports.value.slice(start, end);
});

const totalPages = computed(() => 
  Math.ceil(filteredReports.value.length / itemsPerPage.value)
);

const totalItems = computed(() => filteredReports.value.length);

// Tính toán các trang hiển thị
const visiblePages = computed(() => {
  const total = totalPages.value;
  const current = currentPage.value;
  const delta = 2; // Số trang hiển thị mỗi bên
  
  if (total <= 7) {
    // Nếu tổng số trang <= 7, hiển thị tất cả
    return Array.from({ length: total }, (_, i) => i + 1);
  }
  
  let start = Math.max(1, current - delta);
  let end = Math.min(total, current + delta);
  
  // Điều chỉnh để luôn hiển thị 5 trang
  if (end - start < 4) {
    if (start === 1) {
      end = Math.min(total, start + 4);
    } else {
      start = Math.max(1, end - 4);
    }
  }
  
  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
});

// Methods
const getReportTypeName = (type) => {
  const types = {
    spam: 'Spam / Quảng cáo',
    inappropriate: 'Nội dung không phù hợp',
    harassment: 'Quấy rối / Xúc phạm',
    copyright: 'Vi phạm bản quyền',
    violence: 'Bạo lực / Nguy hiểm',
    hate_speech: 'Phát ngôn thù ghét',
    fake_news: 'Thông tin sai lệch',
    other: 'Khác'
  };
  return types[type] || type;
};

const getReportSubTypeName = (subType) => {
  const subTypes = {
    adult: 'Nội dung người lớn',
    offensive: 'Từ ngữ khiếm nhã',
    sensitive: 'Nội dung nhạy cảm',
    graphic: 'Hình ảnh phản cảm'
  };
  return subTypes[subType] || subType;
};

const getSeverityName = (severity) => {
  const severities = {
    low: 'Thấp',
    medium: 'Trung bình',
    high: 'Cao',
    urgent: 'Khẩn cấp'
  };
  return severities[severity] || severity;
};

const getStatusName = (status) => {
  const statuses = {
    pending: 'Chờ xử lý',
    resolved: 'Đã xử lý',
    rejected: 'Đã từ chối'
  };
  return statuses[status] || status;
};



const formatDate = (date) => {
  return new Date(date).toLocaleDateString('vi-VN');
};

const formatTime = (date) => {
  return new Date(date).toLocaleTimeString('vi-VN');
};

const getTimelineIcon = (type) => {
  const icons = {
    report: 'fas fa-flag',
    resolve: 'fas fa-check',
    reject: 'fas fa-times',
    delete: 'fas fa-trash',
    ban: 'fas fa-user-slash'
  };
  return icons[type] || 'fas fa-info-circle';
};

const handleSearch = () => {
  currentPage.value = 1;
};

// Pagination methods
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    jumpToPage.value = page;
  }
};

const handleItemsPerPageChange = () => {
  currentPage.value = 1;
  jumpToPage.value = 1;
};

const handlePageJump = () => {
  if (jumpToPage.value >= 1 && jumpToPage.value <= totalPages.value) {
    currentPage.value = jumpToPage.value;
  } else {
    jumpToPage.value = currentPage.value;
  }
};



const viewPost = (post) => {
  router.push(`/forum/post/${post.id}`);
};

const resolveReport = (report) => {
  selectedReport.value = report;
  resolveData.value = {
    severity: 'low',
    action: 'warning',
    userNote: '',
    internalNote: ''
  };
  showResolveModal.value = true;
};

const submitResolve = async () => {
  try {
    if (selectedReport.value && resolveData.value.action && resolveData.value.userNote) {
      // Map action từ form sang actionType của API
      const actionTypeMap = {
        'warning': 'WARNING',
        'delete': 'DELETE_POST', 
        'perm_ban': 'BAN_USER'
      };
      
      const actionType = actionTypeMap[resolveData.value.action];
      const note = resolveData.value.userNote;
      
      console.log('Calling approve API with:', {
        reportId: selectedReport.value.id,
        actionType,
        note
      });
      
      // Gọi API để duyệt báo cáo với actionType và note
      const response = await reportApi.approve(selectedReport.value.id, {
        actionType,
        note
      });
      
      console.log('API response:', response);
      
      // Cập nhật trạng thái local
      const index = reports.value.findIndex(r => r.id === selectedReport.value.id);
      if (index !== -1) {
        reports.value[index] = {
          ...selectedReport.value,
          status: 'resolved'
        };
      }
      
      // Làm mới dữ liệu từ server để đảm bảo đồng bộ
      await fetchReports();
      
      alert('Báo cáo đã được duyệt thành công!');
    } else {
      alert('Vui lòng chọn hành động và nhập ghi chú!');
      return;
    }
    
    showResolveModal.value = false;
    resolveData.value = {
      severity: '',
      action: '',
      userNote: '',
      internalNote: ''
    };
    selectedReport.value = null;
  } catch (error) {
    console.error('Error resolving report:', error);
    alert(`Có lỗi xảy ra khi duyệt báo cáo: ${error.message}`);
  }
};

const rejectReport = async (report) => {
  if (confirm('Bạn có chắc chắn muốn từ chối báo cáo này?')) {
    try {
      // Gọi API để từ chối báo cáo
      await reportApi.reject(report.id);
      // Cập nhật trạng thái local
      const index = reports.value.findIndex(r => r.id === report.id);
      if (index !== -1) {
        reports.value[index] = {
          ...report,
          status: 'rejected'
        };
      }
      
      // Làm mới dữ liệu từ server để đảm bảo đồng bộ
      await fetchReports();
      
    } catch (error) {
      console.error('Error rejecting report:', error);
      alert('Có lỗi xảy ra khi từ chối báo cáo. Vui lòng thử lại.');
    }
  }
};

const deletePost = async (post) => {
  if (confirm('Bạn có chắc chắn muốn xóa bài viết này?')) {
    try {
      // Delete post logic here

    } catch (error) {
      console.error('Error deleting post:', error);
    }
  }
};

const banUser = async (user) => {
  if (confirm(`Bạn có chắc chắn muốn cấm người dùng ${user.name}?`)) {
    try {
      // Ban user logic here

    } catch (error) {
      console.error('Error banning user:', error);
    }
  }
};
</script>

<style lang="scss" scoped>
@use '@/components/layout/admin-content/reports/ReportManagement.scss';
</style>