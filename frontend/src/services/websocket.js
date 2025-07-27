// services/websocket.js

let socket = null;
let userId = null;
const reconnectDelay = 5000;
const listeners = new Set();

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
      listeners.forEach((cb) => cb(data));
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

function onMessage(callback) {
  listeners.add(callback);
}

function removeListener(callback) {
  listeners.delete(callback);
}

export default {
  connect,
  send,
  onMessage,
  removeListener,
};
