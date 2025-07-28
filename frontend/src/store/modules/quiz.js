import quizApi from '@/api/modules/quizApi'
import { cloneVNode } from 'vue'

const state = {
  quizzes: [],
  currentQuiz: null,
  loading: false,
  error: null
}

const mutations = {
  setQuizzes(state, quizzes) {
    state.quizzes = quizzes
  },
  addQuiz(state, quiz) {
    state.quizzes.push(quiz)
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
      const response = await quizApi.getAll()
      commit('setQuizzes', response)
      return response
    } catch (error) {
      console.error('Error fetching quizzes:', error)
      commit('setError', error.message)
      throw error
    } finally {
      commit('setLoading', false)
    }
  },

  async createQuiz({ commit }, quizData) {
    try {
      commit('setLoading', true)
      const response = await quizApi.create(quizData)
      commit('addQuiz', response)
      return response
    } catch (error) {
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