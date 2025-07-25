import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/notifications';

const notificationApi = {
  // Lấy 30 thông báo mới nhất của user
  async getAll() {
    try {
      const res = await axios.get(PREFIX);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Đánh dấu tất cả thông báo là đã đọc
  async markAllAsRead() {
    try {
      const res = await axios.put(`${PREFIX}/mark-all-read`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Đánh dấu một thông báo là đã đọc
  async markAsRead(id) {
    try {
      const res = await axios.put(`${PREFIX}/read/${id}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }

};

export default notificationApi;
