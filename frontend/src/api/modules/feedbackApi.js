import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/feedbacks';

const feedbackApi = {
  // Gửi phản hồi mới
  async create(feedbackDto) {
    try {
      const res = await axios.post(PREFIX, feedbackDto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy tất cả phản hồi (dành cho admin)
  async getAll() {
    try {
      const res = await axios.get(PREFIX);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default feedbackApi;
