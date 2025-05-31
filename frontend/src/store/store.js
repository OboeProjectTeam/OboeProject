// store/index.js
import { createStore } from 'vuex';
import header from './modules/header';
import cart from './modules/cart';
import footer from './modules/footer';
import search from './modules/search';
import flashcard from './modules/flashcard';

export default createStore({
  modules: {
    header,
    cart,
    footer,
    search,
    flashcard,
  },
});

