<template>
  <ProfileDetail 
    v-if="user" 
    :user="user" 
    :is-my-profile="true" 
    :active-tab="activeTab"
    @save-profile="handleProfileSave" 
  />
  <div v-else class="loading">
    {{ t('profile.loadingProfile') }}
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import ProfileDetail from '@/components/layout/forum/profile/ProfileDetail.vue';
import api from '@/api';

const { t } = useI18n();

const store = useStore();
const route = useRoute();
const user = ref(null);

const activeTab = computed(() => route.query.tab || 'activities');

// Hàm helper để loại bỏ @gmail.com khỏi username
function removeGmailSuffix(username) {
  if (!username) return '';
  return username.replace(/@gmail\.com$/i, '');
}

function handleProfileSave(updatedUser) {
  // Loại bỏ @gmail.com khỏi username nếu có, nhưng giữ nguyên email
  if (updatedUser.username) {
    updatedUser.username = removeGmailSuffix(updatedUser.username);
  }
  if (updatedUser.userName) {
    updatedUser.userName = removeGmailSuffix(updatedUser.userName);
  }
  // Không loại bỏ @gmail.com khỏi email - giữ nguyên
  
  user.value = updatedUser;
  // Cập nhật store với dữ liệu mới
  store.commit('auth/SET_USER', {
    ...store.getters['auth/currentUser'],
    ...updatedUser
  });
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
    const cleanUsername = removeGmailSuffix(profile.userName);
    user.value = {
      username: cleanUsername,
      userName: cleanUsername, // Add this for consistency
      fullName: (profile.lastName || '') + ' ' + (profile.firstName || ''),
      avatar: profile.avatarUrl,
      avatarUrl: profile.avatarUrl, // Add this for consistency
      title: profile.accountType,
      email: profile.userName,
      day_of_birth: profile.day_of_birth || '', // Ensure it's never null
      address: profile.address || '', // Ensure it's never null
      bio: profile.bio || '',
      website: profile.website || '',
      websiteUrl: profile.website || '',
      location: profile.location || '',
      phone: profile.phone || '', // Add phone field
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
@use '@/views/self/my-profile/MyProfile.scss';
</style>