<template>
  <div class="search-results-page">
    <div class="container mx-auto py-8 px-4">
      <h1 class="text-3xl font-bold mb-6">Kết quả cho "{{ query }}"</h1>

      <!-- Tabs -->
      <div class="tabs-container mb-8" v-if="!loading && !error">
        <ul class="tabs-list">
          <li v-for="tab in availableTabs" :key="tab.key" 
              :class="['tab-item', { 'active': activeTab === tab.key }]"
              @click="activeTab = tab.key">
            {{ tab.name }}
          </li>
        </ul>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>Đang tìm kiếm...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="error-state">
        <div class="error-icon">⚠️</div>
        <p>{{ error }}</p>
        <button @click="loadSearchResults(query)" class="retry-btn">Thử lại</button>
      </div>
      
      <!-- All Results Tab -->
      <div v-else-if="activeTab === 'all'" class="search-content">
        <!-- Users Section -->
        <section class="mt-16" v-if="usersFromAPI.length > 0">
          <div class="section-header">
            <h2 class="text-2xl font-semibold">Người dùng</h2>
            <a href="#" class="view-all" @click.prevent="activeTab = 'users'">Xem tất cả</a>
          </div>
          <div class="single-row">
            <div v-for="user in usersFromAPI.slice(0, 5)" :key="user.id" class="user-card cur" @click="goToUserProfile(user)">
              <img :src="user.avatar" :alt="user.name" class="avatar"/>
              <div class="user-info">
                <h3 class="user-name">{{ user.name }}</h3>
                <div class="user-stats">
                  <span>{{ user.sets }} học phần</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Study Sets Section -->
        <section class="mt-16" v-if="studySets.length > 0">
          <div class="section-header">
            <h2 class="text-2xl font-semibold">Học liệu</h2>
            <a href="#" class="view-all" @click.prevent="activeTab = 'sets'">Xem tất cả</a>
          </div>
          <div class="single-row">
            <div v-for="set in studySets.slice(0, 5)" :key="set.id" class="question-card cur" @click="goToFlashcardLearn(set)">
              <h3 class="question-title">{{ set.title }}</h3>
              <div class="question-header">
                <span class="answer-count">{{ set.termCount }} thuật ngữ</span>
                <div class="author-info">
                  <img :src="set.author.avatar" :alt="set.author.name" class="author-avatar"/>
                  <span>{{ set.author.name }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Questions Section -->
        <section class="mt-16" v-if="questions.length > 0">
          <div class="section-header">
            <h2 class="text-2xl font-semibold">Bài kiểm tra</h2>
            <a href="#" class="view-all" @click.prevent="activeTab = 'questions'">Xem tất cả</a>
          </div>
          <div class="single-row">
            <div v-for="question in questions.slice(0, 5)" :key="question.id" class="question-card cur" @click="goToFlashcardTest(question)">
              <h3 class="question-title">{{ question.title }}</h3>
              <div class="question-header">
                <span class="answer-count">{{ question.answerCount }} câu hỏi</span>
                <div class="author-info">
                  <img :src="question.author.avatar" :alt="question.author.name" class="author-avatar"/>
                  <span>{{ question.author.name }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>

      <!-- Study Sets Tab -->
      <div v-else-if="activeTab === 'sets'" class="study-sets-content">
        <div class="study-sets-container">
          <!-- List section -->
          <div class="study-sets-list">
            <div class="list-header">
              <h2>Học liệu</h2>
              <div class="results-count">{{ studySets.length }} kết quả</div>
            </div>
            <div class="sets-grid">
              <div v-for="set in paginatedList" 
                   :key="set.id" 
                   class="study-set-card"
                   :class="{ 'active': selectedSet?.id === set.id }"
                   @click="selectStudySet(set)">
                <h3>{{ set.title }}</h3>
                <p class="term-count">{{ set.termCount }} thuật ngữ</p>
                <div class="author-info">
                  <img :src="set.author.avatar" :alt="set.author.name" class="author-avatar"/>
                  <span>{{ set.author.name }}</span>
                </div>
                
                <!-- Mobile Preview - only show on mobile -->
                <div v-if="selectedSet?.id === set.id && isMobileView" class="mobile-preview">
                  <div class="preview-header">
                    <div class="header-top">
                      <button class="learn-btn" @click.stop="goToFlashcardLearn(set)">
                        <i class="fas fa-graduation-cap"></i>Học
                      </button>
                    </div>
                  </div>
                  <div class="terms-preview">
                    <div class="terms-header">
                      <div>Mặt trước</div>
                      <div>Mặt sau</div>
                    </div>
                    <div class="terms-list">
                      <div v-for="(term, index) in set.terms" :key="index" class="term-row">
                        <div class="front">{{ term.front }}</div>
                        <div class="back">{{ term.back }}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="pagination">
              <button :disabled="currentPage === 1" @click="currentPage--">
                <i class="fas fa-chevron-left"></i> Trước
              </button>
              <span class="page-number">{{ currentPage }}/{{ totalPages }}</span>
              <button :disabled="currentPage === totalPages" @click="currentPage++">
                Sau <i class="fas fa-chevron-right"></i>
              </button>
            </div>
          </div>

          <!-- Preview section -->
          <div v-if="selectedSet" class="preview-section">
            <div class="preview-header">
              <div class="header-top">
                <h1>{{ selectedSet.title }}</h1>
                <button class="learn-btn" @click.stop="goToFlashcardLearn(selectedSet)">
                  <i class="fas fa-graduation-cap"></i>Học
                </button>
              </div>
            </div>
            <div class="terms-preview">
              <div class="terms-header">
                <div>Mặt trước</div>
                <div>Mặt sau</div>
              </div>
              <div class="terms-list">
                <div v-for="(term, index) in selectedSet.terms" :key="index" class="term-row">
                  <div class="front">{{ term.front }}</div>
                  <div class="back">{{ term.back }}</div>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="preview-section empty-preview">
            <div class="empty-content">
              <i class="fas fa-book-open"></i>
              <p>Chọn một học liệu để xem trước</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Questions Tab -->
      <div v-else-if="activeTab === 'questions'" class="paged-grid-content">
        <div class="grid-container">
          <div v-for="question in paginatedList" :key="question.id" class="question-card-grid" @click="goToFlashcardTest(question)">
            <h3 class="question-title">{{ question.title }}</h3>
            <div class="question-header">
              <span class="answer-count">{{ question.answerCount }} câu hỏi</span>
              <div class="author-info">
                <img :src="question.author.avatar" :alt="question.author.name" class="author-avatar"/>
                <span>{{ question.author.name }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="pagination">
          <button :disabled="currentPage === 1" @click="currentPage--">
            <i class="fas fa-chevron-left"></i> Trước
          </button>
          <span class="page-number">{{ currentPage }}/{{ totalPages }}</span>
          <button :disabled="currentPage === totalPages" @click="currentPage++">
            Sau <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>

      <!-- Users Tab -->
      <div v-else-if="activeTab === 'users'" class="paged-grid-content">
        <div class="grid-container">
          <div v-for="user in paginatedList" :key="user.id" class="user-card-grid" @click="goToUserProfile(user)">
            <img :src="user.avatar" :alt="user.name" class="avatar"/>
            <h3 class="user-name">{{ user.name }}</h3>
            <div class="user-stats">
              <span>{{ user.sets }} học phần</span>
            </div>
          </div>
        </div>
        <div class="pagination">
          <button :disabled="currentPage === 1" @click="currentPage--">
            <i class="fas fa-chevron-left"></i> Trước
          </button>
          <span class="page-number">{{ currentPage }}/{{ totalPages }}</span>
          <button :disabled="currentPage === totalPages" @click="currentPage++">
            Sau <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>

      <div v-if="!loading && !error && !hasAnyResults" class="text-center text-gray-500 mt-12">
        <p>Không tìm thấy kết quả nào cho "{{ query }}".</p>
      </div>
    </div>
  </div>
</template>

<script>
import searchApi from '@/api/modules/searchApi';
import quizApi from '@/api/modules/quizApi';
import api from '@/api';

export default {
  name: 'SearchResults',
  props: {
    query: {
      type: String,
      default: '',
    },
    searchResults: {
      type: Object,
      default: null,
    },
  },
  data() {
    return {
      activeTab: 'all',
      currentPage: 1,
      itemsPerPageConfig: {
        sets: 5,
        questions: 12, // 4 columns * 3 rows
        users: 12,     // 4 columns * 3 rows
      },
      selectedSet: null,
      isMobileView: false,
      tabs: [
        { key: 'all', name: 'Tất cả kết quả' },
        { key: 'sets', name: 'Học liệu' },
        { key: 'questions', name: 'Bài kiểm tra' },
        { key: 'users', name: 'Người dùng' },
      ],
      // Dữ liệu từ API
      apiResults: {
        flashcards: { content: [], totalElements: 0 },
        quizzes: { content: [], totalElements: 0 },
        users: { content: [], totalElements: 0 }
      },
      loading: false,
      error: null
    };
  },
  computed: {
    // Dữ liệu từ API được format lại
    studySets() {
      return this.apiResults.flashcards.content.map(item => ({
        id: item.flashcardId,
        title: item.title,
        termCount: item.termCount,
        author: { 
          name: item.authorName, 
          avatar: item.avatarUrl || 'https://thumbs.dreamstime.com/b/default-avatar-profile-image-vector-social-media-user-icon-potrait-182347582.jpg'
        },
        terms: (item.cardItems || []).map(cardItem => ({
          front: cardItem.word,
          back: cardItem.meaning
        }))
      }));
    },
    questions() {
      return this.apiResults.quizzes.content.map(item => ({
        id: item.quizId,
        title: item.title,
        author: { 
          name: item.userName, 
          avatar: item.avatarUrl || 'https://thumbs.dreamstime.com/b/default-avatar-profile-image-vector-social-media-user-icon-potrait-182347582.jpg'
        },
        answerCount: item.questionCount
      }));
    },
    usersFromAPI() {
      return this.apiResults.users.content.map(item => ({
        id: item.userId,
        userId: item.userId,
        name: item.userName,
        userName: item.userName,
        avatar: item.avatarUrl || 'https://thumbs.dreamstime.com/b/default-avatar-profile-image-vector-social-media-user-icon-potrait-182347582.jpg',
        sets: item.flashcardCount
      }));
    },
    currentList() {
      switch (this.activeTab) {
        case 'sets': return this.studySets;
        case 'questions': return this.questions;
        case 'users': return this.usersFromAPI;
        default: return [];
      }
    },
    currentItemsPerPage() {
      return this.itemsPerPageConfig[this.activeTab] || 1;
    },
    totalPages() {
      if (!this.currentList.length) return 1;
      return Math.ceil(this.currentList.length / this.currentItemsPerPage);
    },
    paginatedList() {
      if (!this.currentList.length) return [];
      const start = (this.currentPage - 1) * this.currentItemsPerPage;
      const end = start + this.currentItemsPerPage;
      return this.currentList.slice(start, end);
    },
    availableTabs() {
      const tabs = [];
      
      // Luôn hiển thị tab "Tất cả kết quả" nếu có ít nhất 1 loại dữ liệu
      if (this.usersFromAPI.length > 0 || this.studySets.length > 0 || this.questions.length > 0) {
        tabs.push({ key: 'all', name: 'Tất cả kết quả' });
      }
      
      // Chỉ hiển thị các tab có dữ liệu
      if (this.studySets.length > 0) {
        tabs.push({ key: 'sets', name: 'Học liệu' });
      }
      if (this.questions.length > 0) {
        tabs.push({ key: 'questions', name: 'Bài kiểm tra' });
      }
      if (this.usersFromAPI.length > 0) {
        tabs.push({ key: 'users', name: 'Người dùng' });
      }
      
      return tabs;
    },
    hasAnyResults() {
      return this.usersFromAPI.length > 0 || this.studySets.length > 0 || this.questions.length > 0;
    }
  },
  methods: {
    selectStudySet(set) {
      this.selectedSet = set;
    },
    goToUserProfile(user) {
      const searchQuery = this.query || this.$route.params.query || this.$route.query.q;
      this.$router.push({ 
        name: 'ProfileDetail', 
        params: { username: user.userName },
        query: { 
          userId: user.userId,
          fromSource: 'search',
          searchQuery: searchQuery // Thêm query tìm kiếm để breadcrumb có thể sử dụng
        }
      });
    },
    async goToFlashcardLearn(flashcard) {
      try {
        // Lấy dữ liệu flashcard từ API
        const flashcardData = await api.flashcard.getById(flashcard.id);
        
        if (!flashcardData || !flashcardData.cardItems || flashcardData.cardItems.length === 0) {
          this.$store.dispatch('showMessage', {
            type: 'error',
            message: 'Không thể tải dữ liệu học liệu'
          });
          return;
        }

        // Convert API response to learning items format for FlashcardLearn
        const learningItems = flashcardData.cardItems.map(item => ({
          type: 'word',
          kanji: item.word || '',
          kana: '',
          meaning: item.meaning || '',
          content: item.word || '',
          backcontent: item.meaning || '',
          front: item.word || '',
          back: item.meaning || ''
        }));

        // Save to store for FlashcardLearn to use
        await this.$store.dispatch('flashcard/setLearningItems', learningItems);

        // Navigate to FlashcardLearn with query params
        const searchQuery = this.query || this.$route.params.query || this.$route.query.q;
        this.$router.push({
          path: '/flashcard/learn',
          query: {
            source: 'search',
            title: flashcard.title,
            description: `Học liệu gồm ${flashcard.termCount} thuật ngữ`,
            setId: flashcard.id,
            creatorName: flashcard.author?.name || 'Người dùng',
            creatorAvatar: flashcard.author?.avatar || '',
            createdAt: new Date().toISOString(),
            searchQuery: searchQuery // Thêm query tìm kiếm để breadcrumb có thể sử dụng
          }
        });
      } catch (error) {
        console.error('Error loading flashcard:', error);
        this.$store.dispatch('showMessage', {
          type: 'error',
          message: 'Có lỗi xảy ra khi tải học liệu'
        });
      }
    },
    async goToFlashcardTest(quiz) {
      try {
        // Gọi API để lấy dữ liệu quiz với questions
        const quizData = await quizApi.getQuizWithQuestions(quiz.id);
        
        if (!quizData || !quizData.questions || quizData.questions.length === 0) {
          this.$store.dispatch('showMessage', {
            type: 'error',
            message: 'Không thể tải dữ liệu bài kiểm tra'
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
        await this.$store.dispatch('flashcard/setLearningItems', learningItems);

        // Navigate to FlashcardTest với đúng route
        this.$router.push({
          name: 'FlashcardTest', // Sử dụng name thay vì path
          query: {
            type: 'multiple-choice', // Default test type
            source: 'search',
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
        this.$store.dispatch('showMessage', {
          type: 'error',
          message: 'Có lỗi xảy ra khi tải bài kiểm tra'
        });
      }
    },
    checkMobileView() {
      this.isMobileView = window.innerWidth <= 768;
    },
    async loadSearchResults(keyword) {
      if (!keyword) return;
      
      this.loading = true;
      this.error = null;
      
      try {
        const response = await searchApi.search_home(keyword, null, 0, 50);
        this.apiResults = {
          flashcards: response.flashcards || { content: [], totalElements: 0 },
          quizzes: response.quizzes || { content: [], totalElements: 0 },
          users: response.users || { content: [], totalElements: 0 }
        };
        
        // Tự động chọn tab đầu tiên có dữ liệu
        this.$nextTick(() => {
          this.setDefaultActiveTab();
        });
      } catch (error) {
        console.error('Lỗi khi tải kết quả tìm kiếm:', error);
        this.error = 'Có lỗi xảy ra khi tìm kiếm. Vui lòng thử lại.';
      } finally {
        this.loading = false;
      }
    },
    async loadSearchResultsByType(keyword, type) {
      if (!keyword) return;
      
      this.loading = true;
      this.error = null;
      
      try {
        const response = await searchApi.search_home(keyword, type, 0, 50);
        // Cập nhật dữ liệu theo type
        if (type === 'flashcard') {
          this.apiResults.flashcards = response.flashcards || { content: [], totalElements: 0 };
        } else if (type === 'quiz') {
          this.apiResults.quizzes = response.quizzes || { content: [], totalElements: 0 };
        } else if (type === 'user') {
          this.apiResults.users = response.users || { content: [], totalElements: 0 };
        }
      } catch (error) {
        console.error('Lỗi khi tải kết quả tìm kiếm theo type:', error);
        this.error = 'Có lỗi xảy ra khi tìm kiếm. Vui lòng thử lại.';
      } finally {
        this.loading = false;
      }
    },
    setDefaultActiveTab() {
      // Nếu tab hiện tại không có trong danh sách available tabs, chọn tab đầu tiên
      const availableTabKeys = this.availableTabs.map(tab => tab.key);
      if (!availableTabKeys.includes(this.activeTab)) {
        if (availableTabKeys.length > 0) {
          this.activeTab = availableTabKeys[0];
        }
      }
    },
    handleRouterState() {
      // Kiểm tra xem có dữ liệu từ props không
      if (this.searchResults) {
        // Sử dụng dữ liệu từ props
        this.apiResults = {
          flashcards: this.searchResults.flashcards || { content: [], totalElements: 0 },
          quizzes: this.searchResults.quizzes || { content: [], totalElements: 0 },
          users: this.searchResults.users || { content: [], totalElements: 0 }
        };
        
        // Tự động chọn tab đầu tiên có dữ liệu
        this.$nextTick(() => {
          this.setDefaultActiveTab();
        });
      } else {
        // Lấy query từ props hoặc route params
        const searchQuery = this.query || this.$route.params.query || this.$route.query.q;
        if (searchQuery) {
          this.loadSearchResults(searchQuery);
        }
      }
    }
  },
  mounted() {
    this.checkMobileView();
    window.addEventListener('resize', this.checkMobileView);
    this.handleRouterState();
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkMobileView);
  },
  watch: {
    query(newQuery) {
      if (newQuery) {
        this.loadSearchResults(newQuery);
      }
    },
    activeTab: {
      immediate: true,
      handler(newTab, oldTab) {
        this.currentPage = 1;
        
        // Gọi API riêng biệt khi chuyển tab (trừ tab 'all')
        if (newTab !== 'all' && newTab !== oldTab) {
          const searchQuery = this.query || this.$route.params.query || this.$route.query.q;
          if (searchQuery) {
            let type = '';
            if (newTab === 'sets') type = 'flashcard';
            else if (newTab === 'questions') type = 'quiz';
            else if (newTab === 'users') type = 'user';
            
            if (type) {
              this.loadSearchResultsByType(searchQuery, type);
            }
          }
        }
        
        if (newTab === 'sets' && this.studySets.length > 0) {
          this.selectStudySet(this.paginatedList[0]);
        } else {
          this.selectedSet = null;
        }
      }
    },
    paginatedList: {
      deep: true,
      handler(newList) {
        if (this.activeTab === 'sets' && newList.length > 0 && !this.selectedSet) {
           this.selectStudySet(newList[0]);
        }
      }
    }
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/home/search-results/SearchResults.scss';
</style>