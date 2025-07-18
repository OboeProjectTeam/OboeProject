import axios from '../axiosConfig';
import { handleApiError, buildQueryString, getPaginationParams } from '../apiUtils';

/**
 * Module chứa các API liên quan đến thông báo
 */
export const notificationApi = {
  /**
   * Lấy danh sách thông báo của người dùng
   * @param {number} page - Số trang
   * @param {number} limit - Số lượng item trên mỗi trang
   * @param {Object} filters - Các điều kiện lọc (read, type, etc.)
   * @returns {Promise} Danh sách thông báo
   */
  async getNotifications(page = 1, limit = 10, filters = {}) {
    try {
      const params = {
        ...getPaginationParams(page, limit),
        ...filters
      };
      const query = buildQueryString(params);
      const response = await axios.get(`/notifications?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Đánh dấu thông báo đã đọc
   * @param {string} notificationId - ID của thông báo
   * @returns {Promise} Thông báo đã cập nhật
   */
  async markAsRead(notificationId) {
    try {
      const response = await axios.put(`/notifications/${notificationId}/read`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Đánh dấu tất cả thông báo đã đọc
   * @returns {Promise} Thông báo thành công
   */
  async markAllAsRead() {
    try {
      const response = await axios.put('/notifications/read-all');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Xóa thông báo
   * @param {string} notificationId - ID của thông báo
   * @returns {Promise} Thông báo xóa thành công
   */
  async deleteNotification(notificationId) {
    try {
      const response = await axios.delete(`/notifications/${notificationId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Xóa tất cả thông báo
   * @returns {Promise} Thông báo xóa thành công
   */
  async deleteAllNotifications() {
    try {
      const response = await axios.delete('/notifications/delete-all');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy số lượng thông báo chưa đọc
   * @returns {Promise} Số lượng thông báo chưa đọc
   */
  async getUnreadCount() {
    try {
      const response = await axios.get('/notifications/unread-count');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Cập nhật cài đặt thông báo
   * @param {Object} settings - Cài đặt thông báo mới
   * @returns {Promise} Cài đặt đã cập nhật
   */
  async updateNotificationSettings(settings) {
    try {
      const response = await axios.put('/notifications/settings', settings);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
}; 