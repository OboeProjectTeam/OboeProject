<template>
  <div class="library">
    <div class="library__header">
      <h1>Thư viện của bạn</h1>
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
        <p>Tạo học liệu đầu tiên của bạn để bắt đầu học tập</p>
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
        <p>Tạo bài kiểm tra để đánh giá kiến thức của bạn</p>
        <router-link to="/create/quiz" class="create-btn">
          Tạo bài kiểm tra
        </router-link>
      </div>
    </div>

    <!-- Blog -->
    <div v-if="activeTab === 'blogs'" class="content-section">
      <div class="content-grid">
        <div v-for="blog in blogs" :key="blog.id" class="content-card">
          <div class="card-info">
            <h3>{{ blog.title }}</h3>
            <p class="card-meta">
              <span>{{ formatDate(blog.createdAt) }}</span>
            </p>
          </div>
          <div class="card-actions">
            <router-link :to="`/blog/${blog.id}`" class="action-btn primary">
              <i class="fas fa-eye"></i>
              Xem
            </router-link>
            <button @click="editBlog(blog.id)" class="action-btn">
              <i class="fas fa-edit"></i>
            </button>
            <button @click="deleteBlog(blog.id)" class="action-btn">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
      </div>
      <div v-if="!blogs.length" class="empty-state">
        <i class="fas fa-blog"></i>
        <h3>Chưa có bài viết nào</h3>
        <p>Chia sẻ kiến thức của bạn qua các bài viết</p>
        <router-link to="/create/blog" class="create-btn">
          Viết bài
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
            <div class="item-content">
              <strong>{{ word.term }}</strong>
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

        <!-- Ngữ pháp yêu thích -->
        <div v-if="activeFavoriteTab === 'grammar'" class="favorite-list">
          <div v-for="item in favorites.grammar" :key="item.id" class="favorite-item">
            <div class="item-content">
              <strong>{{ item.pattern }}</strong>
              <span>{{ item.explanation }}</span>
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
            <div class="item-content">
              <strong>{{ sentence.japanese }}</strong>
              <span>{{ sentence.meaning }}</span>
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

        <!-- Hán tự yêu thích -->
        <div v-if="activeFavoriteTab === 'kanji'" class="favorite-list">
          <div v-for="kanji in favorites.kanji" :key="kanji.id" class="favorite-item">
            <div class="item-content">
              <strong>{{ kanji.character }}</strong>
              <span>{{ kanji.meaning }}</span>
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
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { onMounted } from 'vue'

const store = useStore()
const router = useRouter()

// Thêm hàm để lấy dữ liệu
const fetchData = async () => {
  try {
    // Lấy dữ liệu từ store
    await store.dispatch('flashcard/fetchFlashcardSets')
    
    // Lấy dữ liệu quizzes
    const quizzesData = await store.dispatch('quiz/fetchQuizzes')
    quizzes.value = quizzesData || []
    
    // Lấy dữ liệu blogs
    const blogsData = await store.dispatch('blog/fetchBlogs')
    blogs.value = blogsData || []
    
    // Lấy dữ liệu favorites
    const favoritesData = await store.dispatch('user/fetchFavorites')
    if (favoritesData) {
      favorites.value = {
        vocabulary: favoritesData.vocabulary || [],
        grammar: favoritesData.grammar || [],
        sentences: favoritesData.sentences || [],
        kanji: favoritesData.kanji || []
      }
    }
  } catch (error) {
    console.error('Error fetching data:', error)
  }
}

// Gọi fetchData khi component được tạo
onMounted(() => {
  fetchData()
})

const searchQuery = ref('')
const activeTab = ref('study-sets')
const activeFavoriteTab = ref('vocabulary')
const showSortMenu = ref(false)

// Thêm các biến reactive cho quizzes, blogs và favorites
const quizzes = ref([])
const blogs = ref([])
const favorites = ref({
  vocabulary: [],
  grammar: [],
  sentences: [],
  kanji: []
})

const tabs = [
  { id: 'study-sets', name: 'Học liệu' },
  { id: 'quizzes', name: 'Bài kiểm tra' },
  { id: 'blogs', name: 'Blog' },
  { id: 'favorites', name: 'Mục yêu thích' }
]

const favoriteTabs = [
  { id: 'vocabulary', name: 'Từ vựng', icon: 'fas fa-book' },
  { id: 'grammar', name: 'Ngữ pháp', icon: 'fas fa-pen' },
  { id: 'sentences', name: 'Mẫu câu', icon: 'fas fa-comment-alt' },
  { id: 'kanji', name: 'Hán tự', icon: 'fas fa-language' }
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
  if (!confirm('Bạn có chắc chắn muốn xóa học liệu này?')) return
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
  if (!confirm('Bạn có chắc chắn muốn xóa bài kiểm tra này?')) return
  try {
    // await api.deleteQuiz(id)
    quizzes.value = quizzes.value.filter(quiz => quiz.id !== id)
  } catch (error) {
    console.error('Error deleting quiz:', error)
  }
}

const editBlog = (id) => {
  router.push(`/blog/${id}/edit`)
}

const deleteBlog = async (id) => {
  if (!confirm('Bạn có chắc chắn muốn xóa bài viết này?')) return
  try {
    // await api.deleteBlog(id)
    blogs.value = blogs.value.filter(blog => blog.id !== id)
  } catch (error) {
    console.error('Error deleting blog:', error)
  }
}

const removeFromFavorites = async (type, id) => {
  try {
    // await api.removeFromFavorites(type, id)
    favorites.value[type] = favorites.value[type].filter(item => item.id !== id)
  } catch (error) {
    console.error('Error removing from favorites:', error)
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
</script>

<style lang="scss" scoped>
.library {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.library__header {
  margin-bottom: 32px;

  h1 {
    color: #333;
    margin: 0 0 16px 0;
    font-size: 24px;
  }
}

.search-bar {
  position: relative;
  max-width: 600px;

  input {
    width: 100%;
    padding: 12px 16px;
    padding-right: 40px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 16px;
    transition: all 0.2s;

    &:focus {
      outline: none;
      border-color: #E94560;
      box-shadow: 0 0 0 3px rgba(233, 69, 96, 0.1);
    }
  }

  i {
    position: absolute;
    right: 16px;
    top: 50%;
    transform: translateY(-50%);
    color: #666;
  }
}

.library__tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  border-bottom: 1px solid #ddd;
}

.tab-btn {
  padding: 12px 24px;
  border: none;
  background: none;
  color: #666;
  font-weight: 600;
  cursor: pointer;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: -1px;
    left: 0;
    width: 100%;
    height: 2px;
    background: transparent;
    transition: all 0.2s;
  }

  &.active {
    color: #E94560;

    &::after {
      background: #E94560;
    }
  }

  &:hover {
    color: #E94560;
  }
}

.library__sort {
  margin-bottom: 24px;
  position: relative;
}

.sort-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;

  &:hover {
    border-color: #E94560;
    color: #E94560;
  }
}

.sort-menu {
  position: absolute;
  top: 100%;
  left: 0;
  margin-top: 4px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  z-index: 10;
}

.sort-option {
  width: 100%;
  padding: 8px 16px;
  border: none;
  background: none;
  text-align: left;
  cursor: pointer;
  white-space: nowrap;

  &:hover {
    background: #f8f9fa;
  }

  &.active {
    color: #E94560;
    background: #fff1f3;
  }
}

.content-section {
  min-height: 400px;
}

.content-grid {
  display: grid;
  gap: 16px;
}

.content-card {
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.2s;

  &:hover {
    border-color: #E94560;
    box-shadow: 0 2px 8px rgba(233, 69, 96, 0.1);
  }
}

.card-info {
  margin-bottom: 16px;

  h3 {
    margin: 0 0 8px 0;
    color: #333;
    font-size: 16px;
  }
}

.card-meta {
  display: flex;
  gap: 16px;
  color: #666;
  font-size: 14px;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;

  &:hover {
    background: #f8f9fa;
    border-color: #E94560;
    color: #E94560;
  }

  &.primary {
    background: #E94560;
    border-color: #E94560;
    color: white;

    &:hover {
      background: #d13651;
      border-color: #d13651;
    }
  }

  i {
    font-size: 14px;
  }
}

.empty-state {
  text-align: center;
  padding: 64px 20px;
  color: #666;

  i {
    font-size: 48px;
    color: #ddd;
    margin-bottom: 16px;
  }

  h3 {
    margin: 0 0 8px 0;
    color: #333;
  }

  p {
    margin: 0 0 24px 0;
  }
}

.create-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #E94560;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  transition: background-color 0.2s;

  &:hover {
    background: #d13651;
  }

  i {
    font-size: 14px;
  }
}

.favorites-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
}

.favorite-tab {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  transition: all 0.2s;

  &:hover {
    border-color: #E94560;
    color: #E94560;
  }

  &.active {
    background: #E94560;
    border-color: #E94560;
    color: white;
  }

  i {
    font-size: 14px;
  }
}

.favorite-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.favorite-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover {
    border-color: #E94560;
  }

  .item-content {
    display: flex;
    flex-direction: column;
    gap: 4px;

    strong {
      color: #333;
    }

    span {
      color: #666;
      font-size: 14px;
    }
  }
}

.remove-btn {
  padding: 6px;
  border: none;
  background: none;
  color: #666;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;

  &:hover {
    background-color: #ffe3e3;
    color: #e03131;
  }
}
</style> 