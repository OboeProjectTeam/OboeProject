import axios from '../axiosConfig';
import { handleApiError, buildQueryString, getPaginationParams } from '../apiUtils';

/**
 * Module chứa các API liên quan đến ngữ pháp tiếng Nhật
 */
export const grammarApi = {
  /**
   * Lấy danh sách ngữ pháp có phân trang
   * @param {number} page - Số trang
   * @param {number} limit - Số lượng item trên mỗi trang
   * @param {Object} filters - Các điều kiện lọc (level, category, etc.)
   * @returns {Promise} Danh sách ngữ pháp
   */
  async getGrammars(page = 1, limit = 10, filters = {}) {
    try {
      const params = {
        ...getPaginationParams(page, limit),
        ...filters
      };
      const query = buildQueryString(params);
      const response = await axios.get(`/grammars?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy chi tiết ngữ pháp theo ID
   * @param {string} id - ID của ngữ pháp
   * @returns {Promise} Chi tiết ngữ pháp
   */
  async getGrammarById(id) {
    try {
      const response = await axios.get(`/grammars/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Tìm kiếm ngữ pháp
   * @param {string} keyword - Từ khóa tìm kiếm
   * @returns {Promise} Danh sách ngữ pháp phù hợp
   */
  async searchGrammar(keyword) {
    try {
      const params = { keyword };
      const query = buildQueryString(params);
      const response = await axios.get(`/grammars/search?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách ngữ pháp theo cấp độ JLPT
   * @param {number} level - Cấp độ JLPT (1-5)
   * @returns {Promise} Danh sách ngữ pháp theo cấp độ
   */
  async getGrammarsByJLPTLevel(level) {
    try {
      const response = await axios.get(`/grammars/jlpt/${level}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách ví dụ của ngữ pháp
   * @param {string} grammarId - ID của ngữ pháp
   * @returns {Promise} Danh sách ví dụ
   */
  async getGrammarExamples(grammarId) {
    try {
      const response = await axios.get(`/grammars/${grammarId}/examples`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách ngữ pháp liên quan
   * @param {string} grammarId - ID của ngữ pháp
   * @returns {Promise} Danh sách ngữ pháp liên quan
   */
  async getRelatedGrammars(grammarId) {
    try {
      const response = await axios.get(`/grammars/${grammarId}/related`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
}; 