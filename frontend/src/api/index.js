// Import các module API
import adminApi from './modules/adminApi';
import authApi from './modules/authApi';
import blogApi from './modules/blogApi';
import commentApi from './modules/commentApi';
import favoriteApi from './modules/favoriteApi';
import flashcardApi from './modules/flashcardApi';
import grammarApi from './modules/grammarApi';
import kanjiApi from './modules/kanjiApi';
import messageApi from './modules/messageApi';
import notificationApi from './modules/notificationApi';
import oauthApi from './modules/oauthApi';
import profileApi from './modules/profileApi';
import questionApi from './modules/questionApi';
import quizApi from './modules/quizApi';
import reportApi from './modules/reportApi';
import vocabularyApi from './modules/vocabularyApi';
import sampleSentenceApi from './modules/sampleSentenceApi';

const api = {
    admin: adminApi,
    auth: authApi,
    blog: blogApi,
    comment: commentApi,
    favorite: favoriteApi,
    flashcard: flashcardApi,
    grammar: grammarApi,
    kanji: kanjiApi,
    message: messageApi,
    notification: notificationApi,
    oauth: oauthApi,
    profile: profileApi,
    question: questionApi,
    quiz: quizApi,
    report: reportApi,
    vocabulary: vocabularyApi,
    sampleSentence: sampleSentenceApi
  };
  
  export default api;