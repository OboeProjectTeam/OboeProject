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
          <option value="admin">Quản trị viên</option>
        </select>
        
        <select v-model="statusFilter">
          <option value="">Tất cả trạng thái</option>
          <option value="active">Hoạt động</option>
          <option value="banned">Đã cấm</option>
        </select>

        <button class="btn-add-user" @click="showAddModal = true">
          <i class="fas fa-user-plus"></i>
          Thêm người dùng
        </button>
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
              <option value="admin">Quản trị viên</option>
            </select>
          </div>
          <div class="form-group">
            <label>Trạng thái</label>
            <select v-model="editingUser.status">
              <option value="active">Hoạt động</option>
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

    <!-- Add User Modal -->
    <div class="modal" v-if="showAddModal">
      <div class="modal-content">
        <h3>Thêm người dùng mới</h3>
        <form @submit.prevent="saveNewUser">
          <div class="form-group">
            <label>Họ Tên</label>
            <input type="text" v-model="newUser.name" required>
          </div>
          <div class="form-group">
            <label>Email/Số điện thoại</label>
            <input type="text" v-model="newUser.contact" required>
          </div>
          <div class="form-group">
            <label>Mật khẩu</label>
            <input type="password" v-model="newUser.password" required>
          </div>
          <div class="form-group">
            <label>Vai trò</label>
            <select v-model="newUser.role" required>
              <option value="user">Người dùng</option>
              <option value="admin">Quản trị viên</option>
            </select>
          </div>
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="showAddModal = false">
              Hủy
            </button>
            <button type="submit" class="btn-save">
              Thêm người dùng
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
const showAddModal = ref(false);
const newUser = ref({
  name: '',
  contact: '',
  password: '',
  role: 'user'
});

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
    admin: 'Quản trị viên'
  };
  return roles[role] || role;
};

const getStatusName = (status) => {
  const statuses = {
    active: 'Hoạt động',
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

const saveNewUser = () => {
  // Add new user to the list
  const user = {
    id: users.value.length + 1, // In real app, this would come from the backend
    name: newUser.value.name,
    email: newUser.value.contact, // Using contact field for email/phone
    role: newUser.value.role,
    status: 'active',
    joinDate: new Date().toISOString().split('T')[0],
    avatar: `https://i.pravatar.cc/150?u=${newUser.value.name.replace(/\s+/g, '')}`
  };
  
  users.value.push(user);
  
  // Reset form
  newUser.value = {
    name: '',
    contact: '',
    password: '',
    role: 'user'
  };
  
  // Close modal
  showAddModal.value = false;
};
</script>

<style lang="scss" scoped>
@use '@/components/layout/admin-content/users/UserManagement.scss';
</style> 