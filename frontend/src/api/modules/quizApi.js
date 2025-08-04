import axios from '@/api/axios';
import { handleApiError } from '@/api/apiUtils';

const PREFIX = '/api/quizzes';

const quizApi = {
  async getAll() {
    try {

      const res = await axios.get(PREFIX);

      return res.data;
    } catch (error) {
      console.error('QuizAPI: Error occurred:', error);
      throw new Error(handleApiError(error));
    }
  },

  async getUserQuizzes(page = 0, size = 10) {
    try {

      const res = await axios.get(`${PREFIX}/user`, {
        params: { page, size }
      });

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


      const res = await axios.post(PREFIX, dto);

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

      const res = await axios.post(`${PREFIX}/${quizId}/submit-answers`, {
        answers: answers
      });

      return res.data;
    } catch (error) {
      console.error('QuizAPI: Error submitting answers:', error);
      throw new Error(handleApiError(error));
    }
  },
  // Tìm kiếm quiz theo từ khóa
  async search(keyword) {
    try {
      const res = await axios.get(`${PREFIX}/search`, {
        params: { keyword }
      });
      return res.data;
    } catch (error) {
      console.error('QuizAPI: Error searching quizzes:', error);
      throw new Error(handleApiError(error));
    }
  },
  // Lấy quiz + questions theo ID (dùng trong search result)
  async getQuizWithQuestions(id) {
    try {
      const res = await axios.get(`${PREFIX}/user/${id}`);
      return res.data;
    } catch (error) {
      console.error('QuizAPI: Error getting quiz with questions:', error);
      throw new Error(handleApiError(error));
    }
  },
};

export default quizApi;