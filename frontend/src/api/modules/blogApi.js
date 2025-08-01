import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/blogs';

const blogApi = {
  async getAll({ page = 0, size = 10 }) {
    try {
      const res = await axios.get(`${PREFIX}/get_all`, {
        params: { page, size }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async getById(id) {
    try {
      const res = await axios.get(`${PREFIX}/${id}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async create(blogDTO) {
    try {
      const res = await axios.post(PREFIX, blogDTO);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async update(id, blogDTO) {
    try {
      const res = await axios.put(`${PREFIX}/${id}`, blogDTO);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async delete(id) {
    try {
      const res = await axios.delete(`${PREFIX}/${id}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async search(keyword) {
    try {
      const res = await axios.get(`${PREFIX}/search`, {
        params: { keyword }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async getUserBlogs(page = 0, size = 10) {
    try {
      const res = await axios.get(`${PREFIX}/user/blogs`, {
        params: { page, size }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
  async getFeaturedTopics() {
    try {
      const res = await axios.get(`${PREFIX}/featured-topics`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
  async getFeaturedComments() {
    try {
      const res = await axios.get(`${PREFIX}/featuredcomment`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default blogApi;
