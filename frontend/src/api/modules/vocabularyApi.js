import axios from '../axiosConfig';
import { handleApiError, buildQueryString, getPaginationParams } from '../apiUtils';

/**
 * Module chứa các API liên quan đến từ vựng tiếng Nhật
 */
export const vocabularyApi = {
  /**
   * Lấy danh sách từ vựng có phân trang
   * @param {number} page - Số trang
   * @param {number} limit - Số lượng item trên mỗi trang
   * @param {Object} filters - Các điều kiện lọc (level, category, etc.)
   * @returns {Promise} Danh sách từ vựng
   */
  async getVocabularies(page = 1, limit = 10, filters = {}) {
    try {
      const params = {
        ...getPaginationParams(page, limit),
        ...filters
      };
      const query = buildQueryString(params);
      const response = await axios.get(`/vocabularies?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy chi tiết từ vựng theo ID
   * @param {string} id - ID của từ vựng
   * @returns {Promise} Chi tiết từ vựng
   */
  async getVocabularyById(id) {
    try {
      const response = await axios.get(`/vocabularies/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Tìm kiếm từ vựng
   * @param {string} keyword - Từ khóa tìm kiếm
   * @returns {Promise} Danh sách từ vựng phù hợp
   */
  async searchVocabulary(keyword) {
    try {
      const params = { keyword };
      const query = buildQueryString(params);
      const response = await axios.get(`/vocabularies/search?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách từ vựng theo cấp độ JLPT
   * @param {number} level - Cấp độ JLPT (1-5)
   * @returns {Promise} Danh sách từ vựng theo cấp độ
   */
  async getVocabulariesByJLPTLevel(level) {
    try {
      const response = await axios.get(`/vocabularies/jlpt/${level}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách từ vựng theo chủ đề
   * @param {string} topic - Chủ đề
   * @returns {Promise} Danh sách từ vựng theo chủ đề
   */
  async getVocabulariesByTopic(topic) {
    try {
      const response = await axios.get(`/vocabularies/topic/${topic}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách ví dụ của từ vựng
   * @param {string} vocabularyId - ID của từ vựng
   * @returns {Promise} Danh sách ví dụ
   */
  async getVocabularyExamples(vocabularyId) {
    try {
      const response = await axios.get(`/vocabularies/${vocabularyId}/examples`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách từ vựng liên quan
   * @param {string} vocabularyId - ID của từ vựng
   * @returns {Promise} Danh sách từ vựng liên quan
   */
  async getRelatedVocabularies(vocabularyId) {
    try {
      const response = await axios.get(`/vocabularies/${vocabularyId}/related`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách từ đồng nghĩa và trái nghĩa
   * @param {string} vocabularyId - ID của từ vựng
   * @returns {Promise} Danh sách từ đồng nghĩa và trái nghĩa
   */
  async getSynonymsAndAntonyms(vocabularyId) {
    try {
      const response = await axios.get(`/vocabularies/${vocabularyId}/synonyms-antonyms`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
}; 