// src/store/modules/user.js
const state = {
  favorites: {
    vocabulary: [],
    grammar: [],
    sentences: [],
    kanji: [],
  },
};

// Helper function to find if an item is in a list
const findItem = (list, item) => list.find(i => i.id === item.id);

const getters = {
  getFavorites: (state) => state.favorites,
  isFavorite: (state) => (type, item) => {
    if (!state.favorites[type] || !item) return false;
    return !!findItem(state.favorites[type], item);
  },
};

const mutations = {
  ADD_FAVORITE(state, { type, item }) {
    if (!state.favorites[type]) return;
    // Avoid duplicates
    if (!findItem(state.favorites[type], item)) {
      state.favorites[type].unshift(item);
    }
  },
  REMOVE_FAVORITE(state, { type, itemId }) {
    if (!state.favorites[type]) return;
    state.favorites[type] = state.favorites[type].filter(i => i.id !== itemId);
  },
  SET_FAVORITES(state, favorites) {
    state.favorites = { ...state.favorites, ...favorites };
  }
};

const actions = {
  // In a real app, this would fetch from a persistent source
  fetchFavorites({ commit }) {
    // For now, we commit the current state, but this could be from localStorage or an API
    commit('SET_FAVORITES', state.favorites);
    return state.favorites;
  },
  toggleFavorite({ commit, getters }, { type, item }) {
    if (getters.isFavorite(type, item)) {
      commit('REMOVE_FAVORITE', { type, itemId: item.id });
    } else {
      commit('ADD_FAVORITE', { type, item });
    }
  },
  removeFromFavorites({ commit }, { type, id }) {
      commit('REMOVE_FAVORITE', { type, itemId: id });
  }
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
}; 