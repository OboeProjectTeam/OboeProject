<template>
    <transition name="chatbox-fade">
      <div v-if="visible" class="chatbox" :class="{ minimized }">
        <div class="chatbox-header" @click="toggleMinimize">
          <img :src="user.avatar" class="chatbox-avatar" />
          <span class="chatbox-username">{{ user.name || user.username }}</span>
          <div class="chatbox-actions">
            <button @click.stop="goToMessages">
              <i class="fas fa-external-link-alt"></i>
            </button>
            <button @click.stop="toggleMinimize">
              <i :class="minimized ? 'fas fa-chevron-up' : 'fas fa-chevron-down'"></i>
            </button>
            <button @click.stop="closeBox">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
        <div v-show="!minimized" class="chatbox-body">
          <div class="chatbox-messages">
            <div v-for="msg in messages" :key="msg.id" class="message" :class="{ 'message-sent': msg.isSent }">
              <div class="message-content">
                <p>{{ msg.text }}</p>
                <span class="message-time">{{ msg.time }}</span>
              </div>
            </div>
          </div>
          <div class="chatbox-input">
            <input type="text" v-model="message" @keyup.enter="sendMessage" placeholder="Nhập tin nhắn..." />
            <button @click="sendMessage"><i class="fas fa-paper-plane"></i></button>
          </div>
        </div>
      </div>
    </transition>
  </template>
  
  <script setup>
  import { ref, watch } from 'vue'
  import { useRouter } from 'vue-router'
  const router = useRouter()
  const props = defineProps({
    user: { type: Object, required: true },
    visible: { type: Boolean, default: false }
  })
  const emit = defineEmits(['close', 'send'])
  const minimized = ref(false)
  const message = ref('')
  
  // Demo messages
  const messages = ref([
    {
      id: 1,
      text: "Xin chào! Mình có thể giúp gì cho bạn?",
      time: "10:30",
      isSent: false
    },
    {
      id: 2,
      text: "Chào bạn! Mình muốn hỏi về khóa học N3",
      time: "10:31",
      isSent: true
    },
    {
      id: 3,
      text: "Dạ vâng, bạn muốn biết thông tin gì về khóa học ạ?",
      time: "10:32",
      isSent: false
    },
    {
      id: 4,
      text: "Mình muốn biết học phí và thời gian học",
      time: "10:33",
      isSent: true
    },
    {
      id: 5,
      text: "Khóa học N3 kéo dài 6 tháng, học phí 6 triệu đồng. Lớp học offline vào tối thứ 3,5,7 từ 18h30-20h30",
      time: "10:34",
      isSent: false
    }
  ])
  
  function toggleMinimize() {
    minimized.value = !minimized.value
  }
  function closeBox() {
    emit('close')
  }
  function sendMessage() {
    if (message.value.trim()) {
      messages.value.push({
        id: Date.now(),
        text: message.value,
        time: new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }),
        isSent: true
      })
      emit('send', message.value)
      message.value = ''
    }
  }

  // Add function to navigate to messages page
  function goToMessages() {
    router.push('/messages')
  }
  </script>
  
  <style lang="scss" scoped>
  @use '@/assets/css/index.scss';
  .chatbox {
    position: fixed;
    right: 80px;
    bottom: 0px;
    width: 340px;
    max-width: 95vw;
    background: #fff;
    border-radius: 14px 14px 0 0;
    box-shadow: 0 8px 32px rgba(0,0,0,0.18);
    z-index: 3000;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    transition: box-shadow 0.2s, transform 0.2s;
    font-family: inherit;

    &.minimized .chatbox-body {
      display: none;
    }
  }

  .chatbox-header {
    display: flex;
    align-items: center;
    background: radial-gradient(circle at 80% 90%, rgba(185, 4, 73, 0.15) 0, rgba(185, 4, 73, 0.08) 60%), rgb(185 4 73 / 0%);
    color: #666666ab;
    padding: 12px 16px;
    cursor: pointer;
    user-select: none;
    border-bottom: 1px solid rgba(0,0,0,0.04);

    .chatbox-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      margin-right: 12px;
      border: 2px solid #fff;
      object-fit: cover;
      box-shadow: 0 2px 8px rgba(0,0,0,0.08);
    }
    .chatbox-username {
      flex: 1;
      font-weight: 600;
      font-size: 1.08rem;
      letter-spacing: 0.01em;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .chatbox-actions {
      display: flex;
      align-items: center;
      gap: 6px;

      button {
        background: none;
        border: none;
        color: #666666ab;
        font-size: 1.15rem;
        cursor: pointer;
        padding: 4px 6px;
        border-radius: 4px;
        transition: background 0.18s;
        &:hover {
          background: rgba(0,0,0,0.03);
        }
      }
    }
  }

  .chatbox-body {
    background: #fafbfc;
    padding: 0 0 10px 0;
    display: flex;
    flex-direction: column;
    height: 350px;
    border-radius: 0 0 14px 14px;
  }

  .chatbox-messages {
    flex: 1;
    overflow-y: auto;
    padding: 12px 14px 0 14px;
    font-size: 1rem;
    color: #222;
    display: flex;
    flex-direction: column;
    gap: 10px;

    .message {
      display: flex;
      flex-direction: column;
      max-width: 80%;
      align-self: flex-start;

      .message-content {
        background: #f0f2f5;
        padding: 10px 15px;
        border-radius: 15px;
        position: relative;

        p {
          margin: 0;
          font-size: 0.95rem;
          line-height: 1.4;
        }

        .message-time {
          font-size: 0.75rem;
          color: #666;
          margin-top: 4px;
          display: block;
        }
      }

      &.message-sent {
        align-self: flex-end;

        .message-content {
          background: radial-gradient(circle at 80% 90%, rgba(185, 4, 73, 0.08) 0, rgba(185, 4, 73, 0) 60%), #fff;
          color: #333;
          border: 1px solid rgba(185, 4, 73, 0.1);
          .message-time {
            color: rgba(0, 0, 0, 0.5);
          }
        }
      }
    }
  }

  .chatbox-input {
    display: flex;
    align-items: center;
    padding: 8px 12px 0 12px;
    gap: 8px;

    input {
      flex: 1;
      border-radius: 20px;
      border: 1px solid #e0e0e0;
      padding: 8px 16px;
      font-size: 1rem;
      background: #fff;
      transition: border 0.2s;
      &:focus {
        outline: none;
        border-color: rgba(185, 4, 73, 0.2);
        background: radial-gradient(circle at 80% 90%, rgba(185, 4, 73, 0.03) 0, rgba(185, 4, 73, 0) 60%), #fff;
      }
    }
    button {
      background: radial-gradient(circle at 80% 90%, rgba(185, 4, 73, 0.08) 0, rgba(185, 4, 73, 0) 60%), #fff;
      color: rgba(185, 4, 73, 0.8);
      border: 1px solid rgba(185, 4, 73, 0.1);
      border-radius: 50%;
      width: 38px;
      height: 38px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.15rem;
      cursor: pointer;
      transition: background 0.18s;
      &:hover {
        background: radial-gradient(circle at 80% 90%, rgba(185, 4, 73, 0.12) 0, rgba(185, 4, 73, 0.02) 60%), #fff;
      }
    }
  }

  // Responsive
  @media (max-width: 600px) {
    .chatbox {
      right: 4vw;
      width: 92vw;
      min-width: 0;
      max-width: 98vw;
      bottom: 10px;
    }
    .chatbox-header {
      padding: 10px 8px;
      .chatbox-avatar {
        width: 30px;
        height: 30px;
        margin-right: 8px;
      }
      .chatbox-username {
        font-size: 1rem;
      }
    }
    .chatbox-input {
      padding: 6px 6px 0 6px;
      input {
        font-size: 0.97rem;
        padding: 7px 10px;
      }
    }
  }

  // Add fade transition
  .chatbox-fade-enter-active,
  .chatbox-fade-leave-active {
    transition: opacity 0.3s ease;
  }

  .chatbox-fade-enter-from,
  .chatbox-fade-leave-to {
    opacity: 0;
  }
  </style>