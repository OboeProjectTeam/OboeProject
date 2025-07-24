import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/comments';

const commentApi = {
  async getComments(teamId, page = 0, size = 5) {
    try {
      const res = await axios.get(`${PREFIX}/${teamId}`, {
        params: { page, size }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async createComment(teamId, dto) {
    try {
      const res = await axios.post(`${PREFIX}/${teamId}`, dto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async replyComment(commentId, dto) {
    try {
      const res = await axios.post(`${PREFIX}/reply/${commentId}`, dto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async updateComment(commentId, dto) {
    try {
      const res = await axios.put(`${PREFIX}/${commentId}`, dto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async deleteComment(commentId) {
    try {
      const res = await axios.delete(`${PREFIX}/${commentId}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async countComments(teamId) {
    try {
      const res = await axios.get(`${PREFIX}/count/${teamId}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async getUserComments(page = 0, size = 10) {
    try {
      const res = await axios.get(`${PREFIX}/user`, {
        params: { page, size }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }

};

export default commentApi;
