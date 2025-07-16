import axios from '../axiosConfig';
import { handleApiError, buildQueryString, getPaginationParams } from '../apiUtils';

/**
 * Module chứa các API liên quan đến người dùng
 */
export const userApi = {
  /**
   * Lấy thông tin profile của người dùng hiện tại
   * @returns {Promise} Thông tin chi tiết người dùng
   */
  async getProfile() {
    try {
      const response = await axios.get('/users/profile');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Cập nhật thông tin profile
   * @param {Object} userData - Dữ liệu cần cập nhật (fullName, bio, etc.)
   * @returns {Promise} Thông tin profile đã cập nhật
   */
  async updateProfile(userData) {
    try {
      const response = await axios.put('/users/profile', userData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Upload ảnh đại diện mới
   * @param {FormData} formData - Form data chứa file ảnh
   * @returns {Promise} URL ảnh đại diện mới
   */
  async updateAvatar(formData) {
    try {
      const response = await axios.post('/users/avatar', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Đổi mật khẩu
   * @param {string} oldPassword - Mật khẩu hiện tại
   * @param {string} newPassword - Mật khẩu mới
   * @returns {Promise} Thông báo đổi mật khẩu thành công
   */
  async changePassword(oldPassword, newPassword) {
    try {
      const response = await axios.put('/users/change-password', {
        oldPassword,
        newPassword,
      });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy thông tin người dùng theo ID
   * @param {string} userId - ID của người dùng
   * @returns {Promise} Thông tin người dùng
   */
  async getUserById(userId) {
    try {
      const response = await axios.get(`/users/${userId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách hoạt động của người dùng
   * @param {string} userId - ID của người dùng
   * @param {number} page - Số trang
   * @param {number} limit - Số lượng item trên mỗi trang
   * @returns {Promise} Danh sách hoạt động có phân trang
   */
  async getUserActivities(userId, page = 1, limit = 10) {
    try {
      const params = getPaginationParams(page, limit);
      const query = buildQueryString(params);
      const response = await axios.get(`/users/${userId}/activities?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Cập nhật cài đặt thông báo
   * @param {Object} settings - Cài đặt thông báo mới
   * @returns {Promise} Cài đặt thông báo đã cập nhật
   */
  async updateNotificationSettings(settings) {
    try {
      const response = await axios.put('/users/notification-settings', settings);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
}; 