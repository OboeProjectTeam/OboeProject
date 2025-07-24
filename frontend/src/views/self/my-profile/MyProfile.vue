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
    const profile = await api.profile.getProfile();
    user.value = {
      username: profile.userName,
      fullName: (profile.lastName || '') + ' ' + (profile.firstName || ''),
      avatar: profile.avatarUrl,
      title: profile.accountType,
      email: profile.userName,
      day_of_birth: profile.day_of_birth,
      address: profile.address,
      bio: profile.bio || '',
      website: profile.website || '',
      websiteUrl: profile.website || '',
      location: profile.location || '',
      stats: {
        joined: profile.create_at?.split('T')[0] || '',
        topics: "",
        solutions: "",
        learning_materials: ""
      },
      activities: []
    };
  } catch (err) {
    console.error('Lỗi tải hồ sơ:', err);
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