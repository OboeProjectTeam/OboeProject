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
    state.isAuthenticated = !!token
  },
  CLEAR_AUTH(state) {
    state.user = null
    state.token = null
    state.isAuthenticated = false
    localStorage.removeItem('token')
  }
}

const actions = {
  async setUser({ commit }, user) {
    commit('SET_USER', user)
  },
  async setToken({ commit, dispatch }, token) {
    // Save token to localStorage
    localStorage.setItem('token', token)
    
    // Update store
    commit('SET_TOKEN', token)
    
    try {
      // Fetch user info
      const response = await authApi.getUserInfo()
      if (response.user) {
        commit('SET_USER', response.user)
        return response.user
      }
    } catch (error) {
      console.error('Error fetching user info:', error)
      throw error
    }
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
  async initAuth({ commit, dispatch }) {
    try {
      // Get token from localStorage
      const token = localStorage.getItem('token')
      if (!token) {
        commit('CLEAR_AUTH')
        return
      }

      // Set token in store
      commit('SET_TOKEN', token)
      
      // Fetch user info
      await dispatch('fetchUserInfo')
    } catch (error) {
      console.error('Error initializing auth:', error)
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