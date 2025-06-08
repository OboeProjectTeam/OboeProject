<template>
    <div class="profile-page" v-if="user">
      <div class="profile-header bd-form">
          <img :src="editableUser.avatar" :alt="editableUser.username" class="profile-avatar-large">
          <div class="profile-main-info">
            <h1 class="username-large">{{ editableUser.username }}</h1>
            
            <p v-if="!isEditing" class="full-name-large">{{ editableUser.fullName }}</p>
            <input v-else type="text" v-model="editableUser.fullName" class="form-control mb-2" maxlength="50">

            <p v-if="!isEditing" class="title-large">{{ editableUser.title }}</p>
            <input v-else type="text" v-model="editableUser.title" class="form-control" maxlength="50">
          </div>
          <div class="profile-actions">
            <div v-if="isMyProfile">
              <template v-if="!isEditing">
                <button @click="startEditing" class="btn btn-primary"><i class="fas fa-pencil-alt"></i> Chỉnh sửa hồ sơ</button>
              </template>
              <template v-else>
                <button @click="saveProfile" class="btn btn-success"><i class="fas fa-save"></i> Lưu thay đổi</button>
                <button @click="cancelEditing" class="btn btn-secondary"><i class="fas fa-times"></i> Hủy</button>
              </template>
            </div>
            <template v-else>
              <button class="btn btn-primary"><i class="fas fa-envelope"></i> Gửi tin nhắn</button>
            </template>
          </div>
      </div>
      
      <div v-if="isMyProfile" class="profile-notification bd-form">
        <i class="fas fa-info-circle"></i>
        <span>7 ngày trải nghiệm các tính năng AI miễn phí sau khi cập nhật đầy đủ thông tin hồ sơ.</span>
      </div>

      <div v-if="isMyProfile" class="personal-info-widget bd-form">
        <h3>Thông tin cá nhân</h3>
        <ul class="personal-info-list">
           <li v-for="field in personalFields" :key="field.key">
            <i :class="field.icon"></i>
            <span v-if="!isEditing">{{ editableUser[field.key] || 'Chưa cập nhật' }}</span>
            <input v-else :type="field.type" v-model="editableUser[field.key]" :placeholder="field.placeholder" class="form-control" :maxlength="field.maxlength">
          </li>
        </ul>
      </div>

      <div class="profile-body ">
        <div class="profile-sidebar">
          <div class="sidebar-widget bd-form">
            <h3>Giới thiệu</h3>
              <p v-if="!isEditing" class="bio-large">{{ editableUser.bio }}</p>
              <textarea v-else v-model="editableUser.bio" class="form-control" rows="5" maxlength="500"></textarea>
            
            <ul>
              <li>
                <i class="fas fa-map-marker-alt"></i>
                 <span v-if="!isEditing">{{ editableUser.location || 'Chưa cập nhật' }}</span>
                 <input v-else type="text" v-model="editableUser.location" placeholder="Vị trí" class="form-control" maxlength="50">
              </li>
               <li>
                  <i class="fas fa-globe"></i>
                  <template v-if="!isEditing">
                    <a :href="editableUser.websiteUrl" target="_blank">{{ editableUser.website }}</a>
                  </template>
                  <input v-else type="text" v-model="editableUser.website" placeholder="Website" class="form-control" maxlength="100">
              </li>
              <li><i class="fas fa-calendar-alt"></i> Tham gia {{ editableUser.stats.joined }}</li>
            </ul>
          </div>
          <div class="sidebar-widget bd-form">
            <h3>Thống kê</h3>
            <ul class="stats-list">
              <li><span>Bài viết</span> <strong>{{ editableUser.stats.posted }}</strong></li>
              <li><span>Chủ đề</span> <strong>{{ editableUser.stats.topics }}</strong></li>
              <li><span>Lượt thích</span> <strong>{{ editableUser.stats.likes }}</strong></li>
              <li><span>Giải pháp</span> <strong>{{ editableUser.stats.solutions }}</strong></li>
            </ul>
          </div>
        </div>
        <div class="profile-main-content bd-form">
          <div class="activity-tabs">
            <button class="tab-button" :class="{ active: currentTab === 'all' }" @click="currentTab = 'all'">Tất cả</button>
            <button class="tab-button" :class="{ active: currentTab === 'post' }" @click="currentTab = 'post'">Chủ đề</button>
            <button class="tab-button" :class="{ active: currentTab === 'reply' }" @click="currentTab = 'reply'">Bình luận</button>
            <button class="tab-button" :class="{ active: currentTab === 'answer' }" @click="currentTab = 'answer'">Trả lời</button>
            <button class="tab-button" :class="{ active: currentTab === 'material' }" @click="currentTab = 'material'">Học Liệu</button>
          </div>
  
          <ul class="activity-list">
            <li v-for="activity in paginatedActivities" :key="activity.id" class="activity-item">
              <div class="activity-icon">
                <i v-if="activity.type === 'post'" class="fas fa-file-alt" title="Chủ đề mới"></i>
                <i v-if="activity.type === 'reply'" class="fas fa-reply" title="Bình luận mới"></i>
                <i v-if="activity.type === 'answer'" class="fas fa-check-square" title="Câu trả lời"></i>
                <i v-if="activity.type === 'material'" class="fas fa-book" title="Học liệu"></i>
              </div>
              <div class="activity-content">
                <div v-if="activity.type === 'post'">
                  <div class="activity-title">
                     <router-link :to="activity.url">Đã tạo chủ đề: {{ activity.title }}</router-link>
                  </div>
                   <div class="activity-meta">Trong mục {{ activity.topic }} • {{ activity.timestamp }}</div>
                </div>
                <div v-if="activity.type === 'reply'">
                   <div class="activity-title">
                     <router-link :to="activity.url">Đã bình luận trong: {{ activity.postTitle }}</router-link>
                  </div>
                  <div class="activity-snippet">"{{ activity.content_snippet }}"</div>
                   <div class="activity-meta">vào lúc {{ activity.timestamp }}</div>
                </div>
                <div v-if="activity.type === 'answer'">
                  <div class="activity-title">
                     <router-link :to="activity.url">Đã trả lời câu hỏi: {{ activity.postTitle }}</router-link>
                  </div>
                  <div class="activity-snippet">"{{ activity.content_snippet }}"</div>
                   <div class="activity-meta">vào lúc {{ activity.timestamp }}</div>
                </div>
                <div v-if="activity.type === 'material'">
                  <div class="activity-title">
                     <router-link :to="activity.url">Đã chia sẻ học liệu: {{ activity.title }}</router-link>
                  </div>
                   <div class="activity-meta">Trong mục {{ activity.topic }} • {{ activity.timestamp }}</div>
                </div>
              </div>
            </li>
             <li v-if="paginatedActivities.length === 0" class="activity-item">
                <p>Không có hoạt động nào để hiển thị.</p>
            </li>
          </ul>
          <div class="pagination-controls" v-if="totalPages > 1">
            <button @click="prevPage" :disabled="currentPage === 1" class="btn btn-secondary">Trước</button>
            <span class="page-info">Trang {{ currentPage }} / {{ totalPages }}</span>
            <button @click="nextPage" :disabled="currentPage === totalPages" class="btn btn-secondary">Sau</button>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="loading">
      Đang tải hồ sơ...
    </div>
  </template>
  
  <script setup>
  import { ref, computed, watch } from 'vue';

    const props = defineProps({
    user: {
        type: Object,
        required: true,
    },
    isMyProfile: {
        type: Boolean,
        default: false,
    },
    });

  const emit = defineEmits(['save-profile']);

  const isEditing = ref(false);
  const editableUser = ref(JSON.parse(JSON.stringify(props.user)));

  const personalFields = [
    { key: 'dob', icon: 'fas fa-birthday-cake', placeholder: 'Ngày sinh', type: 'text', maxlength: 20 },
    { key: 'phone', icon: 'fas fa-phone', placeholder: 'Số điện thoại', type: 'text', maxlength: 20 },
    { key: 'email', icon: 'fas fa-envelope', placeholder: 'Email', type: 'email', maxlength: 100 },
    { key: 'hometown', icon: 'fas fa-home', placeholder: 'Quê quán', type: 'text', maxlength: 50 },
    { key: 'address', icon: 'fas fa-map-pin', placeholder: 'Địa chỉ', type: 'text', maxlength: 150 },
  ];

  watch(() => props.user, (newUser) => {
    if (!isEditing.value) {
      editableUser.value = JSON.parse(JSON.stringify(newUser));
    }
  }, { deep: true });

  function startEditing() {
    isEditing.value = true;
  }

  function cancelEditing() {
    isEditing.value = false;
    editableUser.value = JSON.parse(JSON.stringify(props.user));
  }

  function saveProfile() {
    emit('save-profile', editableUser.value);
    isEditing.value = false;
  }

  const currentTab = ref('all');
  const currentPage = ref(1);
  const itemsPerPage = ref(10);

  const filteredActivities = computed(() => {
    if (!props.user || !props.user.activities) {
      return [];
    }
    if (currentTab.value === 'all') {
      return props.user.activities;
    }
    return props.user.activities.filter(activity => activity.type === currentTab.value);
  });

  const totalPages = computed(() => {
    return Math.ceil(filteredActivities.value.length / itemsPerPage.value);
  });

  const paginatedActivities = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value;
    const end = start + itemsPerPage.value;
    return filteredActivities.value.slice(start, end);
  });

  function nextPage() {
    if (currentPage.value < totalPages.value) {
      currentPage.value++;
    }
  }

  function prevPage() {
    if (currentPage.value > 1) {
      currentPage.value--;
    }
  }

  watch(currentTab, () => {
    currentPage.value = 1;
  });
  
  </script>
  
  <style lang="scss" scoped>
  @use '@/assets/css/index.scss' as *;
  @use 'sass:color';
  
  .profile-page {
    max-width: 1200px;
    margin: 0 auto;
    font-family: $font-family-regular;
  }
  
  .profile-header {
    flex-wrap: wrap;
    border: 1px solid #dee2e6;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    margin-bottom: 25px;
    display: flex;
    align-items: center;
    padding: 20px 30px;
    gap: 25px;
  
    .profile-avatar-large {
      width: 100px;
      height: 100px;
      border-radius: 50%;
    }
  
    .profile-main-info {
      flex-grow: 1;
      .username-large {
        font-size: 2rem;
        font-family: $font-family-bold;
        margin: 0 0 4px;
        color: #111;
      }
      .full-name-large {
        font-size: 1.1rem;
        margin: 0 0 4px;
        color: #555;
      }
      .title-large {
        font-size: 1rem;
        margin: 0;
        color: #777;
      }
    }
  
    .profile-actions {
      .btn {
        padding: 10px 20px;
        font-size: 0.9rem;
        font-weight: 600;
        border-radius: 6px;
        border: 1px solid transparent;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        gap: 8px;
        transition: all 0.2s ease;
        margin-left: 10px;
  
        i { font-size: 0.9em; }
      }
      .btn-primary {
        background-color: $primary-color;
        color: white;
        &:hover { background-color: color.adjust($primary-color, $lightness: -5%); }
      }
      .btn-secondary {
        background-color: #f1f3f5;
        color: #495057;
        border-color: #dee2e6;
        &:hover { background-color: #dee2e6; }
      }
      .btn-success {
        background-color: $primary-color;
        color: white;
         &:hover { background-color: $hover-btn-color; }
      }
    }
  }
  
  .profile-body {
    display: grid;
    grid-template-columns: 300px 1fr;
    gap: 25px;
  }
  
  .profile-sidebar {
    display: flex;
    flex-direction: column;
    gap: 25px;
  }
  
  .sidebar-widget {
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  
    h3 {
      font-family: $font-family-bold;
      font-size: 1.2rem;
      margin: 0 0 15px;
      padding-bottom: 10px;
      border-bottom: 1px solid #eee;
    }
    
    .bio-large {
      font-size: 0.95rem;
      line-height: 1.6;
      color: #555;
    }
  
    ul {
      list-style: none;
      padding: 0;
      margin: 15px 0 0;
      
      li {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 10px;
        font-size: 0.9rem;
        color: #333;
  
        i {
          width: 16px;
          text-align: center;
          color: #888;
        }
  
        a {
          color: $primary-color;
          text-decoration: none;
          &:hover { text-decoration: underline; }
        }
      }
    }
  
    .stats-list {
      margin: 0;
      li {
        justify-content: space-between;
        font-size: 1rem;
        strong {
          font-family: $font-family-bold;
          color: #111;
        }
      }
    }
  }
  
  .profile-main-content {
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    border: 1px solid #dee2e6;
    display: flex;
    flex-direction: column;
    min-height: 500px;
  
    .content-header {
      font-family: $font-family-bold;
      font-size: 1.5rem;
      margin: 0 0 20px;
    }
  }
  
  .loading {
    text-align: center;
    padding: 50px;
    font-size: 1.2rem;
  }
  
  .activity-tabs {
    display: flex;
    border-bottom: 1px solid #dee2e6;
    margin-bottom: 20px;
  }
  
  .tab-button {
    padding: 10px 20px;
    border: none;
    background: none;
    cursor: pointer;
    font-size: 1rem;
    font-weight: 600;
    color: #868e96;
    position: relative;
    top: 1px;
    border-bottom: 3px solid transparent;
  
    &.active {
      color: $primary-color;
      border-bottom-color: $primary-color;
    }
  }
  
  .activity-list {
    list-style: none;
    padding: 0;
    margin: 0;
    flex-grow: 1;
  }
  
  .activity-item {
    padding: 15px 10px;
    border-bottom: 1px solid #f1f3f5;
    display: flex;
    gap: 15px;
    align-items: flex-start;
  
    &:last-child {
      border-bottom: none;
    }
  
    .activity-icon {
      font-size: 1.2rem;
      color: #adb5bd;
      width: 25px;
      text-align: center;
      margin-top: 4px;
    }
  
    .activity-content {
      flex: 1;
  
      .activity-title {
        font-size: 1.1rem;
        font-weight: 600;
        margin-bottom: 5px;
  
        a {
          color: #343a40;
          text-decoration: none;
          &:hover {
            color: $primary-color;
          }
        }
      }
  
      .activity-snippet {
        background-color: #f8f9fa;
        border-left: 3px solid #dee2e6;
        padding: 10px 15px;
        margin: 8px 0;
        border-radius: 0 4px 4px 0;
        font-style: italic;
        color: #495057;
      }
  
      .activity-meta {
        font-size: 0.85rem;
        color: #868e96;
      }
    }
  }

  .personal-info-widget {
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    margin-bottom: 25px;
    border: 1px solid #dee2e6;

    h3 {
      font-family: $font-family-bold;
      font-size: 1.2rem;
      margin: 0 0 15px;
      padding-bottom: 10px;
      border-bottom: 1px solid #eee;
    }
  }

  .personal-info-list {
    list-style: none;
    padding: 0;
    margin: 0;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 15px;

    li {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 0.9rem;
      color: #333;

      i {
        width: 16px;
        text-align: center;
        color: #888;
      }
    }
  }

  .form-control {
    width: 100%;
    padding: 8px 12px;
    font-size: 1rem;
    border: 1px solid #ced4da;
    border-radius: 4px;
    transition: border-color .15s ease-in-out,box-shadow .15s ease-in-out;

    &:focus {
      border-color: #80bdff;
      outline: 0;
      box-shadow: 0 0 0 0.2rem rgba(0,123,255,.25);
    }
  }

  .mb-2 {
    margin-bottom: 0.5rem;
  }

  .editable-field {
    display: flex;
    align-items: center;
    gap: 10px;

    p, span, a {
      margin: 0;
    }
    
    .form-control {
      flex-grow: 1;
    }
  }

  .btn-edit, .btn-save, .btn-cancel {
    background: none;
    border: none;
    cursor: pointer;
    padding: 5px;
    line-height: 1;
    border-radius: 4px;

    &:hover {
      background-color: #f1f3f5;
    }
  }

  .btn-edit { color: #868e96; }
  .btn-save { color: $primary-color; }
  .btn-cancel { color: $primary-color; }

  .profile-notification {
    background-color: #e7f3ff;
    border: 1px solid #b3d7ff;
    color: #004085;
    padding: 15px;
    border-radius: 8px;
    margin-bottom: 25px;
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 0.95rem;

    i {
      font-size: 1.2rem;
    }
  }

  .pagination-controls {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 15px;
    margin-top: 20px;

    .page-info {
      font-weight: 600;
    }

    .btn-secondary {
       background-color: #f1f3f5;
       color: #495057;
       border-color: #dee2e6;
       padding: 8px 16px;
       
       &:hover { background-color: #dee2e6; }
       &:disabled {
         background-color: #e9ecef;
         color: #adb5bd;
         cursor: not-allowed;
       }
    }
  }
  </style> 