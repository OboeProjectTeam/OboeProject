<template>
  <ProfileDetail 
    v-if="user" 
    :user="user" 
    :is-my-profile="true" 
    :active-tab="activeTab"
    @save-profile="handleProfileSave" 
  />
  <div v-else class="loading">
    Đang tải hồ sơ...
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import ProfileDetail from '@/components/layout/forum/profile/ProfileDetail.vue';
import api from '@/api';

const store = useStore();
const route = useRoute();
const user = ref(null);

const activeTab = computed(() => route.query.tab || 'activities');

function handleProfileSave(updatedUser) {
  user.value = updatedUser;
  // Trong ứng dụng thực tế, gửi API update tại đây
  console.log('Profile saved!', updatedUser);
}

watch(() => route.query.newPost, async (newPost) => {
  if (newPost === 'true') {
    const latestPost = await store.getters['forum/getLatestPost'];
    if (latestPost && user.value?.activities) {
      const newActivity = {
        type: 'post',
        id: `post-${latestPost.id}`,
        title: latestPost.title,
        timestamp: 'Vừa xong',
        topic: latestPost.category,
        url: `/forum/post/${latestPost.id}`
      };
      user.value.activities.unshift(newActivity);
    }
  }
}, { immediate: true });

onMounted(async () => {
  try {
    console.log('Loading profile data...');
    
    // Load profile data with error handling
    let profile, blogs, comments;
    
    try {
      profile = await api.profile.getProfile();
      console.log('Profile loaded:', profile);
    } catch (error) {
      console.error('Failed to load profile:', error);
      profile = null;
    }
    
    try {
      blogs = await api.blog.getUserBlogs();
      console.log('Blogs loaded:', blogs);
    } catch (error) {
      console.error('Failed to load blogs:', error);
      blogs = [];
    }
    
    try {
      comments = await api.comment.getUserComments();
      console.log('Comments loaded:', comments);
    } catch (error) {
      console.error('Failed to load comments:', error);
      comments = [];
    }

    if (profile) {
      const blogActivities = (blogs || []).map(blog => ({
        type: 'post',
        id: `blog-${blog.id}`,
        title: blog.title,
        timestamp: blog.updatedAt || blog.createdAt,
        topic: blog.category || 'Bài viết',
        url: `/blog/${blog.id}`
      }));

      const commentActivities = (comments || []).map(comment => ({
        type: 'reply',
        id: `comment-${comment.id}`,
        postTitle: comment.postTitle || 'Bài viết',
        content_snippet: comment.content ? comment.content.substring(0, 150) + '...' : '',
        timestamp: comment.createdAt,
        url: `/forum/post/${comment.postId || comment.referenceId}`
      }));

      user.value = {
        // Basic user info
        user_id: profile.user_id,
        username: profile.userName,
        firstName: profile.firstName,
        lastName: profile.lastName,
        fullName: `${profile.firstName || ''} ${profile.lastName || ''}`.trim(),
        avatar: profile.avatarUrl,
        role: profile.role,
        verified: profile.verified,
        accountType: profile.accountType,
        authProvider: profile.authProvider,
        status: profile.status,
        
        // Personal info fields for form
        day_of_birth: profile.day_of_birth || '', // Keep original format from API
        email: profile.userName, // userName is email
        address: profile.address || '',
        
        // Computed fields
        title: profile.accountType === 'PREMIUM' ? 'Thành viên Premium' : 'Thành viên',
        
        // Stats
        stats: {
          topics: blogActivities.length,
          solutions: commentActivities.length,
          learning_materials: 0,
          likes: 0,
          joined: profile.create_at ? new Date(profile.create_at).toLocaleDateString('vi-VN') : 'Không rõ'
        },
        
        // Activities
        activities: [...blogActivities, ...commentActivities].sort((a, b) => 
          new Date(b.timestamp) - new Date(a.timestamp)
        )
      };
    } else {
      // Fallback user data if profile fails to load
      user.value = {
        username: 'User',
        firstName: 'Người',
        lastName: 'dùng',
        fullName: 'Người dùng',
        avatar: 'https://ui-avatars.com/api/?name=User',
        title: 'Thành viên',
        email: '',
        address: '',
        day_of_birth: '',
        stats: {
          topics: 0,
          solutions: 0,
          learning_materials: 0,
          likes: 0,
          joined: 'Không rõ'
        },
        activities: []
      };
    }
    
    console.log('Final user data:', user.value);
  } catch (error) {
    console.error('Error loading profile data:', error);
    
    // Set minimal fallback data
    user.value = {
      username: 'User',
      fullName: 'Người dùng',
      avatar: 'https://ui-avatars.com/api/?name=User',
      bio: '',
      website: '',
      stats: {
        topics: 0,
        solutions: 0,
        learning_materials: 0,
        joined: 'Không rõ'
      },
      activities: []
    };
  }
});
</script>
<style lang="scss" scoped>
.loading {
  text-align: center;
  padding: 50px;
  font-size: 1.2rem;
}
</style>
