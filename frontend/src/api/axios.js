// api/axios.js
import axios from 'axios';
import { useRouter } from 'vue-router';

// Determine base URL based on environment
const getBaseURL = () => {
  if (import.meta.env.DEV) {
    // Development: use Vite proxy
    return '';
  } else {
    // Production: use full domain
    return 'https://oboeru.me/';
  }
};

const instance = axios.create({
  baseURL: getBaseURL(),
  timeout: 10000, // 10 second timeout
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - gửi token tự động nếu có
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;

    } else {

    }


    return config;
  },
  (error) => {
    console.error('Request interceptor error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor - xử lý lỗi authentication
instance.interceptors.response.use(
  (response) => {

    return response;
  },
  (error) => {
    console.error('API Error:', error);
    
    if (error.response) {
      const { status } = error.response;
      
      // Handle authentication errors
      if (status === 401) {
        console.warn('Authentication failed, clearing tokens');
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        
        // Only redirect to login if not already on login/register page
        if (!window.location.pathname.includes('/login') && 
            !window.location.pathname.includes('/register')) {
          window.location.href = '/login';
        }
      }
    } else if (error.request) {
      console.error('Network error - no response received');
    } else {
      console.error('Request setup error:', error.message);
    }
    
    return Promise.reject(error);
  }
);

export default instance;
