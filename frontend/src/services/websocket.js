let socket = null;
let userId = null;
const reconnectDelay = 5000;

let onMessageCallback = null;
let onNotificationCallback = null;

function connect(id) {
  userId = id;
  const wsUrl = `wss://oboeru.me/ws-raw?userId=${userId}`;
  socket = new WebSocket(wsUrl);

  socket.onopen = () => {
    console.log(" WebSocket connected");
  };

  socket.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data);

      // Phân biệt giữa message và notification
      if (data.messageId && data.senderId) {
        if (onMessageCallback) {
          onMessageCallback(data);
        } else {
          console.warn(" onMessageCallback not defined");
        }
      } else if (data.text_notification || data.notifiId) {
        if (onNotificationCallback) {
          onNotificationCallback(data);
        } else {
          console.warn(" onNotificationCallback not defined");
        }
      } else {
        console.warn(" Received unknown WebSocket data format:", data);
      }
    } catch (e) {
      console.error(" Failed to parse WebSocket message", e);
    }
  };

  socket.onclose = () => {
    console.warn(" WebSocket closed. Reconnecting in 5s...");
    setTimeout(() => connect(userId), reconnectDelay);
  };

  socket.onerror = (err) => {
    console.error(" WebSocket error:", err);
  };
}

function send(data) {
  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.send(JSON.stringify(data));
  } else {
    console.warn("⚠️ WebSocket not connected. Cannot send:", data);
  }
}

function disconnect() {
  if (socket) {
    console.log("🔌 Disconnecting WebSocket...");
    socket.close();
    socket = null;
    userId = null;
    onMessageCallback = null;
    onNotificationCallback = null;
  }
}

// Gán callback cho message
function onMessage(callback) {
  onMessageCallback = callback;
}
function removeMessageListener() {
  onMessageCallback = null;
}

// Gán callback cho notification
function onNotification(callback) {
  onNotificationCallback = callback;
}
function removeNotificationListener() {
  onNotificationCallback = null;
}

export default {
  connect,
  send,
  disconnect,
  onMessage,
  onNotification,
  removeMessageListener,
  removeNotificationListener,
};
