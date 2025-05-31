export default {
  namespaced: true,
  state: () => ({
    items: [],
    learningItems: [],
    sortBy: 'recent', // 'recent' or 'alphabetical'
    itemsPerPage: 10,
    currentPage: 1
  }),
  mutations: {
    addItem(state, item) {
      // Add timestamp for sorting by recent
      const itemWithTimestamp = {
        ...item,
        addedAt: new Date().toISOString()
      }
      // Kiểm tra xem item đã tồn tại chưa
      const exists = state.items.some(
        existingItem => 
          existingItem.type === item.type && 
          existingItem.id === item.id
      )
      if (!exists) {
        state.items.push(itemWithTimestamp)
      }
    },
    removeItem(state, item) {
      state.items = state.items.filter(
        existingItem => 
          !(existingItem.type === item.type && existingItem.id === item.id)
      )
    },
    setSortBy(state, sortBy) {
      state.sortBy = sortBy
    },
    setCurrentPage(state, page) {
      state.currentPage = page
    },
    setLearningItems(state, items) {
      state.learningItems = items
    }
  },
  actions: {
    addItem({ commit }, item) {
      commit('addItem', item)
    },
    removeItem({ commit }, item) {
      commit('removeItem', item)
    },
    updateSort({ commit }, sortBy) {
      commit('setSortBy', sortBy)
    },
    updatePage({ commit }, page) {
      commit('setCurrentPage', page)
    },
    setLearningItems({ commit }, items) {
      commit('setLearningItems', items)
    }
  },
  getters: {
    getItemsByType: (state) => (type) => {
      const typeItems = state.items.filter(item => item.type === type)
      
      // Sort items based on sortBy
      if (state.sortBy === 'recent') {
        return typeItems.sort((a, b) => new Date(b.addedAt) - new Date(a.addedAt))
      } else if (state.sortBy === 'alphabetical') {
        return typeItems.sort((a, b) => {
          const aText = a.kanji || a.kana || a.pattern || ''
          const bText = b.kanji || b.kana || b.pattern || ''
          return aText.localeCompare(bText, 'ja')
        })
      }
      return typeItems
    },
    getPaginatedItems: (state, getters) => (type) => {
      const items = getters.getItemsByType(type)
      const start = (state.currentPage - 1) * state.itemsPerPage
      const end = start + state.itemsPerPage
      return items.slice(start, end)
    },
    getTotalPages: (state, getters) => (type) => {
      const items = getters.getItemsByType(type)
      return Math.ceil(items.length / state.itemsPerPage)
    },
    isInFlashcard: (state) => (type, id) => {
      if (type === 'kanji') {
        return state.items.some(
          item => item.type === type && (item.id === id || item.kanji === id)
        );
      }
      return state.items.some(
        item => item.type === type && item.id === id
      );
    },
    getLearningItems: (state) => state.learningItems
  }
} 