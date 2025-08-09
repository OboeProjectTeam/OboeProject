<template>
  <div class="library">
    <div class="library__header">
      <h1>{{ t('library.myLibrary') }}</h1>
    </div>

    <div class="library__tabs">
      <button v-for="tab in tabs" :key="tab.id" :class="['tab-btn', { active: activeTab === tab.id }]"
        @click="activeTab = tab.id">
        {{ t(tab.nameKey) }}
      </button>
    </div>

    <div class="library__sort" v-if="activeTab === 'study-sets'">
      <button class="sort-btn" @click="toggleSortMenu">
        <i class="fas fa-sort"></i>
        {{ currentSort.label }}
      </button>
      <div class="sort-menu" v-if="showSortMenu">
        <button v-for="option in sortOptions" :key="option.value"
          :class="['sort-option', { active: currentSort.value === option.value }]" @click="selectSort(option)">
          {{ option.label }}
        </button>
      </div>
    </div>

    <!-- Học liệu -->
    <div v-if="activeTab === 'study-sets'" class="content-section">
      <div v-if="studySetsLoading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        <p>{{ t('library.loadingStudySets') }}</p>
      </div>
      <div v-else class="content-grid">
        <div v-for="set in filteredStudySets" :key="set.id" class="content-card">
          <div class="card-info">
            <h3>{{ set.title }}</h3>
            <p v-if="set.description" class="card-description">{{ set.description }}</p>
            <p class="card-meta">
              <span>{{ set.cardCount }} {{ t('home.terms') }}</span>
              <span>{{ formatDate(set.updatedAt) }}</span>
            </p>
          </div>
          <div class="card-actions">
            <button @click="startLearning(set)" class="action-btn primary">
              <i class="fas fa-book"></i>
              {{ t('home.learnNow') }}
            </button>
            <button @click="deleteSet(set.id)" class="action-btn">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
        <div v-if="!studySets.length && !studySetsLoading" class="empty-state">
          <i class="fas fa-book-open"></i>
          <h3>{{ t('library.noStudySets') }}</h3>
          <p>{{ t('library.noStudySetsMessage') }}</p>
          <router-link to="/create/flashcard" class="create-btn">
            {{ t('library.createStudySet') }}
          </router-link>
        </div>
      </div>
    </div>

    <!-- Bài kiểm tra -->
    <div v-if="activeTab === 'quizzes'" class="content-section">
      <div v-if="quizzesLoading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        <p>{{ t('library.loadingQuizzes') }}</p>
      </div>
      <div v-else class="content-grid">
        <div v-for="quiz in quizzes" :key="quiz.id" class="content-card">
          <div class="card-info">
            <h3>{{ quiz.title }}</h3>
            <p class="card-description">{{ quiz.description }}</p>
            <p class="card-meta">
              <span>{{ formatDate(quiz.updatedAt) }}</span>
            </p>
          </div>
          <div class="card-actions">
            <button @click="startQuiz(quiz)" class="action-btn primary">
              <i class="fas fa-play"></i>
              {{ t('home.takeQuiz') }}
            </button>
            <button @click="deleteQuiz(quiz.id)" class="action-btn">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
        <div v-if="!quizzes.length && !quizzesLoading" class="empty-state">
          <i class="fas fa-question-circle"></i>
          <h3>{{ t('library.noQuizzes') }}</h3>
          <p>{{ t('library.noQuizzesMessage') }}</p>
          <router-link to="/create/quiz" class="create-btn">
            {{ t('library.createQuiz') }}
          </router-link>
        </div>
      </div>
    </div>
    <!-- Mục yêu thích -->
    <div v-if="activeTab === 'favorites'" class="content-section">
      <div v-if="favoritesLoading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        <p>{{ t('library.loadingFavorites') }}</p>
      </div>
      <div v-else>
        <div class="favorites-tabs">
          <button v-for="tab in favoriteTabs" :key="tab.id"
            :class="['favorite-tab', { active: activeFavoriteTab === tab.id }]" @click="activeFavoriteTab = tab.id">
            <i :class="tab.icon"></i>
            {{ t(tab.nameKey) }}
          </button>
        </div>

        <div class="favorites-content">
          <!-- Từ vựng yêu thích -->
          <div v-if="activeFavoriteTab === 'vocabulary'" class="favorite-list">
            <div v-for="word in favorites.vocabulary" :key="word.id" class="favorite-item">
              <div class="item-content" @click="goToDetail('vocabulary', word)">
                <strong>{{ word.title }}</strong>
                <span>{{ word.content }}</span>
              </div>
              <button @click="removeFromFavorites('vocabulary', word.id)" class="remove-btn">
                <i class="fas fa-times"></i>
              </button>
            </div>
            <div v-if="!favorites.vocabulary.length" class="empty-state">
              <i class="fas fa-book"></i>
              <h3>{{ t('library.noFavoriteVocabulary') }}</h3>
              <p>{{ t('library.markFavoriteVocabulary') }}</p>
            </div>
          </div>
          <!-- Hán tự yêu thích -->
          <div v-if="activeFavoriteTab === 'kanji'" class="favorite-list">
            <div v-for="kanji in favorites.kanji" :key="kanji.id" class="favorite-item">
              <div class="item-content" @click="goToDetail('kanji', kanji)">
                <strong>{{ kanji.title }}</strong>
                <span>{{ kanji.content }}</span>
              </div>
              <button @click="removeFromFavorites('kanji', kanji.id)" class="remove-btn">
                <i class="fas fa-times"></i>
              </button>
            </div>
            <div v-if="!favorites.kanji.length" class="empty-state">
              <i class="fas fa-language fa-3x"></i>
              <h3>{{ t('library.noFavoriteKanji') }}</h3>
              <p>{{ t('library.markFavoriteKanji') }}</p>
            </div>
          </div>
          <!-- Ngữ pháp yêu thích -->
          <div v-if="activeFavoriteTab === 'grammar'" class="favorite-list">
            <div v-for="item in favorites.grammar" :key="item.id" class="favorite-item">
              <div class="item-content" @click="goToDetail('grammar', item)">
                <strong>{{ item.title }}</strong>
                <span>{{ item.content }}</span>
              </div>
              <button @click="removeFromFavorites('grammar', item.id)" class="remove-btn">
                <i class="fas fa-times"></i>
              </button>
            </div>
            <div v-if="!favorites.grammar.length" class="empty-state">
              <i class="fas fa-book"></i>
              <h3>{{ t('library.noFavoriteGrammar') }}</h3>
              <p>{{ t('library.markFavoriteGrammar') }}</p>
            </div>
          </div>

          <!-- Mẫu câu yêu thích -->
          <div v-if="activeFavoriteTab === 'sentences'" class="favorite-list">
            <div v-for="sentence in favorites.sentences" :key="sentence.id" class="favorite-item">
              <div class="item-content" @click="goToDetail('sentences', sentence)">
                <strong>{{ sentence.title }}</strong>
                <span>{{ sentence.content }}</span>
              </div>
              <button @click="removeFromFavorites('sentences', sentence.id)" class="remove-btn">
                <i class="fas fa-times"></i>
              </button>
            </div>
            <div v-if="!favorites.sentences.length" class="empty-state">
              <i class="fas fa-comment-alt"></i>
              <h3>{{ t('library.noFavoriteSentences') }}</h3>
              <p>{{ t('library.markFavoriteSentences') }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete confirmation popup -->
    <ThePopup v-if="showDeletePopup" :title="t('library.confirmDelete')"
      :message="deleteType === 'flashcard' ? t('library.deleteStudySetMessage', { title: deleteTitle }) : t('library.deleteQuizMessage', { title: deleteTitle })"
      :confirm-text="t('common.delete')" :show-cancel="true" @confirm="handleDeleteConfirm" @cancel="handleDeleteCancel" />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import api from '@/api'
import ThePopup from '@/components/common/popup/ThePopup.vue'

const store = useStore()
const router = useRouter()
const route = useRoute()
const { t } = useI18n()

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

    // Lấy dữ liệu favorites
    await store.dispatch('user/fetchFavorites')
  } catch (error) {
    console.error('Error fetching data:', error)
  }
}

const searchQuery = ref('')
const activeTab = ref('study-sets')
const activeFavoriteTab = ref('vocabulary')
const showSortMenu = ref(false)
const studySetsData = ref([])
const studySetsLoading = ref(false)
const quizzesData = ref([])
const quizzesLoading = ref(false)
const favoritesData = ref({ vocabulary: [], grammar: [], sentences: [], kanji: [] })
const favoritesLoading = ref(false)

// Popup state
const showDeletePopup = ref(false)
const deleteType = ref('') // 'flashcard' or 'quiz'
const deleteId = ref('')
const deleteTitle = ref('')

// Gọi fetchData khi component được tạo
onMounted(() => {
  // Set tabs based on route query first
  if (route.query.tab) {
    activeTab.value = route.query.tab;
    if (route.query.favoriteTab) {
      activeFavoriteTab.value = route.query.favoriteTab;
    }
  }

  // Load data based on active tab
  if (activeTab.value === 'study-sets') {
    loadUserFlashcards()
  } else if (activeTab.value === 'quizzes') {
    loadUserQuizzes()
  } else if (activeTab.value === 'favorites') {
    loadUserFavorites()
  }

  fetchData()
})

// Watch activeTab để load data khi tab thay đổi
watch(activeTab, (newTab) => {
  if (newTab === 'study-sets') {
    loadUserFlashcards()
  } else if (newTab === 'quizzes') {
    loadUserQuizzes()
  } else if (newTab === 'favorites') {
    loadUserFavorites()
  }
}, { immediate: false })

// Thêm các biến reactive cho quizzes, blogs và favorites
const quizzes = computed(() => quizzesData.value)
const blogs = ref([])
const favorites = computed(() => favoritesData.value)
const history = computed(() => store.getters['history/getTestHistory'] || [])

const tabs = [
  { id: 'study-sets', name: 'Học liệu', nameKey: 'library.studySets' },
  { id: 'quizzes', name: 'Bài kiểm tra', nameKey: 'library.quizzes' },
  { id: 'favorites', name: 'Mục yêu thích', nameKey: 'library.favorites' },
]

const favoriteTabs = [
  { id: 'vocabulary', name: 'Từ vựng', nameKey: 'library.vocabulary', icon: 'fas fa-book' },
  { id: 'kanji', name: 'Hán tự', nameKey: 'library.kanji', icon: 'fas fa-language' },
  { id: 'grammar', name: 'Ngữ pháp', nameKey: 'library.grammar', icon: 'fas fa-pen' },
  { id: 'sentences', name: 'Mẫu câu', nameKey: 'library.sentences', icon: 'fas fa-comment-alt' }
]

const sortOptions = computed(() => [
  { value: 'recent', label: t('library.sortRecent') },
  { value: 'oldest', label: t('library.sortOldest') },
  { value: 'name-asc', label: t('library.sortNameAsc') },
  { value: 'name-desc', label: t('library.sortNameDesc') }
])

const currentSort = ref({ value: 'recent', label: t('library.sortRecent') })

// Function để load flashcards từ API
const loadUserFlashcards = async () => {
  try {
    studySetsLoading.value = true
    const response = await api.flashcard.getUserFlashcards()

    // Handle different response formats
    const flashcards = response.content || response.data || response


    // Map flashcards to study sets format
    const mappedSets = (Array.isArray(flashcards) ? flashcards : []).map(flashcard => ({
      id: flashcard.set_id,
      title: flashcard.term || 'Flashcard', // Use term as title
      description: flashcard.description || '', // Add description field
      cardCount: flashcard.cardItems?.length || 0,
      updatedAt: flashcard.created,
      createdAt: flashcard.created, // Add created timestamp
      creator: {
        name: flashcard.user?.userName || flashcard.user?.firstName || t('common.user'),
        avatar: flashcard.user?.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(flashcard.user?.userName || 'User')}`
      },
      cards: flashcard.cardItems?.map(item => ({
        front: item.word,
        back: item.meaning
      })) || []
    }))


    studySetsData.value = mappedSets
  } catch (error) {
    studySetsData.value = []
  } finally {
    studySetsLoading.value = false
  }
}

// Function để load quizzes từ API
const loadUserQuizzes = async () => {
  try {
    quizzesLoading.value = true
    const response = await api.quiz.getUserQuizzes()
    // Handle different response formats
    const quizzes = response.content || response.data || response

    // Map quizzes to expected format với safe date handling
    const mappedQuizzes = (Array.isArray(quizzes) ? quizzes : []).map(quiz => ({
      id: quiz.quizzesID || quiz.id,
      title: quiz.title || 'Quiz',
      questionCount: quiz.questionCount || 0,
      updatedAt: quiz.updatedAt || quiz.createdAt || new Date().toISOString(),
      description: quiz.description || ''
    }))

    quizzesData.value = mappedQuizzes
  } catch (error) {
    quizzesData.value = []
  } finally {
    quizzesLoading.value = false
  }
}

// Function để load favorites từ API
const loadUserFavorites = async () => {
  try {
    favoritesLoading.value = true

    // Gọi API cho từng loại yêu thích
    const [vocabularyRes, kanjiRes, grammarRes, sentencesRes] = await Promise.all([
      api.favorite.getUserFavorites('vocabulary'),
      api.favorite.getUserFavorites('kanji'),
      api.favorite.getUserFavorites('grammar'),
      api.favorite.getUserFavorites('samplesentence')
    ])

    // Map dữ liệu từ API response
    favoritesData.value = {
      vocabulary: (vocabularyRes.data || vocabularyRes || []).map(item => ({
        id: item.vocabularyId,
        title: item.title,
        content: item.content,
        favoritesAt: item.favoritesAt,
        type: 'vocabulary'
      })),
      kanji: (kanjiRes.data || kanjiRes || []).map(item => ({
        id: item.kanjiId,
        title: item.title,
        content: item.content,
        favoritesAt: item.favoritesAt,
        type: 'kanji'
      })),
      grammar: (grammarRes.data || grammarRes || []).map(item => ({
        id: item.grammaId,
        title: item.title,
        content: item.content,
        favoritesAt: item.favoritesAt,
        type: 'grammar'
      })),
      sentences: (sentencesRes.data || sentencesRes || []).map(item => ({
        id: item.sampleSentenceId,
        title: item.title,
        content: item.content,
        favoritesAt: item.favoritesAt,
        type: 'sentence'
      }))
    }
  } catch (error) {
    console.error('Error loading favorites:', error)
    favoritesData.value = { vocabulary: [], grammar: [], sentences: [], kanji: [] }
  } finally {
    favoritesLoading.value = false
  }
}

// Lấy danh sách bộ thẻ từ API thay vì store
const studySets = computed(() => studySetsData.value)

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
const deleteSet = (id) => {
  // Find the set title for confirmation
  const set = studySetsData.value.find(s => s.id === id)
  deleteType.value = 'flashcard'
  deleteId.value = id
  deleteTitle.value = set?.title || t('library.thisStudySet')
  showDeletePopup.value = true
}


const deleteQuiz = (id) => {
  // Find the quiz title for confirmation
  const quiz = quizzesData.value.find(q => q.id === id)
  deleteType.value = 'quiz'
  deleteId.value = id
  deleteTitle.value = quiz?.title || t('library.thisQuiz')
  showDeletePopup.value = true
}

// Handle popup confirmation
const handleDeleteConfirm = async () => {
  showDeletePopup.value = false

  try {
    if (deleteType.value === 'flashcard') {
      // Call API to delete flashcard
      await api.flashcard.delete(deleteId.value)

      // Remove from local data
      studySetsData.value = studySetsData.value.filter(set => set.id !== deleteId.value)

      // Show success message
      store.dispatch('message/showMessage', {
        type: 'success',
        text: t('library.deleteStudySetSuccess')
      })
    } else if (deleteType.value === 'quiz') {
      // Call API to delete quiz
      await api.quiz.delete(deleteId.value)

      // Remove from local data
      quizzesData.value = quizzesData.value.filter(quiz => quiz.id !== deleteId.value)

      // Show success message
      store.dispatch('message/showMessage', {
        type: 'success',
        text: t('library.deleteQuizSuccess')
      })
    }
  } catch (error) {
    // Show error message
    const itemType = deleteType.value === 'flashcard' ? t('library.studySet') : t('library.quiz')
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('library.deleteError', { itemType, error: error.message })
    })
  }

  // Reset popup state
  deleteType.value = ''
  deleteId.value = ''
  deleteTitle.value = ''
}

// Handle popup cancel
const handleDeleteCancel = () => {
  showDeletePopup.value = false
  deleteType.value = ''
  deleteId.value = ''
  deleteTitle.value = ''
}

const editBlog = (id) => {
  router.push(`/blog/${id}/edit`)
}

const deleteBlog = async (id) => {
  if (!confirm(t('library.confirmDeleteBlog'))) return
  try {
    // await api.deleteBlog(id)
    blogs.value = blogs.value.filter(blog => blog.id !== id)
  } catch (error) {
    console.error('Error deleting blog:', error)
  }
}

const removeFromFavorites = async (type, id) => {
  try {
    // Use the new toggleFavorite API
    await store.dispatch('user/toggleFavorite', {
      type: type === 'sentences' ? 'sentence' : type, // Convert 'sentences' to 'sentence' for API
      itemId: id
    });

    // Reload favorites data to ensure UI is up to date
    await loadUserFavorites();

    // Show success message
    store.dispatch('message/showMessage', {
      type: 'success',
      text: t('library.removedFromFavorites')
    });
  } catch (error) {
    console.error('Error removing from favorites:', error);
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('library.removeFavoriteError', { error: error.message })
    });
  }
}

const formatDate = (timestamp) => {
  if (!timestamp) return 'N/A'

  const date = new Date(timestamp)
  if (isNaN(date.getTime())) {
    return 'N/A'
  }

  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }).format(date)
}

const startLearning = async (set) => {
  try {
    // Chuyển đổi cards thành format phù hợp cho learning items
    const learningItems = set.cards.map(card => ({
      type: 'word',
      kanji: card.front,
      kana: '',
      meaning: card.back
    }))

    // Lưu vào store
    await store.dispatch('flashcard/setLearningItems', learningItems)

    // Chuyển đến trang học với thông tin flashcard set
    router.push({
      path: '/flashcard/learn',
      query: {
        source: 'library',
        title: set.title,
        description: set.description || t('library.studySetDescription', { count: set.cardCount }),
        setId: set.id,
        creatorName: set.creator?.name || t('common.user'),
        creatorAvatar: set.creator?.avatar || '',
        createdAt: set.createdAt || ''
      }
    })
  } catch (error) {
    console.error('Error starting learning:', error)
  }
}

const startQuiz = async (quiz) => {
  try {
    const response = await api.question.getByQuiz(quiz.id)
    const questions = response.content || response.data || response
    if (!Array.isArray(questions) || questions.length === 0) {
      store.dispatch('message/showMessage', {
        type: 'error',
        text: t('library.quizNoQuestions')
      })
      return
    }

    // Map đúng trường từ API
    const learningItems = questions.map(q => ({
      type: 'question',
      id: q.questionID,
      front: q.questionName,
      back: q.correctAnswer,
      options: q.options,
      content: q.questionName,
      backcontent: q.correctAnswer
    }))
    await store.dispatch('flashcard/setLearningItems', learningItems)
    router.push({
      path: '/flashcard/test',
      query: {
        type: 'multiple-choice',
        source: 'library',
        title: quiz.title,
        description: quiz.description || t('library.quizDescription', { count: questions.length }),
        quizId: quiz.id
      }
    })
  } catch (error) {
    store.dispatch('message/showMessage', {
      type: 'error',
      text: t('library.loadQuizError', { error: error.message })
    })
  }
}

const deleteHistoryItem = async (id) => {
  if (!confirm(t('library.confirmDeleteHistory'))) return;
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

<script>
export default {
  components: {
    ThePopup
  }
}
</script>

<style lang="scss" scoped>
@use '@/views/self/my-library/MyLibrary.scss';
</style>