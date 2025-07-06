let nextHistoryId = 1;
const initialHistory = [];

const state = {
  testHistory: initialHistory,
};

const getters = {
  getTestHistory: (state) => state.testHistory,
  getHistoryItemById: (state) => (id) => {
    return state.testHistory.find(item => item.id === id);
  },
};

const actions = {
  async fetchHistory({ commit }) {
    // In a real app, this would fetch from a persistent storage (API, localStorage)
    commit('SET_HISTORY', state.testHistory);
    return state.testHistory;
  },
  async saveTestToHistory({ commit }, testData) {
    const newHistoryItem = {
      ...testData,
      id: nextHistoryId++,
      createdAt: new Date().toISOString(),
    };
    commit('ADD_TO_HISTORY', newHistoryItem);
    return newHistoryItem;
  },
  async deleteTestFromHistory({ commit }, itemId) {
    commit('REMOVE_FROM_HISTORY', itemId);
  },
};

const mutations = {
  SET_HISTORY(state, history) {
    state.testHistory = history;
  },
  ADD_TO_HISTORY(state, item) {
    state.testHistory.unshift(item);
  },
  REMOVE_FROM_HISTORY(state, itemId) {
    state.testHistory = state.testHistory.filter(item => item.id !== itemId);
  },
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations,
}; 