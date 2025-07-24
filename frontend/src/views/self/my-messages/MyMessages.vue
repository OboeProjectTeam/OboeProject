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
        <div v-if="conversationsLoading" class="loading-conversations">
          <i class="fas fa-spinner fa-spin"></i>
          <p>Đang tải cuộc trò chuyện...</p>
        </div>
        <div v-else-if="conversations.length === 0" class="empty-conversations">
          <i class="fas fa-comments"></i>
          <p>Chưa có cuộc trò chuyện nào</p>
        </div>
        <div v-else v-for="chat in conversations" 
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
          <div v-if="conversationMessagesLoading" class="loading-messages">
            <i class="fas fa-spinner fa-spin"></i>
            <p>Đang tải tin nhắn...</p>
          </div>
          <div v-else-if="selectedChat.messages?.length === 0" class="empty-messages">
            <i class="fas fa-comment-dots"></i>
            <p>Chưa có tin nhắn nào</p>
            <small>Hãy bắt đầu cuộc trò chuyện!</small>
          </div>
          <div v-else v-for="message in selectedChat.messages" 
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
                 :disabled="sendingMessage"
                 @keyup.enter="sendMessage">
          <button class="send-btn" @click="sendMessage" :disabled="sendingMessage || !newMessage.trim()">
            <i v-if="sendingMessage" class="fas fa-spinner fa-spin"></i>
            <i v-else class="fas fa-paper-plane"></i>
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import ConfirmDialog from '@/components/common/popup/ThePopup.vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import api from '@/api'

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
const store = useStore()

// Loading state
const conversationsLoading = ref(false)
const conversationMessagesLoading = ref(false)
const sendingMessage = ref(false)

// Get current user info
const currentUser = computed(() => store.getters['auth/currentUser'])

// Get current user ID from different sources
const getCurrentUserId = async () => {
  try {
    // First try to get from profile API which might have more complete user info
    const profileResponse = await api.profile.getProfile()
    console.log('Profile API response:', profileResponse)
    
    if (profileResponse?.user_id) {
      localStorage.setItem('currentUserId', profileResponse.user_id)
      return profileResponse.user_id
    }
    
    // Fallback: try to decode JWT token to get user ID
    const token = localStorage.getItem('token')
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]))
        console.log('JWT payload:', payload)
        // JWT might contain user ID or username that we can use
      } catch (e) {
        console.error('Error decoding JWT:', e)
      }
    }
    
    return null
  } catch (error) {
    console.error('Error getting current user ID:', error)
    return null
  }
}

// Conversations data from API
const conversations = ref([])

// Load chat partners from API
const loadChatPartners = async () => {
  try {
    conversationsLoading.value = true
    console.log('Loading chat partners...')
    
    const response = await api.message.getChatPartners()
    console.log('Chat partners API response:', response)
    
    // Handle different response formats
    const partnersData = Array.isArray(response) ? response : (response.content || response.data || response)
    console.log('Partners data:', partnersData)
    
    // Map API data to conversation format based on actual UserSummaryDTO
    const mappedConversations = (Array.isArray(partnersData) ? partnersData : []).map(partner => {
      console.log('Mapping partner:', partner)
      
      // Build full name from firstName and lastName
      const firstName = partner.firstName || ''
      const lastName = partner.lastName || ''
      const fullName = `${firstName} ${lastName}`.trim() || partner.userName || 'Người dùng'
      
      return {
        id: partner.userId,
        name: fullName,
        avatar: partner.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(fullName)}&background=random`,
        lastMessage: partner.lastMessageContent || 'Chưa có tin nhắn',
        lastMessageTime: partner.lastMessageTime ? formatMessageTime(partner.lastMessageTime) : '',
        unreadCount: 0, // Not provided in UserSummaryDTO
        messages: [] // Will be loaded when chat is selected
      }
    })
    
    console.log('Mapped conversations:', mappedConversations)
    conversations.value = mappedConversations
    
    // Store in Vuex
    store.commit('message/setChatPartners', partnersData)
    
    // Show success message
    store.dispatch('message/showMessage', {
      type: 'success',
      text: `Đã tải ${mappedConversations.length} cuộc trò chuyện`
    })
  } catch (error) {
    console.error('Failed to load chat partners:', error)
    
    // Show error message
    store.dispatch('message/showMessage', {
      type: 'error',
      text: 'Không thể tải danh sách tin nhắn: ' + error.message
    })
    
    // Set empty array on error
    conversations.value = []
  } finally {
    conversationsLoading.value = false
  }
}

// Format message time
const formatMessageTime = (dateString) => {
  try {
    if (!dateString) return ''
    
    // Handle LocalDateTime format from backend (e.g., "2025-07-23T13:08:36")
    const date = new Date(dateString)
    
    // Check if date is valid
    if (isNaN(date.getTime())) {
      console.error('Invalid date:', dateString)
      return 'Invalid Date'
    }
    
    const now = new Date()
    const diffMs = now - date
    const diffHours = Math.floor(diffMs / 3600000)
    const diffDays = Math.floor(diffMs / 86400000)
    
    console.log('Formatting time:', dateString, 'parsed date:', date, 'diffHours:', diffHours, 'diffDays:', diffDays)
    
    // Same day - show time only
    if (diffDays === 0) {
      return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
    }
    
    // Within a week - show days ago
    if (diffDays < 7) {
      return `${diffDays} ngày trước`
    }
    
    // Older - show date
    return date.toLocaleDateString('vi-VN')
  } catch (error) {
    console.error('Error formatting time:', dateString, error)
    return 'Lỗi thời gian'
  }
}

const selectChat = async (chat) => {
  try {
    selectedChat.value = chat
    chat.unreadCount = 0
    
    // Add mobile view handling
    if (window.innerWidth <= 768) {
      isMobileAndChatOpen.value = true
    }
    
    // Start loading messages
    conversationMessagesLoading.value = true
    
    console.log('Loading conversation with user:', chat.id)
    
    // Call API to get conversation
    const response = await api.message.getConversation(chat.id)
    console.log('Conversation API response:', response)
    
    // Handle different response formats
    const conversationData = Array.isArray(response) ? response : (response.content || response.data || response)
    console.log('Conversation data:', conversationData)
    
    // Map messages to expected format based on MessageDTO
    const mappedMessages = (Array.isArray(conversationData) ? conversationData : []).map(message => {
      console.log('Mapping message:', message)
      
      const senderId = message.senderId
      const receiverId = message.receiverId
      
      // Try to get current user ID from different sources
      let currentUserId = currentUser.value?.id || 
                          currentUser.value?.userId || 
                          currentUser.value?.user_id ||
                          currentUser.value?.user?.id ||
                          currentUser.value?.user?.userId ||
                          localStorage.getItem('currentUserId')
      
      // TEMPORARY: If no currentUserId found, try to determine from message pattern
      // Based on API response, we see 2 users:
      // - "c6bc94fe-94e5-48e0-95f1-14847e7a8f7a" (nghianhbh00970@fpt.edu.vn)  
      // - "5c936e0d-0629-4638-ba79-a58f597e2718" (vuongancut789@gmail.com)
      if (!currentUserId && currentUser.value?.username) {
        if (currentUser.value.username.includes('nghianhbh00970')) {
          currentUserId = 'c6bc94fe-94e5-48e0-95f1-14847e7a8f7a'
        } else if (currentUser.value.username.includes('vuongancut789')) {
          currentUserId = '5c936e0d-0629-4638-ba79-a58f597e2718'
        }
        console.log('TEMPORARY userId mapping based on username:', currentUserId)
      }
      
      // Determine if message was sent by current user
      const isSent = senderId === currentUserId || String(senderId) === String(currentUserId)
      
      console.log('=== MESSAGE MAPPING DEBUG ===')
      console.log('Message sender ID:', senderId, 'type:', typeof senderId)
      console.log('Current user ID:', currentUserId, 'type:', typeof currentUserId)
      console.log('Current user object (full):', JSON.stringify(currentUser.value, null, 2))
      console.log('LocalStorage user ID:', localStorage.getItem('currentUserId'))
      console.log('Strict equal?:', senderId === currentUserId)
      console.log('String comparison:', String(senderId) === String(currentUserId))
      console.log('Is sent (final):', isSent)
      console.log('===============================')
      
      return {
        id: message.messageId,
        content: message.sentMessage,
        time: message.sentDateTime ? formatMessageTime(message.sentDateTime) : '',
        isSent: isSent,
        senderId: senderId,
        receiverId: receiverId,
        senderName: message.senderName
      }
    })
    
    console.log('Mapped messages:', mappedMessages)
    
    // Update chat with loaded messages
    chat.messages = mappedMessages
    
    scrollToBottom()
    
  } catch (error) {
    console.error('Failed to load conversation:', error)
    
    // Show error message
    store.dispatch('message/showMessage', {
      type: 'error',
      text: 'Không thể tải cuộc trò chuyện: ' + error.message
    })
    
    // Still select chat but with empty messages
    chat.messages = []
    scrollToBottom()
  } finally {
    conversationMessagesLoading.value = false
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

const sendMessage = async () => {
  if (!newMessage.value.trim() || !selectedChat.value || sendingMessage.value) return

  const messageContent = newMessage.value.trim()
  const receiverId = selectedChat.value.id
  
  try {
    sendingMessage.value = true
    console.log('Sending message to:', receiverId, 'Content:', messageContent)
    
    // Create MessageDTO for API
    const messageDTO = {
      receiverId: receiverId,
      sentMessage: messageContent
    }
    
    // Call API to send message
    const response = await api.message.sendMessage(messageDTO)
    console.log('Message sent, API response:', response)
    
    // Map response to message format
    const newMessageObj = {
      id: response.messageId,
      content: response.sentMessage,
      time: formatMessageTime(response.sentDateTime),
      isSent: true,
      senderId: response.senderId,
      receiverId: response.receiverId,
      senderName: response.senderName
    }
    
    // Add message to chat
    if (selectedChat.value.messages) {
      selectedChat.value.messages.push(newMessageObj)
    } else {
      selectedChat.value.messages = [newMessageObj]
    }
    
    // Update last message in conversation list
    selectedChat.value.lastMessage = messageContent
    selectedChat.value.lastMessageTime = formatMessageTime(response.sentDateTime)
    
    // Clear input and scroll
    newMessage.value = ''
    scrollToBottom()
    
    console.log('Message added to chat successfully')
    
  } catch (error) {
    console.error('Failed to send message:', error)
    
    // Show error message
         store.dispatch('message/showMessage', {
       type: 'error',
       text: 'Không thể gửi tin nhắn: ' + error.message
     })
   } finally {
     sendingMessage.value = false
   }
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

onMounted(async () => {
  // Get current user ID first
  await getCurrentUserId()
  
  // Load chat partners when component mounts
  loadChatPartners()
  
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