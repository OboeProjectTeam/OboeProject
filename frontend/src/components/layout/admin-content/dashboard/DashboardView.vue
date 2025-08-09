`<template>
    <div class="dashboard">
      <!-- Statistics Cards -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon users">
            <i class="fas fa-users"></i>
          </div>
          <div class="stat-info">
            <h3>{{ t('admin.users') }}</h3>
            <div class="stat-value">{{ stats.totalUsers }}</div>
            <div class="stat-change increase">
              <i class="fas fa-arrow-up"></i>
              {{ t('admin.newInMonth', { count: stats.newUsers }) }}
            </div>
          </div>
        </div>
  
        <div class="stat-card">
          <div class="stat-icon posts">
            <i class="fas fa-file-alt"></i>
          </div>
          <div class="stat-info">
            <h3>{{ t('forum.posts') }}</h3>
            <div class="stat-value">{{ stats.totalPosts }}</div>
            <div class="stat-change increase">
              <i class="fas fa-arrow-up"></i>
              {{ t('admin.newInMonth', { count: stats.newPosts }) }}
            </div>
          </div>
        </div>
  
        <div class="stat-card">
          <div class="stat-icon reports">
            <i class="fas fa-flag"></i>
          </div>
          <div class="stat-info">
            <h3>{{ t('admin.reports') }}</h3>
            <div class="stat-value">{{ stats.pendingReports }}</div>
            <div class="stat-change warning">
              {{ t('admin.pending') }}
            </div>
          </div>
        </div>
  
        <div class="stat-card">
          <div class="stat-icon feedback">
            <i class="fas fa-comments"></i>
          </div>
          <div class="stat-info">
            <h3>{{ t('admin.feedback') }}</h3>
            <div class="stat-value">{{ stats.pendingFeedback }}</div>
            <div class="stat-change warning">
              {{ t('admin.pending') }}
            </div>
          </div>
        </div>
      </div>
  
      <!-- Recent Activity -->
      <div class="recent-activity">
        <h2>{{ t('admin.recentActivity') }}</h2>
        
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
        <h2>{{ t('admin.quickActions') }}</h2>
        
        <div class="actions-grid">
          <button class="action-btn" @click="navigateTo('admin-users')">
            <i class="fas fa-user-plus"></i>
            <span>{{ t('admin.addUser') }}</span>
          </button>
          
          <button class="action-btn" @click="navigateTo('admin-reports')">
            <i class="fas fa-exclamation-circle"></i>
            <span>{{ t('admin.viewReports') }}</span>
          </button>
          
          <button class="action-btn" @click="navigateTo('admin-feedback')">
            <i class="fas fa-reply"></i>
            <span>{{ t('admin.replyFeedback') }}</span>
          </button>
          
          <button class="action-btn">
            <i class="fas fa-cog"></i>
            <span>{{ t('admin.systemSettings') }}</span>
          </button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import api from '@/api';
import { toast } from 'sonner';

const router = useRouter();
const { t } = useI18n();

const stats = ref({
  totalUsers: 0,
  newUsers: 0,
  totalPosts: 0,
  newPosts: 0,
  pendingReports: 0,
  pendingFeedback: 0
});

const recentActivities = ref([]);

// Fetch dashboard data from API
const fetchDashboardData = async () => {
  try {
    const data = await api.dashboard.getAdminDashboard();
    
    // Map API response to component data
    stats.value = {
      totalUsers: data.summary.users.count,
      newUsers: data.summary.users.monthly_change,
      totalPosts: data.summary.posts.count,
      newPosts: data.summary.posts.monthly_change,
      pendingReports: data.summary.post_reports.count,
      pendingFeedback: data.summary.feedback.count
    };

    // Map recent activities
    recentActivities.value = data.recent_activities.map((activity, index) => ({
      id: index + 1,
      type: getActivityType(activity.type),
      title: activity.type,
      description: activity.message,
      timestamp: activity.time
    }));
  } catch (error) {
    console.error('Error fetching dashboard data:', error);
    toast.error(t('admin.loadDashboardError'));
  }
};

// Map activity type from API to component type
const getActivityType = (apiType) => {
  if (apiType.includes('Người dùng') || apiType.includes('đăng ký')) return 'user';
  if (apiType.includes('Báo cáo') || apiType.includes('báo cáo')) return 'report';
  if (apiType.includes('Phản hồi') || apiType.includes('phản hồi')) return 'feedback';
  if (apiType.includes('Bài viết') || apiType.includes('bài viết')) return 'post';
  return 'info';
};

onMounted(() => {
  fetchDashboardData();
});
  
  const getActivityIcon = (type) => {
    const icons = {
      user: 'fas fa-user',
      report: 'fas fa-flag',
      feedback: 'fas fa-comment',
      post: 'fas fa-file-alt'
    };
    return icons[type] || 'fas fa-info-circle';
  };
  
  const formatTime = (timeString) => {
  // API trả về chuỗi thời gian như "20 giờ trước", "5 ngày trước"
  // Trả về trực tiếp chuỗi từ API
  return timeString;
};
  
  const navigateTo = (route) => {
    router.push({ name: route });
  };
  </script>
  
  <style lang="scss" scoped>
    @use '@/components/layout/admin-content/dashboard/DashboardView.scss';
  </style>