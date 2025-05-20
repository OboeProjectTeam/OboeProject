// store/modules/header.js
export default {
    namespaced: true,
    state: () => ({
      options: ['Từ Vựng', 'Hán Tự', 'Ngữ Pháp', 'Mẫu câu'],
      activeIndex: 0,
    }),
    mutations: {
      setActiveIndex(state, index) {
        state.activeIndex = index;
      },
    },
    getters: {
      options: (state) => state.options,
      activeIndex: (state) => state.activeIndex,
    },
  };
  