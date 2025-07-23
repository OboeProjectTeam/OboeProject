const state = {
  message: null
}

const mutations = {
  setMessage(state, message) {
    state.message = message
  },
  clearMessage(state) {
    state.message = null
  }
}

const actions = {
  showMessage({ commit }, message) {
    commit('setMessage', message)
    // Auto clear after 3 seconds
    setTimeout(() => {
      commit('clearMessage')
    }, 3000)
  }
}

const getters = {
  getMessage: state => state.message
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
} 