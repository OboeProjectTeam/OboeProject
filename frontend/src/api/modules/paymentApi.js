import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/payment';

const paymentApi = {
  // Tạo thanh toán MoMo
  async createMomoPayment(userId) {
    try {
      const res = await axios.post(`${PREFIX}/momo`, null, {
        params: { userId }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Xử lý callback từ MoMo (thường dùng cho webhook)
  async handleMomoCallback(payload) {
    try {
      const res = await axios.post(`${PREFIX}/momo-notify`, payload);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default paymentApi; 