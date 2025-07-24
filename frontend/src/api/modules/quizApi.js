import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/quizzes';

const quizApi = {
  async getAll() {
    try {
      console.log('QuizAPI: Fetching all quizzes');
      const res = await axios.get(PREFIX);
      console.log('QuizAPI: Response received:', res.data);
      return res.data;
    } catch (error) {
      console.error('QuizAPI: Error occurred:', error);
      throw new Error(handleApiError(error));
    }
  },

  async getUserQuizzes(page = 0, size = 10) {
    try {
      console.log('QuizAPI: Fetching user quizzes');
      const res = await axios.get(`${PREFIX}/user`, {
        params: { page, size }
      });
      console.log('QuizAPI: User quizzes received:', res.data);
      return res.data;
    } catch (error) {
      console.error('QuizAPI: Error fetching user quizzes:', error);
      throw new Error(handleApiError(error));
    }
  },

  // Lấy quiz theo ID
  async getById(id) {
    try {
      const res = await axios.get(`${PREFIX}/${id}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  async create(dto) {
    try {
      console.log('QuizAPI: Sending POST request to', PREFIX);
      console.log('QuizAPI: Request data:', dto);
      const res = await axios.post(PREFIX, dto);
      console.log('QuizAPI: Response received:', res.data);
      return res.data;
    } catch (error) {
      console.error('QuizAPI: Error occurred:', error);
      throw new Error(handleApiError(error));
    }
  },

  // Cập nhật quiz
  async update(id, dto) {
    try {
      const res = await axios.put(`${PREFIX}/${id}`, dto);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Xóa quiz
  async delete(id) {
    try {
      const res = await axios.delete(`${PREFIX}/${id}`);
      return res.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Nộp bài làm quiz
  async submitAnswers(quizId, answers) {
    try {
      console.log('QuizAPI: Submitting answers for quiz', quizId);
      const res = await axios.post(`${PREFIX}/${quizId}/submit-answers`, {
        answers: answers
      });
      console.log('QuizAPI: Submit response:', res.data);
      return res.data;
    } catch (error) {
      console.error('QuizAPI: Error submitting answers:', error);
      throw new Error(handleApiError(error));
    }
  }
};

export default quizApi;