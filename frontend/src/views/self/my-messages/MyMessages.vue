<template>
  <div class="messages-container">
    <!-- Sidebar with conversations -->
    <div class="messages-sidebar">
      <div class="sidebar-header">
        <h2>Tin nhắn</h2>
      </div>
      <div class="search-box">
        <i class="fas fa-search"></i>
        <input type="text" placeholder="Tìm kiếm cuộc trò chuyện...">
      </div>

      <div class="conversations-list">
        <div v-for="chat in conversations" 
             :key="chat.id" 
             class="conversation-item"
             :class="{ active: selectedChat?.id === chat.id }"
             @click="selectChat(chat)">
          <div class="avatar">
            <img
              :src="chat.avatar"
              :alt="chat.name"
              class="avatar-img"
              @click.stop="openSidebarMenu($event, chat)"
            >
          </div>
          <div class="conversation-info">
            <div class="conversation-header">
              <h3>{{ chat.name }}</h3>
              <span class="time">{{ chat.lastMessageTime }}</span>
            </div>
            <div class="conversation-preview">
              <p>{{ chat.lastMessage }}</p>
              <span v-if="chat.unreadCount" class="unread-badge">{{ chat.unreadCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Main chat area -->
    <div class="messages-main">
      <template v-if="selectedChat">
        <div class="chat-header">
          <div class="chat-user-info">
            <img :src="selectedChat.avatar" :alt="selectedChat.name">
            <div>
              <h3>{{ selectedChat.name }}</h3>
            </div>
          </div>
        </div>

        <div class="chat-messages" ref="messagesContainer">
          <div v-for="message in selectedChat.messages" 
               :key="message.id" 
               class="message"
               :class="{ 'message-sent': message.isSent }">
            <div class="message-content">
              <p>{{ message.content }}</p>
              <span class="message-time">{{ message.time }}</span>
            </div>
          </div>
        </div>

        <div class="chat-input">
          <button class="attachment-btn">
            <i class="fas fa-paperclip"></i>
          </button>
          <input type="text" 
                 v-model="newMessage" 
                 placeholder="Nhập tin nhắn..."
                 @keyup.enter="sendMessage">
          <button class="send-btn" @click="sendMessage">
            <i class="fas fa-paper-plane"></i>
          </button>
        </div>
      </template>

      <div v-else class="no-chat-selected">
        <i class="fas fa-comments"></i>
        <h2>Chọn một cuộc trò chuyện để bắt đầu</h2>
        <p>Hoặc tạo tin nhắn mới để kết nối với bạn bè</p>
      </div>
    </div>

    <Teleport to="body">
      <div
        v-if="sidebarMenuUser"
        class="sidebar-user-menu"
        :style="{ top: sidebarMenuPosition.y + 'px', left: sidebarMenuPosition.x + 'px' }"
        @click.stop
      >
        <div class="menu-item" @click="viewProfile(sidebarMenuUser)">Xem hồ sơ</div>
        <div class="menu-item" @click="openChatBox(sidebarMenuUser)">Mở Box Chat</div>
        <div class="menu-item" @click="deleteConversation(sidebarMenuUser)">Xóa trò chuyện</div>
        <div class="menu-item" @click="blockUser(sidebarMenuUser)">Chặn</div>
      </div>
    </Teleport>

    <ConfirmDialog
      v-if="confirmDialog.show"
      :title="confirmDialog.title"
      :message="confirmDialog.message"
      :confirmText="confirmDialog.confirmText"
      @confirm="handleConfirm"
      @cancel="handleCancel"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import ConfirmDialog from '@/components/common/popup/ThePopup.vue'
import { useRouter } from 'vue-router'

const selectedChat = ref(null)
const newMessage = ref('')
const messagesContainer = ref(null)
const sidebarMenuUser = ref(null)
const sidebarMenuPosition = ref({ x: 0, y: 0 })

const confirmDialog = ref({
  show: false,
  title: '',
  message: '',
  confirmText: 'Xác nhận',
  onConfirm: null
})

const router = useRouter()

// Demo data
const conversations = ref([
  {
    id: 1,
    name: 'Mai An',
    avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026704d',
    lastMessage: 'Cảm ơn bạn đã chia sẻ tài liệu!',
    lastMessageTime: '10:30',
    unreadCount: 2,
    messages: [
      { id: 1, content: 'Chào bạn!', time: '10:00', isSent: false },
      { id: 2, content: 'Mình vừa tìm thấy tài liệu hay về ngữ pháp N3', time: '10:05', isSent: true },
      { id: 3, content: 'Cảm ơn bạn đã chia sẻ tài liệu!', time: '10:30', isSent: false }
    ]
  },
  {
    id: 2,
    name: 'Hùng Trần',
    avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026705d',
    lastMessage: 'Bạn có thể giải thích thêm về cách dùng ～ておく không?',
    lastMessageTime: '09:15',
    unreadCount: 0,
    messages: [
      { id: 1, content: 'Chào bạn!', time: '09:00', isSent: false },
      { id: 2, content: 'Bạn có thể giải thích thêm về cách dùng ～ておく không?', time: '09:15', isSent: false }
    ]
  },
  {
    id: 3,
    name: 'Lan Anh',
    avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026707d',
    lastMessage: 'Mình sẽ gửi cho bạn tài liệu ôn thi JLPT N2',
    lastMessageTime: 'Hôm qua',
    unreadCount: 0,
    messages: [
      { id: 1, content: 'Chào bạn!', time: 'Hôm qua', isSent: false },
      { id: 2, content: 'Mình sẽ gửi cho bạn tài liệu ôn thi JLPT N2', time: 'Hôm qua', isSent: false }
    ]
  }
])

const selectChat = (chat) => {
  selectedChat.value = chat
  chat.unreadCount = 0
  scrollToBottom()
}

const sendMessage = () => {
  if (!newMessage.value.trim()) return

  const message = {
    id: Date.now(),
    content: newMessage.value,
    time: new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }),
    isSent: true
  }

  selectedChat.value.messages.push(message)
  newMessage.value = ''
  scrollToBottom()
}

const scrollToBottom = () => {
  setTimeout(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  }, 100)
}

const openSidebarMenu = (event, user) => {
  sidebarMenuUser.value = user
  // Lấy vị trí chuột để đặt menu
  sidebarMenuPosition.value = { x: event.clientX, y: event.clientY }
}

const closeSidebarMenu = () => {
  sidebarMenuUser.value = null
}

const viewProfile = (user) => {
  closeSidebarMenu()
  // Nếu user có username, chuyển hướng sang trang hồ sơ
  if (user.username) {
    router.push(`/forum/u/${user.username}`)
  } else if (user.name) {
    // Nếu user chỉ có name, dùng name làm username
    router.push(`/forum/u/${user.name}`)
  }
}

const deleteConversation = (user) => {
  showConfirm({
    title: 'Xác nhận xóa',
    message: `Bạn có chắc muốn xóa cuộc trò chuyện với ${user.name || user.username}?`,
    confirmText: 'Xóa',
    onConfirm: () => {
      // Xử lý xóa ở đây
      closeSidebarMenu()
    }
  })
}

const blockUser = (user) => {
  showConfirm({
    title: 'Xác nhận chặn',
    message: `Bạn có chắc muốn chặn ${user.name || user.username}?`,
    confirmText: 'Chặn',
    onConfirm: () => {
      // Xử lý chặn ở đây
      closeSidebarMenu()
    }
  })
}

const openChatBox = (user) => {
  // Emit send-message event through router meta
  router.currentRoute.value.meta.emit?.('send-message', user)
  closeSidebarMenu()
}

function showConfirm({ title, message, confirmText = 'Xác nhận', onConfirm }) {
  confirmDialog.value = { show: true, title, message, confirmText, onConfirm }
}

function handleConfirm() {
  if (confirmDialog.value.onConfirm) confirmDialog.value.onConfirm()
  confirmDialog.value.show = false
}

function handleCancel() {
  confirmDialog.value.show = false
}

onMounted(() => {
  scrollToBottom()
  window.addEventListener('click', closeSidebarMenu)
})

onUnmounted(() => {
  window.removeEventListener('click', closeSidebarMenu)
})
</script>

<style lang="scss" scoped>
@use '@/assets/css/index.scss';
.messages-container {
  display: flex;
  height: calc(100vh - 140px);
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.messages-sidebar {
  width: 320px;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;

  .sidebar-header {
    padding: 19.5px;
    border-bottom: 1px solid #eee;
    display: flex;
    justify-content: space-between;
    align-items: center;

    h2 {
      margin: 0;
      font-size: 1.5rem;
    }

    .new-message-btn {
      padding: 8px 16px;
      background: $primary-color;
      color: white;
      border: none;
      border-radius: 20px;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 8px;

      &:hover {
        background: $hover-btn-color;
      }
    }
  }

  .search-box {
    padding: 15px;
    position: relative;

    input {
      width: 100%;
      padding: 10px 15px 10px 35px;
      border: 1px solid #ddd;
      border-radius: 20px;
      font-size: 0.9rem;

      &:focus {
        outline: none;
        border-color: $primary-color;
      }
    }

    i {
      position: absolute;
      left: 25px;
      top: 50%;
      transform: translateY(-50%);
      color: #666;
    }
  }

  .conversations-list {
    flex: 1;
    overflow-y: auto;

    .conversation-item {
      padding: 15px;
      display: flex;
      gap: 20px;
      cursor: pointer;
      transition: background-color 0.2s;

      &:hover {
        background-color: #f8f9fa;
      }

      &.active {
        background-color: #e9ecef;
      }

      .avatar {
        position: relative;
        margin-right: 12px;

        img {
          width: 50px;
          height: 50px;
          border-radius: 50%;
          object-fit: cover;
        }

        .status {
          position: absolute;
          bottom: 2px;
          right: 2px;
          width: 12px;
          height: 12px;
          border-radius: 50%;
          background: $primary-color;
          border: 2px solid white;
        }
      }

      .conversation-info {
        flex: 1;
        min-width: 0;

        .conversation-header {
          display: flex;
          justify-content: space-between;
          align-items: baseline;
          margin-bottom: 4px;

          h3 {
            margin: 0;
            font-size: 1rem;
            font-weight: 600;
          }

          .time {
            font-size: 0.8rem;
            color: #666;
          }
        }

        .conversation-preview {
          display: flex;
          justify-content: space-between;
          align-items: center;

          p {
            margin: 0;
            font-size: 0.9rem;
            color: #666;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            max-width: 180px;
          }

          .unread-badge {
            background: $primary-color;
            color: white;
            font-size: 0.8rem;
            padding: 2px 6px;
            border-radius: 10px;
            min-width: 20px;
            text-align: center;
          }
        }
      }
    }
  }
}

.messages-main {
  flex: 1;
  display: flex;
  flex-direction: column;

  .chat-header {
    padding: 16px 21px;
    border-bottom: 1px solid #eee;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .chat-user-info {
      display: flex;
      align-items: center;
      gap: 12px;

      img {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        object-fit: cover;
      }

      h3 {
        margin: 0;
        font-size: 1.1rem;
      }

      .status-text {
        font-size: 0.8rem;
        color: #666;
      }
    }
  }

  .chat-messages {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 15px;

    .message {
      display: flex;
      flex-direction: column;
      max-width: 70%;

      &.message-sent {
        align-self: flex-end;

        .message-content {
          background: $primary-color;
          color: white;

          .message-time {
            color: rgba(255, 255, 255, 0.8);
          }
        }
      }

      .message-content {
        background: #f8f9fa;
        padding: 10px 15px;
        border-radius: 15px;
        position: relative;

        p {
          margin: 0;
          font-size: 0.95rem;
        }

        .message-time {
          font-size: 0.75rem;
          color: #666;
          margin-top: 4px;
          display: block;
        }
      }
    }
  }

  .chat-input {
    padding: 15px 20px;
    border-top: 1px solid #eee;
    display: flex;
    gap: 10px;
    align-items: center;

    input {
      flex: 1;
      padding: 10px 15px;
      border: 1px solid #ddd;
      border-radius: 20px;
      font-size: 0.95rem;

      &:focus {
        outline: none;
        border-color: $primary-color;
      }
    }

    .attachment-btn,
    .send-btn {
      width: 40px;
      height: 40px;
      border: none;
      border-radius: 50%;
      background: #f8f9fa;
      color: #666;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;

      &:hover {
        background: #e9ecef;
        color: $primary-color;
      }
    }
  }

  .no-chat-selected {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #666;
    text-align: center;
    padding: 20px;

    i {
      font-size: 4rem;
      margin-bottom: 20px;
      color: #ddd;
    }

    h2 {
      margin: 0 0 10px;
      font-size: 1.5rem;
    }

    p {
      margin: 0;
      font-size: 1rem;
    }
  }
}

.sidebar-user-menu {
  position: fixed;
  z-index: 2000;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.12);
  min-width: 160px;
  padding: 8px 0;
  .menu-item {
    padding: 10px 20px;
    cursor: pointer;
    color: #333;
    font-size: 1rem;
    transition: background 0.2s;
    &:hover {
      background: #f5f5f5;
      color: var($primary-color);
    }
    &:not(:last-child) {
      border-bottom: 1px solid #f0f0f0;
    }
  }
}
</style> 