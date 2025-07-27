<template>
    <transition name="chatbox-fade">
      <div v-if="visible" class="chatbox" :class="{ minimized }">
        <div class="chatbox-header" @click="toggleMinimize">
          <img :src="user.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(user.name || user.username)}&background=random`" class="chatbox-avatar" />
          <span class="chatbox-username">{{ user.name || user.username }}</span>
          <div class="chatbox-actions">
            <button @click.stop="goToMessages" title="Mở trang tin nhắn">
              <i class="fas fa-external-link-alt"></i>
            </button>
            <button @click.stop="toggleMinimize" :title="minimized ? 'Mở rộng' : 'Thu gọn'">
              <i :class="minimized ? 'fas fa-chevron-up' : 'fas fa-chevron-down'"></i>
            </button>
            <button @click.stop="closeBox" title="Đóng">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
        <div v-show="!minimized" class="chatbox-body">
          <div class="chatbox-messages" ref="messagesContainer">
            <!-- Loading state -->
            <div v-if="messagesLoading" class="loading-messages">
              <i class="fas fa-spinner fa-spin"></i>
              <p>Đang tải tin nhắn...</p>
            </div>
            <!-- Empty state -->
            <div v-else-if="messages.length === 0" class="empty-messages">
              <i class="fas fa-comment-dots"></i>
              <p>Chưa có tin nhắn nào</p>
              <small>Hãy bắt đầu cuộc trò chuyện!</small>
            </div>
            <!-- Messages -->
            <div v-else v-for="msg in messages" :key="msg.id" class="message" :class="{ 'message-sent': msg.isSent }">
              <div class="message-content">
                <p>{{ msg.content || msg.text }}</p>
                <span class="message-time">{{ msg.time }}</span>
              </div>
            </div>
          </div>
          <div class="chatbox-input">
            <input 
              type="text" 
              v-model="newMessage" 
              @keyup.enter="sendMessage" 
              :disabled="sendingMessage"
              placeholder="Nhập tin nhắn..." 
            />
            <button @click="sendMessage" :disabled="sendingMessage || !newMessage.trim()">
              <i v-if="sendingMessage" class="fas fa-spinner fa-spin"></i>
              <i v-else class="fas fa-paper-plane"></i>
            </button>
          </div>
        </div>
      </div>
    </transition>
  </template>
  
  <script setup>
  import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { useStore } from 'vuex'
  import api from '@/api'
  import WebSocketService from '@/services/websocket'
  
  const router = useRouter()
  
  const props = defineProps({
    user: { 
      type: Object, 
      required: true 
    },
    visible: { 
      type: Boolean, 
      default: false 
    }
  })
  
  const emit = defineEmits(['close', 'message-sent', 'message-received', 'conversation-loaded'])
  
  const minimized = ref(false)
  const newMessage = ref('')
  const messages = ref([])
  const messagesLoading = ref(false)
  const sendingMessage = ref(false)
  const messagesContainer = ref(null)
  
  // Get current user from store
  const store = useStore()
  const currentUser = computed(() => store.getters['auth/currentUser'])
  const currentUserId = computed(() => {
    if (!currentUser.value) return null
    return currentUser.value.userId || currentUser.value.user_id || currentUser.value.id
  })
  
  // WebSocket connection and subscription
  const connectWebSocket = async () => {
    try {
      if (!WebSocketService.isConnected()) {
        await WebSocketService.connect()
      }
      
      // Subscribe to messages for current user
      if (currentUserId.value) {
        WebSocketService.subscribeToMessages(currentUserId.value, handleIncomingMessage)
      }
    } catch (error) {
      console.error('Failed to connect WebSocket:', error)
    }
  }
  
  // Handle incoming WebSocket messages
  const handleIncomingMessage = (messageData) => {
    console.log('ChatBox: Received WebSocket message:', messageData)
    
    // Check if this message is for the current conversation
    const chatUserId = props.user?.id || props.user?.userId
    const isForCurrentChat = 
      (messageData.senderId === chatUserId && messageData.receiverId === currentUserId.value) ||
      (messageData.senderId === currentUserId.value && messageData.receiverId === chatUserId)
    
    if (isForCurrentChat) {
      // Map message to expected format
      const newMessage = {
        id: messageData.messageId,
        content: messageData.sentMessage,
        text: messageData.sentMessage,
        time: formatMessageTime(messageData.sentDateTime),
        isSent: messageData.senderId === currentUserId.value,
        senderId: messageData.senderId,
        receiverId: messageData.receiverId,
        senderName: messageData.senderName
      }
      
      // Add message to conversation
      messages.value.push(newMessage)
      
      // Scroll to bottom
      setTimeout(() => {
        scrollToBottom()
      }, 100)
      
      // Emit to parent
      emit('message-received', newMessage)
    }
  }
  
  // Load conversation from API - always fresh data
  const loadConversation = async () => {
    const userId = props.user?.id || props.user?.userId
    
    if (!userId) {
      console.error('ChatBox: No user ID provided. User object:', props.user)
      return
    }
    
    try {
      messagesLoading.value = true
      
      const response = await api.message.getConversation(userId)
   
      
      // Handle different response formats
      let conversationData = response
      if (response && typeof response === 'object') {
        // If response has nested data
        if (response.content) {
          conversationData = response.content
        } else if (response.data) {
          conversationData = response.data
        } else if (Array.isArray(response)) {
          conversationData = response
        }
      }
      
      // Ensure conversationData is an array
      if (!Array.isArray(conversationData)) {
        conversationData = []
      }
      
      // Map messages to expected format
      const mappedMessages = conversationData.map(message => {
        
        const senderId = message.senderId
        const currentUserIdValue = currentUserId.value
        
        // Determine if message was sent by current user
        const isSent = senderId === currentUserIdValue || String(senderId) === String(currentUserIdValue)
        
        const mappedMessage = {
          id: message.messageId,
          content: message.sentMessage,
          text: message.sentMessage, // Compatibility with old format
          time: message.sentDateTime ? formatMessageTime(message.sentDateTime) : '',
          isSent: isSent,
          senderId: senderId,
          receiverId: message.receiverId,
          senderName: message.senderName
        }
        
        return mappedMessage
      })
      
      
      // Set messages
      messages.value = mappedMessages
      
      // Emit to parent that conversation is loaded
      emit('conversation-loaded', mappedMessages)
      
      // Scroll to bottom after messages are set
      setTimeout(() => {
        scrollToBottom()
      }, 100)
      
      
    } catch (error) {
      
      // Set empty array on error
      messages.value = []
    } finally {
      messagesLoading.value = false
    }
  }
  
  // Format message time
  const formatMessageTime = (dateString) => {
    try {
      if (!dateString) return ''
      
      const date = new Date(dateString)
      if (isNaN(date.getTime())) {
        return 'Invalid Date'
      }
      
      const now = new Date()
      const diffDays = Math.floor((now - date) / 86400000)
      
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
      return 'Lỗi thời gian'
    }
  }
  
  // Send message
  const sendMessage = async () => {
    if (!newMessage.value.trim() || sendingMessage.value) return
    
    const messageContent = newMessage.value.trim()
    const receiverId = props.user.id || props.user.userId
    
    if (!receiverId) {
      return
    }
    
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
        text: response.sentMessage, // Compatibility
        time: formatMessageTime(response.sentDateTime),
        isSent: true,
        senderId: response.senderId,
        receiverId: response.receiverId,
        senderName: response.senderName
      }
      
      // Add message to local state
      messages.value.push(newMessageObj)
      
      // Emit to parent component
      emit('message-sent', newMessageObj)
      
      // Clear input and scroll
      newMessage.value = ''
      scrollToBottom()
      

    } catch (error) {
      console.error('ChatBox: Failed to send message:', error)
    } finally {
      sendingMessage.value = false
    }
  }
  
  // Scroll to bottom
  const scrollToBottom = () => {
    nextTick(() => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    })
  }
  
  function toggleMinimize() {
    minimized.value = !minimized.value
    if (!minimized.value) {
      scrollToBottom()
    }
  }
  
  function closeBox() {
    emit('close')
  }
  
  // Navigate to messages page with user context
  function goToMessages() {
    router.push({
      name: 'messages',
      query: {
        userId: props.user.id || props.user.userId,
        userName: props.user.name || props.user.username,
        fullName: props.user.fullName || props.user.name,
        avatar: props.user.avatarUrlReceiver || props.user.avatarUrl
      }
    })
         // Close chat box when navigating
     emit('close')
   }

       // Debug: Add onMounted to see if component is mounting
    onMounted(() => {
      console.log('ChatBox mounted')
      
      // Connect WebSocket
      connectWebSocket()
      
      // If already visible on mount, load conversation
      if (props.visible) {
        loadConversation()
      }
    })
    
    // Cleanup WebSocket on unmount
    onUnmounted(() => {
      console.log('ChatBox unmounted')
      if (currentUserId.value) {
        WebSocketService.unsubscribeFromMessages(currentUserId.value)
      }
    })
    
    // Watch for user changes - clear messages when user changes
    watch(() => props.user, (newUser, oldUser) => {
      if (newUser && oldUser && newUser.id !== oldUser.id) {
        messages.value = []
        
        if (props.visible) {
          loadConversation()
        }
      }
    }, { deep: true })
    
    watch(() => props.visible, (isVisible) => {
      if (isVisible) {
        messages.value = []
        
        loadConversation()
        nextTick(() => {
          scrollToBottom()
        })
      }
    }, { immediate: true })
   </script>
  
  <style lang="scss" scoped>
    @use '@/components/layout/chat-box/ChatBox.scss';
    
    .loading-messages, .empty-messages {
      text-align: center;
      padding: 20px;
      color: #666;
      
      .fas {
        margin-bottom: 10px;
        font-size: 24px;
      }
      
      p {
        margin: 0;
        font-weight: 500;
      }
      
      small {
        display: block;
        margin-top: 5px;
        opacity: 0.7;
      }
    }
    
    .chatbox-input {
      input:disabled, button:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }
    }
  </style>