<template>
  <div class="messages-container">
    <!-- Sidebar with conversations -->
    <div class="messages-sidebar" :class="{ 'mobile-hidden': isMobileAndChatOpen }">
      <div class="sidebar-header">
        <h2>{{ t('myMessages.title') }}</h2>
      </div>
      <div class="search-box">
        <i class="fas fa-search"></i>
        <input type="text" :placeholder="t('myMessages.searchPlaceholder')">
      </div>

      <div class="conversations-list">
        <div v-if="conversationsLoading" class="loading-conversations">
          <i class="fas fa-spinner fa-spin"></i>
          <p>{{ t('myMessages.loadingConversations') }}</p>
        </div>
        <div v-else-if="conversations.length === 0" class="empty-conversations">
          <i class="fas fa-comments"></i>
          <p>{{ t('myMessages.noConversations') }}</p>
        </div>
        <div v-else v-for="chat in conversations" 
             :key="chat.id" 
             class="conversation-item"
             :class="{ active: selectedChat?.id === chat.id }"
             @click="selectChat(chat)">
          <div class="avatar">
            <img
              :src="chat.avatarUrlReceiver"
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
            <img :src="selectedChat.avatarUrlReceiver" :alt="selectedChat.name">
            <div>
              <h3>{{ selectedChat.name }}</h3>
            </div>
          </div>
        </div>

        <div class="chat-messages" ref="messagesContainer">
          <div v-if="conversationMessagesLoading" class="loading-messages">
            <i class="fas fa-spinner fa-spin"></i>
            <p>{{ t('myMessages.loadingMessages') }}</p>
          </div>
          <div v-else-if="selectedChat.messages?.length === 0" class="empty-messages">
            <i class="fas fa-comment-dots"></i>
            <p>{{ t('myMessages.noMessages') }}</p>
            <small>{{ t('myMessages.startConversation') }}</small>
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
          <!-- <button class="attachment-btn">
            <i class="fas fa-paperclip"></i>
          </button> -->
          <input type="text" 
                 v-model="newMessage" 
                 :placeholder="t('myMessages.messagePlaceholder')"
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
        <h2>{{ t('myMessages.selectChat') }}</h2>
        <p>{{ t('myMessages.createNewMessage') }}</p>
      </div>
    </div>

    <Teleport to="body">
      <div
        v-if="sidebarMenuUser"
        class="sidebar-user-menu"
        :style="{ top: sidebarMenuPosition.y + 'px', left: sidebarMenuPosition.x + 'px' }"
        @click.stop
      >
        <div class="menu-item" @click="viewProfile(sidebarMenuUser)">{{ t('myMessages.viewProfile') }}</div>
        <div class="menu-item" @click="openChatBox(sidebarMenuUser)">{{ t('myMessages.openChatBox') }}</div>
     
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
import { useI18n } from 'vue-i18n'
import ConfirmDialog from '@/components/common/popup/ThePopup.vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import api from '@/api'
import WebSocketService from '@/services/websocket'


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
const { t } = useI18n()

// Define emits for parent component
const emit = defineEmits(['send-message'])

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
    
    if (profileResponse?.user_id) {
      localStorage.setItem('currentUserId', profileResponse.user_id)
      return profileResponse.user_id
    }
    
    // Fallback: try to decode JWT token to get user ID
    const token = localStorage.getItem('token')
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]))
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
    
    const response = await api.message.getChatPartners()
    
    // Handle different response formats
    const partnersData = Array.isArray(response) ? response : (response.content || response.data || response)
    
         // Map API data to conversation format based on actual UserSummaryDTO
     const mappedConversations = (Array.isArray(partnersData) ? partnersData : []).map(partner => {
       // Build full name from firstName and lastName
       const firstName = partner.firstName || ''
       const lastName = partner.lastName || ''
       const fullName = `${firstName} ${lastName}`.trim() || partner.userName || t('myMessages.defaultUser')
       
       return {
         id: partner.userId,
         name: fullName,
         username: partner.userName, // Add username for profile navigation
         fullName: fullName,
         avatarUrlReceiver: partner.avatarUrlReceiver || `https://ui-avatars.com/api/?name=${encodeURIComponent(fullName)}&background=random`,
         lastMessage: partner.lastMessageContent || t('myMessages.noMessages'),
         lastMessageTime: partner.lastMessageTime ? formatMessageTime(partner.lastMessageTime) : '',
         unreadCount: 0, // Not provided in UserSummaryDTO
         messages: [] // Will be loaded when chat is selected
       }
     })
    
    conversations.value = mappedConversations
    
    // Store in Vuex
    store.commit('message/setChatPartners', partnersData)
    
    // Show success message
    store.dispatch('message/showMessage', {
      type: 'success',
      text: t('myMessages.loadedConversations', { count: mappedConversations.length })
    })
  } catch (error) {
    // Show error message
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('myMessages.loadError') + ': ' + error.message
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
      return t('myMessages.invalidDate')
    }
    
    const now = new Date()
    const diffMs = now - date
    const diffHours = Math.floor(diffMs / 3600000)
    const diffDays = Math.floor(diffMs / 86400000)
    
    
    // Same day - show time only
    if (diffDays === 0) {
      return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
    }
    
    // Within a week - show days ago
    if (diffDays < 7) {
      return t('myMessages.daysAgo', { days: diffDays })
    }
    
    // Older - show date
    return date.toLocaleDateString('vi-VN')
  } catch (error) {
    console.error('Error formatting time:', dateString, error)
    return t('myMessages.timeError')
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
    
    
    // Call API to get conversation
    const response = await api.message.getConversation(chat.id)
    
    // Handle different response formats
    const conversationData = Array.isArray(response) ? response : (response.content || response.data || response)
    
    // Map messages to expected format based on MessageDTO
    const mappedMessages = (Array.isArray(conversationData) ? conversationData : []).map(message => {
      
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
      }
      
      // Determine if message was sent by current user
      const isSent = senderId === currentUserId || String(senderId) === String(currentUserId)
      
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
    
    // Update chat with loaded messages
    chat.messages = mappedMessages
    
    scrollToBottom()
    
  } catch (error) {
    
    // Show error message
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('myMessages.conversationLoadError') + ': ' + error.message
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
    
    // Create MessageDTO for API
    const messageDTO = {
      receiverId: receiverId,
      sentMessage: messageContent
    }
    
    // Call API to send message
    const response = await api.message.sendMessage(messageDTO)
    
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
    
  } catch (error) {
    // Show error message
         store.dispatch('message/showMessage', {
       type: 'error',
       text: t('myMessages.sendError') + ': ' + error.message
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
  
  // Check if user has required data
  if (!user.username && !user.name) {
    console.error('MyMessages: No username or name found for user:', user)
    return
  }
  
  if (!user.id) {
    console.error('MyMessages: No user ID found for user:', user)
    return
  }
  
  // Use username if available, otherwise fallback to name
  const usernameForRoute = user.username || user.name
  
  // Navigate to forum profile page with userId in query params
  router.push({
    path: `/forum/u/${usernameForRoute}`,
    query: {
      userId: user.id,
      fromSource: 'messages'
    }
  })
}
const openChatBox = (user) => {
  const userId = user.id
  if (!userId) {
    console.error('MyMessages: No userId found in user object:', user)
    return
  }
  
  // Prepare user data for ChatBox
  const chatUser = {
    id: userId,
    userId: userId,
    name: user.name,
    username: user.name,
    fullName: user.name,
    avatarUrlReceiver: user.avatarUrlReceiver
  }
  // Emit to App.vue to open global ChatBox
  emit('send-message', chatUser)
  
  closeSidebarMenu()
}

function handleConfirm() {
  if (confirmDialog.value.onConfirm) confirmDialog.value.onConfirm()
  confirmDialog.value.show = false
}

function handleCancel() {
  confirmDialog.value.show = false
}

// Handle incoming user from other pages (like profile page)
const handleIncomingUser = () => {
  const query = router.currentRoute.value.query
  
  if (query.userId && query.userName) {
    // Check if user already exists in conversations
    const existingChat = conversations.value.find(chat => chat.id === query.userId)
    
    if (existingChat) {
      // User already in conversations, select it
      selectChat(existingChat)
    } else {
      // Create new conversation entry
      const newChat = {
        id: query.userId,
        name: query.fullName || query.userName,
        avatarUrlReceiver: query.avatarUrlReceiver || `https://ui-avatars.com/api/?name=${encodeURIComponent(query.fullName || query.userName)}&background=random`,
        lastMessage: t('myMessages.startConversation'),
        lastMessageTime: '',
        unreadCount: 0,
        messages: []
      }
      
      // Add to conversations list
      conversations.value.unshift(newChat)
      
      // Select the new chat
      selectChat(newChat)
      
      // Show notification
      store.dispatch('message/showMessage', {
        type: 'info',
        text: t('myMessages.startConversationWith', { name: newChat.name })
      })
    }
    
    // Clear query params to prevent re-triggering
    router.replace({ name: 'messages' })
  }
}



const updateHeaderHeight = () => {
  const header = document.querySelector('.header') 
  if (header) {
    document.documentElement.style.setProperty('--header-height', `${header.offsetHeight}px`)
  }
}

const handleIncomingRealtimeMessage = (message) => {
  const chat = conversations.value.find(c => c.id === message.senderId || c.id === message.receiverId)

  if (chat) {
    const newMsg = {
      id: message.messageId,
      content: message.sentMessage,
      time: formatMessageTime(message.sentDateTime),
      isSent: false,
      senderId: message.senderId,
      receiverId: message.receiverId,
      senderName: message.senderName
    }

    chat.lastMessage = message.sentMessage
    chat.lastMessageTime = formatMessageTime(message.sentDateTime)

    if (!chat.messages) chat.messages = []
    chat.messages.push(newMsg)

    // Nếu đang mở đúng chat thì scroll xuống cuối
    if (selectedChat.value?.id === chat.id) {
      scrollToBottom()
    } else {
      chat.unreadCount = (chat.unreadCount || 0) + 1
    }
  } else {
    // Tin nhắn từ người mới → reload danh sách
    loadChatPartners()
  }
}


onMounted(async () => {
  const userId = await getCurrentUserId()
  if (userId) {
    WebSocketService.connect(userId)

    // Nghe tin nhắn mới realtime
    WebSocketService.onMessage((data) => {
      if (data.messageId && data.senderId) {
        handleIncomingRealtimeMessage(data)
      }
    })
  }
  // Get current user ID first
  await getCurrentUserId()
  
  // Load chat partners when component mounts
  await loadChatPartners()
  
  // Check if there's a user to start conversation with
  handleIncomingUser()
  
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