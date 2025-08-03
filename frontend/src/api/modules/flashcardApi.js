import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/flashcards';

const flashcardApi = {
  // Tạo flashcard
  async create(dto) {
    try {
      const res = await axios.post(PREFIX, dto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy flashcards của user (có phân trang & tìm kiếm)
  async getUserFlashcards({ page = 0, size = 10, term = '' } = {}) {
    try {
      const params = { page, size };
      if (term && term.trim()) {
        params.term = term;
      }
      const res = await axios.get(PREFIX, { params });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy flashcard theo ID
  async getById(id) {
    try {
      const res = await axios.get(`${PREFIX}/${id}`);
      return res.data;
    } catch (error) {
      console.error('flashcardApi.getById error:', error);
      throw error;
    }
  },

  // Cập nhật flashcard
  async update(id, dto) {
    try {
      const res = await axios.put(`${PREFIX}/${id}`, dto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Xoá flashcard
  async delete(id) {
    try {
      const res = await axios.delete(`${PREFIX}/${id}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Search flashcards by term
  async searchByTerm(term, page = 0, size = 10) {
    try {
      const res = await axios.get(PREFIX, {
        params: { term, page, size }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
  // Lấy 5 flashcard mới nhất của user
  async getTop5Latest() {
    try {
      const res = await axios.get(`${PREFIX}/latest`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
  // Lấy toàn bộ flashcard
  async getAll() {
    try {
      const res = await axios.get(`${PREFIX}/all`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
  // Tìm kiếm toàn bộ flashcard 
  async searchGlobal(keyword) {
    try {
      const res = await axios.get(`${PREFIX}/search`, {
        params: { keyword }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default flashcardApi;