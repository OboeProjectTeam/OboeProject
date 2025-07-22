import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/kanji';

const kanjiApi = {
  // Lấy danh sách có phân trang
  async getAll({ page = 0, size = 10 } = {}) {
    try {
      const res = await axios.get(PREFIX, {
        params: { page, size }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy chi tiết theo ID
  async getById(id) {
    try {
      const res = await axios.get(`${PREFIX}/${id}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Tạo Kanji (admin)
  async create(dto) {
    try {
      const res = await axios.post(PREFIX, dto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Cập nhật Kanji (admin)
  async update(id, dto) {
    try {
      const res = await axios.put(`${PREFIX}/${id}`, dto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Xoá Kanji (admin)
  async delete(id) {
    try {
      const res = await axios.delete(`${PREFIX}/${id}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Tìm kiếm theo từ khoá
  async search(keyword) {
    try {
      const res = await axios.get(`${PREFIX}/search`, {
        params: { keyword }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy từ liên quan
  async getRelated(kanjiId) {
    try {
      const res = await axios.get(`${PREFIX}/${kanjiId}/related`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default kanjiApi;
