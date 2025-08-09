import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';
const PREFIX = '/api/dashboard';

const dashboardApi = {
  async getAdminDashboard() {
    try {
      const res = await axios.get(`${PREFIX}/admin`);
      return res.data; // Trả về object chứa summary và recent_activities
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default dashboardApi;
