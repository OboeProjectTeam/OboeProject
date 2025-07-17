/**
 * API module for handling OAuth2 authentication
 */
import axiosInstance from '../axios';
import { API_CONFIG } from '../config';

const oauthApi = {
  // Lấy URL đăng nhập Google
  getGoogleAuthUrl: () => {
    return `${API_CONFIG.BASE_URL}/oauth2/authorization/google`;
  },

  // Lấy URL đăng nhập Facebook
  getFacebookAuthUrl: () => {
    return `${API_CONFIG.BASE_URL}/oauth2/authorization/facebook`;
  },

  // Xử lý callback sau khi đăng nhập thành công
  handleOAuthSuccess: async (token) => {
    return axiosInstance.get('/oauth2/success', {
      params: { token }
    });
  }
};

export default oauthApi; 