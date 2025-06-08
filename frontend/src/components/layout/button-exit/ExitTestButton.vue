<template>
  <div>
    <button class="exit-button secondary-button" @click="requestExit">
      <i class="fas fa-times-circle"></i>
      Thoát & Ôn Luyện
    </button>

    <Teleport to="body">
      <div v-if="showConfirmExitPopup" class="confirm-exit-popup">
        <div class="modal-overlay" @click="cancelExit"></div>
        <div class="popup-content">
          <h3>Xác nhận thoát</h3>
          <p>Bạn có chắc muốn thoát và quay lại ôn luyện? Tiến trình sẽ không được lưu.</p>
          <div class="popup-actions">
            <button class="secondary-button" @click="cancelExit">
              <i class="fas fa-ban"></i>
              Hủy
            </button>
            <button class="primary-button" @click="confirmExit">
              <i class="fas fa-check-circle"></i>
              Xác nhận
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const emit = defineEmits(['exit']);
const showConfirmExitPopup = ref(false);

const requestExit = () => {
  showConfirmExitPopup.value = true;
};

const cancelExit = () => {
  showConfirmExitPopup.value = false;
};

const confirmExit = () => {
  showConfirmExitPopup.value = false;
  emit('exit');
};
</script>

<style lang="scss" scoped>
/* Scoped styles for the trigger button itself */
.exit-button {
  padding: 8px 16px;
  font-size: 0.9rem;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background-color: #f1f3f5;
  color: #495057;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;

  &:hover {
    background-color: #e9ecef;
  }
}
</style>

<style lang="scss">
/* GLOBAL (non-scoped) styles for the teleported modal */
.confirm-exit-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;

  .modal-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.6);
  }

  .popup-content {
    position: relative;
    background: white;
    padding: 25px 30px;
    border-radius: 12px;
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
    z-index: 10001;
    text-align: center;
    max-width: 400px;
    margin: 20px;

    h3 {
      font-size: 1.5rem;
      margin-top: 0;
      margin-bottom: 15px;
      font-weight: 700;
    }

    p {
      font-size: 1rem;
      color: #666;
      margin-bottom: 25px;
    }
  }

  .popup-actions {
    display: flex;
    justify-content: center;
    gap: 15px;
  }

  .primary-button, .secondary-button {
    padding: 10px 20px;
    font-size: 1rem;
    border-radius: 8px;
    border: 1px solid transparent;
    cursor: pointer;
    font-weight: 600;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    transition: all 0.2s ease;
  }

  .primary-button {
    background-color: $primary-color; /* Your primary color */
    color: white;
    &:hover {
      opacity: 0.9;
    }
  }

  .secondary-button {
    background-color: #f1f3f5;
    color: #495057;
    border-color: #dee2e6;
    &:hover {
      background-color: #e9ecef;
    }
  }
}
</style> 