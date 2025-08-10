<template>
  <div class="home-page">
    <!-- Hero Section with Search -->
    <section class="hero-section">
      <div class="container">
        <h1>{{ t('home.welcome') }}</h1>
        <p>{{ t('home.whatToLearn') }}</p>
        <div class="main-search-bar">
          <input type="text" v-model="searchQuery" :placeholder="t('home.searchPlaceholder')" @keyup.enter="performSearch" />
          <button @click="performSearch"><i class="fas fa-search"></i></button>
        </div>
      </div>
    </section>

    <!-- Recent Activity Section - Only show if there are recent sets or loading -->
    <section v-if="recentSetsLoading || recentSets.length > 0" class="content-section">
      <div class="container">
        <div class="section-header">
          <h2>{{ t('home.recentAccess') }}</h2>
          <router-link to="/library" class="view-all-link">{{ t('home.viewAll') }}</router-link>
        </div>
        
        <!-- Loading state -->
        <div v-if="recentSetsLoading" class="horizontal-scroll">
          <div v-for="i in 3" :key="i" class="content-card loading">
            <div class="loading-placeholder"></div>
            <div class="loading-placeholder small"></div>
            <div class="loading-placeholder button"></div>
          </div>
        </div>
        
        <!-- Recent sets list -->
        <div v-else class="horizontal-scroll">
          <div v-for="set in recentSets" :key="set.id" class="content-card">
            <h3>{{ set.title }}</h3>
            <p v-if="set.description" class="description">{{ set.description }}</p>
            <div class="card-meta">
              <span>{{ set.cardCount }} {{ t('home.terms') }}</span>
              <span v-if="set.created" class="date">{{ new Date(set.created).toLocaleDateString('vi-VN') }}</span>
            </div>
            <button @click="startLearning(set)" class="learn-now-btn">{{ t('home.learnNow') }}</button>
          </div>
        </div>
      </div>
    </section>

    <!-- Recommended Study Sets Section -->
    <section class="content-section">
      <div class="container">
        <div class="section-header">
          <h2>{{ t('home.recommendedQuizzes') }}</h2>
        </div>
        
        <!-- Loading state -->
        <div v-if="recommendedSetsLoading" class="content-grid">
          <div v-for="i in 3" :key="i" class="content-card large loading">
            <div class="card-info">
              <div class="loading-placeholder"></div>
              <div class="loading-placeholder"></div>
              <div class="loading-placeholder small"></div>
              <div class="author-info">
                <div class="loading-placeholder avatar-loading"></div>
                <div class="loading-placeholder small"></div>
              </div>
            </div>
            <div class="card-actions">
              <div class="loading-placeholder button"></div>
            </div>
          </div>
        </div>
        
        <!-- No data state -->
        <div v-else-if="recommendedSets.length === 0" class="no-data-state">
          <p>{{ t('home.noRecommendedSets') }}</p>
        </div>
        
        <!-- Recommended sets list -->
        <div v-else class="content-grid">
           <div v-for="set in recommendedSets" :key="set.id" class="content-card large">
             <div class="card-info">
                <h3>{{ set.title }}</h3>
                <p>{{ set.description }}</p>
                <div class="author-info">
                  <img :src="set.author.avatar" alt="author avatar" class="avatar">
                  <span>{{ set.author.name }}</span>
                </div>
             </div>
             <div class="card-actions">
                <button @click="startQuiz(set)" class="learn-now-btn">{{ t('home.takeQuiz') }}</button>
             </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Recommended Forum Topics Section -->
    <section class="content-section">
      <div class="container">
        <div class="section-header">
          <h2>{{ t('home.featuredTopics') }}</h2>
          <router-link to="/forum" class="view-all-link">{{ t('home.viewAll') }}</router-link>
        </div>
        
        <!-- Loading state -->
        <div v-if="recommendedTopicsLoading" class="forum-grid">
          <div v-for="i in 3" :key="i" class="forum-topic-card loading">
            <div class="loading-placeholder"></div>
            <div class="loading-placeholder small"></div>
            <div class="loading-placeholder button"></div>
          </div>
        </div>
        
        <!-- No data state -->
        <div v-else-if="recommendedTopics.length === 0" class="no-data-state">
          <p>{{ t('home.noFeaturedTopics') }}</p>
        </div>
        
        <!-- Featured topics list -->
        <div v-else class="forum-grid">
          <div v-for="topic in recommendedTopics" :key="topic.id" class="forum-topic-card">
            <h4>{{ topic.title }}</h4>
            <p>{{ topic.postCount }} {{ t('home.comments') }}</p>
            <router-link :to="`/forum/post/${topic.id}`" class="view-topic-btn">
              {{ t('home.viewTopic') }} <i class="fas fa-arrow-right"></i>
            </router-link>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import flashcardApi from '@/api/modules/flashcardApi';
import learningMaterialApi from '@/api/modules/learningMaterialApi';
import blogApi from '@/api/modules/blogApi';
import searchApi from '@/api/modules/searchApi';
import quizApi from '@/api/modules/quizApi';

const store = useStore();
const router = useRouter();
const { t } = useI18n();

const searchQuery = ref('');

// Recent sets - will be loaded from API
const recentSets = ref([]);
const recentSetsLoading = ref(false);

// Recommended sets - will be loaded from API
const recommendedSets = ref([]);
const recommendedSetsLoading = ref(false);

// Featured topics - will be loaded from API
const recommendedTopics = ref([]);
const recommendedTopicsLoading = ref(false);

// Load recent flashcards from API
const loadRecentSets = async () => {
  try {
    recentSetsLoading.value = true;
    const response = await flashcardApi.getTop5Latest();
    
    // Transform API response to match component format
    recentSets.value = response.map(item => ({
      id: item.set_id,
      title: item.term,
      description: item.description,
      cardCount: item.cardItems ? item.cardItems.length : 0,
      created: item.created,
      cardItems: item.cardItems || []
    }));
    
  } catch (error) {
    console.error('Error loading recent sets:', error);
    // Keep empty array if API fails
    recentSets.value = [];
  } finally {
    recentSetsLoading.value = false;
  }
};

// Load recommended materials from API
const loadRecommendedSets = async () => {
  try {
    recommendedSetsLoading.value = true;
    const response = await learningMaterialApi.getSuggestedMaterials();
    
    if (response && Array.isArray(response)) {
      recommendedSets.value = response.map(item => ({
        id: item.quizId,
        title: item.title,
        description: item.description,
        author: {
          name: item.author,
          avatar: item.avatarUrl || '/default-avatar.png'
        },
        type: 'quiz'
      }));
    } else {
      recommendedSets.value = [];
    }
  } catch (error) {
    console.error('Error loading recommended sets:', error);
    recommendedSets.value = [];
  } finally {
    recommendedSetsLoading.value = false;
  }
};

// Load featured topics from API
const loadFeaturedTopics = async () => {
  try {
    recommendedTopicsLoading.value = true;
    const response = await blogApi.getFeaturedComments();
    
    if (response && Array.isArray(response)) {
      recommendedTopics.value = response.map(item => ({
        id: item.blogId,
        title: item.title,
        postCount: item.commentCount
      }));
    } else {
      recommendedTopics.value = [];
    }
  } catch (error) {
    console.error('Error loading featured topics:', error);
    recommendedTopics.value = [];
  } finally {
    recommendedTopicsLoading.value = false;
  }
};

const performSearch = async () => {
  if (!searchQuery.value.trim()) return;
  
  try {
    // Gọi API search_home để lấy tất cả dữ liệu (flashcards, quizzes, users)
    const searchResults = await searchApi.search_home(searchQuery.value.trim());
    
    // Chuyển hướng đến trang search với dữ liệu qua params
    router.push({ 
      name: 'SearchResults',
      params: { 
        query: searchQuery.value.trim(),
        searchResults: searchResults
      }
    });
  } catch (error) {
    console.error('Error performing search:', error);
    // Nếu API lỗi, vẫn chuyển hướng đến trang search để xử lý
    router.push({ 
      name: 'SearchResults',
      params: { query: searchQuery.value.trim() }
    });
  }
};

const startLearning = async (set) => {
  try {
    // If set has cardItems, use them directly
    if (set.cardItems && set.cardItems.length > 0) {
      // Convert cardItems to learning format
      const learningItems = set.cardItems.map(item => ({
        type: 'word',
        kanji: item.word || '',
        kana: '',
        meaning: item.meaning || '',
        content: item.word || '',
        backcontent: item.meaning || '',
        front: item.word || '',
        back: item.meaning || ''
      }));
      
      // Save to store
      await store.dispatch('flashcard/setLearningItems', learningItems);
      
      // Navigate to learn page
      router.push({
        path: '/flashcard/learn',
        query: {
          source: 'home',
          title: set.title,
          description: set.description || `Học liệu gồm ${set.cardCount} thuật ngữ`,
          setId: set.id,
          createdAt: set.created || new Date().toISOString()
        }
      });
    } else {
      // If no cardItems, try to fetch from API
      const flashcardData = await flashcardApi.getById(set.id);
      
      if (flashcardData && flashcardData.cardItems && flashcardData.cardItems.length > 0) {
        const learningItems = flashcardData.cardItems.map(item => ({
          type: 'word',
          kanji: item.word || item.front || '',
          kana: '',
          meaning: item.meaning || item.back || '',
          content: item.word || item.front || '',
          backcontent: item.meaning || item.back || '',
          front: item.word || item.front || '',
          back: item.meaning || item.back || ''
        }));
        
        await store.dispatch('flashcard/setLearningItems', learningItems);
        
        router.push({
          path: '/flashcard/learn',
          query: {
            source: 'home',
            title: flashcardData.title || set.title,
            description: flashcardData.description || set.description || `Học liệu gồm ${flashcardData.cardItems.length} thuật ngữ`,
            setId: set.id,
            createdAt: flashcardData.created || set.created || new Date().toISOString()
          }
        });
      } else {
        store.dispatch('showMessage', {
          type: 'error',
          text: t('home.noContent')
        });
      }
    }
  } catch (error) {
    console.error('Error starting learning:', error);
    store.dispatch('showMessage', {
      type: 'error',
      text: t('home.cannotLoadData')
    });
  }
};

const startQuiz = async (quiz) => {
  try {
    // Gọi API để lấy dữ liệu quiz với questions
    const quizData = await quizApi.getQuizWithQuestions(quiz.id);
    
    if (!quizData || !quizData.questions || quizData.questions.length === 0) {
      store.dispatch('showMessage', {
        type: 'error',
        text: t('home.noQuestionsInQuiz')
      });
      return;
    }

    // Convert API response to learning items format for FlashcardTest
    // Mapping theo cấu trúc API response thực tế
    const learningItems = quizData.questions.map(question => {
      // Xử lý options - nếu là string thì split, nếu là array thì giữ nguyên
      let options = [];
      if (question.options) {
        if (typeof question.options === 'string') {
          // Nếu options là string như "あなた自身;seek;メニュー;thing"
          options = question.options.split(';').map(opt => opt.trim());
        } else if (Array.isArray(question.options)) {
          options = question.options;
        }
      }

      return {
        type: 'quiz',
        front: question.questionName || question.questionText || question.question || '',
        back: question.correctAnswer || '',
        content: question.questionName || question.questionText || question.question || '',
        backcontent: question.correctAnswer || '',
        options: options, // Cho multiple choice
        questionType: 'multiple-choice', // Mặc định là multiple choice
        questionID: question.questionID || question.id
      };
    });

    // Save to store for FlashcardTest to use
    await store.dispatch('flashcard/setLearningItems', learningItems);

    // Navigate to FlashcardTest với đúng route
    router.push({
      name: 'FlashcardTest', // Sử dụng name thay vì path
      query: {
        type: 'multiple-choice', // Default test type
        source: 'home',
        title: quizData.title || quiz.title,
        description: quizData.description || `Bài kiểm tra gồm ${quizData.questions.length} câu hỏi`,
        quizId: quizData.quizzesID || quiz.id,
        creatorName: quiz.author?.name || 'Người dùng',
        creatorAvatar: quiz.author?.avatar || '',
        createdAt: quiz.createdAt || new Date().toISOString()
      }
    });
  } catch (error) {
    console.error('Error loading quiz:', error);
    store.dispatch('showMessage', {
      type: 'error',
      text: t('home.cannotOpenQuiz')
    });
  }
};

// Load data when component is mounted
onMounted(() => {
  loadRecentSets();
  loadRecommendedSets();
  loadFeaturedTopics();
});

</script>

<style lang="scss" scoped>
@use '@/views/home/TheHome.scss';
</style>
