`<template>
    <div class="dashboard">
      <!-- Statistics Cards -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon users">
            <i class="fas fa-users"></i>
          </div>
          <div class="stat-info">
            <h3>Người dùng</h3>
            <div class="stat-value">{{ stats.totalUsers }}</div>
            <div class="stat-change increase">
              <i class="fas fa-arrow-up"></i>
              {{ stats.newUsers }} mới trong tháng
            </div>
          </div>
        </div>
  
        <div class="stat-card">
          <div class="stat-icon posts">
            <i class="fas fa-file-alt"></i>
          </div>
          <div class="stat-info">
            <h3>Bài viết</h3>
            <div class="stat-value">{{ stats.totalPosts }}</div>
            <div class="stat-change increase">
              <i class="fas fa-arrow-up"></i>
              {{ stats.newPosts }} mới trong tháng
            </div>
          </div>
        </div>
  
        <div class="stat-card">
          <div class="stat-icon reports">
            <i class="fas fa-flag"></i>
          </div>
          <div class="stat-info">
            <h3>Báo cáo</h3>
            <div class="stat-value">{{ stats.pendingReports }}</div>
            <div class="stat-change warning">
              Chờ xử lý
            </div>
          </div>
        </div>
  
        <div class="stat-card">
          <div class="stat-icon feedback">
            <i class="fas fa-comments"></i>
          </div>
          <div class="stat-info">
            <h3>Phản hồi</h3>
            <div class="stat-value">{{ stats.pendingFeedback }}</div>
            <div class="stat-change warning">
              Chờ xử lý
            </div>
          </div>
        </div>
      </div>
  
      <!-- Recent Activity -->
      <div class="recent-activity">
        <h2>Hoạt động gần đây</h2>
        
        <div class="activity-list">
          <div v-for="activity in recentActivities" 
            :key="activity.id" 
            class="activity-item"
          >
            <div class="activity-icon" :class="activity.type">
              <i :class="getActivityIcon(activity.type)"></i>
            </div>
            <div class="activity-content">
              <div class="activity-header">
                <span class="activity-title">{{ activity.title }}</span>
                <span class="activity-time">{{ formatTime(activity.timestamp) }}</span>
              </div>
              <p class="activity-description">{{ activity.description }}</p>
            </div>
          </div>
        </div>
      </div>
  
      <!-- Quick Actions -->
      <div class="quick-actions">
        <h2>Thao tác nhanh</h2>
        
        <div class="actions-grid">
          <button class="action-btn" @click="navigateTo('admin-users')">
            <i class="fas fa-user-plus"></i>
            <span>Thêm người dùng</span>
          </button>
          
          <button class="action-btn" @click="navigateTo('admin-reports')">
            <i class="fas fa-exclamation-circle"></i>
            <span>Xem báo cáo</span>
          </button>
          
          <button class="action-btn" @click="navigateTo('admin-feedback')">
            <i class="fas fa-reply"></i>
            <span>Trả lời phản hồi</span>
          </button>
          
          <button class="action-btn">
            <i class="fas fa-cog"></i>
            <span>Cài đặt hệ thống</span>
          </button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  
  const router = useRouter();
  
  // Mock data - replace with real API calls
  const stats = ref({
    totalUsers: 1234,
    newUsers: 56,
    totalPosts: 789,
    newPosts: 23,
    pendingReports: 12,
    pendingFeedback: 8
  });
  
  const recentActivities = ref([
    {
      id: 1,
      type: 'user',
      title: 'Người dùng mới đăng ký',
      description: 'John Doe đã tạo tài khoản mới',
      timestamp: new Date(Date.now() - 30 * 60000) // 30 minutes ago
    },
    {
      id: 2,
      type: 'report',
      title: 'Báo cáo mới',
      description: 'Một bài viết đã bị báo cáo vi phạm',
      timestamp: new Date(Date.now() - 2 * 3600000) // 2 hours ago
    },
    {
      id: 3,
      type: 'feedback',
      title: 'Phản hồi mới',
      description: 'Người dùng gửi phản hồi về tính năng mới',
      timestamp: new Date(Date.now() - 5 * 3600000) // 5 hours ago
    }
  ]);
  
  const getActivityIcon = (type) => {
    const icons = {
      user: 'fas fa-user',
      report: 'fas fa-flag',
      feedback: 'fas fa-comment',
      post: 'fas fa-file-alt'
    };
    return icons[type] || 'fas fa-info-circle';
  };
  
  const formatTime = (timestamp) => {
    const now = new Date();
    const diff = now - timestamp;
    
    // Less than 1 minute
    if (diff < 60000) {
      return 'Vừa xong';
    }
    
    // Less than 1 hour
    if (diff < 3600000) {
      const minutes = Math.floor(diff / 60000);
      return `${minutes} phút trước`;
    }
    
    // Less than 1 day
    if (diff < 86400000) {
      const hours = Math.floor(diff / 3600000);
      return `${hours} giờ trước`;
    }
    
    // More than 1 day
    const days = Math.floor(diff / 86400000);
    return `${days} ngày trước`;
  };
  
  const navigateTo = (route) => {
    router.push({ name: route });
  };
  </script>
  
  <style lang="scss" scoped>
  .dashboard {
    display: flex;
    flex-direction: column;
    gap: 30px;
  }
  
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
  }
  
  .stat-card {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 20px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    
    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      i {
        font-size: 24px;
        color: #fff;
      }
      
      &.users {
        background: linear-gradient(135deg, #4299e1, #2b6cb0);
      }
      
      &.posts {
        background: linear-gradient(135deg, #48bb78, #2f855a);
      }
      
      &.reports {
        background: linear-gradient(135deg, #ed8936, #c05621);
      }
      
      &.feedback {
        background: linear-gradient(135deg, #667eea, #4c51bf);
      }
    }
    
    .stat-info {
      flex: 1;
      
      h3 {
        margin: 0;
        font-size: 0.9rem;
        color: #718096;
      }
      
      .stat-value {
        font-size: 1.8rem;
        font-weight: 600;
        color: #2d3748;
        margin: 5px 0;
      }
      
      .stat-change {
        font-size: 0.85rem;
        display: flex;
        align-items: center;
        gap: 5px;
        
        &.increase {
          color: #48bb78;
        }
        
        &.decrease {
          color: #e53e3e;
        }
        
        &.warning {
          color: #ed8936;
        }
        
        i {
          font-size: 12px;
        }
      }
    }
  }
  
  .recent-activity {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    
    h2 {
      margin: 0 0 20px;
      font-size: 1.25rem;
      color: #2d3748;
    }
  }
  
  .activity-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }
  
  .activity-item {
    display: flex;
    align-items: flex-start;
    gap: 15px;
    padding: 15px;
    border-radius: 8px;
    background: #f7fafc;
    
    .activity-icon {
      width: 40px;
      height: 40px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      i {
        color: #fff;
      }
      
      &.user {
        background: #4299e1;
      }
      
      &.report {
        background: #ed8936;
      }
      
      &.feedback {
        background: #667eea;
      }
      
      &.post {
        background: #48bb78;
      }
    }
    
    .activity-content {
      flex: 1;
      
      .activity-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 5px;
        
        .activity-title {
          font-weight: 500;
          color: #2d3748;
        }
        
        .activity-time {
          font-size: 0.85rem;
          color: #718096;
        }
      }
      
      .activity-description {
        margin: 0;
        color: #4a5568;
        font-size: 0.9rem;
      }
    }
  }
  
  .quick-actions {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    
    h2 {
      margin: 0 0 20px;
      font-size: 1.25rem;
      color: #2d3748;
    }
  }
  
  .actions-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 15px;
  }
  
  .action-btn {
    background: #f7fafc;
    border: none;
    border-radius: 8px;
    padding: 15px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    cursor: pointer;
    transition: all 0.2s;
    
    i {
      font-size: 24px;
      color: #4a5568;
    }
    
    span {
      font-size: 0.9rem;
      color: #2d3748;
      font-weight: 500;
    }
    
    &:hover {
      background: #edf2f7;
      transform: translateY(-2px);
    }
  }
  </style>`