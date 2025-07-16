import axios from 'axios';
import store from '@/store/store';

/**
 * Khởi tạo instance axios với các cấu hình mặc định
 * - baseURL: URL cơ sở của API
 * - timeout: Thời gian chờ tối đa cho mỗi request
 * - headers: Header mặc định cho mọi request
 */
const axiosInstance = axios.create({
  baseURL: 'https://oboeru.me/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  }
});

/**
 * Interceptor xử lý trước khi gửi request
 * - Tự động thêm token xác thực vào header nếu có
 * - Token được lấy từ Vuex store
 */
axiosInstance.interceptors.request.use(
  (config) => {
    const token = store.state.auth.token;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Interceptor xử lý sau khi nhận response
 * - Xử lý refresh token khi token hết hạn (401)
 * - Tự động thử lại request ban đầu sau khi refresh token thành công
 * - Logout nếu refresh token thất bại
 */
axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // Xử lý lỗi 401 (Unauthorized)
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        // Thử refresh token
        const refreshToken = store.state.auth.refreshToken;
        if (refreshToken) {
          const response = await axios.post('https://oboeru.me/api/auth/refresh-token', {
            refreshToken
          });
          const { token } = response.data;
          // Lưu token mới vào store
          store.commit('auth/setToken', token);
          // Cập nhật token trong request ban đầu
          originalRequest.headers.Authorization = `Bearer ${token}`;
          // Thử lại request ban đầu
          return axiosInstance(originalRequest);
        }
      } catch (refreshError) {
        // Nếu refresh token thất bại, đăng xuất người dùng
        store.dispatch('auth/logout');
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance; 