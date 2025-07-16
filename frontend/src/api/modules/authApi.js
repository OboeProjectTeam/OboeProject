import axios from '../axiosConfig';
import { handleApiError } from '../apiUtils';

/**
 * Module chứa các API liên quan đến xác thực người dùng
 */
const authApi = {
  /**
   * Đăng ký tài khoản mới
   * @param {Object} userData - Thông tin đăng ký (username, password, etc.)
   * @returns {Promise} Thông báo đã gửi email xác thực
   */
  async signup(userData) {
    try {
      const response = await axios.post('/api/auth/signup', userData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Xác thực tài khoản
   * @param {string} token - Token xác thực từ email
   * @returns {Promise} Thông báo xác thực thành công
   */
  async verify(token) {
    try {
      const response = await axios.get('/api/auth/verify', {
        params: { token }
      });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Đăng nhập với username và mật khẩu
   * @param {string} userName - Tên đăng nhập
   * @param {string} passWord - Mật khẩu
   * @returns {Promise} Thông tin người dùng và token
   */
  async login(userName, passWord) {
    try {
      const response = await axios.post('/api/auth/login', { userName, passWord });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Cập nhật thông tin cá nhân
   * @param {Object} userData - Thông tin cần cập nhật
   * @returns {Promise} Thông tin người dùng đã cập nhật
   */
  async updateProfile(userData) {
    try {
      const response = await axios.put('/api/auth/updateProfile', userData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Đổi mật khẩu
   * @param {Object} passwordData - Dữ liệu đổi mật khẩu (oldPassword, newPassword)
   * @returns {Promise} Thông báo đổi mật khẩu thành công
   */
  async changePassword(passwordData) {
    try {
      const response = await axios.put('/api/auth/changePassword', passwordData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default authApi; 