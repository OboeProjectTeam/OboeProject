// src/store/modules/quiz.js

// Mock data to simulate a database
let nextQuizId = 1
const initialQuizzes = [
  // You can add some initial quiz data here if you want
]

const state = {
  quizzes: initialQuizzes
}

const getters = {
  getQuizById: (state) => (id) => {
    return state.quizzes.find(quiz => quiz.id === id)
  },
  getAllQuizzes: (state) => {
    return state.quizzes
  }
}

const actions = {
  async fetchQuizzes({ commit, state }) {
    // In a real app, you'd fetch this from an API
    commit('SET_QUIZZES', state.quizzes)
    return state.quizzes
  },

  async createQuiz({ commit }, quizData) {
    // In a real app, you'd send this to an API and get the created quiz back
    const newQuiz = {
      ...quizData,
      id: nextQuizId++,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      questionCount: quizData.questions.length
    }
    commit('ADD_QUIZ', newQuiz)
    return newQuiz
  },

  async updateQuiz({ commit, state }, { id, quizData }) {
    // In a real app, you'd send this to an API
    const index = state.quizzes.findIndex(q => q.id === id)
    if (index !== -1) {
      const updatedQuiz = { ...state.quizzes[index], ...quizData, updatedAt: new Date().toISOString() }
      commit('UPDATE_QUIZ', { index, quiz: updatedQuiz })
      return updatedQuiz
    }
  },

  async deleteQuiz({ commit }, quizId) {
    // In a real app, you'd send a delete request to an API
    commit('REMOVE_QUIZ', quizId)
  }
}

const mutations = {
  SET_QUIZZES(state, quizzes) {
    state.quizzes = quizzes
  },
  ADD_QUIZ(state, quiz) {
    state.quizzes.unshift(quiz) // Add to the beginning of the array
  },
  UPDATE_QUIZ(state, { index, quiz }) {
    state.quizzes[index] = quiz
  },
  REMOVE_QUIZ(state, quizId) {
    state.quizzes = state.quizzes.filter(quiz => quiz.id !== quizId)
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
} 