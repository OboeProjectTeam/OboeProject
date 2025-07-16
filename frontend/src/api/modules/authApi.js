import axios from '../axiosConfig';
import { handleApiError } from '../apiUtils';

/**
 * Module chứa các API liên quan đến xác thực người dùng
 */
export const authApi = {
  /**
   * Đăng nhập với email và mật khẩu
   * @param {string} email - Email đăng nhập
   * @param {string} password - Mật khẩu
   * @returns {Promise} Thông tin người dùng và token
   */
  async login(email, password) {
    try {
      const response = await axios.post('/auth/login', { email, password });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Đăng ký tài khoản mới
   * @param {Object} userData - Thông tin đăng ký (email, password, username, etc.)
   * @returns {Promise} Thông tin tài khoản đã tạo
   */
  async register(userData) {
    try {
      const response = await axios.post('/auth/register', userData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Đăng xuất khỏi hệ thống
   * - Xóa token ở server
   * - Không cần response data
   */
  async logout() {
    try {
      await axios.post('/auth/logout');
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Làm mới token xác thực
   * @param {string} refreshToken - Refresh token hiện tại
   * @returns {Promise} Token mới
   */
  async refreshToken(refreshToken) {
    try {
      const response = await axios.post('/auth/refresh-token', { refreshToken });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Gửi yêu cầu khôi phục mật khẩu
   * @param {string} email - Email cần khôi phục mật khẩu
   * @returns {Promise} Thông báo đã gửi email
   */
  async forgotPassword(email) {
    try {
      const response = await axios.post('/auth/forgot-password', { email });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Đặt lại mật khẩu mới
   * @param {string} token - Token từ email khôi phục
   * @param {string} newPassword - Mật khẩu mới
   * @returns {Promise} Thông báo đã đổi mật khẩu thành công
   */
  async resetPassword(token, newPassword) {
    try {
      const response = await axios.post('/auth/reset-password', {
        token,
        newPassword
      });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Xác thực email
   * @param {string} token - Token xác thực từ email
   * @returns {Promise} Thông báo xác thực thành công
   */
  async verifyEmail(token) {
    try {
      const response = await axios.post('/auth/verify-email', { token });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
}; 