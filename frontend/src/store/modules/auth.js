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
  setToken({ commit }, token) {
    commit('SET_TOKEN', token)
  },
  logout({ commit }) {
    commit('CLEAR_AUTH')
  },
  initAuth({ commit }) {
    const token = localStorage.getItem('token')
    if (token) {
      commit('SET_TOKEN', token)
    }
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