import axios from '../axiosConfig';
import { handleApiError, buildQueryString, getPaginationParams } from '../apiUtils';

/**
 * Module chứa các API liên quan đến tin nhắn
 */
export const messageApi = {
  /**
   * Lấy danh sách cuộc trò chuyện
   * @param {number} page - Số trang
   * @param {number} limit - Số lượng item trên mỗi trang
   * @returns {Promise} Danh sách cuộc trò chuyện
   */
  async getConversations(page = 1, limit = 10) {
    try {
      const params = getPaginationParams(page, limit);
      const query = buildQueryString(params);
      const response = await axios.get(`/messages/conversations?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy tin nhắn của một cuộc trò chuyện
   * @param {string} conversationId - ID của cuộc trò chuyện
   * @param {number} page - Số trang
   * @param {number} limit - Số lượng item trên mỗi trang
   * @returns {Promise} Danh sách tin nhắn
   */
  async getMessages(conversationId, page = 1, limit = 20) {
    try {
      const params = getPaginationParams(page, limit);
      const query = buildQueryString(params);
      const response = await axios.get(`/messages/${conversationId}?${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Gửi tin nhắn mới
   * @param {string} conversationId - ID của cuộc trò chuyện
   * @param {Object} messageData - Nội dung tin nhắn
   * @returns {Promise} Tin nhắn đã gửi
   */
  async sendMessage(conversationId, messageData) {
    try {
      const response = await axios.post(`/messages/${conversationId}`, messageData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Tạo cuộc trò chuyện mới
   * @param {Object} conversationData - Thông tin cuộc trò chuyện
   * @returns {Promise} Cuộc trò chuyện đã tạo
   */
  async createConversation(conversationData) {
    try {
      const response = await axios.post('/messages/conversations', conversationData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Đánh dấu tin nhắn đã đọc
   * @param {string} conversationId - ID của cuộc trò chuyện
   * @returns {Promise} Thông báo thành công
   */
  async markAsRead(conversationId) {
    try {
      const response = await axios.put(`/messages/${conversationId}/read`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Xóa tin nhắn
   * @param {string} messageId - ID của tin nhắn
   * @returns {Promise} Thông báo xóa thành công
   */
  async deleteMessage(messageId) {
    try {
      const response = await axios.delete(`/messages/${messageId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Lấy số lượng tin nhắn chưa đọc
   * @returns {Promise} Số lượng tin nhắn chưa đọc
   */
  async getUnreadCount() {
    try {
      const response = await axios.get('/messages/unread-count');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  /**
   * Gửi file đính kèm trong tin nhắn
   * @param {string} conversationId - ID của cuộc trò chuyện
   * @param {FormData} formData - Form data chứa file
   * @returns {Promise} Thông tin file đã upload
   */
  async sendAttachment(conversationId, formData) {
    try {
      const response = await axios.post(`/messages/${conversationId}/attachment`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
}; 