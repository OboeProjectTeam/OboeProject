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
            <th class="sortable-header cursor-pointer hover:bg-gray-50" @click="toggleDateSort">
              Ngày tham gia
              <span class="sort-icon ml-1">{{ getSortIcon() }}</span>
            </th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in paginatedUsers" :key="user.id">
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
    <div class="pagination-container">
      <div class="pagination-info">
        <span>
          Hiển thị {{ (currentPage - 1) * itemsPerPage + 1 }} - 
          {{ Math.min(currentPage * itemsPerPage, filteredUsers.length) }} 
          trong tổng số {{ filteredUsers.length }} người dùng
        </span>
      </div>
      
      <div class="pagination">
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
        
        <div class="pagination-numbers">
           <template v-for="page in visiblePages" :key="page">
             <span v-if="page === '...'" class="pagination-ellipsis">...</span>
             <button
               v-else
               class="pagination-number"
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
      
      <div class="items-per-page">
        <label>Hiển thị:</label>
        <select v-model="itemsPerPage" @change="currentPage = 1">
          <option :value="5">5</option>
          <option :value="10">10</option>
          <option :value="20">20</option>
          <option :value="50">50</option>
        </select>
        <span>/ trang</span>
      </div>
    </div>

    <!-- Edit User Modal -->
    <div class="modal" v-if="showEditModal">
      <div class="modal-content">
        <h3>Chỉnh sửa người dùng</h3>
        <form @submit.prevent="saveUserEdit">
          <div class="form-group">
            <label>Họ</label>
            <input type="text" v-model="editingUser.firstName" required>
          </div>
          <div class="form-group">
            <label>Tên</label>
            <input type="text" v-model="editingUser.lastName" required>
          </div>
          <div class="form-group">
            <label>Tên đăng nhập</label>
            <input type="text" v-model="editingUser.username" required>
          </div>
          <div class="form-group">
            <label>Địa chỉ</label>
            <input type="text" v-model="editingUser.address" placeholder="Nhập địa chỉ">
          </div>
          <div class="form-group">
            <label>Ngày sinh</label>
            <input type="date" v-model="editingUser.dayOfBirth">
          </div>
          <div class="form-group">
            <label>URL Avatar</label>
            <input type="url" v-model="editingUser.avatar" placeholder="https://example.com/avatar.jpg">
          </div>
          <div class="form-group">
            <label>Loại tài khoản</label>
            <select v-model="editingUser.accountType">
              <option value="FREE">Miễn phí</option>
              <option value="PREMIUM">Cao cấp</option>
            </select>
          </div>
          <div class="form-group">
            <label>Vai trò</label>
            <select v-model="editingUser.originalRole">
              <option value="ROLE_USER">Người dùng</option>
              <option value="ROLE_ADMIN">Quản trị viên</option>
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
        <form @submit.prevent="showConfirmation">
          <div class="form-group">
            <label>Họ</label>
            <input type="text" v-model="newUser.firstName" required>
          </div>
          <div class="form-group">
            <label>Tên</label>
            <input type="text" v-model="newUser.lastName" required>
          </div>
          <div class="form-group">
            <label>Tên đăng nhập (Email/Số điện thoại)</label>
            <input type="text" v-model="newUser.userName" required>
          </div>
          <div class="form-group">
            <label>Mật khẩu</label>
            <input type="password" v-model="newUser.passWord" required>
          </div>
          <div class="form-group">
            <label>Địa chỉ</label>
            <input type="text" v-model="newUser.address" required>
          </div>
          <div class="form-group">
            <label>Ngày sinh</label>
            <input type="date" v-model="newUser.dayOfBirth" required>
          </div>
          <div class="form-group">
            <label>Vai trò</label>
            <select v-model="newUser.role" required>
              <option value="ROLE_USER">Người dùng</option>
              <option value="ROLE_ADMIN">Quản trị viên</option>
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

    <!-- Confirm Popup -->
    <ThePopup
      v-if="showConfirmPopup"
      title="Xác nhận thêm người dùng"
      :message="getConfirmMessage()"
      confirm-text="Thêm người dùng"
      @confirm="confirmAddUser"
      @cancel="cancelAddUser"
    />

    <!-- Error Popup -->
    <ThePopup
      v-if="showErrorPopup"
      title="Lỗi"
      :message="errorMessage"
      confirm-text="Đóng"
      :show-cancel="false"
      @confirm="closeErrorPopup"
    />

    <!-- Ban User Confirmation Popup -->
    <ThePopup
      v-if="showBanConfirmPopup"
      title="Xác nhận cấm người dùng"
      :message="getBanConfirmMessage()"
      confirm-text="Cấm"
      @confirm="confirmBanUser"
      @cancel="cancelBanUser"
    />

    <!-- Delete User Confirmation Popup -->
    <ThePopup
      v-if="showDeleteConfirmPopup"
      title="Xác nhận xóa người dùng"
      :message="getDeleteConfirmMessage()"
      confirm-text="Xóa"
      @confirm="confirmDeleteUser"
      @cancel="cancelDeleteUser"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import api from '@/api';
import { toast } from 'sonner';
import ThePopup from '@/components/common/popup/ThePopup.vue';
const users = ref([]);
const loading = ref(false);
const searchQuery = ref('');
const roleFilter = ref('');
const statusFilter = ref('');
const currentPage = ref(1);
const itemsPerPage = ref(10);
const showEditModal = ref(false);
const editingUser = ref(null);
const showAddModal = ref(false);
const showConfirmPopup = ref(false);
const showErrorPopup = ref(false);
const errorMessage = ref('');
const showBanConfirmPopup = ref(false);
const showDeleteConfirmPopup = ref(false);
const userToBan = ref(null);
const userToDelete = ref(null);
// Biến cho chức năng sắp xếp theo ngày tham gia
const sortOrder = ref('none'); // 'none', 'asc' (cũ nhất trước), 'desc' (mới nhất trước)
const newUser = ref({
  firstName: '',
  lastName: '',
  userName: '',
  password: '',
  address: '',
  dayOfBirth: '',
  role: 'ROLE_USER'
});

// Fetch users from API
const fetchUsers = async () => {
  try {
    loading.value = true;
    const data = await api.admin.getAllUsers();
    
    // Kiểm tra xem data có phải là array không
    if (!Array.isArray(data)) {
      console.warn('API returned non-array data:', data);
      users.value = [];
      return;
    }
    // Map API response to component format - sử dụng đúng cấu trúc từ API
    users.value = data.map(user => {
      return {
        id: user.user_id, // Sử dụng user_id từ API
        name: `${user.firstName} ${user.lastName}`,
        username: user.userName,
        email: user.userName, // API không có email riêng, dùng userName
        role: user.role === 'ROLE_ADMIN' ? 'admin' : 'user',
        // Sử dụng status từ API: ACTION = active, BANNED = banned
        status: user.status === 'ACTION' ? 'active' : 'banned',
        joinDate: user.create_at ? new Date(user.create_at).toISOString().split('T')[0] : '',
        avatar: user.avatarUrl ? user.avatarUrl.trim() : 'https://thumbs.dreamstime.com/b/default-avatar-profile-image-vector-social-media-user-icon-potrait-182347582.jpg',
        accountType: user.accountType, // FREE hoặc PREMIUM
        authProvider: user.authProvider, // EMAIL, GOOGLE, FACEBOOK
        dayOfBirth: user.day_of_birth,
        address: user.address,
        verified: user.verified,
        originalRole: user.role, // ROLE_ADMIN hoặc ROLE_USER
        originalStatus: user.status, // ACTION hoặc BANNED
        firstName: user.firstName,
        lastName: user.lastName,
        createAt: user.create_at,
        updateAt: user.update_at
      };
    });
  } catch (error) {
    console.error('Error fetching users:', error);
    errorMessage.value = `Không thể tải danh sách người dùng!\n\nLỗi: ${error.message}`;
    showErrorPopup.value = true;
  } finally {
    loading.value = false;
  }
};

// Không cần hàm searchUsers nữa - sử dụng client-side filtering

// Computed properties - Client-side filtering
const filteredUsers = computed(() => {
  let result = users.value;
  
  // Search theo tên người dùng (client-side)
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(user => 
      user.name.toLowerCase().includes(query) ||
      user.username.toLowerCase().includes(query) ||
      user.email.toLowerCase().includes(query)
    );
  }
  
  // Áp dụng role filter
  if (roleFilter.value) {
    result = result.filter(user => user.role === roleFilter.value);
  }
  
  // Áp dụng status filter
  if (statusFilter.value) {
    result = result.filter(user => user.status === statusFilter.value);
  }
  
  // Áp dụng sắp xếp theo ngày tham gia
  if (sortOrder.value !== 'none') {
    result = [...result].sort((a, b) => {
      const dateA = new Date(a.createAt || a.joinDate);
      const dateB = new Date(b.createAt || b.joinDate);
      
      if (sortOrder.value === 'asc') {
        return dateA - dateB; // Cũ nhất trước
      } else if (sortOrder.value === 'desc') {
        return dateB - dateA; // Mới nhất trước
      }
      return 0;
    });
  }
  
  return result;
});

const totalPages = computed(() => 
  Math.ceil(filteredUsers.value.length / itemsPerPage.value)
);

const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return filteredUsers.value.slice(start, end);
});

const visiblePages = computed(() => {
  const pages = [];
  const total = totalPages.value;
  const current = currentPage.value;
  
  if (total <= 7) {
    // Hiển thị tất cả trang nếu ít hơn 7 trang
    for (let i = 1; i <= total; i++) {
      pages.push(i);
    }
  } else {
    // Luôn hiển thị trang đầu
    pages.push(1);
    
    if (current <= 4) {
      // Nếu trang hiện tại gần đầu
      for (let i = 2; i <= 5; i++) {
        pages.push(i);
      }
      pages.push('...');
      pages.push(total);
    } else if (current >= total - 3) {
      // Nếu trang hiện tại gần cuối
      pages.push('...');
      for (let i = total - 4; i <= total; i++) {
        pages.push(i);
      }
    } else {
      // Trang hiện tại ở giữa
      pages.push('...');
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i);
      }
      pages.push('...');
      pages.push(total);
    }
  }
  
  return pages;
});

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

// Toggle sắp xếp theo ngày tham gia
const toggleDateSort = () => {
  if (sortOrder.value === 'none') {
    sortOrder.value = 'desc'; // Mới nhất trước
  } else if (sortOrder.value === 'desc') {
    sortOrder.value = 'asc'; // Cũ nhất trước
  } else {
    sortOrder.value = 'none'; // Không sắp xếp
  }
};

// Lấy icon cho sort
const getSortIcon = () => {
  if (sortOrder.value === 'desc') {
    return '↓'; // Mới nhất trước
  } else if (sortOrder.value === 'asc') {
    return '↑'; // Cũ nhất trước
  }
  return '↕'; // Không sắp xếp
};

const handleSearch = () => {
  // Reset về trang đầu khi search
  currentPage.value = 1;
  // Không cần gọi API, chỉ cần computed property filteredUsers sẽ tự động filter
};

const editUser = (user) => {
  editingUser.value = { ...user };
  showEditModal.value = true;
};

const saveUserEdit = async () => {
  try {
    // Prepare data for API - theo đúng cấu trúc body yêu cầu từ API response
    const updateData = {
      userName: editingUser.value.username,
      firstName: editingUser.value.firstName,
      lastName: editingUser.value.lastName,
      avatarUrl: editingUser.value.avatar || 'https://thumbs.dreamstime.com/b/default-avatar-profile-image-vector-social-media-user-icon-potrait-182347582.jpg',
      address: editingUser.value.address,
      day_of_birth: editingUser.value.dayOfBirth,
      accountType: editingUser.value.accountType || 'FREE',
      role: editingUser.value.originalRole // Sử dụng trực tiếp originalRole từ dropdown
    };
    
    // Sử dụng user_id để gọi API update
    await api.admin.updateUser(editingUser.value.id, updateData);
    
    // Update local data với dữ liệu mới
    const index = users.value.findIndex(u => u.id === editingUser.value.id);
    if (index !== -1) {
      users.value[index] = { 
        ...users.value[index],
        name: `${updateData.firstName} ${updateData.lastName}`,
        firstName: updateData.firstName,
        lastName: updateData.lastName,
        username: updateData.userName,
        avatar: updateData.avatarUrl,
        address: updateData.address,
        dayOfBirth: updateData.day_of_birth,
        accountType: updateData.accountType,
        originalRole: updateData.role,
        role: updateData.role === 'ROLE_ADMIN' ? 'admin' : 'user' // Cập nhật role hiển thị
      };
    }
    
    showEditModal.value = false;
    toast.success('Cập nhật người dùng thành công');
  } catch (error) {
    console.error('Error updating user:', error);
    errorMessage.value = `Không thể cập nhật người dùng!\n\nLỗi: ${error.message}`;
    showErrorPopup.value = true;
  }
};

const banUser = (user) => {
  userToBan.value = user;
  showBanConfirmPopup.value = true;
};

const getBanConfirmMessage = () => {
  if (!userToBan.value) return '';
  return `Bạn có chắc chắn muốn cấm người dùng "${userToBan.value.name}" không?\n\nHành động này sẽ ngăn người dùng truy cập vào hệ thống.`;
};

const confirmBanUser = async () => {
  showBanConfirmPopup.value = false;
  if (!userToBan.value) return;
  
  try {
    // Sử dụng API updateStatus với status BANNED
    const response = await api.admin.updateStatus(userToBan.value.id, 'BAN');
    // Refresh toàn bộ danh sách từ server để đảm bảo dữ liệu đồng bộ
    await fetchUsers();
    
    toast.success('Đã cấm người dùng');
  } catch (error) {
    console.error('=== ERROR BANNING USER ===');
    console.error('Error details:', error);
    console.error('Error response:', error.response?.data);
    errorMessage.value = `Không thể cấm người dùng!\n\nLỗi: ${error.message}`;
    showErrorPopup.value = true;
  } finally {
    userToBan.value = null;
  }
};

const cancelBanUser = () => {
  showBanConfirmPopup.value = false;
  userToBan.value = null;
};

const unbanUser = async (user) => {
  try {
    // Sử dụng API updateStatus với status ACTION
    await api.admin.updateStatus(user.id, 'ACTION');
    // Refresh toàn bộ danh sách từ server để đảm bảo dữ liệu đồng bộ
    await fetchUsers();
    
    toast.success('Đã bỏ cấm người dùng');
  } catch (error) {
    console.error('=== ERROR UNBANNING USER ===');
    console.error('Error details:', error);
    console.error('Error response:', error.response?.data);
    errorMessage.value = `Không thể bỏ cấm người dùng!\n\nLỗi: ${error.message}`;
    showErrorPopup.value = true;
  }
};

const deleteUser = (user) => {
  userToDelete.value = user;
  showDeleteConfirmPopup.value = true;
};

const getDeleteConfirmMessage = () => {
  if (!userToDelete.value) return '';
  return `Bạn có chắc chắn muốn xóa người dùng "${userToDelete.value.name}" không?\n\nHành động này không thể hoàn tác và sẽ xóa vĩnh viễn tất cả dữ liệu của người dùng.`;
};

const confirmDeleteUser = async () => {
  showDeleteConfirmPopup.value = false;
  if (!userToDelete.value) return;
  
  try {
    // Sử dụng user_id để gọi API delete
    await api.admin.deleteUser(userToDelete.value.id);
    
    // Refresh toàn bộ danh sách từ server
    await fetchUsers();
    
    toast.success('Đã xóa người dùng');
  } catch (error) {
    console.error('=== ERROR DELETING USER ===');
    console.error('Error details:', error);
    console.error('Error response:', error.response?.data);
    errorMessage.value = `Không thể xóa người dùng!\n\nLỗi: ${error.message}`;
    showErrorPopup.value = true;
  } finally {
    userToDelete.value = null;
  }
};

const cancelDeleteUser = () => {
  showDeleteConfirmPopup.value = false;
  userToDelete.value = null;
};

// Hiển thị popup xác nhận
const showConfirmation = () => {
  showConfirmPopup.value = true;
};

// Tạo message cho popup xác nhận
const getConfirmMessage = () => {
  return `Bạn có chắc chắn muốn thêm người dùng với thông tin sau không?

Họ tên: ${newUser.value.firstName} ${newUser.value.lastName}
Tên đăng nhập: ${newUser.value.userName}
Địa chỉ: ${newUser.value.address}
Ngày sinh: ${newUser.value.dayOfBirth}
Vai trò: ${newUser.value.role === 'ROLE_ADMIN' ? 'Quản trị viên' : 'Người dùng'}`;
};

// Xác nhận thêm người dùng
const confirmAddUser = async () => {
  showConfirmPopup.value = false;
  await saveNewUser();
};

// Hủy thêm người dùng
const cancelAddUser = () => {
  showConfirmPopup.value = false;
};

// Đóng popup lỗi
const closeErrorPopup = () => {
  showErrorPopup.value = false;
  errorMessage.value = '';
};

const saveNewUser = async () => {
  try {
    // Prepare data for API - exact format as tested in Postman
    const userData = {
      userName: newUser.value.userName,
      passWord: newUser.value.passWord,
      firstName: newUser.value.firstName,
      lastName: newUser.value.lastName,
      address: newUser.value.address,
      day_of_birth: newUser.value.dayOfBirth, // Format: YYYY-MM-DD
      role: newUser.value.role
    };
    
    await api.admin.createUser(userData);
    // Refresh user list
    await fetchUsers();
    
    // Reset form
    newUser.value = {
      firstName: '',
      lastName: '',
      userName: '',
      password: '',
      address: '',
      dayOfBirth: '',
      role: 'ROLE_USER'
    };
    
    // Close modal
    showAddModal.value = false;
    
    toast.success('Đã thêm người dùng mới thành công');
  } catch (error) {
    console.error('Error creating user:', error);
    
    // Hiển thị popup lỗi thay vì toast
    errorMessage.value = `Không thể thêm người dùng!\n\nLỗi: ${error.message}`;
    showErrorPopup.value = true;
  }
};

// Watchers để reset trang khi thay đổi filter
watch([roleFilter, statusFilter], () => {
  currentPage.value = 1;
});

// Load users when component mounts
onMounted(() => {
  fetchUsers();
});
</script>

<style lang="scss" scoped>
@use '@/components/layout/admin-content/users/UserManagement.scss';
</style>