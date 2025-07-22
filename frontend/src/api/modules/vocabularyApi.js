import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/vocabulary';

const vocabularyApi = {
  // Lấy danh sách từ vựng có phân trang
  async getAll({ page = 0, size = 10 } = {}) {
    try {
      const res = await axios.get(PREFIX, { params: { page, size } });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy chi tiết từ vựng theo ID
  async getById(id) {
    try {
      const res = await axios.get(`${PREFIX}/${id}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Tạo mới từ vựng (admin)
  async create(dto) {
    try {
      const res = await axios.post(PREFIX, dto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Cập nhật từ vựng (admin)
  async update(id, dto) {
    try {
      const res = await axios.put(`${PREFIX}/${id}`, dto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Xoá từ vựng (admin)
  async delete(id) {
    try {
      const res = await axios.delete(`${PREFIX}/${id}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Tìm kiếm từ vựng theo keyword
  async search(keyword) {
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

export default vocabularyApi;
