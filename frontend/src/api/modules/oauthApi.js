/**
 * API module for handling OAuth2 authentication
 */
import axiosInstance from '../axios';
import { API_CONFIG } from '../config';

const oauthApi = {
  // Lấy URL đăng nhập Google
  getGoogleAuthUrl: () => {
    return API_CONFIG.getFullUrl(API_CONFIG.ENDPOINTS.OAUTH2.GOOGLE);
  },

  // Lấy URL đăng nhập Facebook
  getFacebookAuthUrl: () => {
    return API_CONFIG.getFullUrl(API_CONFIG.ENDPOINTS.OAUTH2.FACEBOOK);
  },

  // Xử lý callback sau khi đăng nhập thành công
  handleOAuthSuccess: async (token) => {
    return axiosInstance.get(API_CONFIG.ENDPOINTS.OAUTH2.SUCCESS, {
      params: { token }
    });
  }
};

export default oauthApi; 