// store/modules/cart.js
export default {
  namespaced: true,
  state: () => ({
    slides: [
      {
        content: "覚え",
        backcontent: "Oboe",
      },
      {
        content: "覚え",
        backcontent: "Oboe",
      },
      {
        content: "覚え",
        backcontent: "Oboe",        
      },
      {
        content: "覚え",
        backcontent: "Oboe",
      },
      {
        content: "覚え",
        backcontent: "Oboe",
      },
      {
        content: "覚え",
        backcontent: "Oboe",
      },
    ],
  }),
  mutations: {
    setSlides(state, newSlides) {
      state.slides = newSlides;
    },
  },
  getters: {
    slides: (state) => state.slides,
  },
};
