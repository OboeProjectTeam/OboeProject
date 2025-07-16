import axios from '../axiosConfig';
import { handleApiError, buildQueryString, getPaginationParams } from '../apiUtils';

/**
 * Module chứa các API liên quan đến mục yêu thích
 */
export const favoriteApi = {
  /**
   * Lấy danh sách mục yêu thích của người dùng
   * @param {number} page - Số trang
   * @param {number} limit - Số lượng item trên mỗi trang
   * @param {Object} filters - Các điều kiện lọc (type, etc.)
   * @returns {Promise} Danh sách mục yêu thích
   */
  async getFavorites(page = 1, limit = 10, filters = {}) {
    try {
      const params = {
        ...getPaginationParams(page, limit),
        ...filters
      };
      const query = buildQueryString(params);
      const response = await axios.get(`/favorites?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Thêm vào mục yêu thích
   * @param {Object} favoriteData - Dữ liệu yêu thích
   * @param {string} favoriteData.type - Loại (kanji, vocabulary, grammar, etc.)
   * @param {string} favoriteData.itemId - ID của item
   * @returns {Promise} Mục yêu thích đã tạo
   */
  async addToFavorites(favoriteData) {
    try {
      const response = await axios.post('/favorites', favoriteData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Xóa khỏi mục yêu thích
   * @param {string} favoriteId - ID của mục yêu thích
   * @returns {Promise} Thông báo xóa thành công
   */
  async removeFromFavorites(favoriteId) {
    try {
      const response = await axios.delete(`/favorites/${favoriteId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Kiểm tra item có trong mục yêu thích không
   * @param {string} type - Loại item
   * @param {string} itemId - ID của item
   * @returns {Promise} Trạng thái yêu thích
   */
  async checkFavoriteStatus(type, itemId) {
    try {
      const response = await axios.get(`/favorites/check/${type}/${itemId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách yêu thích theo loại
   * @param {string} type - Loại item (kanji, vocabulary, grammar, etc.)
   * @param {number} page - Số trang
   * @param {number} limit - Số lượng item trên mỗi trang
   * @returns {Promise} Danh sách mục yêu thích theo loại
   */
  async getFavoritesByType(type, page = 1, limit = 10) {
    try {
      const params = getPaginationParams(page, limit);
      const query = buildQueryString(params);
      const response = await axios.get(`/favorites/type/${type}?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Đồng bộ mục yêu thích giữa các thiết bị
   * @param {Array} favoriteIds - Danh sách ID mục yêu thích
   * @returns {Promise} Danh sách đã đồng bộ
   */
  async syncFavorites(favoriteIds) {
    try {
      const response = await axios.post('/favorites/sync', { favoriteIds });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
}; 