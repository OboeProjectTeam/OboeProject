// filepath: src/router/index.js
import TheHome from "@/views/home/TheHome.vue";
import TheIntro from "@/views/intro/TheIntro.vue";
import TheLogin from "@/views/auth/login/TheLogin.vue";
import TheRegister from "@/views/auth/register/TheRegister.vue";
import WordDetail from '@/views/search-japanese/word-detail/WordDetail.vue' 
import KanjiDetail from '@/views/search-japanese/kanji-detail/KanjiDetail.vue'
import GrammarDetail from '@/views/search-japanese/grammar-detail/GrammarDetail.vue'
import SentenceDetail from '@/views/search-japanese/sentence-detail/SentenceDetail.vue'
import FlashcardLearn from '@/views/flashcard/flashcard-learn/FlashcardLearn.vue'
import CreateFlashcard from '@/views/create-learn/create-flashcard/CreateFlashcard.vue'
import CreateQuiz from '@/views/create-learn/create-quiz/CreateQuiz.vue'
import Library from '@/views/self/my-library/MyLibrary.vue'
import FlashcardTest from '@/views/flashcard/flashcard-test/FlashcardTest.vue'
import FlashcardMatch from '@/views/flashcard/flashcard-match/FlashcardMatch.vue'
import TheForum from "@/views/forum/forum-home/TheForum.vue";
import ForumPostDetail from "@/views/forum/forum-post-detail/ForumPostDetail.vue";
import CreateForumPost from "@/views/forum/forum-post-create/CreateForumPost.vue";
import ProfileDetail from "@/views/forum/forum-profile/ForumProfile.vue";
import MyProfile from '@/views/self/my-profile/MyProfile.vue';
import TheSettings from '@/views/self/my-settings/TheSettings.vue';
import TheUpgrade from '@/views/pay-fee/upgrade/TheUpgrade.vue';
import ThePayment from '@/views/pay-fee/payment/ThePayment.vue';
import SearchResults from '@/views/home/search-results/SearchResults.vue';
import TheMessages from '@/views/self/my-messages/MyMessages.vue'
import AdminLayout from '@/views/admin/AdminLayout.vue';
import UserManagement from '@/components/layout/admin-content/users/UserManagement.vue';
import ReportManagement from '@/components/layout/admin-content/reports/ReportManagement.vue';
import FeedbackManagement from '@/components/layout/admin-content/feedback/FeedbackManagement.vue';
import DictionaryManagement from '@/components/layout/admin-content/dictionary/DictionaryManagement.vue';
import DashboardView from '@/components/layout/admin-content/dashboard/DashboardView.vue'
import FooterServicesLayout from '@/views/footer-services/FooterServicesLayout.vue';
import TheTerms from '@/views/footer-services/terms/TheTerms.vue';
import ThePrivacy from '@/views/footer-services/privacy/ThePrivacy.vue';
import StudyGuide from '@/views/footer-services/study-guide/StudyGuide.vue';
import PaymentGuide from '@/views/footer-services/payment-guide/PaymentGuide.vue';
import WhyFlashcard from '@/views/footer-services/why-flashcard/WhyFlashcard.vue';
import WritingGuide from '@/views/footer-services/writing-guide/WritingGuide.vue';
import JapanNews from '@/views/footer-services/japan-news/JapanNews.vue';
import TheContact from '@/views/footer-services/contact/TheContact.vue';
import TheBenefits from '@/views/footer-services/japanese-benefits/TheBenefits.vue';
import KanjiGuide from '@/views/footer-services/kanji-guide/KanjiGuide.vue';


const routes = [
  {
    path: "/",
    name: "Home",
    component: TheHome,
    meta: {
      requiresAuth: true
    }
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
    path: "/forum/edit/:id",
    name: "EditForumPost",
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
    path: "/forum/u/:username",
    name: "ProfileDetail",
    component: ProfileDetail,
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
    component: MyProfile,
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
    path: '/flashcard/learn',
    name: 'flashcardLearn',
    component: FlashcardLearn,
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
  },
  {
    path: '/flashcard/match',
    name: 'FlashcardMatch',
    component: FlashcardMatch,
  },
  {
    path: '/search/:query?',
    name: 'SearchResults',
    component: SearchResults,
    props: route => ({ 
      query: route.params.query || route.query.q || '',
      searchResults: route.params.searchResults
    }),
    meta: { requiresAuth: true }
  },
  {
    path: '/messages',
    name: 'messages',
    component: TheMessages,
    meta: {
      requiresAuth: true
    }
  },
  {
  path: '/admin',
  name: 'admin',
  component: AdminLayout,
  // meta: { requiresAuth: true, requiresAdmin: true },
  children: [
    {
      path: '',
      name: 'admin-dashboard',
      component: DashboardView,
      meta: { title: 'Tổng quan' }
    },
    {
      path: 'users',
      name: 'admin-users',
      component: UserManagement,
      meta: { title: 'Quản lý người dùng' }
    },
    {
      path: 'reports',
      name: 'admin-reports',
      component: ReportManagement,
      meta: { title: 'Quản lý báo cáo' }
    },
    {
      path: 'feedback',
      name: 'admin-feedback',
      component: FeedbackManagement,
      meta: { title: 'Quản lý đóng góp ý kiến' }
    },
    {
      path: 'dictionary',
      name: 'admin-dictionary',
      component: DictionaryManagement,
      meta: { title: 'Quản lý từ điển' }
    }
  ]
  },
  {
    path: '/footer-services',
    name: 'FooterServices',
    component: FooterServicesLayout,
    children: [
      {
        path: '',
        name: 'terms',
        component: TheTerms
      },
      {
        path: 'privacy',
        name: 'Privacy',
        component: ThePrivacy
      },
      {
        path: 'study-guide',
        name: 'StudyGuide',
        component: StudyGuide
      },
      {
        path: 'payment-guide',
        name: 'PaymentGuide',
        component: PaymentGuide
      },
      {
        path: 'why-flashcard',
        name: 'WhyFlashcard',
        component: WhyFlashcard
      },
      {
        path: 'writing-guide',
        name: 'WritingGuide',
        component: WritingGuide
      },
      {
        path: 'benefits-japanese',
        name: 'BenefitsJapanese',
        component: TheBenefits
      },
      {
        path: 'kanji-guide',
        name: 'KanjiGuide',
        component: KanjiGuide
      },
      {
        path: 'japan-news',
        name: 'JapanNews',
        component: JapanNews
      },
      {
        path: 'contact',
        name: 'Contact',
        component: TheContact
      },
    ]
  }
];

export default routes;