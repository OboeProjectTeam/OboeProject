<template>
  <div class="user-management">
    <div class="filters">
      <div class="search-box">
        <i class="fas fa-search"></i>
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="Tìm kiếm người dùng..."
          @input="handleSearch"
        >
      </div>
      
      <div class="filter-options">
        <select v-model="roleFilter">
          <option value="">Tất cả vai trò</option>
          <option value="user">Người dùng</option>
          <option value="moderator">Người kiểm duyệt</option>
          <option value="admin">Quản trị viên</option>
        </select>
        
        <select v-model="statusFilter">
          <option value="">Tất cả trạng thái</option>
          <option value="active">Hoạt động</option>
          <option value="inactive">Không hoạt động</option>
          <option value="banned">Đã cấm</option>
        </select>
      </div>
    </div>

    <div class="table-container">
      <table class="users-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Người dùng</th>
            <th>Email</th>
            <th>Vai trò</th>
            <th>Trạng thái</th>
            <th>Ngày tham gia</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in filteredUsers" :key="user.id">
            <td>{{ user.id }}</td>
            <td class="user-info">
              <img :src="user.avatar" :alt="user.name">
              <div>
                <span class="user-name">{{ user.name }}</span>
                <span class="user-username">@{{ user.username }}</span>
              </div>
            </td>
            <td>{{ user.email }}</td>
            <td>
              <span class="role-badge" :class="user.role">
                {{ getRoleName(user.role) }}
              </span>
            </td>
            <td>
              <span class="status-badge" :class="user.status">
                {{ getStatusName(user.status) }}
              </span>
            </td>
            <td>{{ formatDate(user.joinDate) }}</td>
            <td>
              <div class="actions">
                <button 
                  class="btn-edit"
                  @click="editUser(user)"
                  title="Chỉnh sửa"
                >
                  <i class="fas fa-edit"></i>
                </button>
                <button 
                  class="btn-ban"
                  v-if="user.status !== 'banned'"
                  @click="banUser(user)"
                  title="Cấm người dùng"
                >
                  <i class="fas fa-ban"></i>
                </button>
                <button 
                  class="btn-unban"
                  v-else
                  @click="unbanUser(user)"
                  title="Bỏ cấm"
                >
                  <i class="fas fa-undo"></i>
                </button>
                <button 
                  class="btn-delete"
                  @click="deleteUser(user)"
                  title="Xóa"
                >
                  <i class="fas fa-trash"></i>
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

    <!-- Edit User Modal -->
    <div class="modal" v-if="showEditModal">
      <div class="modal-content">
        <h3>Chỉnh sửa người dùng</h3>
        <form @submit.prevent="saveUserEdit">
          <div class="form-group">
            <label>Tên hiển thị</label>
            <input type="text" v-model="editingUser.name">
          </div>
          <div class="form-group">
            <label>Email</label>
            <input type="email" v-model="editingUser.email">
          </div>
          <div class="form-group">
            <label>Vai trò</label>
            <select v-model="editingUser.role">
              <option value="user">Người dùng</option>
              <option value="moderator">Người kiểm duyệt</option>
              <option value="admin">Quản trị viên</option>
            </select>
          </div>
          <div class="form-group">
            <label>Trạng thái</label>
            <select v-model="editingUser.status">
              <option value="active">Hoạt động</option>
              <option value="inactive">Không hoạt động</option>
              <option value="banned">Đã cấm</option>
            </select>
          </div>
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="showEditModal = false">
              Hủy
            </button>
            <button type="submit" class="btn-save">
              Lưu thay đổi
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
const users = ref([
  {
    id: 1,
    name: 'John Doe',
    username: 'johndoe',
    email: 'john@example.com',
    role: 'admin',
    status: 'active',
    joinDate: '2024-01-01',
    avatar: 'https://i.pravatar.cc/150?u=john'
  },
  // Add more mock users...
]);

const searchQuery = ref('');
const roleFilter = ref('');
const statusFilter = ref('');
const currentPage = ref(1);
const itemsPerPage = 10;
const showEditModal = ref(false);
const editingUser = ref(null);

// Computed properties
const filteredUsers = computed(() => {
  let result = users.value;
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(user => 
      user.name.toLowerCase().includes(query) ||
      user.username.toLowerCase().includes(query) ||
      user.email.toLowerCase().includes(query)
    );
  }
  
  if (roleFilter.value) {
    result = result.filter(user => user.role === roleFilter.value);
  }
  
  if (statusFilter.value) {
    result = result.filter(user => user.status === statusFilter.value);
  }
  
  return result;
});

const totalPages = computed(() => 
  Math.ceil(filteredUsers.value.length / itemsPerPage)
);

// Methods
const getRoleName = (role) => {
  const roles = {
    user: 'Người dùng',
    moderator: 'Người kiểm duyệt',
    admin: 'Quản trị viên'
  };
  return roles[role] || role;
};

const getStatusName = (status) => {
  const statuses = {
    active: 'Hoạt động',
    inactive: 'Không hoạt động',
    banned: 'Đã cấm'
  };
  return statuses[status] || status;
};

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('vi-VN');
};

const handleSearch = () => {
  currentPage.value = 1;
};

const editUser = (user) => {
  editingUser.value = { ...user };
  showEditModal.value = true;
};

const saveUserEdit = () => {
  const index = users.value.findIndex(u => u.id === editingUser.value.id);
  if (index !== -1) {
    users.value[index] = { ...editingUser.value };
  }
  showEditModal.value = false;
};

const banUser = (user) => {
  if (confirm('Bạn có chắc chắn muốn cấm người dùng này?')) {
    const index = users.value.findIndex(u => u.id === user.id);
    if (index !== -1) {
      users.value[index] = { ...user, status: 'banned' };
    }
  }
};

const unbanUser = (user) => {
  const index = users.value.findIndex(u => u.id === user.id);
  if (index !== -1) {
    users.value[index] = { ...user, status: 'active' };
  }
};

const deleteUser = (user) => {
  if (confirm('Bạn có chắc chắn muốn xóa người dùng này?')) {
    users.value = users.value.filter(u => u.id !== user.id);
  }
};
</script>

<style lang="scss" scoped>
.user-management {
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

.table-container {
  overflow-x: auto;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
  
  th, td {
    padding: 12px;
    text-align: left;
    border-bottom: 1px solid #e2e8f0;
  }
  
  th {
    background: #f7fafc;
    font-weight: 600;
    color: #2d3748;
  }
}

.user-info {
  display: flex;
  align-items: center;
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
    }
    
    .user-username {
      color: #718096;
      font-size: 0.9rem;
    }
  }
}

.role-badge, .status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.85rem;
}

.role-badge {
  &.admin {
    background: #ebf8ff;
    color: #2b6cb0;
  }
  
  &.moderator {
    background: #faf5ff;
    color: #6b46c1;
  }
  
  &.user {
    background: #f0fff4;
    color: #2f855a;
  }
}

.status-badge {
  &.active {
    background: #f0fff4;
    color: #2f855a;
  }
  
  &.inactive {
    background: #f7fafc;
    color: #718096;
  }
  
  &.banned {
    background: #fff5f5;
    color: #c53030;
  }
}

.actions {
  display: flex;
  gap: 8px;
  
  button {
    background: none;
    border: none;
    padding: 6px;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s;
    
    &:hover {
      background: #f7fafc;
    }
    
    &.btn-edit {
      color: #4299e1;
    }
    
    &.btn-ban {
      color: #e53e3e;
    }
    
    &.btn-unban {
      color: #48bb78;
    }
    
    &.btn-delete {
      color: #e53e3e;
    }
  }
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
  
  input, select {
    width: 100%;
    padding: 8px 12px;
    border: 1px solid #e2e8f0;
    border-radius: 6px;
    
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