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
  
  console.log('getItemId called with:', { item, type });
  console.log('Item keys:', Object.keys(item));
  
  let result;
  switch (type) {
    case 'vocabulary':
    case 'word': // Handle 'word' as alias for 'vocabulary'
      result = item.vocabularyId || item.id;
      console.log('Vocabulary ID extraction:', { vocabularyId: item.vocabularyId, id: item.id, result });
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
  
  console.log('Final result:', result);
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
        return fav.grammarId === itemId;
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
      console.log('Fetching user favorites...');
      const favoriteItems = await favoriteApi.getUserFavorites();
      console.log('Fetched favorite items:', favoriteItems);
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
    console.log('Toggle favorite called with:', { type, itemId });
    
    if (!itemId) {
      console.error('Invalid itemId for toggle favorite:', itemId);
      return;
    }

    try {
      // Find existing favorite by itemId and type
      const existingFavorite = state.favoriteItems.find(fav => {
        switch (type) {
          case 'word':
            return fav.vocabularyId === itemId;
          case 'grammar':
            return fav.grammarId === itemId;
          case 'kanji':
            return fav.kanjiId === itemId;
          case 'sentence':
            return fav.sampleSentenceId === itemId;
          default:
            return false;
        }
      });
      
      console.log('Existing favorite found:', existingFavorite);
      
      if (existingFavorite) {
        // Remove from favorites
        console.log('Removing favorite with ID:', existingFavorite.favoritesId);
        await favoriteApi.deleteFavorite(existingFavorite.favoritesId);
        console.log('Successfully removed from favorites');
        
        // Refresh favorites to get updated state
        await dispatch('fetchFavorites');
      } else {
        // Add to favorites
        const favoriteDTO = {};
        
        // Set the appropriate ID field based on type
        switch (type) {
          case 'word':
            favoriteDTO.vocabularyId = itemId;
            break;
          case 'grammar':
            favoriteDTO.grammarId = itemId;
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

        console.log('Creating favorite with DTO:', favoriteDTO);
        const favoriteItem = await favoriteApi.createFavorite(favoriteDTO);
        console.log('Created favorite item:', favoriteItem);
        
        // Refresh favorites to get updated state
        await dispatch('fetchFavorites');
      }
    } catch (error) {
      console.error('Error toggling favorite:', error);
      console.error('Error details:', error.message);
      console.error('Error response:', error.response?.data);
      // You might want to show a user-friendly error message here
    }
  },

  async removeFromFavorites({ commit, state }, { type, item }) {
    try {
      const existingFavorite = findFavoriteByItem(state.favoriteItems, type, item);
      if (existingFavorite) {
        await favoriteApi.deleteFavorite(existingFavorite.favoritesId);
        commit('REMOVE_FAVORITE_ITEM', existingFavorite.favoritesId);
      }
      commit('REMOVE_FAVORITE', { type, item });
    } catch (error) {
      console.error('Error removing from favorites:', error);
    }
  }
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
};