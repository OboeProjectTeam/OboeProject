import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/statistics';

const statisticsApi = {
  // Lấy thống kê của user hiện tại
  async getUserStats() {
    try {
      const res = await axios.get(`${PREFIX}/me`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy chi tiết hoạt động của user với phân trang
  async getUserActivity(page = 0, size = 10) {
    try {
      const res = await axios.get(`${PREFIX}/detail`, {
        params: { page, size }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default statisticsApi; 