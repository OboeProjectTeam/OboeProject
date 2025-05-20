// store/index.js
import { createStore } from 'vuex';
import header from './modules/header';
import cart from './modules/cart';
import footer from './modules/footer';
import search from './modules/search';
export default createStore({
  modules: {
    header,
    cart,
    footer,
    search
  },
});
