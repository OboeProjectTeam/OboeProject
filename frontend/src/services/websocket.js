// services/websocket.js

let socket = null;
let userId = null;
const reconnectDelay = 5000;

const messageListeners = new Set();
const notificationListeners = new Set();

function connect(id) {
  userId = id;
  const wsUrl = `wss://oboeru.me/ws-raw?userId=${userId}`;
  socket = new WebSocket(wsUrl);

  socket.onopen = () => {
    console.log("✅ WebSocket connected");
  };

  socket.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data);

      if (data.messageId && data.senderId) {
        messageListeners.forEach((cb) => cb(data));
      } else if (data.notifiId && data.textNotification) {
        notificationListeners.forEach((cb) => cb(data));
      } else {
        console.warn("🟡 Received unknown WebSocket data format:", data);
      }

    } catch (err) {
      console.warn("Received non-JSON message:", event.data);
    }
  };

  socket.onclose = () => {
    console.warn("WebSocket closed. Reconnecting in 5s...");
    setTimeout(() => connect(userId), reconnectDelay);
  };

  socket.onerror = (err) => {
    console.error("WebSocket error:", err);
  };
}

function send(data) {
  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.send(JSON.stringify(data));
  } else {
    console.warn("WebSocket not connected. Cannot send:", data);
  }
}

// 👇 Cho tin nhắn
function onMessage(callback) {
  messageListeners.add(callback);
}
function removeMessageListener(callback) {
  messageListeners.delete(callback);
}

// 👇 Cho thông báo
function onNotification(callback) {
  notificationListeners.add(callback);
}
function removeNotificationListener(callback) {
  notificationListeners.delete(callback);
}

export default {
  connect,
  send,
  onMessage,
  onNotification,
  removeMessageListener,
  removeNotificationListener,
};
