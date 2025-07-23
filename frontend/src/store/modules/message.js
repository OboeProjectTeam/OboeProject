const state = {
  message: null,
  chatPartners: []
}

const mutations = {
  setMessage(state, message) {
    state.message = message
  },
  clearMessage(state) {
    state.message = null
  },
  setChatPartners(state, partners) {
    state.chatPartners = partners || []
  },
  clearChatPartners(state) {
    state.chatPartners = []
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
  getMessage: state => state.message,
  getChatPartners: state => state.chatPartners
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
} 