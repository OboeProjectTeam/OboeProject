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
          <option value="pending">Chờ xử lý</option>
          <option value="resolved">Đã xử lý</option>
          <option value="rejected">Đã từ chối</option>
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

    <div class="table-container">
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
          <tr v-for="report in filteredReports" :key="report.id">
            <td>{{ report.id }}</td>
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
                <button class="btn-history" @click="viewReportHistory(report.post.id)">
                  <i class="fas fa-history"></i>
                  Lịch sử báo cáo
                </button>
              </div>
            </td>
            <td class="reporter-info-cell">
              <div class="reporter-info">
                <img :src="report.reporter.avatar" :alt="report.reporter.name">
                <div>
                  <span class="reporter-name">{{ report.reporter.name }}</span>
                  <span class="reporter-email">{{ report.reporter.email }}</span>
                  <span class="report-count" :class="getReportCountClass(report.reporter.reportCount)">
                    {{ report.reporter.reportCount }} báo cáo
                  </span>
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
                <button 
                  class="btn-delete-post"
                  @click="deletePost(report.post)"
                  title="Xóa bài viết"
                >
                  <i class="fas fa-trash"></i>
                </button>
                <button 
                  class="btn-ban-user"
                  @click="banUser(report.post.author)"
                  title="Cấm người dùng"
                >
                  <i class="fas fa-user-slash"></i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
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
            <label>Mức độ vi phạm</label>
            <select v-model="resolveData.severity" required>
              <option value="low">Thấp - Cảnh báo</option>
              <option value="medium">Trung bình - Hạn chế tính năng</option>
              <option value="high">Cao - Cấm tạm thời</option>
              <option value="urgent">Nghiêm trọng - Cấm vĩnh viễn</option>
            </select>
          </div>

          <div class="form-group">
            <label>Hành động</label>
            <select v-model="resolveData.action" required>
              <option value="warning">Gửi cảnh báo</option>
              <option value="delete">Xóa bài viết</option>
              <option value="restrict">Hạn chế quyền đăng bài</option>
              <option value="temp_ban">Cấm tạm thời (7 ngày)</option>
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

          <div class="form-group">
            <label>Ghi chú nội bộ</label>
            <textarea 
              v-model="resolveData.internalNote"
              placeholder="Ghi chú cho đội ngũ quản trị..."
              rows="2"
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
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// Filters
const searchQuery = ref('');
const statusFilter = ref('');
const typeFilter = ref('');
const severityFilter = ref('');
const currentPage = ref(1);
const itemsPerPage = 10;

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

// Mock data
const reports = ref([
  {
    id: 1,
    post: {
      id: 1,
      title: 'Hướng dẫn học lập trình Python cho người mới bắt đầu',
      excerpt: 'Bài viết chia sẻ các bước cơ bản để bắt đầu học Python một cách hiệu quả...',
      author: {
        id: 1,
        name: 'John Doe'
      }
    },
    reporter: {
      id: 2,
      name: 'Jane Smith',
      email: 'jane@example.com',
      avatar: 'https://i.pravatar.cc/150?u=jane',
      reportCount: 5
    },
    type: 'spam',
    reason: 'Bài viết chứa nhiều link quảng cáo và nội dung không liên quan',
    severity: 'medium',
    status: 'pending',
    reportDate: '2024-03-15T10:30:00',
    evidence: [
      'https://example.com/evidence1',
      'https://example.com/evidence2'
    ]
  },
  // Add more mock reports...
]);

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

const totalPages = computed(() => 
  Math.ceil(filteredReports.value.length / itemsPerPage)
);

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

const getReportCountClass = (count) => {
  if (count <= 3) return 'low';
  if (count <= 7) return 'medium';
  return 'high';
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

const viewPost = (post) => {
  router.push(`/forum/post/${post.id}`);
};

const viewReportHistory = async (postId) => {
  // Mock history data - replace with API call
  reportHistory.value = [
    {
      id: 1,
      type: 'report',
      title: 'Báo cáo mới',
      description: 'Người dùng báo cáo vi phạm về spam',
      date: '2024-03-15T10:30:00'
    },
    {
      id: 2,
      type: 'resolve',
      title: 'Đã xử lý',
      description: 'Admin đã xem xét và xử lý báo cáo',
      date: '2024-03-15T11:00:00',
      action: 'Gửi cảnh báo cho người vi phạm'
    }
  ];
  showHistoryModal.value = true;
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
    if (selectedReport.value) {
      // Here you would send the resolution to your backend
      const index = reports.value.findIndex(r => r.id === selectedReport.value.id);
      if (index !== -1) {
        reports.value[index] = {
          ...selectedReport.value,
          status: 'resolved'
        };
      }
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
  }
};

const rejectReport = async (report) => {
  if (confirm('Bạn có chắc chắn muốn từ chối báo cáo này?')) {
    try {
      const index = reports.value.findIndex(r => r.id === report.id);
      if (index !== -1) {
        reports.value[index] = {
          ...report,
          status: 'rejected'
        };
      }
    } catch (error) {
      console.error('Error rejecting report:', error);
    }
  }
};

const deletePost = async (post) => {
  if (confirm('Bạn có chắc chắn muốn xóa bài viết này?')) {
    try {
      // Delete post logic here
      console.log('Delete post:', post);
    } catch (error) {
      console.error('Error deleting post:', error);
    }
  }
};

const banUser = async (user) => {
  if (confirm(`Bạn có chắc chắn muốn cấm người dùng ${user.name}?`)) {
    try {
      // Ban user logic here
      console.log('Ban user:', user);
    } catch (error) {
      console.error('Error banning user:', error);
    }
  }
};
</script>

<style lang="scss" scoped>
.report-management {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filters {
  display: flex;
  gap: 20px;  
  align-items: center;
  flex-wrap: wrap;
  padding-bottom: 20px;
  border-bottom: 1px solid #e2e8f0;

  .search-box {
    flex: 1;
    min-width: 300px;
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
      font-size: 14px;

      &:focus {
        outline: none;
        border-color: #4299e1;
      }
    }
  }

  .filter-options {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;

    select {
      padding: 8px 12px;
      border: 1px solid #e2e8f0;
      border-radius: 6px;
      background: #fff;
      font-size: 14px;
      min-width: 150px;

      &:focus {
        outline: none;
        border-color: #4299e1;
      }
    }
  }
}

.table-container {
  flex: 1;
  overflow: auto;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
}

.reports-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  font-size: 14px;

  th, td {
    padding: 12px 16px;
    text-align: left;
    border-bottom: 1px solid #e2e8f0;
    white-space: nowrap;
  }

  th {
    background: #f7fafc;
    font-weight: 600;
    color: #2d3748;
    position: sticky;
    top: 0;
    z-index: 10;
  }

  td {
    background: #fff;
  }

  tbody tr {
    &:hover td {
      background: #f7fafc;
    }
  }

  .reporter-info-cell {
    padding: 0;
  }

  .reporter-info {
    display: flex;
    align-items: center;
    gap: 12px;
    min-width: 200px;
    padding: 12px 16px;
    height: 100%;

    img {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      object-fit: cover;
    }

    div {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .reporter-name {
      font-weight: 500;
      color: #2d3748;
    }

    .reporter-email {
      font-size: 12px;
      color: #718096;
    }

    .report-count {
      font-size: 11px;
      padding: 2px 6px;
      border-radius: 10px;
      display: inline-block;
      background: #feebc8;
      color: #c05621;
      
      &.low { background: #e2e8f0; color: #4a5568; }
      &.medium { background: #feebc8; color: #c05621; }
      &.high { background: #fed7d7; color: #c53030; }
    }
  }

  .post-info {
    min-width: 250px;
    white-space: normal;

    .post-preview {
      h4 {
        margin: 0 0 4px 0;
        font-size: 14px;
        color: #2d3748;
      }

      p {
        margin: 0;
        color: #718096;
        font-size: 13px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
    }

    .post-actions {
      margin-top: 8px;
      display: flex;
      gap: 8px;

      button {
        padding: 4px 8px;
        font-size: 12px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 4px;
        color: #4a5568;
        background: #edf2f7;

        &:hover {
          background: #e2e8f0;
        }

        i {
          font-size: 10px;
        }
      }
    }
  }

  .type-badge, .subtype-badge {
    display: inline-block;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    margin-bottom: 4px;
  }

  .type-badge {
    background: #ebf8ff;
    color: #2b6cb0;

    &.spam { background: #feebc8; color: #c05621; }
    &.inappropriate { background: #fed7d7; color: #c53030; }
    &.harassment { background: #fbd38d; color: #c05621; }
    &.copyright { background: #e9d8fd; color: #6b46c1; }
    &.violence { background: #fed7d7; color: #c53030; }
    &.hate_speech { background: #fed7d7; color: #c53030; }
    &.fake_news { background: #feebc8; color: #c05621; }
    &.other { background: #e2e8f0; color: #4a5568; }
  }

  .subtype-badge {
    background: #edf2f7;
    color: #4a5568;
    margin-left: 4px;
  }

  .report-reason {
    min-width: 200px;
    white-space: normal;

    .reason-text {
      color: #4a5568;
    }
  }

  .severity-badge {
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    display: inline-block;

    &.low { background: #e2e8f0; color: #4a5568; }
    &.medium { background: #feebc8; color: #c05621; }
    &.high { background: #fed7d7; color: #c53030; }
    &.urgent { background: #feb2b2; color: #c53030; }
  }

  .status-badge {
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    display: inline-block;

    &.pending { background: #fefcbf; color: #b7791f; }
    &.resolved { background: #c6f6d5; color: #2f855a; }
    &.rejected { background: #e2e8f0; color: #4a5568; }
  }

  .date-info {
    display: flex;
    flex-direction: column;
    gap: 2px;

    .date {
      color: #2d3748;
    }

    .time {
      font-size: 12px;
      color: #718096;
    }
  }

  .actions {
    display: flex;
    gap: 8px;

    button {
      width: 32px;
      height: 32px;
      border: none;
      border-radius: 6px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.2s;

      i {
        font-size: 14px;
      }

      &.btn-resolve {
        background: #c6f6d5;
        color: #2f855a;
        &:hover { background: #9ae6b4; }
      }

      &.btn-reject {
        background: #e2e8f0;
        color: #4a5568;
        &:hover { background: #cbd5e0; }
      }

      &.btn-delete-post {
        background: #fed7d7;
        color: #c53030;
        &:hover { background: #feb2b2; }
      }

      &.btn-ban-user {
        background: #fed7d7;
        color: #c53030;
        &:hover { background: #feb2b2; }
      }
    }
  }
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding-top: 20px;

  button {
    width: 36px;
    height: 36px;
    border: 1px solid #e2e8f0;
    border-radius: 6px;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    &:not(:disabled):hover {
      background: #f7fafc;
    }

    i {
      font-size: 14px;
      color: #4a5568;
    }
  }

  span {
    color: #4a5568;
    font-size: 14px;
  }
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;

  .modal-content {
    background: #fff;
    border-radius: 8px;
    width: 100%;
    max-width: 500px;
    max-height: 90vh;
    overflow-y: auto;
  }

  .modal-header {
    padding: 16px 20px;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    align-items: center;
    justify-content: space-between;

    h3 {
      margin: 0;
      font-size: 18px;
      color: #2d3748;
    }

    .close-btn {
      background: none;
      border: none;
      color: #718096;
      cursor: pointer;
      padding: 4px;
      border-radius: 4px;

      &:hover {
        background: #f7fafc;
      }

      i {
        font-size: 16px;
      }
    }
  }

  .resolve-form {
    padding: 20px;

    .form-group {
      margin-bottom: 16px;

      label {
        display: block;
        margin-bottom: 8px;
        color: #4a5568;
        font-weight: 500;
      }

      select, textarea {
        width: 100%;
        padding: 8px 12px;
        border: 1px solid #e2e8f0;
        border-radius: 6px;
        font-size: 14px;

        &:focus {
          outline: none;
          border-color: #4299e1;
        }
      }

      textarea {
        resize: vertical;
        min-height: 80px;
      }
    }
  }

  .modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 20px;

    button {
      padding: 8px 16px;
      border-radius: 6px;
      font-size: 14px;
      cursor: pointer;
      border: none;

      &.btn-cancel {
        background: #e2e8f0;
        color: #4a5568;

        &:hover {
          background: #cbd5e0;
        }
      }

      &.btn-save {
        background: #4299e1;
        color: #fff;

        &:hover {
          background: #3182ce;
        }
      }
    }
  }
}
</style> 