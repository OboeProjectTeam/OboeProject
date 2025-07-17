/**
 * API module for handling comments
 */
import axiosClient from '../axiosConfig';

/**
 * Lấy tất cả comment theo ID (blog, kanji, etc.)
 * @param {string} id - ID của đối tượng cần lấy comment
 * @param {number} page - Số trang
 * @param {number} size - Số lượng item trên một trang
 * @returns {Promise} Promise chứa danh sách comment
 */
export const getComments = (id, page = 0, size = 5) => {
  return axiosClient.get(`/api/comments/${id}`, {
    params: { page, size }
  });
};

/**
 * Tạo comment mới cho một đối tượng
 * @param {string} teamId - ID của đối tượng được comment
 * @param {Object} commentData - Dữ liệu comment
 * @returns {Promise} Promise chứa comment đã tạo
 */
export const createComment = (teamId, commentData) => {
  return axiosClient.post(`/api/comments/${teamId}`, commentData);
};

/**
 * Trả lời một comment
 * @param {string} commentId - ID của comment được trả lời
 * @param {Object} replyData - Dữ liệu trả lời
 * @returns {Promise} Promise chứa comment trả lời đã tạo
 */
export const replyToComment = (commentId, replyData) => {
  return axiosClient.post(`/api/comments/reply/${commentId}`, replyData);
};

/**
 * Cập nhật một comment
 * @param {string} commentId - ID của comment cần cập nhật
 * @param {Object} commentData - Dữ liệu comment mới
 * @returns {Promise} Promise chứa comment đã cập nhật
 */
export const updateComment = (commentId, commentData) => {
  return axiosClient.put(`/api/comments/${commentId}`, commentData);
};

/**
 * Xóa một comment
 * @param {string} commentId - ID của comment cần xóa
 * @returns {Promise} Promise chứa kết quả xóa
 */
export const deleteComment = (commentId) => {
  return axiosClient.delete(`/api/comments/${commentId}`);
}; 