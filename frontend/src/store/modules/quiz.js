import quizApi from '@/api/modules/quizApi'

const state = () => ({
  quizzes: [],
  currentQuiz: null,
  loading: false,
  error: null
})

const mutations = {
  setQuizzes(state, quizzes) {
    state.quizzes = Array.isArray(quizzes) ? quizzes : []
  },
  addQuiz(state, quiz) {
    // Ensure quizzes is always an array
    console.log('Before addQuiz - state.quizzes:', state.quizzes, 'Type:', typeof state.quizzes, 'Is Array:', Array.isArray(state.quizzes));
    if (!Array.isArray(state.quizzes)) {
      console.log('state.quizzes is not an array, initializing...');
      state.quizzes = []
    }
    console.log('Adding quiz:', quiz);
    state.quizzes.push(quiz)
    console.log('After addQuiz - state.quizzes:', state.quizzes);
  },
  setCurrentQuiz(state, quiz) {
    state.currentQuiz = quiz
  },
  setLoading(state, loading) {
    state.loading = loading
  },
  setError(state, error) {
    state.error = error
  }
}

const actions = {
  async fetchQuizzes({ commit }) {
    try {
      commit('setLoading', true)
      console.log('Fetching user quizzes...')
      const response = await quizApi.getUserQuizzes()
      console.log('User quizzes fetched:', response)
      commit('setQuizzes', response)
      return response
    } catch (error) {
      console.error('Error fetching user quizzes:', error)
      commit('setError', error.message)
      // Don't throw error to prevent breaking the UI
      commit('setQuizzes', [])
      return []
    } finally {
      commit('setLoading', false)
    }
  },

  async createQuiz({ commit }, quizData) {
    try {
      commit('setLoading', true)
      console.log('CreateQuiz action - quizData:', quizData);
      const response = await quizApi.create(quizData)
      console.log('CreateQuiz action - API response:', response);
      console.log('CreateQuiz action - calling addQuiz mutation...');
      commit('addQuiz', response)
      console.log('CreateQuiz action - mutation completed');
      return response
    } catch (error) {
      console.error('CreateQuiz action - error:', error);
      commit('setError', error.message)
      throw error
    } finally {
      commit('setLoading', false)
    }
  }
}

const getters = {
  getAllQuizzes: state => state.quizzes,
  getCurrentQuiz: state => state.currentQuiz,
  isLoading: state => state.loading,
  getError: state => state.error
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
} 