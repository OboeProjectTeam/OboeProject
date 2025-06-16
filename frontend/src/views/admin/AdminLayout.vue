<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <div class="admin-sidebar" :class="{ 'collapsed': isSidebarCollapsed }">
      <div class="sidebar-header">
          <img src="/src/assets/img/logo_tab_oboe.svg" alt="Logo" class="w-45px" />
        <h1>Admin Panel</h1>
      </div>
      
      <nav class="sidebar-nav">
        <router-link 
          to="/admin" 
          class="nav-item" 
          :class="{ 'active': $route.path === '/admin' }"
          :title="isSidebarCollapsed ? 'Tổng quan' : ''"
          exact
        >
          <i class="fas fa-chart-line"></i>
          <span>Tổng quan</span>
        </router-link>
        
        <router-link 
          to="/admin/users" 
          class="nav-item" 
          :class="{ 'active': $route.path.startsWith('/admin/users') }"
          :title="isSidebarCollapsed ? 'Quản lý người dùng' : ''"
        >
          <i class="fas fa-users"></i>
          <span>Quản lý người dùng</span>
        </router-link>
        
        <router-link 
          to="/admin/reports" 
          class="nav-item" 
          :class="{ 'active': $route.path.startsWith('/admin/reports') }"
          :title="isSidebarCollapsed ? 'Bài viết bị báo cáo' : ''"
        >
          <i class="fas fa-flag"></i>
          <span>Bài viết bị báo cáo</span>
        </router-link>
        
        <router-link 
          to="/admin/feedback" 
          class="nav-item" 
          :class="{ 'active': $route.path.startsWith('/admin/feedback') }"
          :title="isSidebarCollapsed ? 'Đóng góp ý kiến' : ''"
        >
          <i class="fas fa-comments"></i>
          <span>Đóng góp ý kiến</span>
        </router-link>

        <router-link 
          to="/" 
          class="nav-item"
          :title="isSidebarCollapsed ? 'Quay lại trang chủ' : ''"
        >
          <i class="fas fa-arrow-left"></i>
          <span>Quay lại trang chủ</span>
        </router-link>
      </nav>

      <!-- Move toggle button outside of header -->
      <button class="toggle-sidebar" @click="toggleSidebar">
        <i class="fas" :class="isSidebarCollapsed ? 'fa-chevron-right' : 'fa-chevron-left'"></i>
      </button>
    </div>

    <!-- Main Content -->
    <div class="admin-main">
      <div class="admin-header">
        <div class="header-left">
          <h2>{{ currentPageTitle }}</h2>
        </div>
        <div class="header-right">
          <div class="admin-profile">
            <img :src="adminAvatar" alt="Admin" class="admin-avatar">
            <span class="admin-name">{{ adminName }}</span>
            <button class="btn-logout" @click="handleLogout">
              <i class="fas fa-sign-out-alt"></i>
            </button>
          </div>
        </div>
      </div>
      
      <div class="admin-content">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

// Sidebar state
const isSidebarCollapsed = ref(false);

const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value;
};

// Mock admin data - replace with real data from your auth system
const adminAvatar = 'https://i.pravatar.cc/150?u=admin';
const adminName = 'Admin';

const currentPageTitle = computed(() => {
  switch (route.path) {
    case '/admin':
      return 'Tổng quan';
    case '/admin/users':
      return 'Quản lý người dùng';
    case '/admin/reports':
      return 'Bài viết bị báo cáo';
    case '/admin/feedback':
      return 'Đóng góp ý kiến';
    default:
      return 'Admin Panel';
  }
});

const handleLogout = () => {
  // Handle logout logic here
  router.push('/login');
};
</script>

<style lang="scss" scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
}

.admin-sidebar {
  width: 280px;
  min-width: 280px;
  background: #1a1c23;
  color: #fff;
  padding: 20px;
  display: flex;
  flex-direction: column;
  position: relative;
  transition: all 0.3s ease;
  z-index: 100;

  &.collapsed {
    width: 80px;
    min-width: 80px;
    padding: 20px 10px;

    .sidebar-header {
      justify-content: center;

      h1 {
        opacity: 0;
        width: 0;
        margin: 0;
      }
    }

    .nav-item {
      justify-content: center;
      padding: 12px;

      span {
        opacity: 0;
        width: 0;
        margin: 0;
      }

      i {
        margin: 0;
      }
    }
  }

  .sidebar-header {
    display: flex;
    align-items: center;
    gap: 15px;
    padding-bottom: 20px;
    margin-bottom: 20px;
    border-bottom: 1px solid rgba(255,255,255,0.1);
    position: relative;

    .logo {
      width: 40px;
      height: 40px;
      min-width: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(255,255,255,0.1);
      border-radius: 8px;

      i {
        font-size: 24px;
        color: #fff;
      }
    }

    h1 {
      font-size: 1.5rem;
      margin: 0;
      white-space: nowrap;
      overflow: hidden;
      transition: all 0.3s ease;
    }
  }

  .toggle-sidebar {
    position: absolute;
    right: -12px;
    top: 213px; // Position at 1/3 of viewport height
    width: 24px;
    height: 24px;
    background: #fff;
    border: none;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    box-shadow: 0 2px 5px rgba(0,0,0,0.2);
    z-index: 100;
    transition: all 0.3s ease;

    &:hover {
      background: #f8fafc;
      box-shadow: 0 3px 8px rgba(0,0,0,0.3);
    }

    i {
      font-size: 12px;
      color: #1a1c23;
    }
  }
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow: hidden;

  .nav-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    color: #a0aec0;
    text-decoration: none;
    border-radius: 8px;
    transition: all 0.3s ease;
    overflow: hidden;

    i {
      min-width: 20px;
      text-align: center;
      transition: margin 0.3s ease;
    }

    span {
      white-space: nowrap;
      transition: all 0.3s ease;
    }

    &:hover {
      background: rgba(255,255,255,0.1);
      color: #fff;
    }

    &.active {
      background: #2d3748;
      color: #fff;
    }
  }
}

.admin-main {
  flex: 1;
  background: #f7fafc;
  display: flex;
  flex-direction: column;
  min-width: 0; // Prevent content from overflowing
  transition: all 0.3s ease;
}

.admin-header {
  background: #fff;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e2e8f0;

  h2 {
    margin: 0;
    font-size: 1.5rem;
    color: #2d3748;
  }
}

.admin-profile {
  display: flex;
  align-items: center;
  gap: 12px;

  .admin-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
  }

  .admin-name {
    font-weight: 600;
    color: #2d3748;
  }

  .btn-logout {
    background: none;
    border: none;
    color: #718096;
    cursor: pointer;
    padding: 8px;
    border-radius: 4px;
    transition: all 0.3s ease;

    &:hover {
      background: #f1f5f9;
      color: #e53e3e;
    }
  }
}

.admin-content {
  padding: 20px;
  flex: 1;
  overflow: auto;
}
</style> 