import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/messages';

const messageApi = {
  // Gửi tin nhắn mới
  async sendMessage(messageDto) {
    try {
      const res = await axios.post(PREFIX, messageDto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy danh sách những người đã nhắn tin
  async getChatPartners() {
    try {
      const res = await axios.get(`${PREFIX}/partners`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy toàn bộ cuộc trò chuyện với một người
  async getConversation(userBId) {
    try {
      const res = await axios.get(`${PREFIX}/conversation/${userBId}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Xoá một tin nhắn
  async deleteMessage(messageId) {
    try {
      const res = await axios.delete(`${PREFIX}/${messageId}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default messageApi;
