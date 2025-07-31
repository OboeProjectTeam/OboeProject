import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/questions';

const questionApi = {
  // Tạo mới câu hỏi
  async create(dto) {
    try {
      const res = await axios.post(PREFIX, dto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Lấy danh sách câu hỏi theo quiz ID
  async getByQuiz(quizId, size = 100) {
    try {
      const res = await axios.get(`${PREFIX}/by-quiz/${quizId}`, {
        params: { size }
      });
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  }
};

export default questionApi;
