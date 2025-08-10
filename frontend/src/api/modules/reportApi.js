import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/reports';

const reportApi = {
  // Gửi báo cáo mới
  async create(reportDto) {
    try {
      const res = await axios.post(PREFIX, reportDto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy tất cả báo cáo (dành cho admin)
  async getAll() {
    try {
      const res = await axios.get(PREFIX);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Cập nhật trạng thái báo cáo
  async updateStatus(reportId, status) {
    try {
      const res = await axios.patch(`${PREFIX}/${reportId}/status`, null, {
        params: { status }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy báo cáo theo user
  async getByUser(userId) {
    try {
      const res = await axios.get(`${PREFIX}/user/${userId}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Xoá báo cáo
  async delete(reportId) {
    try {
      const res = await axios.delete(`${PREFIX}/${reportId}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
  // Tìm kiếm báo cáo blog

  async searchBlogReports({ title, type, status }) {
    try {
      const res = await axios.get(`${PREFIX}/search`, {
        params: { title, type, status }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
  // Lấy tất cả báo cáo blog
  async getAllBlogReports() {
    try {
      const res = await axios.get(`${PREFIX}/all-blog-reports`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
  // Duyệt báo cáo blog
  async approve(reportId, { actionType, note } = {}) {
    try {
      const res = await axios.put(
        `${PREFIX}/approve/${reportId}`,
        null, // body rỗng vì backend nhận qua @RequestParam
        { params: { actionType, note } } // thêm query params
      );
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
  // Từ chối báo cáo blog

  async reject(reportId) {
    try {
      const res = await axios.put(`${PREFIX}/rejected/${reportId}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }



};

export default reportApi;
