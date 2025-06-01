// filepath: src/router/index.js
import TheHome from "@/views/home/TheHome.vue";
import TheIntro from "@/views/intro/TheIntro.vue";
import TheLogin from "@/views/login/TheLogin.vue";
import TheRegister from "@/views/register/TheRegister.vue";
import WordDetail from '@/views/word-detail/WordDetail.vue' 
import KanjiDetail from '@/views/kanji-detail/KanjiDetail.vue'
import GrammarDetail from '@/views/grammar-detail/GrammarDetail.vue'
import SentenceDetail from '@/views/sentence-detail/SentenceDetail.vue'
import FlashcardView from '@/views/flashcard-list-view/FlashcardView.vue'
import FlashcardLearn from '@/views/flashcard-learn/FlashcardLearn.vue'
import CreateFlashcard from '@/views/create/CreateFlashcard.vue'
import CreateQuiz from '@/views/create/CreateQuiz.vue'
import Library from '@/views/my-library/MyLibrary.vue'
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
    meta: {
      requiresGuest: true
    }
  },
  {
    path: "/register",
    name: "Register",
    component: TheRegister,
    meta: {
      requiresGuest: true
    }
  },
  {
    path: '/word/:id',
    name: 'WordDetail',
    component: WordDetail
  },
  {
    path: '/kanji/:id',
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
  },
  {
    path: '/flashcard',
    name: 'flashcard',
    component: FlashcardView,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/flashcard/learn',
    name: 'flashcardLearn',
    component: FlashcardLearn,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/create/flashcard',
    name: 'CreateFlashcard',
    component: CreateFlashcard,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/create/quiz',
    name: 'CreateQuiz',
    component: CreateQuiz,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/library',
    name: 'Library',
    component: Library,
    meta: {
      requiresAuth: true
    }
  }
]

export default routes;