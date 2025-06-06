<template>
  <div class="profile-page-container">
    <div class="profile-card">
      <!-- Header -->
      <div class="card-header">
        <div class="avatar-container">
          <img src="https://ui-avatars.com/api/?name=Joo+Thin&background=d8e2ff&color=4361ee&bold=true&size=128" alt="User Avatar" class="avatar-img">
          <button class="avatar-change-btn">
            <i class="fas fa-camera"></i>
          </button>
        </div>
        <div class="user-identity">
          <h2 class="user-name">Joo Thin</h2>
          <p class="user-email">hoangdu1999s2@gmail.com</p>
        </div>
      </div>

      <!-- Form Body -->
      <div class="card-body">
        <h3 class="section-title">Thông tin cá nhân</h3>
        <div class="form-grid">
          <!-- First Name -->
          <div class="form-group">
            <label for="first-name"><i class="fas fa-user icon"></i>Họ</label>
            <input type="text" id="first-name" value="Joo" disabled>
          </div>
          <!-- Last Name -->
          <div class="form-group">
            <label for="last-name"><i class="fas fa-user icon"></i>Tên</label>
            <input type="text" id="last-name" value="Thin" disabled>
          </div>
          <!-- Date of Birth -->
          <div class="form-group">
            <label for="dob"><i class="fas fa-calendar-alt icon"></i>Ngày sinh</label>
            <input type="date" id="dob" placeholder="Chưa có thông tin">
          </div>
          <!-- Phone Number -->
          <div class="form-group">
            <label for="phone"><i class="fas fa-phone icon"></i>Số điện thoại</label>
            <input type="text" id="phone" placeholder="Chưa có thông tin">
          </div>
          <!-- Hometown -->
          <div class="form-group full-width">
            <label for="hometown"><i class="fas fa-map-marker-alt icon"></i>Quê quán</label>
            <input type="text" id="hometown" placeholder="Chưa có thông tin">
          </div>
          <!-- Address -->
          <div class="form-group full-width">
            <label for="address"><i class="fas fa-home icon"></i>Địa chỉ thường trú</label>
            <textarea id="address" rows="3" placeholder="Chưa có thông tin"></textarea>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="card-footer">
        <button class="btn btn-primary">Lưu thay đổi</button>
        <button class="btn btn-secondary">Hủy bỏ</button>
      </div>
    </div>
  </div>
</template>

<script setup>
// No logic as requested, setup script is minimal.
</script>

<style lang="scss" scoped>
@use '@/assets/css/index.scss' as *;
@use 'sass:color';

.profile-page-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 40px 20px;
  min-height: 100vh;
  font-family: $font-family-regular;
}

.profile-card {
  width: 100%;
  max-width: 850px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 8px 25px -5px rgba(0, 0, 0, 0.08);
  border: 1px solid #e9ecef;
  overflow: hidden;
}

.card-header {
  background: linear-gradient(135deg, color.adjust($btn-primary, $lightness: 5%), color.adjust($btn-primary, $lightness: -10%));
  color: white;
  padding: 30px;
  display: flex;
  align-items: center;
  gap: 25px;
}

.avatar-container {
  position: relative;
  flex-shrink: 0;
  .avatar-img {
    width: 110px;
    height: 110px;
    border-radius: 50%;
    border: 4px solid white;
    object-fit: cover;
    box-shadow: 0 4px 15px rgba(0,0,0,0.2);
  }
  .avatar-change-btn {
    position: absolute;
    bottom: 5px;
    right: 5px;
    background: white;
    border: none;
    border-radius: 50%;
    width: 32px;
    height: 32px;
    display: flex;
    justify-content: center;
    align-items: center;
    color: $btn-primary;
    cursor: pointer;
    box-shadow: 0 2px 5px rgba(0,0,0,0.2);
    transition: transform 0.2s ease;
    &:hover {
      transform: scale(1.1);
    }
  }
}

.user-identity {
  .user-name {
    margin: 0;
    font-size: 26px;
    font-weight: 700;
  }
  .user-email {
    margin: 4px 0 0;
    font-size: 16px;
    opacity: 0.8;
  }
}

.card-body {
  padding: 30px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 25px;
  color: #333;
  padding-bottom: 10px;
  border-bottom: 1px solid #eef2f9;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 25px;
}

.form-group {
  &.full-width {
    grid-column: 1 / -1;
  }
  label {
    display: block;
    margin-bottom: 8px;
    font-weight: 600;
    color: #555;
    font-size: 14px;
    display: flex;
    align-items: center;
    .icon {
      margin-right: 8px;
      color: $btn-primary;
    }
  }
  input, textarea {
    width: 100%;
    padding: 12px 15px;
    border: 1px solid #dcdfe6;
    border-radius: 8px;
    font-size: 15px;
    transition: border-color 0.2s ease;
    background-color: #fdfdfd;
    &:focus {
      outline: none;
      border-color: $btn-primary;
      box-shadow: 0 0 0 3px color.adjust($btn-primary, $alpha: -0.8);
    }
    &[disabled] {
      background-color: #f5f7fa;
      color: #909399;
      cursor: not-allowed;
    }
  }
  textarea {
    resize: vertical;
  }
}

.card-footer {
  padding: 20px 30px;
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  border-top: 1px solid #eef2f9;
}

.btn {
  padding: 10px 25px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 15px;

  &.btn-primary {
    background-color: $btn-primary;
    color: white;
    &:hover {
      background-color: color.adjust($btn-primary, $lightness: -7%);
      box-shadow: 0 4px 15px -5px color.adjust($btn-primary, $alpha: -0.5);
      transform: translateY(-2px);
    }
  }

  &.btn-secondary {
    background-color: #e9ecef;
    color: #495057;
    &:hover {
      background-color: #d8dde3;
    }
  }
}
</style> 