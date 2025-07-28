import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/search';

const searchApi = {
  // Tìm kiếm theo từ khóa và loại (type: grammar, vocab,...)
  async search(keyword, type) {
    try {
      const response = await axios.get(PREFIX, {
        params: { keyword, type }
      });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Gợi ý tất cả loại (không phân loại)
  async suggest(keyword) {
    try {
      const response = await axios.get(`${PREFIX}/suggest`, {
        params: { keyword }
      });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default searchApi;
