import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';
const PREFIX = '/api/users';

const userApi = {
  async searchUsers(keyword) {
    try {
      const response = await axios.get(`${PREFIX}/search`, {
        params: { keyword }
      });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default userApi;
