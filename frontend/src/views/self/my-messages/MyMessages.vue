<template>
  <div class="messages-container">
    <!-- Sidebar with conversations -->
    <div class="messages-sidebar" :class="{ 'mobile-hidden': isMobileAndChatOpen }">
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
    <div class="messages-main" :class="{ 'mobile-visible': isMobileAndChatOpen }">
      <template v-if="selectedChat">
        <div class="chat-header">
          <!-- Add back button for mobile -->
          <div v-if="isMobileView" class="back-button" @click="closeChat">
            <i class="fas fa-arrow-left"></i>
          </div>
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
const isMobileView = ref(false)

// Add mobile view state
const isMobileAndChatOpen = ref(false)

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
  
  // Add mobile view handling
  if (window.innerWidth <= 768) {
    isMobileAndChatOpen.value = true
  }
}

// Add close chat function for mobile
const closeChat = () => {
  if (window.innerWidth <= 768) {
    isMobileAndChatOpen.value = false
  }
}

// Add window resize handler
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768
  if (!isMobileView.value) {
    isMobileAndChatOpen.value = false
  }
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

const updateHeaderHeight = () => {
  const header = document.querySelector('.header') 
  if (header) {
    document.documentElement.style.setProperty('--header-height', `${header.offsetHeight}px`)
  }
}

onMounted(() => {
  scrollToBottom()
  window.addEventListener('click', closeSidebarMenu)
  
  // Add mobile-related event listeners
  window.addEventListener('resize', handleResize)
  handleResize() // Initial check
  
  // Handle browser back button
  window.addEventListener('popstate', () => {
    if (isMobileAndChatOpen.value) {
      closeChat()
    }
  })
  
  // Theo dõi chiều cao header
  updateHeaderHeight()
  window.addEventListener('scroll', updateHeaderHeight)
  window.addEventListener('resize', updateHeaderHeight)
})

onUnmounted(() => {
  window.removeEventListener('click', closeSidebarMenu)
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('popstate', closeChat)
  window.removeEventListener('scroll', updateHeaderHeight)
  window.removeEventListener('resize', updateHeaderHeight)
})
</script>

<style lang="scss" scoped>
@use '@/views/self/my-messages/MyMessages.scss';
</style> 