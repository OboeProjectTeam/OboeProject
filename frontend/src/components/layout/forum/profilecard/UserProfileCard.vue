<template>
  <div class="user-profile-card" @mousedown.stop>
    <div class="card-content">
      <div class="card-header">
        <img :src="user.avatar" :alt="user.name" class="profile-avatar">
        <div class="profile-info">
          <router-link :to="profileLink" class="username-link">
            <h2 class="username">{{ user.username }}</h2>
          </router-link>
          <p class="full-name">{{ user.fullName }}</p>
          <p class="title">{{ user.title }}</p>
        </div>
        <div class="card-actions">
          <button class="btn btn-primary" @click="$emit('send-message', user)"><i class="fas fa-envelope"></i> Gửi tin nhắn</button>
          <button class="btn btn-secondary"><i class="fas fa-filter"></i> {{ user.postsInTopic }} bài viết</button>
        </div>
      </div>
      <div class="card-body">
        <p class="bio">{{ user.bio }}</p>
        <div class="location-website">
          <span v-if="user.website"><i class="fas fa-globe"></i> <a :href="user.websiteUrl" target="_blank">{{ user.website }}</a></span>
          <span v-if="user.location"><i class="fas fa-map-marker-alt"></i> {{ user.location }}</span>
        </div>
        <div class="user-stats">
          <div class="stat-item">
            <div>Đã đăng</div>
            <div class="stat-value">{{ user.stats.posted }}</div>
          </div>
          <div class="stat-item">
            <div>Tham gia</div>
            <div class="stat-value">{{ user.stats.joined }}</div>
          </div>
          <div class="stat-item">
            <div>Đã đọc</div>
            <div class="stat-value">{{ user.stats.read }}</div>
          </div>
          <div class="stat-item">
            <div>Giải pháp</div>
            <div class="stat-value">{{ user.stats.solutions }}</div>
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
@use '@/assets/css/index.scss' as *;
@use 'sass:color';

.user-profile-card {
  position: absolute;
  width: 580px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.12);
  z-index: 100;
  font-family: $font-family-regular;
  color: #333;

  .card-content {
    padding: 20px;
  }

  .card-header {
    display: flex;
    gap: 20px;
    padding-bottom: 20px;
    margin-bottom: 15px;

    .profile-avatar {
      width: 90px;
      height: 90px;
      border-radius: 50%;
    }
    .profile-info {
      flex-grow: 1;
      padding-top: 5px;
      .username-link {
        text-decoration: none;
        color: inherit;
        &:hover {
          .username {
            color: $primary-color;
          }
        }
      }
      .username {
        font-size: 2.2rem;
        font-family: $font-family-bold;
        color: #111;
        margin: 0 0 4px;
        transition: color 0.2s ease;
      }
      .full-name, .title {
        margin: 0 0 4px;
        font-size: 1.1rem;
        color: #555;
      }
       .title {
         font-weight: 500;
       }
    }
    .card-actions {
      display: flex;
      flex-direction: column;
      gap: 10px;
      padding-top: 5px;
      .btn {
        padding: 8px 15px;
        font-size: 0.9rem;
        font-weight: 600;
        border-radius: 6px;
        border: 1px solid #ccc;
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 8px;
        transition: all 0.2s ease;
        i { font-size: 0.9em; }
      }
      .btn-primary {
        background-color: $primary-color;
        border-color: $primary-color;
        color: white;
        &:hover { background-color: color.adjust($primary-color, $lightness: -5%); }
      }
      .btn-secondary {
        background-color: #f1f3f5;
        color: #495057;
         &:hover { background-color: #dee2e6; }
      }
    }
  }

  .card-body {
    .bio {
      margin: 0 0 18px;
      font-size: 1.05rem;
    }
    .location-website {
      display: flex;
      gap: 25px;
      margin-bottom: 18px;
      font-size: 0.95rem;
      span {
        display: flex;
        align-items: center;
        gap: 8px;
        i { color: #868e96; font-size: 1.1em; }
        a {
          color: $primary-color;
          text-decoration: none;
          font-weight: 500;
          &:hover { text-decoration: underline; }
        }
      }
    }
    .user-stats {
      display: flex;
      justify-content: space-around;
      text-align: center;
      padding: 15px;
      background: #f8f9fa;
      border-radius: 8px;

      .stat-item {
        flex: 1;
        color: #6c757d;
        font-size: 0.9rem;
        font-weight: 500;

        &:not(:last-child) {
          border-right: 1px solid #e0e0e0;
        }

    
      }
    }
  }
  
  .card-footer {
    border-top: 1px solid #f1f3f5;
    padding-top: 20px;
    margin-top: 20px;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .badge {
      padding: 5px 12px;
      border-radius: 6px;
      font-size: 0.85rem;
      font-weight: 600;
      border: 1px solid #dee2e6;
      display: inline-flex;
      align-items: center;
      gap: 6px;

      &.more-badge {
        background-color: #f8f9fa;
        color: #495057;
      }
    }
  }
}
</style> 