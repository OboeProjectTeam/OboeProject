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
import CreateFlashcard from '@/views/create-flashcard/CreateFlashcard.vue'
import CreateQuiz from '@/views/create-quiz/CreateQuiz.vue'
import Library from '@/views/my-library/MyLibrary.vue'
import FlashcardTest from '@/views/flashcard-test/FlashcardTest.vue'
import FlashcardMatch from '@/views/flashcard-match/FlashcardMatch.vue'
import TheForum from "../views/forum/TheForum.vue";
import ForumPostDetail from "../views/forum/ForumPostDetail.vue";
import CreateForumPost from "../views/forum/CreateForumPost.vue";
import TheProfile from '@/views/profile/TheProfile.vue';
import TheSettings from '@/views/settings/TheSettings.vue';
import TheUpgrade from '@/views/upgrade/TheUpgrade.vue';
import ThePayment from '@/views/payment/ThePayment.vue';

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
    path: "/forum",
    name: "TheForum",
    component: TheForum,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: "/forum/post/create",
    name: "CreateForumPost",
    component: CreateForumPost,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: "/forum/post/:id",
    name: "ForumPostDetail",
    component: ForumPostDetail,
    meta: {
      requiresAuth: true
    }
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
    path: "/profile",
    name: "Profile",
    component: TheProfile,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: "/settings",
    name: "Settings",
    component: TheSettings,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: "/upgrade",
    name: "Upgrade",
    component: TheUpgrade,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: "/payment",
    name: "Payment",
    component: ThePayment,
    meta: {
      requiresAuth: true
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
  },
  {
    path: '/flashcard/test',
    name: 'FlashcardTest',
    component: FlashcardTest,
    props: route => ({
      type: route.query.type,
      deckId: route.query.deckId,
      source: route.query.source
    }),
    meta: { requiresAuth: true }
  },
  {
    path: '/flashcard/match',
    name: 'FlashcardMatch',
    component: FlashcardMatch,
    meta: { requiresAuth: true }
  }
]

export default routes;