import { createStore } from 'vuex';
import auth from './modules/auth';
import forum from './modules/forum';

export default createStore({
  modules: {
    auth,
    forum
  }
}); 