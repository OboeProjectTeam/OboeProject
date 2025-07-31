// src/store/modules/user.js
import favoriteApi from '@/api/modules/favoriteApi'

const state = {
  favorites: {
    vocabulary: [],
    grammar: [],
    sentences: [],
    kanji: [],
  },
  favoriteItems: [], // Store full favorite objects from API
};

// Helper function to get the ID from an item based on its type
const getItemId = (item, type) => {
  if (!item) return null;
  
  let result;
  switch (type) {
    case 'vocabulary':
    case 'word': // Handle 'word' as alias for 'vocabulary'
      result = item.vocabularyId || item.id;
      break;
    case 'grammar':
      result = item.grammarId || item.id; // grammarId is the primary field
      break;
    case 'kanji':
      result = item.id || item.kanjiId;
      break;
    case 'sentences':
    case 'sentence': // Handle 'sentence' as alias for 'sentences'
      result = item.sampleSentenceId || item.id;
      break;
    default:
      result = item.id;
  }
  
  return result;
};

// Helper function to find if an item is in a list
const findItem = (list, item, type) => {
  const itemId = getItemId(item, type);
  return list.find(i => getItemId(i, type) === itemId);
};

// Helper function to find favorite by item ID and type
const findFavoriteByItem = (favoriteItems, type, item) => {
  const itemId = getItemId(item, type);
  if (!itemId) return null;
  
  return favoriteItems.find(fav => {
    switch (type) {
      case 'vocabulary':
      case 'word': // Handle 'word' as alias for 'vocabulary'
        return fav.vocabularyId === itemId;
      case 'grammar':
        return fav.grammaId === itemId; // Note: API uses 'grammaId' not 'grammarId'
      case 'kanji':
        return fav.kanjiId === itemId;
      case 'sentences':
      case 'sentence': // Handle 'sentence' as alias for 'sentences'
        return fav.sampleSentenceId === itemId;
      default:
        return false;
    }
  });
};

const getters = {
  getFavorites: (state) => state.favorites,
  isFavorite: (state) => (type, item) => {
    if (!item) return false;
    return !!findFavoriteByItem(state.favoriteItems, type, item);
  },
};

const mutations = {
  ADD_FAVORITE(state, { type, item }) {
    if (!state.favorites[type]) return;
    // Avoid duplicates
    if (!findItem(state.favorites[type], item, type)) {
      state.favorites[type].unshift(item);
    }
  },
  REMOVE_FAVORITE(state, { type, item }) {
    if (!state.favorites[type]) return;
    const itemId = getItemId(item, type);
    state.favorites[type] = state.favorites[type].filter(i => getItemId(i, type) !== itemId);
  },
  SET_FAVORITES(state, favorites) {
    state.favorites = { ...state.favorites, ...favorites };
  },
  SET_FAVORITE_ITEMS(state, favoriteItems) {
    state.favoriteItems = favoriteItems;
  },
  ADD_FAVORITE_ITEM(state, favoriteItem) {
    // Avoid duplicates
    const exists = state.favoriteItems.find(fav => fav.favoritesId === favoriteItem.favoritesId);
    if (!exists) {
      state.favoriteItems.push(favoriteItem);
    }
  },
  REMOVE_FAVORITE_ITEM(state, favoritesId) {
    state.favoriteItems = state.favoriteItems.filter(fav => fav.favoritesId !== favoritesId);
  }
};

const actions = {
  // Fetch user favorites from API
  async fetchFavorites({ commit }) {
    try {
      const favoriteItems = await favoriteApi.getUserFavorites();
      commit('SET_FAVORITE_ITEMS', favoriteItems || []);
      return favoriteItems || [];
    } catch (error) {
      console.error('Error fetching favorites:', error);
      console.error('Error details:', error.message);
      commit('SET_FAVORITE_ITEMS', []);
      return [];
    }
  },

  async toggleFavorite({ commit, dispatch, state }, { type, itemId }) {
    if (!itemId) {
      console.error('Invalid itemId for toggle favorite:', itemId);
      return;
    }

    try {
      // Prepare DTO based on type
      const favoriteDTO = {};
      
      // Set the appropriate ID field based on type
      switch (type) {
        case 'word':
          favoriteDTO.vocabularyId = itemId;
          break;
        case 'grammar':
          favoriteDTO.grammaId = itemId; // Note: API expects 'grammaId' not 'grammarId'
          break;
        case 'kanji':
          favoriteDTO.kanjiId = itemId;
          break;
        case 'sentence':
          favoriteDTO.sampleSentenceId = itemId;
          break;
        default:
          console.error('Unknown favorite type:', type);
          return;
      }
      
      // Call the toggle API - it will either add or remove based on current state
      const result = await favoriteApi.toggleFavorite(favoriteDTO);
      
      // Refresh favorites to get updated state
      await dispatch('fetchFavorites');
      
      return result;
    } catch (error) {
      console.error('Error toggling favorite:', error);
      console.error('Error details:', error.message);
      // You might want to show a user-friendly error message here
      throw error;
    }
  },
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
};