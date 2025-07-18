import authApi from '@/api/modules/authApi'

const state = {
  user: null,
  token: null,
  isAuthenticated: false
}

const mutations = {
  SET_USER(state, user) {
    state.user = user
    state.isAuthenticated = !!user
  },
  SET_TOKEN(state, token) {
    state.token = token
  },
  CLEAR_AUTH(state) {
    state.user = null;
    state.token = null;
    state.isAuthenticated = false;
    localStorage.removeItem('token');
  }
}

const actions = {
  setUser({ commit }, user) {
    commit('SET_USER', user)
  },
  setToken({ commit, dispatch }, token) {
    commit('SET_TOKEN', token)
    localStorage.setItem('token', token)
    // After setting token, fetch user info
    return dispatch('fetchUserInfo')
  },
  async fetchUserInfo({ commit }) {
    try {
      const response = await authApi.getUserInfo()
      if (response.user) {
        commit('SET_USER', response.user)
        return response.user
      }
    } catch (error) {
      console.error('Error fetching user info:', error)
      commit('CLEAR_AUTH')
      throw error
    }
  },
  initAuth({ commit, dispatch }) {
    // Get token from localStorage
    const token = localStorage.getItem('token')
    if (token) {
      commit('SET_TOKEN', token)
      // Fetch real user data
      return dispatch('fetchUserInfo')
    } else {
      commit('CLEAR_AUTH')
    }
  },
  logout({ commit }) {
    commit('CLEAR_AUTH')
  }
}

const getters = {
  isAuthenticated: state => state.isAuthenticated,
  currentUser: state => state.user,
  token: state => state.token
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
} 