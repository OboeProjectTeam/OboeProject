// api/axios.js
import axios from 'axios';

const instance = axios.create({
  baseURL: 'https://oboeru.me/',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Gửi token tự động nếu có
instance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default instance;
