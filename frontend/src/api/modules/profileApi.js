import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/profile';

const profileApi = {
  // Lấy thông tin người dùng hiện tại (dựa trên token)
  async getProfile() {
    try {
      const res = await axios.get(PREFIX);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default profileApi;
