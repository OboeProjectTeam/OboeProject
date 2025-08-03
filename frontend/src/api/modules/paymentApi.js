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
  },
  // Thanh toán Payos
  async createPayOsPayment(amount = 99000) {
    try {
      const res = await axios.post(`${PREFIX}/payos`, null, {
        params: { amount }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
  // Xử lý callback từ PayOS
  async handlePayOsCallback(payload) {
    try {
      const res = await axios.post(`${PREFIX}/payos-notify`, payload);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }

};

export default paymentApi;