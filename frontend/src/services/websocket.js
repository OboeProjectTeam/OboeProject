import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'

class WebSocketService {
  constructor() {
    this.stompClient = null
    this.connected = false
    this.subscriptions = new Map()
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.reconnectDelay = 3000
  }

  connect() {
    return new Promise((resolve, reject) => {
      try {
        // Determine WebSocket URL based on environment
        const getWebSocketURL = () => {
          if (import.meta.env.DEV) {
            // Development: use full domain to avoid proxy issues
            return 'https://oboeru.me/ws'
          } else {
            // Production: use full domain
            return 'https://oboeru.me/ws'
          }
        }

        // Create SockJS connection
        const socket = new SockJS(getWebSocketURL())
        this.stompClient = Stomp.over(socket)

        // Disable debug logs in production
        this.stompClient.debug = (str) => {
          console.log('STOMP: ' + str)
        }

        // Connect to WebSocket
        this.stompClient.connect(
          {}, // headers
          (frame) => {
            console.log('WebSocket connected: ' + frame)
            this.connected = true
            this.reconnectAttempts = 0
            resolve(frame)
          },
          (error) => {
            console.error('WebSocket connection error:', error)
            this.connected = false
            this.handleReconnect()
            reject(error)
          }
        )

        // Handle connection close
        this.stompClient.onWebSocketClose = () => {
          console.log('WebSocket connection closed')
          this.connected = false
          this.handleReconnect()
        }

      } catch (error) {
        console.error('Failed to create WebSocket connection:', error)
        reject(error)
      }
    })
  }

  handleReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      console.log(`Attempting to reconnect... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`)
      
      setTimeout(() => {
        this.connect().catch(error => {
          console.error('Reconnection failed:', error)
        })
      }, this.reconnectDelay)
    } else {
      console.error('Max reconnection attempts reached')
    }
  }

  // Subscribe to receive messages for a specific user
  subscribeToMessages(userId, callback) {
    if (!this.connected || !this.stompClient) {
      console.error('WebSocket not connected')
      return null
    }

    const destination = `/receiver/${userId}`
    console.log('Subscribing to:', destination)

    const subscription = this.stompClient.subscribe(destination, (message) => {
      try {
        const messageData = JSON.parse(message.body)
        console.log('Received message via WebSocket:', messageData)
        callback(messageData)
      } catch (error) {
        console.error('Error parsing WebSocket message:', error)
      }
    })

    // Store subscription for cleanup
    this.subscriptions.set(`messages_${userId}`, subscription)
    return subscription
  }

  // Subscribe to notifications for a specific user
  subscribeToNotifications(userId, callback) {
    if (!this.connected || !this.stompClient) {
      console.error('WebSocket not connected')
      return null
    }

    const destination = `/notification/${userId}`
    console.log('Subscribing to notifications:', destination)

    const subscription = this.stompClient.subscribe(destination, (notification) => {
      try {
        const notificationText = notification.body
        console.log('Received notification via WebSocket:', notificationText)
        callback(notificationText)
      } catch (error) {
        console.error('Error parsing WebSocket notification:', error)
      }
    })

    // Store subscription for cleanup
    this.subscriptions.set(`notifications_${userId}`, subscription)
    return subscription
  }

  // Unsubscribe from messages
  unsubscribeFromMessages(userId) {
    const subscription = this.subscriptions.get(`messages_${userId}`)
    if (subscription) {
      subscription.unsubscribe()
      this.subscriptions.delete(`messages_${userId}`)
      console.log('Unsubscribed from messages for user:', userId)
    }
  }

  // Unsubscribe from notifications
  unsubscribeFromNotifications(userId) {
    const subscription = this.subscriptions.get(`notifications_${userId}`)
    if (subscription) {
      subscription.unsubscribe()
      this.subscriptions.delete(`notifications_${userId}`)
      console.log('Unsubscribed from notifications for user:', userId)
    }
  }

  // Disconnect WebSocket
  disconnect() {
    if (this.stompClient && this.connected) {
      // Unsubscribe from all subscriptions
      this.subscriptions.forEach((subscription) => {
        subscription.unsubscribe()
      })
      this.subscriptions.clear()

      // Disconnect
      this.stompClient.disconnect(() => {
        console.log('WebSocket disconnected')
        this.connected = false
      })
    }
  }

  // Check if connected
  isConnected() {
    return this.connected
  }
}

// Create singleton instance
const webSocketService = new WebSocketService()

export default webSocketService