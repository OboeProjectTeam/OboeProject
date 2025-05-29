// filepath: src/router/index.js
import TheHome from "../views/home/TheHome.vue";
import TheIntro from "../views/intro/TheIntro.vue";
import TheLogin from "../views/login/TheLogin.vue";
import TheRegister from "../views/register/TheRegister.vue";
import WordDetail from '@/views/word-detail/WordDetail.vue' 
import KanjiDetail from '@/views/kanji-detail/KanjiDetail.vue'
import GrammarDetail from '@/views/grammar-detail/GrammarDetail.vue'
import SentenceDetail from '@/views/sentence-detail/SentenceDetail.vue'

const routes = [
  {
    path: "/",
    name: "Home",
    component: TheHome,
  },
  {
    path: "/intro",
    name: "Intro",
    component: TheIntro,
  },
  {
    path: "/login",
    name: "Login",
    component: TheLogin,
  },
  {
    path: "/register",
    name: "Register",
    component: TheRegister,
  },
  {
    path: '/word/:id',
    name: 'WordDetail',
    component: WordDetail
  },
  {
    path: '/kanji/:kanji',
    name: 'KanjiDetail',
    component: KanjiDetail
  },
  {
    path: '/grammar/:id',
    name: 'GrammarDetail',
    component: GrammarDetail
  },
  {
    path: '/sentence/:id',
    name: 'SentenceDetail',
    component: SentenceDetail
  }
];

export default routes;