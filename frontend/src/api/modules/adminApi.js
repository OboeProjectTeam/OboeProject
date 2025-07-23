import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/admin';

const adminApi = {
  async createUser(userDTO) {
    try {
      const res = await axios.post(`${PREFIX}/create`, userDTO);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async updateUser(id, userDTO) {
    try {
      const res = await axios.put(`${PREFIX}/update/${id}`, userDTO);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async deleteUser(id) {
    try {
      const res = await axios.delete(`${PREFIX}/delete/${id}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async changeRole(id, role) {
    try {
      const res = await axios.put(`${PREFIX}/change-role/${id}`, null, {
        params: { role }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async updateStatus(id, status) {
    try {
      const res = await axios.put(`${PREFIX}/update-status/${id}`, null, {
        params: { status }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async resetPassword(id, newPassword) {
    try {
      const res = await axios.put(`${PREFIX}/reset-password/${id}`, null, {
        params: { newPassword }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async getAllUsers() {
    try {
      const res = await axios.get(`${PREFIX}/all`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async getUserById(id) {
    try {
      const res = await axios.get(`${PREFIX}/${id}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async searchUsers(keyword) {
    try {
      const res = await axios.get(`${PREFIX}/search`, {
        params: { keyword }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default adminApi;
