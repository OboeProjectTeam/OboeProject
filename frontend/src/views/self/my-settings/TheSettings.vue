<template>
  <div class="settings-page-container">
    <div class="settings-sidebar">
      <h2 class="sidebar-title">Cài đặt</h2>
      <ul class="nav-menu">
        <li>
          <a href="#account" 
             class="nav-link" 
             :class="{ active: currentSection === 'account' }"
             @click="setCurrentSection('account', $event)">
            <i class="fas fa-rocket icon"></i>Nâng cấp
          </a>
        </li>
        <li>
          <a href="#appearance" 
             class="nav-link" 
             :class="{ active: currentSection === 'appearance' }"
             @click="setCurrentSection('appearance', $event)">
            <i class="fas fa-paint-brush icon"></i>Giao diện
          </a>
        </li>
        <li>
          <a href="#privacy" 
             class="nav-link" 
             :class="{ active: currentSection === 'privacy' }"
             @click="setCurrentSection('privacy', $event)">
            <i class="fas fa-shield-alt icon"></i>Bảo mật & Riêng tư
          </a>
        </li>
        <li>
          <a href="#danger" 
             class="nav-link" 
             :class="{ active: currentSection === 'danger' }"
             @click="setCurrentSection('danger', $event)">
            <i class="fas fa-exclamation-triangle icon"></i>Vùng nguy hiểm
          </a>
        </li>
      </ul>
    </div>

    <div class="settings-content">
      <!-- Account Upgrade Section -->
      <div id="account" class="settings-card">
        <h3 class="card-title">Nâng Cấp Tài Khoản</h3>
        <div class="upgrade-promo">
          <div class="promo-icon">
            <i class="fas fa-star"></i>
          </div>
          <div class="promo-text">
            <h4>Mở khóa toàn bộ tính năng với Oboe Pro</h4>
          </div>
          <button class="btn btn-upgrade" @click="goToUpgrade">Nâng cấp ngay</button>
        </div>
      </div>

      <!-- Appearance Section -->
      <div id="appearance" class="settings-card">
        <h3 class="card-title">Giao Diện</h3>
        <!-- Dark Mode -->
        <div class="setting-item">
          <div class="item-info">
            <label>Chế độ tối (Dark Mode)</label>
            <p class="item-description">Thay đổi giao diện để phù hợp với môi trường sáng hoặc tối.</p>
          </div>
          <div class="item-control">
            <label class="switch">
              <input type="checkbox" v-model="isDark">
              <span class="slider round"></span>
            </label>
          </div>
        </div>
        <!-- Language -->
        <div class="setting-item">
          <div class="item-info">
            <label>Ngôn ngữ</label>
            <p class="item-description">Chọn ngôn ngữ hiển thị cho toàn bộ trang web.</p>
          </div>
          <div class="item-control">
            <div class="language-select-wrapper">
              <i class="fas fa-globe select-icon"></i>
              <select class="language-select">
                <option value="vi">Tiếng Việt</option>
                <option value="en">English</option>
                <option value="jp">日本語</option>
              </select>
            </div>
          </div>
        </div>
      </div>

      <!-- Privacy Section -->
      <div id="privacy" class="settings-card">
        <h3 class="card-title">Bảo Mật & Quyền Riêng Tư</h3>
        <!-- Private Account -->
        <div class="setting-item">
          <div class="item-info">
            <label>Tài khoản riêng tư</label>
            <p class="item-description">Nếu bật, hồ sơ và các hoạt động của bạn sẽ không hiển thị công khai.</p>
          </div>
          <div class="item-control">
            <label class="switch">
              <input type="checkbox" checked>
              <span class="slider round"></span>
            </label>
          </div>
        </div>

        <!-- Change Password -->
        <div class="setting-item-divider"></div>
        <div class="setting-item">
          <div class="item-info">
            <label>Đổi mật khẩu</label>
            <p class="item-description">Thay đổi mật khẩu của bạn để tăng cường bảo mật.</p>
          </div>
          <div class="item-control">
            <button v-if="!isChangingPassword" class="btn btn-secondary" @click="isChangingPassword = true">Đổi mật khẩu</button>
            <div v-else class="password-fields">
              <input v-model="oldPassword" type="password" placeholder="Mật khẩu cũ" class="password-input">
              <input v-model="newPassword" type="password" placeholder="Mật khẩu mới" class="password-input">
              <input v-model="confirmPassword" type="password" placeholder="Xác nhận mật khẩu mới" class="password-input">
              <div class="password-buttons">
                <button class="btn btn-secondary" @click="isChangingPassword = false">Hủy</button>
                <button class="btn btn-primary">Lưu thay đổi</button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Danger Zone Section -->
      <div id="danger" class="settings-card danger-zone">
        <h3 class="card-title">Vùng Nguy Hiểm</h3>
        <!-- Delete Account -->
        <div class="setting-item">
          <div class="item-info">
            <label>Xóa tài khoản</label>
            <p class="item-description">Hành động này không thể hoàn tác. Tất cả dữ liệu của bạn sẽ bị xóa vĩnh viễn.</p>
          </div>
          <div class="item-control">
            <button class="btn btn-danger">Xóa tài khoản của tôi</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useDarkMode } from '@/composables/useDarkMode';

const router = useRouter();
const { isDark } = useDarkMode();
const isChangingPassword = ref(false);
const currentSection = ref('account');

const oldPassword = ref('');
const newPassword = ref('');
const confirmPassword = ref('');

const setCurrentSection = (section, event) => {
  if (event) {
    event.preventDefault(); // Prevent default hash navigation
  }
  currentSection.value = section;
  
  // Find the target element and content container
  const targetElement = document.getElementById(section);
  const contentContainer = document.querySelector('.settings-content');
  
  if (targetElement && contentContainer) {
    // Get the relative position of the target element within the content container
    const containerRect = contentContainer.getBoundingClientRect();
    const targetRect = targetElement.getBoundingClientRect();
    const scrollOffset = targetRect.top - containerRect.top + contentContainer.scrollTop - 20; // 20px offset from top

    // Smooth scroll the content container
    contentContainer.scrollTo({
      top: scrollOffset,
      behavior: "smooth"
    });
  }

  // Update URL hash without triggering scroll
  window.history.pushState(null, '', `#${section}`);
};

// Handle hash changes in URL
const handleHashChange = () => {
  const hash = window.location.hash.slice(1);
  if (hash) {
    currentSection.value = hash;
    setCurrentSection(hash);
  }
};

onMounted(() => {
  // Set initial section from URL hash if exists
  if (window.location.hash) {
    const hash = window.location.hash.slice(1);
    currentSection.value = hash;
    // Small delay to ensure elements are rendered
    setTimeout(() => setCurrentSection(hash), 100);
  }
  window.addEventListener('hashchange', handleHashChange);
});

onUnmounted(() => {
  window.removeEventListener('hashchange', handleHashChange);
});

const goToUpgrade = () => {
  router.push('/upgrade');
};
</script>

<style lang="scss" scoped>
@use '@/views/self/my-settings/TheSettings.scss';
</style>