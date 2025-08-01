import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/favorites';

const favoriteApi = {
  // Toggle yêu thích (thêm hoặc hủy)
  async toggleFavorite(favoritesDTO) {
    try {
      const res = await axios.post(`${PREFIX}/toggle`, favoritesDTO);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy danh sách mục yêu thích của người dùng (theo type nếu có)
  async getUserFavorites(type = '') {
    try {
      const res = await axios.get(`${PREFIX}/user`, {
        params: type ? { type } : {}
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

};

export default favoriteApi;
