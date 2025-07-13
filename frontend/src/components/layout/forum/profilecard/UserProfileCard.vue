<template>
  <div class="user-profile-card" @mousedown.stop>
    <div class="card-content">
      <div class="card-header">
        <img :src="user.avatar" :alt="user.username" class="profile-avatar">
        <div class="profile-info">
          <router-link :to="profileLink" class="username-link">
            <h2 class="username">{{ user.username }}</h2>
          </router-link>
          <p class="full-name">{{ user.fullName || '' }}</p>
          <p class="title">{{ user.title || '' }}</p>
        </div>
        <div class="card-actions">
          <button class="btn btn-primary" @click="$emit('send-message', user)"><i class="fas fa-envelope"></i> Gửi tin nhắn</button>
        </div>
      </div>
      <div class="card-body">
        <p class="bio">{{ user.bio || '' }}</p>
        <div class="location-website">
          <span v-if="user.website && user.websiteUrl"><i class="fas fa-globe"></i> <a :href="user.websiteUrl" target="_blank">{{ user.website }}</a></span>
          <span v-if="user.location"><i class="fas fa-map-marker-alt"></i> {{ user.location }}</span>
        </div>
        <div class="user-stats">
          <div class="stat-item">
            <div>Đã đăng</div>
            <div class="stat-value">{{ user.stats?.posted || 0 }}</div>
          </div>
          <div class="stat-item">
            <div>Tham gia</div>
            <div class="stat-value">{{ user.stats?.joined || '-' }}</div>
          </div>
          <div class="stat-item">
            <div>Đã đọc</div>
            <div class="stat-value">{{ user.stats?.read || 0 }}</div>
          </div>
          <div class="stat-item">
            <div>Giải pháp</div>
            <div class="stat-value">{{ user.stats?.solutions || 0 }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  user: {
    type: Object,
    required: true
  },
  postId: {
    type: [String, Number],
    default: null
  },
  postTitle: {
    type: String,
    default: null
  }
});

defineEmits(['send-message']);

const profileLink = computed(() => {
  let link = `/forum/u/${props.user.username}`;
  if (props.postId && props.postTitle) {
    link += `?fromPostId=${props.postId}&fromPostTitle=${encodeURIComponent(props.postTitle)}`;
  }
  return link;
});
</script>

<style lang="scss" scoped>
@use '@/components/layout/forum/profilecard/UserProfileCard.scss';
</style> 