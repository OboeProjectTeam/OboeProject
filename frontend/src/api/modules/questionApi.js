/**
 * API module for handling questions
 */
import axiosClient from '../axiosConfig';

/**
 * Lấy danh sách câu hỏi có phân trang
 * @param {number} page - Số trang
 * @param {number} size - Số lượng item trên một trang
 * @returns {Promise} Promise chứa danh sách câu hỏi
 */
export const getQuestions = (page = 0, size = 10) => {
  return axiosClient.get('/api/questions', {
    params: { page, size }
  });
};

/**
 * Lấy chi tiết một câu hỏi
 * @param {string} id - ID của câu hỏi
 * @returns {Promise} Promise chứa thông tin câu hỏi
 */
export const getQuestionById = (id) => {
  return axiosClient.get(`/api/questions/${id}`);
};

/**
 * Tạo câu hỏi mới
 * @param {Object} questionData - Dữ liệu câu hỏi
 * @returns {Promise} Promise chứa câu hỏi đã tạo
 */
export const createQuestion = (questionData) => {
  return axiosClient.post('/api/questions', questionData);
};

/**
 * Cập nhật câu hỏi
 * @param {string} id - ID của câu hỏi
 * @param {Object} questionData - Dữ liệu câu hỏi mới
 * @returns {Promise} Promise chứa câu hỏi đã cập nhật
 */
export const updateQuestion = (id, questionData) => {
  return axiosClient.put(`/api/questions/${id}`, questionData);
};

/**
 * Xóa câu hỏi
 * @param {string} id - ID của câu hỏi
 * @returns {Promise} Promise chứa kết quả xóa
 */
export const deleteQuestion = (id) => {
  return axiosClient.delete(`/api/questions/${id}`);
};

/**
 * Tìm kiếm câu hỏi
 * @param {string} keyword - Từ khóa tìm kiếm
 * @returns {Promise} Promise chứa danh sách câu hỏi phù hợp
 */
export const searchQuestions = (keyword) => {
  return axiosClient.get('/api/questions/search', {
    params: { keyword }
  });
}; 