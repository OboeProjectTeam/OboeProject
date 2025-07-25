// store/index.js
import createPersistedState from 'vuex-persistedstate'
import { createStore } from 'vuex';
import header from './modules/header';
import cart from './modules/cart';
import footer from './modules/footer';
import search from './modules/search';
import flashcard from './modules/flashcard';
import auth from './modules/auth';
import quiz from './modules/quiz';
import history from './modules/history';
import user from './modules/user';
import forum from './modules/forum';
import message from './modules/message';

export default createStore({
  modules: {
    user,
    header,
    cart,
    footer,
    search,
    flashcard,
    auth,
    quiz,
    history,
    forum,
    message
  },
  plugins: [createPersistedState()]
});

