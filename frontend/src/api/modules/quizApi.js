import axios from '../axiosConfig';
import { handleApiError, buildQueryString, getPaginationParams } from '../apiUtils';

/**
 * Module chứa các API liên quan đến quiz và câu hỏi
 */
export const quizApi = {
  /**
   * Lấy danh sách quiz có phân trang
   * @param {number} page - Số trang
   * @param {number} limit - Số lượng item trên mỗi trang
   * @returns {Promise} Danh sách quiz
   */
  async getQuizzes(page = 1, limit = 10) {
    try {
      const params = getPaginationParams(page, limit);
      const query = buildQueryString(params);
      const response = await axios.get(`/quizzes?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Tạo quiz mới
   * @param {Object} quizData - Dữ liệu quiz
   * @returns {Promise} Quiz đã tạo
   */
  async createQuiz(quizData) {
    try {
      const response = await axios.post('/quizzes', quizData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Cập nhật quiz
   * @param {string} id - ID của quiz
   * @param {Object} quizData - Dữ liệu cần cập nhật
   * @returns {Promise} Quiz đã cập nhật
   */
  async updateQuiz(id, quizData) {
    try {
      const response = await axios.put(`/quizzes/${id}`, quizData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Xóa quiz
   * @param {string} id - ID của quiz
   * @returns {Promise} Thông báo xóa thành công
   */
  async deleteQuiz(id) {
    try {
      const response = await axios.delete(`/quizzes/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy chi tiết quiz theo ID
   * @param {string} id - ID của quiz
   * @returns {Promise} Chi tiết quiz
   */
  async getQuizById(id) {
    try {
      const response = await axios.get(`/quizzes/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách câu hỏi của quiz
   * @param {string} quizId - ID của quiz
   * @returns {Promise} Danh sách câu hỏi
   */
  async getQuizQuestions(quizId) {
    try {
      const response = await axios.get(`/quizzes/${quizId}/questions`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Nộp bài làm quiz
   * @param {string} quizId - ID của quiz
   * @param {Object} answers - Đáp án của người dùng
   * @returns {Promise} Kết quả làm quiz
   */
  async submitQuiz(quizId, answers) {
    try {
      const response = await axios.post(`/quizzes/${quizId}/submit`, answers);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
}; 