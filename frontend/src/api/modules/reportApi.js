import axios from '../axiosConfig';
import { handleApiError, buildQueryString, getPaginationParams } from '../apiUtils';

/**
 * Module chứa các API liên quan đến báo cáo
 */
export const reportApi = {
  /**
   * Gửi báo cáo mới
   * @param {Object} reportData - Dữ liệu báo cáo
   * @param {string} reportData.type - Loại báo cáo (user, content, comment, etc.)
   * @param {string} reportData.targetId - ID của đối tượng bị báo cáo
   * @param {string} reportData.reason - Lý do báo cáo
   * @param {string} reportData.description - Mô tả chi tiết
   * @returns {Promise} Báo cáo đã tạo
   */
  async createReport(reportData) {
    try {
      const response = await axios.post('/reports', reportData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách báo cáo của người dùng
   * @param {number} page - Số trang
   * @param {number} limit - Số lượng item trên mỗi trang
   * @param {Object} filters - Các điều kiện lọc (status, type, etc.)
   * @returns {Promise} Danh sách báo cáo
   */
  async getUserReports(page = 1, limit = 10, filters = {}) {
    try {
      const params = {
        ...getPaginationParams(page, limit),
        ...filters
      };
      const query = buildQueryString(params);
      const response = await axios.get(`/reports/user?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Cập nhật trạng thái báo cáo
   * @param {string} reportId - ID của báo cáo
   * @param {string} status - Trạng thái mới
   * @returns {Promise} Báo cáo đã cập nhật
   */
  async updateReportStatus(reportId, status) {
    try {
      const response = await axios.put(`/reports/${reportId}/status`, { status });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Thêm bình luận vào báo cáo
   * @param {string} reportId - ID của báo cáo
   * @param {string} comment - Nội dung bình luận
   * @returns {Promise} Bình luận đã thêm
   */
  async addReportComment(reportId, comment) {
    try {
      const response = await axios.post(`/reports/${reportId}/comments`, { comment });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy chi tiết báo cáo
   * @param {string} reportId - ID của báo cáo
   * @returns {Promise} Chi tiết báo cáo
   */
  async getReportDetail(reportId) {
    try {
      const response = await axios.get(`/reports/${reportId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy lịch sử báo cáo của một đối tượng
   * @param {string} targetType - Loại đối tượng (user, content, comment)
   * @param {string} targetId - ID của đối tượng
   * @returns {Promise} Lịch sử báo cáo
   */
  async getReportHistory(targetType, targetId) {
    try {
      const response = await axios.get(`/reports/history/${targetType}/${targetId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
}; 