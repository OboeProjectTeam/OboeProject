// src/services/websocket-stomp.js
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

class StompWebSocket {
  constructor(url, userId) {
    this.url = url
    this.userId = userId
    this.client = null
    this.subscriptions = {}
  }

  connect(onConnected, onMessage) {
    const socket = new SockJS(this.url)
    this.client = new Client({
      webSocketFactory: () => socket,
      debug: function (str) {
        console.log('[STOMP]', str)
      },
      reconnectDelay: 3000,
      onConnect: () => {
        console.log('STOMP connected')
        this.subscribeToMessages(onMessage)
        if (onConnected) onConnected()
      },
      onStompError: (frame) => {
        console.error('STOMP error', frame)
      }
    })
    this.client.activate()
  }

  subscribeToMessages(onMessage) {
    const dest = `/receiver/${this.userId}`
    this.subscriptions[dest] = this.client.subscribe(dest, (message) => {
      const data = JSON.parse(message.body)
      if (onMessage) onMessage(data)
    })
  }

  send(destination, body) {
    if (this.client && this.client.connected) {
      this.client.publish({ destination, body: JSON.stringify(body) })
    } else {
      console.warn('STOMP client not connected')
    }
  }

  disconnect() {
    if (this.client) this.client.deactivate()
  }
}

export default StompWebSocket
