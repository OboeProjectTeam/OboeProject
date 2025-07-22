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
  }
};

export default flashcardApi;
