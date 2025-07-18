// API Configuration
const BASE_URL = 'https://oboeru.me';

export const API_CONFIG = {
  // Cấu hình chung cho axios
  AXIOS_CONFIG: {
    baseURL: BASE_URL,
    timeout: 30000,
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }
  },

  // Chỉ giữ các OAuth endpoints vì đây là redirect URLs
  ENDPOINTS: {
    OAUTH2: {
      GOOGLE: '/oauth2/authorization/google',
      FACEBOOK: '/oauth2/authorization/facebook',
      SUCCESS: '/oauth2/success'
    }
  },

  // Helper function để lấy full URL (dùng cho OAuth redirect)
  getFullUrl: (path) => `${BASE_URL}${path}`
}; 