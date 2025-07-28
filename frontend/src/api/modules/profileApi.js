import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/profile';

const profileApi = {
  // 1. Lấy thông tin người dùng hiện tại
  async getProfile() {
    try {
      const res = await axios.get(PREFIX);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // 2. Lấy thông tin người dùng theo ID
  async getUserProfileById(userId) {
    try {
      const res = await axios.get(`${PREFIX}/${userId}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // 3. Lấy hoạt động của người dùng theo ID và phân trang
  async getUserActivities(userId, page = 0, size = 10) {
    try {
      const res = await axios.get(`${PREFIX}/detail/${userId}`, {
        params: { page, size }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default profileApi;