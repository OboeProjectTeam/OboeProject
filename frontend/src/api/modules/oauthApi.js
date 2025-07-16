/**
 * API module for handling OAuth2 authentication
 */
import axiosClient from '../axiosConfig';

/**
 * Xử lý kết quả đăng nhập OAuth2 thành công
 * @param {string} token - Token nhận được sau khi đăng nhập OAuth2
 * @returns {Promise} Promise chứa thông tin đăng nhập
 */
export const handleOAuthSuccess = (token) => {
  return axiosClient.get('/oauth2/success', {
    params: { token }
  });
};

/**
 * Khởi tạo đăng nhập qua Google
 * @returns {Promise} Promise chứa URL đăng nhập Google
 */
export const initiateGoogleLogin = () => {
  return axiosClient.get('/oauth2/google/url');
};

/**
 * Khởi tạo đăng nhập qua Facebook
 * @returns {Promise} Promise chứa URL đăng nhập Facebook
 */
export const initiateFacebookLogin = () => {
  return axiosClient.get('/oauth2/facebook/url');
}; 