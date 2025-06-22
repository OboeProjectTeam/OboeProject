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
              <button class="btn btn-primary" @click="handleSendMessage"><i class="fas fa-envelope"></i> Gửi tin nhắn</button>
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
              <li><span>Chủ đề</span> <strong>{{ editableUser.stats.topics }}</strong></li>
              <li><span>Lượt thích</span> <strong>{{ editableUser.stats.likes }}</strong></li>
              <li><span>Bình luận</span> <strong>{{ editableUser.stats.solutions }}</strong></li>
              <li><span>Học liệu</span> <strong>{{ editableUser.stats.learning_materials }}</strong></li>
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
  import { useStore } from 'vuex';
  import { useRouter } from 'vue-router';

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

  const router = useRouter();

  const emit = defineEmits(['save-profile', 'send-message']);

  const store = useStore();

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

  function handleSendMessage() {
    if (router.currentRoute.value.meta.emit) {
      router.currentRoute.value.meta.emit('send-message', props.user);
    }
  }
  
  </script>
  
  <style lang="scss" scoped>
  @use '@/components/layout/forum/profile/ProfileDetail.scss';
  </style> 