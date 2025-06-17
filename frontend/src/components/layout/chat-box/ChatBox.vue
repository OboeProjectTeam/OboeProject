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
    @use '@/components/layout/chat-box/ChatBox.scss';
  </style>