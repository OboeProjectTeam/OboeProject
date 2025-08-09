<template>
    <div class="confirm-dialog-backdrop" @click.self="onCancel">
      <div class="confirm-dialog">
        <div class="confirm-title">{{ title }}</div>
        <div class="confirm-message" :class="{ 'html-content': useHtml }" v-if="!useHtml">{{ message }}</div>
        <div class="confirm-message html-content" v-if="useHtml" v-html="message"></div>
        <div class="confirm-actions">
          <button
            v-if="showCancel"
            class="btn btn-secondary"
            @click="onCancel"
          >
            Hủy
          </button>
          <button class="btn btn-primary" @click="onConfirm">{{ confirmText }}</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  const props = defineProps({
    title: { type: String, default: 'Xác nhận' },
    message: { type: String, required: true },
    confirmText: { type: String, default: 'Xác nhận' },
    showCancel: { type: Boolean, default: true },
    useHtml: { type: Boolean, default: false }
  })
  const emit = defineEmits(['confirm', 'cancel'])
  
  function onConfirm() {
    emit('confirm')
  }
  function onCancel() {
    emit('cancel')
  }
  </script>
  
  <style lang="scss" scoped>
  @use '@/assets/css/index.scss';
  .confirm-dialog-backdrop {
    position: fixed;
    z-index: 3000;
    inset: 0;
    background: rgba(0,0,0,0.25);
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .confirm-dialog {
    background: #fff;
    border-radius: 10px;
    min-width: 320px;
    max-width: 90vw;
    width: 600px;
    padding: 24px 20px 18px 20px;
    box-shadow: 0 8px 32px rgba(0,0,0,0.18);
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .confirm-title {
    font-size: 1.2rem;
    font-weight: 600;
    margin-bottom: 4px;
  }
  .confirm-message {
    font-size: 1rem;
    color: #444;
    line-height: 1.5;
    white-space: pre-line;
    text-align: left;
  }
  .confirm-message.html-content {
    max-height: 400px;
    overflow-y: auto;
  }
  .confirm-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 8px;
  }
  .btn {
    padding: 7px 18px;
    border-radius: 6px;
    border: none;
    font-size: 1rem;
    cursor: pointer;
    font-weight: 500;
    transition: background 0.2s;
  }
  .btn-primary {
    background: $primary-color;
    color: #fff;
  }
  .btn-secondary {
    background: #f1f3f5;
    color: #333;
  }
  .btn-primary:hover {
    filter: brightness(0.9);
  }
  .btn-secondary:hover {
    background: #e9ecef;
  }
  </style>