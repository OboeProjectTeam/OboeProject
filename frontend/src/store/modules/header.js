// store/modules/header.js
export default {
    namespaced: true,
    state: () => ({
      optionKeys: ['vocabulary', 'kanji', 'grammar', 'sentences'],
      activeIndex: 0,
    }),
    mutations: {
      setActiveIndex(state, index) {
        state.activeIndex = index;
      },
    },
    getters: {
      optionKeys: (state) => state.optionKeys,
      activeIndex: (state) => state.activeIndex,
    },
  };
  