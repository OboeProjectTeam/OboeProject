import axios from '../axiosConfig';
import { handleApiError, buildQueryString, getPaginationParams } from '../apiUtils';

/**
 * Module chứa các API liên quan đến blog và bài viết
 */
export const blogApi = {
  /**
   * Lấy danh sách bài viết có phân trang
   * @param {number} page - Số trang
   * @param {number} limit - Số lượng item trên mỗi trang
   * @param {Object} filters - Các điều kiện lọc (category, tag, etc.)
   * @returns {Promise} Danh sách bài viết
   */
  async getBlogs(page = 1, limit = 10, filters = {}) {
    try {
      const params = {
        ...getPaginationParams(page, limit),
        ...filters
      };
      const query = buildQueryString(params);
      const response = await axios.get(`/blogs?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Tạo bài viết mới
   * @param {Object} blogData - Dữ liệu bài viết
   * @returns {Promise} Bài viết đã tạo
   */
  async createBlog(blogData) {
    try {
      const response = await axios.post('/blogs', blogData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Cập nhật bài viết
   * @param {string} id - ID của bài viết
   * @param {Object} blogData - Dữ liệu cần cập nhật
   * @returns {Promise} Bài viết đã cập nhật
   */
  async updateBlog(id, blogData) {
    try {
      const response = await axios.put(`/blogs/${id}`, blogData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Xóa bài viết
   * @param {string} id - ID của bài viết
   * @returns {Promise} Thông báo xóa thành công
   */
  async deleteBlog(id) {
    try {
      const response = await axios.delete(`/blogs/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy chi tiết bài viết theo ID
   * @param {string} id - ID của bài viết
   * @returns {Promise} Chi tiết bài viết
   */
  async getBlogById(id) {
    try {
      const response = await axios.get(`/blogs/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Thêm bình luận vào bài viết
   * @param {string} blogId - ID của bài viết
   * @param {Object} commentData - Nội dung bình luận
   * @returns {Promise} Bình luận đã tạo
   */
  async addComment(blogId, commentData) {
    try {
      const response = await axios.post(`/blogs/${blogId}/comments`, commentData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy danh sách bình luận của bài viết
   * @param {string} blogId - ID của bài viết
   * @param {number} page - Số trang
   * @param {number} limit - Số lượng item trên mỗi trang
   * @returns {Promise} Danh sách bình luận
   */
  async getBlogComments(blogId, page = 1, limit = 10) {
    try {
      const params = getPaginationParams(page, limit);
      const query = buildQueryString(params);
      const response = await axios.get(`/blogs/${blogId}/comments?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Like/Unlike bài viết
   * @param {string} blogId - ID của bài viết
   * @returns {Promise} Trạng thái like mới
   */
  async toggleLike(blogId) {
    try {
      const response = await axios.post(`/blogs/${blogId}/like`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
}; 