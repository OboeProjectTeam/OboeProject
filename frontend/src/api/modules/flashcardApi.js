import axios from '../axiosConfig';
import { handleApiError, buildQueryString, getPaginationParams } from '../apiUtils';

/**
 * Module chứa các API liên quan đến flashcard
 */
export const flashcardApi = {
  /**
   * Lấy danh sách flashcard có phân trang
   * @param {number} page - Số trang
   * @param {number} limit - Số lượng item trên mỗi trang
   * @returns {Promise} Danh sách flashcard
   */
  async getFlashcards(page = 1, limit = 10) {
    try {
      const params = getPaginationParams(page, limit);
      const query = buildQueryString(params);
      const response = await axios.get(`/flashcards?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Tạo flashcard mới
   * @param {Object} flashcardData - Dữ liệu flashcard
   * @returns {Promise} Flashcard đã tạo
   */
  async createFlashcard(flashcardData) {
    try {
      const response = await axios.post('/flashcards', flashcardData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Cập nhật flashcard
   * @param {string} id - ID của flashcard
   * @param {Object} flashcardData - Dữ liệu cần cập nhật
   * @returns {Promise} Flashcard đã cập nhật
   */
  async updateFlashcard(id, flashcardData) {
    try {
      const response = await axios.put(`/flashcards/${id}`, flashcardData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Xóa flashcard
   * @param {string} id - ID của flashcard
   * @returns {Promise} Thông báo xóa thành công
   */
  async deleteFlashcard(id) {
    try {
      const response = await axios.delete(`/flashcards/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy chi tiết flashcard theo ID
   * @param {string} id - ID của flashcard
   * @returns {Promise} Chi tiết flashcard
   */
  async getFlashcardById(id) {
    try {
      const response = await axios.get(`/flashcards/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách flashcard theo người dùng
   * @param {string} userId - ID của người dùng
   * @returns {Promise} Danh sách flashcard của người dùng
   */
  async getFlashcardsByUser(userId) {
    try {
      const response = await axios.get(`/flashcards/user/${userId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
}; 