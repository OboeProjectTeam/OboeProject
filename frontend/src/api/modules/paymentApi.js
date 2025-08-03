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
  async createPayOsPayment() {
    try {
      const res = await axios.post(`${PREFIX}/payos`);
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
  },
  // Xử lý khi thanh toán thành công
  async handlePaymentSuccess(params) {
    try {
      const res = await axios.get(`${PREFIX}/payment-success`, { params });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
  //Xử lý khi người dùng hủy thanh toán
  async handlePaymentCancel(orderCode) {
    try {
      const res = await axios.get(`${PREFIX}/payment-cancel`, {
        params: { orderCode }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
  //Tra cứu trạng thái thanh toán theo orderCode
  async getPaymentStatus(orderCode) {
    try {
      const res = await axios.get(`${PREFIX}/status`, {
        params: { orderCode }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }

};

export default paymentApi;