import axios from '../axiosConfig';
import { handleApiError, buildQueryString, getPaginationParams } from '../apiUtils';

/**
 * Module chứa các API liên quan đến kanji
 */
export const kanjiApi = {
  /**
   * Lấy danh sách kanji có phân trang
   * @param {number} page - Số trang
   * @param {number} limit - Số lượng item trên mỗi trang
   * @param {Object} filters - Các điều kiện lọc (level, radical, etc.)
   * @returns {Promise} Danh sách kanji
   */
  async getKanjis(page = 1, limit = 10, filters = {}) {
    try {
      const params = {
        ...getPaginationParams(page, limit),
        ...filters
      };
      const query = buildQueryString(params);
      const response = await axios.get(`/kanjis?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy chi tiết kanji theo ID
   * @param {string} id - ID của kanji
   * @returns {Promise} Chi tiết kanji
   */
  async getKanjiById(id) {
    try {
      const response = await axios.get(`/kanjis/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Tìm kiếm kanji
   * @param {string} keyword - Từ khóa tìm kiếm
   * @returns {Promise} Danh sách kanji phù hợp
   */
  async searchKanji(keyword) {
    try {
      const params = { keyword };
      const query = buildQueryString(params);
      const response = await axios.get(`/kanjis/search?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách kanji theo cấp độ JLPT
   * @param {number} level - Cấp độ JLPT (1-5)
   * @returns {Promise} Danh sách kanji theo cấp độ
   */
  async getKanjisByJLPTLevel(level) {
    try {
      const response = await axios.get(`/kanjis/jlpt/${level}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách kanji theo bộ thủ
   * @param {string} radical - Bộ thủ
   * @returns {Promise} Danh sách kanji có cùng bộ thủ
   */
  async getKanjisByRadical(radical) {
    try {
      const response = await axios.get(`/kanjis/radical/${radical}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách kanji liên quan
   * @param {string} kanjiId - ID của kanji
   * @returns {Promise} Danh sách kanji liên quan
   */
  async getRelatedKanjis(kanjiId) {
    try {
      const response = await axios.get(`/kanjis/${kanjiId}/related`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy thông tin về cách viết của kanji
   * @param {string} kanjiId - ID của kanji
   * @returns {Promise} Thông tin stroke order
   */
  async getKanjiStrokes(kanjiId) {
    try {
      const response = await axios.get(`/kanjis/${kanjiId}/strokes`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
}; 