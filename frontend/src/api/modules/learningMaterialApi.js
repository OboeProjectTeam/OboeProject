import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/learning-materials';

const learningMaterialApi = {
  // Lấy học liệu được đề xuất (3 từ 10 quiz đầu tiên)
  async getSuggestedMaterials() {
    try {
      const res = await axios.get(`${PREFIX}/suggested`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy học liệu ngẫu nhiên (random bằng Java)
  async getRandomMaterials() {
    try {
      const res = await axios.get(`${PREFIX}/random`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy học liệu ngẫu nhiên từ DB (random bằng native query)
  async getRandomMaterialsFromDB() {
    try {
      const res = await axios.get(`${PREFIX}/random-db`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default learningMaterialApi; 