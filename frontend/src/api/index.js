import axios from './axiosConfig';
import { authApi } from './modules/authApi';
import { userApi } from './modules/userApi';
import * as apiUtils from './apiUtils';

/**
 * Export các module API và utilities
 * - axiosInstance: Instance axios đã được cấu hình
 * - authApi: API xác thực người dùng
 * - userApi: API quản lý người dùng
 * - apiUtils: Các hàm tiện ích cho API
 */
export {
  axios as axiosInstance,
  authApi,
  userApi,
  apiUtils
}; 