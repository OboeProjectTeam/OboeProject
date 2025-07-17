import axios from '../axiosConfig';
import { handleApiError } from '../apiUtils';

/**
 * Module chứa các API liên quan đến profile người dùng
 */
export const profileApi = {
  /**
   * Lấy thông tin profile của người dùng hiện tại
   * @returns {Promise} Thông tin chi tiết người dùng (không bao gồm mật khẩu)
   */
  async getProfile() {
    try {
      const response = await axios.get('/profile');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
}; 