<template>
  <div class="settings-page-container">
    <div class="settings-sidebar">
      <h2 class="sidebar-title">Cài đặt</h2>
      <ul class="nav-menu">
        <li><a href="#account" class="nav-link active"><i class="fas fa-rocket icon"></i>Nâng cấp</a></li>
        <li><a href="#appearance" class="nav-link"><i class="fas fa-paint-brush icon"></i>Giao diện</a></li>
        <li><a href="#privacy" class="nav-link"><i class="fas fa-shield-alt icon"></i>Bảo mật & Riêng tư</a></li>
        <li><a href="#danger" class="nav-link"><i class="fas fa-exclamation-triangle icon"></i>Vùng nguy hiểm</a></li>
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
            <p>Truy cập không giới hạn, tạo học liệu nâng cao và trải nghiệm không quảng cáo.</p>
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
            <p class="item-description">Nếu bật, hồ sơ và các hoạt động của  sẽ không hiển thị công khai.</p>
          </div>
          <div class="item-control">
            <label class="switch">
              <input type="checkbox" checked>
              <span class="slider round"></span>
            </label>
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
            <p class="item-description">Hành động này không thể hoàn tác. Tất cả dữ liệu của  sẽ bị xóa vĩnh viễn.</p>
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
import { useRouter } from 'vue-router';
import { useDarkMode } from '@/composables/useDarkMode';

const router = useRouter();
const { isDark } = useDarkMode();

const goToUpgrade = () => {
  router.push('/upgrade');
};
</script>

<style lang="scss" scoped>
@use '@/assets/css/index.scss' as *;
@use 'sass:color';

.settings-page-container {
  display: flex;
  background-color: #ffffff;
  min-height: calc(100vh - 140px);
  font-family: $font-family-regular;
}

.settings-sidebar {
  width: 260px;
  flex-shrink: 0;
  background-color: #ffffff;
  padding: 30px;
  border-right: 1px solid #e9ecef;

  .sidebar-title {
    font-size: 24px;
    font-weight: 700;
    margin: 0 0 30px 0;
    color: #333;
  }

  .nav-menu {
    list-style: none;
    padding: 0;
    margin: 0;
    .nav-link {
      display: flex;
      align-items: center;
      padding: 12px 15px;
      margin-bottom: 8px;
      border-radius: 8px;
      text-decoration: none;
      color: #495057;
      font-weight: 500;
      transition: background-color 0.2s ease, color 0.2s ease;
      .icon {
        margin-right: 12px;
        width: 20px;
        text-align: center;
      }
      &:hover, &.active {
        background-color: color.adjust($primary-color, $alpha: -0.9);
        color: $primary-color;
      }
    }
  }
}

.settings-content {
  flex-grow: 1;
  padding: 40px;
  max-width: 900px;
}

.settings-card {
  background: #ffffff;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px -10px rgba(0,0,0,0.05);
  border: 1px solid #dfe4e9;

  .card-title {
    font-size: 20px;
    padding: 20px 25px;
    margin: 0;
    border-bottom: 1px solid #dfe4e9;
  }
}

.upgrade-promo {
  display: flex;
  align-items: center;
  padding: 25px;
  background: linear-gradient(45deg, color.adjust($primary-color, $saturation: -15%, $lightness: 10%), color.adjust(#772C3F, $saturation: -10%, $lightness: 15%));
  border-radius: 0 0 12px 12px;
  color: white;

  .promo-icon {
    font-size: 32px;
    margin-right: 20px;
    color: #ffc107;
  }
  .promo-text {
    flex-grow: 1;
    h4 { margin: 0 0 5px 0; }
    p { margin: 0; opacity: 0.9; font-size: 14px; }
  }
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  &:not(:last-child) {
    border-bottom: 1px solid #dfe4e9;
  }
  .item-info {
    label { font-weight: 600; font-size: 16px; color: #343a40; }
    .item-description { font-size: 14px; color: #6c757d; margin: 4px 0 0; }
  }
}

.btn {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 15px;

  &.btn-upgrade {
    background: white;
    color: $primary-color;
    font-weight: 700;
    &:hover { background: #f8f9fa; transform: scale(1.05); }
  }

  &.btn-danger {
    background-color: $primary-color;
    color: white;
    &:hover { background-color: color.adjust($primary-color, $lightness: -7%); }
  }
}

.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 28px;
  input { opacity: 0; width: 0; height: 0; }
  .slider {
    position: absolute;
    cursor: pointer;
    top: 0; left: 0; right: 0; bottom: 0;
    background-color: #ccc;
    transition: .4s;
    &.round { border-radius: 28px; }
    &:before {
      position: absolute;
      content: "";
      height: 20px; width: 20px;
      left: 4px; bottom: 4px;
      background-color: white;
      transition: .4s;
      border-radius: 50%;
    }
  }
  input:checked + .slider { background-color: $primary-color; }
  input:checked + .slider:before { transform: translateX(22px); }
}

.language-select-wrapper {
  position: relative;
  .select-icon {
    position: absolute;
    left: 15px;
    top: 50%;
    transform: translateY(-50%);
    color: #adb5bd;
    pointer-events: none;
  }
  .language-select {
    -webkit-appearance: none;
    -moz-appearance: none;
    appearance: none;
    background-color: #f8f9fa;
    border: 1px solid #dee2e6;
    border-radius: 8px;
    padding: 10px 15px 10px 40px;
    font-size: 15px;
    font-weight: 500;
    color: #495057;
    cursor: pointer;
    width: 150px;
  }
}

.danger-zone {
  border-color: #dc3545;
  .card-title, .item-info label {
    color: #dc3545;
  }
}
</style>