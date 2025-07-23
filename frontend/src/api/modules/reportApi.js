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
  }
};

export default reportApi;
