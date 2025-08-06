import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/sample-sentences';

const sampleSentenceApi = {
  // Tạo mẫu câu mới
  async create(dto) {
    try {
      const res = await axios.post(PREFIX, dto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Cập nhật mẫu câu
  async update(id, dto) {
    try {
      const res = await axios.put(`${PREFIX}/${id}`, dto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Xoá mẫu câu
  async delete(id) {
    try {
      const res = await axios.delete(`${PREFIX}/${id}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy mẫu câu theo ID
  async getById(id) {
    try {
      const res = await axios.get(`${PREFIX}/${id}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy tất cả mẫu câu với phân trang
  async getAll({ page = 0, size = 10 } = {}) {
    try {
      const res = await axios.get(PREFIX, { params: { page, size } });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Tìm kiếm mẫu câu theo keyword
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

export default sampleSentenceApi;
