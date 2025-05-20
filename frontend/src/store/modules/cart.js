// store/modules/cart.js
export default {
  namespaced: true,
  state: () => ({
    slides: [
      {
        title: "Slide 1",
        description:
          "Lorem ipsum dolor sit, amet consectetur adipisicing elit. Magnam, cupiditate fugiat ratione vero suscipit labore voluptatum obcaecati quasi vitae explicabo eum accusantium modi eveniet commodi, assumenda illo aliquid veritatis delectus.",
        content:
          "Lorem ipsum dolor sit amet consectetur adipisicing elit. Et a fuga minima obcaecati repellat, accusamus quis aut sunt eius illum reiciendis repellendus nihil rem. Minima dolorum perspiciatis neque amet esse?",
        backdescription: "mô tả chi tiết mặt sau",
        backcontent: "Oboeru",
      },
      {
        title: "Slide 2",
        description: "mô tả chi tiết",
        content:
          "https://images2.thanhnien.vn/528068263637045248/2024/1/25/c3c8177f2e6142e8c4885dbff89eb92a-65a11aeea03da880-1706156293184503262817.jpg",
        backdescription: "mô tả chi tiết mặt sau",
        backcontent: "Oboeru",
      },
      {
        title: "Slide 3",
        description: "mô tả chi tiết",
        content: "Oboe",
        backdescription: "mô tả chi tiết mặt sau",
        backcontent: "Oboeru",
      },
      {
        title: "Slide 4",
        description: "mô tả chi tiết",
        content: "Oboe",
        backdescription: "mô tả chi tiết mặt sau",
        backcontent: "Oboeru",
      },
      {
        title: "Slide 5",
        description: "mô tả chi tiết",
        content: "Oboe",
        backdescription: "mô tả chi tiết mặt sau",
        backcontent: "Oboeru",
      },
      {
        title: "Slide 6",
        description: "mô tả chi tiết",
        content: "Oboe",
        backdescription: "mô tả chi tiết mặt sau",
        backcontent: "Oboeru",
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
