<template>
  <div class="home-page">
    <!-- Hero Section with Search -->
    <section class="hero-section">
      <div class="container">
        <h1>Chào mừng trở lại!</h1>
        <p>Hôm nay bạn muốn học gì?</p>
        <div class="main-search-bar">
          <input type="text" v-model="searchQuery" placeholder="Tìm kiếm học liệu từ cộng đồng..." @keyup.enter="performSearch" />
          <button @click="performSearch"><i class="fas fa-search"></i></button>
        </div>
      </div>
    </section>

    <!-- Recent Activity Section - Only show if there are recent sets or loading -->
    <section v-if="recentSetsLoading || recentSets.length > 0" class="content-section">
      <div class="container">
        <div class="section-header">
          <h2>Truy cập gần đây</h2>
          <router-link to="/library" class="view-all-link">Xem tất cả</router-link>
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
              <span>{{ set.cardCount }} thuật ngữ</span>
              <span v-if="set.created" class="date">{{ new Date(set.created).toLocaleDateString('vi-VN') }}</span>
            </div>
            <button @click="startLearning(set)" class="learn-now-btn">Học ngay</button>
          </div>
        </div>
      </div>
    </section>

    <!-- Recommended Study Sets Section -->
    <section class="content-section">
      <div class="container">
        <div class="section-header">
          <h2>Học liệu đề xuất cho bạn</h2>
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
          <p>Chưa có học liệu đề xuất nào</p>
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
                <button @click="startQuiz(set)" class="learn-now-btn">Làm bài</button>
             </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Recommended Forum Topics Section -->
    <section class="content-section">
      <div class="container">
        <div class="section-header">
          <h2>Chủ đề nổi bật</h2>
          <router-link to="/forum" class="view-all-link">Xem tất cả</router-link>
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
          <p>Chưa có chủ đề nổi bật nào</p>
        </div>
        
        <!-- Featured topics list -->
        <div v-else class="forum-grid">
          <div v-for="topic in recommendedTopics" :key="topic.id" class="forum-topic-card">
            <h4>{{ topic.title }}</h4>
            <p>{{ topic.postCount }} bình luận</p>
            <router-link :to="`/forum/post/${topic.id}`" class="view-topic-btn">
              Xem chủ đề <i class="fas fa-arrow-right"></i>
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
import flashcardApi from '@/api/modules/flashcardApi';
import learningMaterialApi from '@/api/modules/learningMaterialApi';
import blogApi from '@/api/modules/blogApi';
import searchApi from '@/api/modules/searchApi';

const store = useStore();
const router = useRouter();

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
    
    console.log('Recent sets loaded:', recentSets.value);
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
          text: 'Học liệu không có nội dung'
        });
      }
    }
  } catch (error) {
    console.error('Error starting learning:', error);
    store.dispatch('showMessage', {
      type: 'error',
      text: 'Không thể tải dữ liệu học liệu'
    });
  }
};

const startQuiz = (quiz) => {
  try {
    // Navigate to quiz page
    router.push({
      path: '/quiz',
      query: {
        id: quiz.id,
        title: quiz.title,
        description: quiz.description,
        source: 'home'
      }
    });
  } catch (error) {
    console.error('Error starting quiz:', error);
    store.dispatch('showMessage', {
      type: 'error',
      text: 'Không thể mở bài quiz'
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

/* Additional styles for loading and no-data states */
.content-card .description {
  color: #6b7280;
  font-size: 0.85rem;
  margin-bottom: 0.75rem;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  font-size: 0.8rem;
  color: #9ca3af;
}

.card-meta .date {
  font-size: 0.75rem;
}

/* Loading states */
.content-card.loading {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  min-width: 280px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.forum-topic-card.loading {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.loading-placeholder {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
  border-radius: 4px;
  height: 1.2rem;
  margin-bottom: 0.75rem;
}

.loading-placeholder.small {
  height: 0.9rem;
  width: 60%;
}

.loading-placeholder.button {
  height: 2.5rem;
  margin-top: 1rem;
  margin-bottom: 0;
}

@keyframes loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

.no-data-state {
  text-align: center;
  padding: 3rem 1rem;
  color: rgba(255, 255, 255, 0.8);
  font-size: 1rem;
}

.no-data-state p {
  margin: 0;
}
</style>
