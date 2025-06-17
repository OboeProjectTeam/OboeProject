<template>
  <div class="library">
    <div class="library__header">
      <h1>Thư viện của tôi </h1>
      <div class="search-bar">
        <input type="text" v-model="searchQuery" placeholder="Tìm kiếm thẻ ghi nhớ" />
        <i class="fas fa-search"></i>
      </div>
    </div>

    <div class="library__tabs">
      <button 
        v-for="tab in tabs" 
        :key="tab.id"
        :class="['tab-btn', { active: activeTab === tab.id }]"
        @click="activeTab = tab.id"
      >
        {{ tab.name }}
      </button>
    </div>

    <div class="library__sort" v-if="activeTab === 'study-sets'">
      <button 
        class="sort-btn"
        @click="toggleSortMenu"
      >
        <i class="fas fa-sort"></i>
        {{ currentSort.label }}
      </button>
      <div class="sort-menu" v-if="showSortMenu">
        <button 
          v-for="option in sortOptions" 
          :key="option.value"
          :class="['sort-option', { active: currentSort.value === option.value }]"
          @click="selectSort(option)"
        >
          {{ option.label }}
        </button>
      </div>
    </div>

    <!-- Học liệu -->
    <div v-if="activeTab === 'study-sets'" class="content-section">
      <div class="content-grid">
        <div v-for="set in filteredStudySets" :key="set.id" class="content-card">
          <div class="card-info">
            <h3>{{ set.title }}</h3>
            <p class="card-meta">
              <span>{{ set.cardCount }} thuật ngữ</span>
              <span>{{ formatDate(set.updatedAt) }}</span>
            </p>
          </div>
          <div class="card-actions">
            <button @click="startLearning(set)" class="action-btn primary">
              <i class="fas fa-book"></i>
              Học ngay
            </button>
            <button @click="editSet(set.id)" class="action-btn">
              <i class="fas fa-edit"></i>
            </button>
            <button @click="deleteSet(set.id)" class="action-btn">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
      </div>
      <div v-if="!studySets.length" class="empty-state">
        <i class="fas fa-book-open"></i>
        <h3>Chưa có học liệu nào</h3>
        <p>Tạo học liệu đầu tiên của  để bắt đầu học tập</p>
        <router-link to="/create/flashcard" class="create-btn">
          Tạo học liệu
        </router-link>
      </div>
    </div>

    <!-- Bài kiểm tra -->
    <div v-if="activeTab === 'quizzes'" class="content-section">
      <div class="content-grid">
        <div v-for="quiz in quizzes" :key="quiz.id" class="content-card">
          <div class="card-info">
            <h3>{{ quiz.title }}</h3>
            <p class="card-meta">
              <span>{{ quiz.questionCount }} câu hỏi</span>
              <span>{{ formatDate(quiz.updatedAt) }}</span>
            </p>
          </div>
          <div class="card-actions">
            <router-link :to="`/quiz/${quiz.id}`" class="action-btn primary">
              <i class="fas fa-play"></i>
              Làm bài
            </router-link>
            <button @click="editQuiz(quiz.id)" class="action-btn">
              <i class="fas fa-edit"></i>
            </button>
            <button @click="deleteQuiz(quiz.id)" class="action-btn">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
      </div>
      <div v-if="!quizzes.length" class="empty-state">
        <i class="fas fa-question-circle"></i>
        <h3>Chưa có bài kiểm tra nào</h3>
        <p>Tạo bài kiểm tra để đánh giá kiến thức của </p>
        <router-link to="/create/quiz" class="create-btn">
          Tạo bài kiểm tra
        </router-link>
      </div>
    </div>
    <!-- Mục yêu thích -->
    <div v-if="activeTab === 'favorites'" class="content-section">
      <div class="favorites-tabs">
        <button 
          v-for="tab in favoriteTabs" 
          :key="tab.id"
          :class="['favorite-tab', { active: activeFavoriteTab === tab.id }]"
          @click="activeFavoriteTab = tab.id"
        >
          <i :class="tab.icon"></i>
          {{ tab.name }}
        </button>
      </div>

      <div class="favorites-content">
        <!-- Từ vựng yêu thích -->
        <div v-if="activeFavoriteTab === 'vocabulary'" class="favorite-list">
          <div v-for="word in favorites.vocabulary" :key="word.id" class="favorite-item">
            <div class="item-content" @click="goToDetail('vocabulary', word)">
              <strong>{{ word.kanji }}</strong>
              <span>{{ word.meaning }}</span>
            </div>
            <button @click="removeFromFavorites('vocabulary', word.id)" class="remove-btn">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div v-if="!favorites.vocabulary.length" class="empty-state">
            <i class="fas fa-book"></i>
            <h3>Chưa có từ vựng yêu thích</h3>
            <p>Đánh dấu từ vựng yêu thích để xem lại sau</p>
          </div>
        </div>
        <!-- Hán tự yêu thích -->
        <div v-if="activeFavoriteTab === 'kanji'" class="favorite-list">
          <div v-for="kanji in favorites.kanji" :key="kanji.id" class="favorite-item">
            <div class="item-content" @click="goToDetail('kanji', kanji)">
              <strong>{{ kanji.kanji }}</strong>
              <span>{{ kanji.kanjiname }}</span>
            </div>
            <button @click="removeFromFavorites('kanji', kanji.id)" class="remove-btn">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div v-if="!favorites.kanji.length" class="empty-state">
            <i class="fas fa-language fa-3x"></i>
            <h3>Chưa có hán tự yêu thích</h3>
            <p>Đánh dấu hán tự yêu thích để xem lại sau</p>
          </div>
        </div>
        <!-- Ngữ pháp yêu thích -->
        <div v-if="activeFavoriteTab === 'grammar'" class="favorite-list">
          <div v-for="item in favorites.grammar" :key="item.id" class="favorite-item">
            <div class="item-content" @click="goToDetail('grammar', item)">
              <strong>{{ item.kana }}</strong>
              <span>{{ item.meaning }}</span>
            </div>
            <button @click="removeFromFavorites('grammar', item.id)" class="remove-btn">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div v-if="!favorites.grammar.length" class="empty-state">
            <i class="fas fa-book"></i>
            <h3>Chưa có ngữ pháp yêu thích</h3>
            <p>Đánh dấu ngữ pháp yêu thích để xem lại sau</p>
          </div>
        </div>

        <!-- Mẫu câu yêu thích -->
        <div v-if="activeFavoriteTab === 'sentences'" class="favorite-list">
          <div v-for="sentence in favorites.sentences" :key="sentence.id" class="favorite-item">
            <div class="item-content" @click="goToDetail('sentences', sentence)">
              <strong>{{ sentence.sentence }}</strong>
              <span>{{ sentence.translation }}</span>
            </div>
            <button @click="removeFromFavorites('sentences', sentence.id)" class="remove-btn">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div v-if="!favorites.sentences.length" class="empty-state">
            <i class="fas fa-comment-alt"></i>
            <h3>Chưa có mẫu câu yêu thích</h3>
            <p>Đánh dấu mẫu câu yêu thích để xem lại sau</p>
          </div>
        </div>

        
      </div>
    </div>
    <!-- Lịch sử -->
    <div v-if="activeTab === 'history'" class="content-section">
      <div class="content-grid">
        <div v-for="item in history" :key="item.id" class="content-card">
          <div class="card-info">
            <h3>{{ item.title }}</h3>
            <p class="card-meta">
              <span>{{ item.description }}</span>
              <span>{{ formatDate(item.createdAt) }}</span>
            </p>
          </div>
          <div class="card-actions">
            <button @click="reviewTest(item.id)" class="action-btn primary">
              <i class="fas fa-search"></i>
              Xem lại
            </button>
            <button @click="deleteHistoryItem(item.id)" class="action-btn">
                <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
      </div>
      <div v-if="!history.length" class="empty-state">
        <i class="fas fa-history"></i>
        <h3>Chưa có lịch sử nào</h3>
        <p>Hoàn thành một bài kiểm tra để xem lại lịch sử tại đây.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { onMounted } from 'vue'

const store = useStore()
const router = useRouter()
const route = useRoute()

// Thêm hàm để lấy dữ liệu
const fetchData = async () => {
  try {
    // Lấy dữ liệu từ store
    await store.dispatch('flashcard/fetchFlashcardSets')
    
    // Lấy dữ liệu quizzes
    await store.dispatch('quiz/fetchQuizzes')
    
    // Lấy dữ liệu blogs
    const blogsData = await store.dispatch('blog/fetchBlogs')
    blogs.value = blogsData || []
    
    // Lấy dữ liệu history
    await store.dispatch('history/fetchHistory')
    
    // Lấy dữ liệu favorites
    await store.dispatch('user/fetchFavorites')
  } catch (error) {
    console.error('Error fetching data:', error)
  }
}

// Gọi fetchData khi component được tạo
onMounted(() => {
  // Set tabs based on route query first
  if (route.query.tab) {
    activeTab.value = route.query.tab;
    if (route.query.favoriteTab) {
      activeFavoriteTab.value = route.query.favoriteTab;
    }
  }
  fetchData()
})

const searchQuery = ref('')
const activeTab = ref('study-sets')
const activeFavoriteTab = ref('vocabulary')
const showSortMenu = ref(false)

// Thêm các biến reactive cho quizzes, blogs và favorites
const quizzes = computed(() => store.getters['quiz/getAllQuizzes'])
const blogs = ref([])
const favorites = computed(() => store.getters['user/getFavorites'] || { vocabulary: [], grammar: [], sentences: [], kanji: [] })
const history = computed(() => store.getters['history/getTestHistory'] || [])

const tabs = [
  { id: 'study-sets', name: 'Học liệu' },
  { id: 'quizzes', name: 'Bài kiểm tra' },
  { id: 'favorites', name: 'Mục yêu thích' },
  { id: 'history', name: 'Lịch sử' }
]

const favoriteTabs = [
  { id: 'vocabulary', name: 'Từ vựng', icon: 'fas fa-book' },
  { id: 'kanji', name: 'Hán tự', icon: 'fas fa-language' },
  { id: 'grammar', name: 'Ngữ pháp', icon: 'fas fa-pen' },
  { id: 'sentences', name: 'Mẫu câu', icon: 'fas fa-comment-alt' }
]

const sortOptions = [
  { value: 'recent', label: 'Gần đây nhất' },
  { value: 'oldest', label: 'Cũ nhất' },
  { value: 'name-asc', label: 'Tên A-Z' },
  { value: 'name-desc', label: 'Tên Z-A' }
]

const currentSort = ref(sortOptions[0])

// Lấy danh sách bộ thẻ từ store
const studySets = computed(() => store.getters['flashcard/getAllFlashcardSets'])

const filteredStudySets = computed(() => {
  let filtered = [...studySets.value]
  
  // Áp dụng tìm kiếm
  if (searchQuery.value) {
    filtered = filtered.filter(set => 
      set.title.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  
  // Áp dụng sắp xếp
  switch (currentSort.value.value) {
    case 'recent':
      filtered.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
      break
    case 'oldest':
      filtered.sort((a, b) => new Date(a.updatedAt) - new Date(b.updatedAt))
      break
    case 'name-asc':
      filtered.sort((a, b) => a.title.localeCompare(b.title))
      break
    case 'name-desc':
      filtered.sort((a, b) => b.title.localeCompare(a.title))
      break
  }
  
  return filtered
})

const toggleSortMenu = () => {
  showSortMenu.value = !showSortMenu.value
}

const selectSort = (option) => {
  currentSort.value = option
  showSortMenu.value = false
}

const editSet = (id) => {
  router.push(`/flashcards/${id}/edit`)
}

const deleteSet = async (id) => {
  if (!confirm(' có chắc chắn muốn xóa học liệu này?')) return
  try {
    await store.dispatch('flashcard/deleteFlashcardSet', id)
  } catch (error) {
    console.error('Error deleting study set:', error)
  }
}

const editQuiz = (id) => {
  router.push(`/quiz/${id}/edit`)
}

const deleteQuiz = async (id) => {
  if (!confirm(' có chắc chắn muốn xóa bài kiểm tra này?')) return
  try {
    await store.dispatch('quiz/deleteQuiz', id)
  } catch (error) {
    console.error('Error deleting quiz:', error)
  }
}

const editBlog = (id) => {
  router.push(`/blog/${id}/edit`)
}

const deleteBlog = async (id) => {
  if (!confirm(' có chắc chắn muốn xóa bài viết này?')) return
  try {
    // await api.deleteBlog(id)
    blogs.value = blogs.value.filter(blog => blog.id !== id)
  } catch (error) {
    console.error('Error deleting blog:', error)
  }
}

const removeFromFavorites = async (type, id) => {
  try {
    await store.dispatch('user/removeFromFavorites', { type, id });
  } catch (error) {
    console.error('Error removing from favorites:', error);
  }
}

const formatDate = (timestamp) => {
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }).format(new Date(timestamp))
}

const startLearning = async (set) => {
  try {
    console.log('Original set:', set)
    
    // Chuyển đổi cards thành format phù hợp cho learning items
    const learningItems = set.cards.map(card => ({
      type: 'word',
      kanji: card.front,
      kana: '',
      meaning: card.back
    }))
    
    console.log('Converted learning items:', learningItems)
    
    // Lưu vào store
    await store.dispatch('flashcard/setLearningItems', learningItems)
    
    // Chuyển đến trang học
    router.push('/flashcard/learn')
  } catch (error) {
    console.error('Error starting learning:', error)
  }
}

const deleteHistoryItem = async (id) => {
  if (!confirm('Bạn có chắc chắn muốn xóa mục lịch sử này không?')) return;
  try {
    await store.dispatch('history/deleteTestFromHistory', id);
  } catch (error) {
    console.error('Error deleting history item:', error);
  }
};

const reviewTest = (id) => {
    router.push({ name: 'FlashcardTest', query: { historyId: id } });
}

const goToDetail = (type, item) => {
  let routeName = '';
  switch (type) {
    case 'vocabulary':
      routeName = 'WordDetail';
      break;
    case 'kanji':
      routeName = 'KanjiDetail';
      break;
    case 'grammar':
      routeName = 'GrammarDetail';
      break;
    case 'sentences':
      routeName = 'SentenceDetail';
      break;
  }

  if (routeName && item.id) {
    router.push({
      name: routeName,
      params: { id: item.id },
      query: { from: 'library', favoriteTab: activeFavoriteTab.value }
    });
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/self/my-library/MyLibrary.scss';
</style> 