import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/favorites';

const favoriteApi = {
  async createFavorite(favoritesDTO) {
    try {
      const res = await axios.post(`${PREFIX}`, favoritesDTO);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

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

  async deleteFavorite(favoriteId) {
    try {
      const res = await axios.delete(`${PREFIX}/${favoriteId}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default favoriteApi;
