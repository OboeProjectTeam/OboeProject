// Import các module API
import adminApi from './modules/adminApi';
import authApi from './modules/authApi';
import blogApi from './modules/blogApi';
import commentApi from './modules/commentApi';
import dashboardApi from './modules/dashboardApi';
import favoriteApi from './modules/favoriteApi';
import flashcardApi from './modules/flashcardApi';
import grammarApi from './modules/grammarApi';
import kanjiApi from './modules/kanjiApi';
import messageApi from './modules/messageApi';
import notificationApi from './modules/notificationApi';
import profileApi from './modules/profileApi';
import questionApi from './modules/questionApi';
import quizApi from './modules/quizApi';
import reportApi from './modules/reportApi';
import vocabularyApi from './modules/vocabularyApi';
import sampleSentenceApi from './modules/sampleSentenceApi';
import paymentApi from './modules/paymentApi';
import statisticsApi from './modules/statisticsApi';
import learningMaterialApi from './modules/learningMaterialApi';
import searchApi from './modules/searchApi';
import aiApi from './modules/aiApi';
import aiReplyApi from './modules/aiRepApi';
import userApi from './modules/userApi';

const api = {
    user: userApi,
    ai: aiApi,
    aiReply: aiReplyApi,
    admin: adminApi,
    auth: authApi,
    blog: blogApi,
    comment: commentApi,
    dashboard: dashboardApi,
    favorite: favoriteApi,
    flashcard: flashcardApi,
    grammar: grammarApi,
    kanji: kanjiApi,
    learningMaterial: learningMaterialApi,
    message: messageApi,
    notification: notificationApi,
    payment: paymentApi,
    profile: profileApi,
    question: questionApi,
    quiz: quizApi,
    report: reportApi,
    search: searchApi,
    statistics: statisticsApi,
    vocabulary: vocabularyApi,
    sampleSentence: sampleSentenceApi,
  };
  
  export default api;