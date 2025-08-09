import { handleApiError } from '../apiUtils';
import { jwtDecode } from 'jwt-decode'
import axios from '../axios';

/**
 * Kiểm tra token JWT có hết hạn hay chưa
 * @param {string} token - Token JWT
 * @returns {boolean} true nếu token đã hết hạn hoặc không hợp lệ
 */
export function isTokenExpired(token) {
  try {
    const decoded = jwtDecode(token);
    return decoded.exp * 1000 < Date.now();
  } catch (e) {
    return true;
  }
}

/**
 * Module chứa các API liên quan đến xác thực người dùng
 */
const authApi = {
  async signup(userData) {
    try {
      const response = await axios.post('/api/auth/signup', userData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async verify(token) {
    try {
      const response = await axios.get('/api/auth/verify', {
        params: { token },
      });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async login(userName, passWord) {
    try {
      const response = await axios.post('/api/auth/login', { userName, passWord });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
  async logout() {
    try {
      const response = await axios.post('/api/auth/logout');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },  
  async updateProfile(userData) {
    try {
      const response = await axios.put('/api/auth/updateProfile', userData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async changePassword(passwordData) {
    try {
      const response = await axios.put('/api/auth/changePassword', passwordData);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async uploadAvatar(file) {
    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await axios.post('/api/auth/uploadAvatar', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
  async getCurrentUser() {
    try {
      const response = await axios.get('/api/auth/me');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async loginWithFirebase(idToken) {
    try {
      const response = await axios.post('/api/auth/loginWithFirebase', { idToken });
      return response.data;
    } catch (error) {
      console.error(" Firebase login error:", error.response?.data || error.message);
      throw new Error(handleApiError(error));
    }
}


}

export default authApi;
