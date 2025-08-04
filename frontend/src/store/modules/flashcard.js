export default {
  namespaced: true,
  state: () => ({
    items: [],
    learningItems: [],
    sortBy: 'recent', // 'recent' or 'alphabetical'
    itemsPerPage: 10,
    currentPage: 1,
    flashcardSets: [] // Thêm state cho bộ thẻ ghi nhớ
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
      state.learningItems = items.map(item => {
        // Ensure each item has a unique ID
        const id = item.id ? item.id : `item-${Date.now()}-${Math.random()}`;
        
        // Giữ nguyên cấu trúc dữ liệu đã được format từ FlashcardList
        // Chỉ thêm các trường cần thiết nếu chưa có
        return {
          id,
          type: item.type || 'word',
          status: item.status || 'learning',
          // Giữ nguyên tất cả các trường khác từ item
          ...item
        };
      });
    },
    // Thêm mutations cho bộ thẻ ghi nhớ
    addFlashcardSet(state, set) {
      const setWithTimestamp = {
        ...set,
        id: Date.now().toString(), // Tạo ID đơn giản
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
      state.flashcardSets.push(setWithTimestamp)
    },
    updateFlashcardSet(state, { id, set }) {
      const index = state.flashcardSets.findIndex(s => s.id === id)
      if (index !== -1) {
        state.flashcardSets[index] = {
          ...state.flashcardSets[index],
          ...set,
          updatedAt: new Date().toISOString()
        }
      }
    },
    deleteFlashcardSet(state, id) {
      state.flashcardSets = state.flashcardSets.filter(set => set.id !== id)
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
    },
    // Thêm actions cho bộ thẻ ghi nhớ
    createFlashcardSet({ commit }, set) {
      commit('addFlashcardSet', set)
    },
    updateFlashcardSet({ commit }, payload) {
      commit('updateFlashcardSet', payload)
    },
    deleteFlashcardSet({ commit }, id) {
      commit('deleteFlashcardSet', id)
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
      return state.items.some(
        item => item.type === type && item.id === id
      );
    },
    getLearningItems: (state) => {
      return state.learningItems;
    },
    // Thêm getters cho bộ thẻ ghi nhớ
    getAllFlashcardSets: (state) => {
      return state.flashcardSets.sort((a, b) => 
        new Date(b.updatedAt) - new Date(a.updatedAt)
      )
    },
    getFlashcardSetById: (state) => (id) => {
      return state.flashcardSets.find(set => set.id === id)
    }
  }
}