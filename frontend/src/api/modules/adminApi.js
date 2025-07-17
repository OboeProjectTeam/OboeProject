import axios from '../axiosConfig';
import { handleApiError, buildQueryString, getPaginationParams } from '../apiUtils';

/**
 * Module chứa các API liên quan đến quản trị viên
 */
export const adminApi = {
  /**
   * Lấy thống kê tổng quan
   * @returns {Promise} Dữ liệu thống kê
   */
  async getDashboardStats() {
    try {
      const response = await axios.get('/admin/dashboard/stats');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Quản lý người dùng
   */
  users: {
    /**
     * Lấy danh sách người dùng
     * @param {number} page - Số trang
     * @param {number} limit - Số lượng item trên mỗi trang
     * @param {Object} filters - Các điều kiện lọc
     * @returns {Promise} Danh sách người dùng
     */
    async getUsers(page = 1, limit = 10, filters = {}) {
      try {
        const params = {
          ...getPaginationParams(page, limit),
          ...filters
        };
        const query = buildQueryString(params);
        const response = await axios.get(`/admin/users?${query}`);
        return response.data;
      } catch (error) {
        throw new Error(handleApiError(error));
      }
    },

    /**
     * Cập nhật thông tin người dùng
     * @param {string} userId - ID của người dùng
     * @param {Object} userData - Thông tin cần cập nhật
     * @returns {Promise} Thông tin người dùng đã cập nhật
     */
    async updateUser(userId, userData) {
      try {
        const response = await axios.put(`/admin/users/${userId}`, userData);
        return response.data;
      } catch (error) {
        throw new Error(handleApiError(error));
      }
    },

    /**
     * Khóa/Mở khóa tài khoản người dùng
     * @param {string} userId - ID của người dùng
     * @param {boolean} isBlocked - Trạng thái khóa
     * @returns {Promise} Thông báo thành công
     */
    async toggleUserBlock(userId, isBlocked) {
      try {
        const response = await axios.put(`/admin/users/${userId}/block`, { isBlocked });
        return response.data;
      } catch (error) {
        throw new Error(handleApiError(error));
      }
    }
  },

  /**
   * Quản lý nội dung
   */
  content: {
    /**
     * Lấy danh sách nội dung cần kiểm duyệt
     * @param {number} page - Số trang
     * @param {number} limit - Số lượng item trên mỗi trang
     * @param {Object} filters - Các điều kiện lọc
     * @returns {Promise} Danh sách nội dung
     */
    async getContentForReview(page = 1, limit = 10, filters = {}) {
      try {
        const params = {
          ...getPaginationParams(page, limit),
          ...filters
        };
        const query = buildQueryString(params);
        const response = await axios.get(`/admin/content/review?${query}`);
        return response.data;
      } catch (error) {
        throw new Error(handleApiError(error));
      }
    },

    /**
     * Phê duyệt/Từ chối nội dung
     * @param {string} contentId - ID của nội dung
     * @param {boolean} isApproved - Trạng thái phê duyệt
     * @param {string} reason - Lý do từ chối (nếu có)
     * @returns {Promise} Thông báo thành công
     */
    async reviewContent(contentId, isApproved, reason = '') {
      try {
        const response = await axios.put(`/admin/content/${contentId}/review`, {
          isApproved,
          reason
        });
        return response.data;
      } catch (error) {
        throw new Error(handleApiError(error));
      }
    }
  },

  /**
   * Quản lý báo cáo
   */
  reports: {
    /**
     * Lấy danh sách báo cáo
     * @param {number} page - Số trang
     * @param {number} limit - Số lượng item trên mỗi trang
     * @param {Object} filters - Các điều kiện lọc
     * @returns {Promise} Danh sách báo cáo
     */
    async getReports(page = 1, limit = 10, filters = {}) {
      try {
        const params = {
          ...getPaginationParams(page, limit),
          ...filters
        };
        const query = buildQueryString(params);
        const response = await axios.get(`/admin/reports?${query}`);
        return response.data;
      } catch (error) {
        throw new Error(handleApiError(error));
      }
    },

    /**
     * Xử lý báo cáo
     * @param {string} reportId - ID của báo cáo
     * @param {string} action - Hành động xử lý
     * @param {string} note - Ghi chú xử lý
     * @returns {Promise} Thông báo thành công
     */
    async handleReport(reportId, action, note = '') {
      try {
        const response = await axios.put(`/admin/reports/${reportId}`, {
          action,
          note
        });
        return response.data;
      } catch (error) {
        throw new Error(handleApiError(error));
      }
    }
  },

  /**
   * Quản lý cài đặt hệ thống
   */
  settings: {
    /**
     * Lấy cài đặt hệ thống
     * @returns {Promise} Cài đặt hệ thống
     */
    async getSettings() {
      try {
        const response = await axios.get('/admin/settings');
        return response.data;
      } catch (error) {
        throw new Error(handleApiError(error));
      }
    },

    /**
     * Cập nhật cài đặt hệ thống
     * @param {Object} settings - Cài đặt mới
     * @returns {Promise} Cài đặt đã cập nhật
     */
    async updateSettings(settings) {
      try {
        const response = await axios.put('/admin/settings', settings);
        return response.data;
      } catch (error) {
        throw new Error(handleApiError(error));
      }
    }
  }
}; 