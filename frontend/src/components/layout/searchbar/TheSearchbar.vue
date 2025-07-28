<template>
  <div class="search-bar" ref="searchBarRef">
    <input
      type="text"
      v-model="searchQuery"
      @input="onSearch"
      @click="clearSearch"
      :placeholder="placeholder"
    />
    <ul v-if="searchResults.length && showSuggestions" class="suggestions">
      <li v-if="isLoading" class="suggestion-item loading">
        <i class="fas fa-spinner fa-spin"></i>
        Đang tìm kiếm...
      </li>
      <li
        v-else
        v-for="(item, index) in searchResults"
        :key="index"
        class="suggestion-item"
        @click.stop="selectSuggestion(item)"
      >
        <div class="word-display">
          <strong>{{ item.word }}</strong>
          <span v-if="item.reading" class="reading">{{ item.reading }}</span>
        </div>
        <div class="meaning">{{ item.meaning }}</div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import api from '@/api'

const props = defineProps({
  placeholder: {
    type: String,
    default: 'Tìm kiếm...'
  }
})

const router = useRouter()
const emit = defineEmits(['search'])
const searchQuery = ref('')
const showSuggestions = ref(false)
const searchBarRef = ref(null)
const searchResults = ref([])
const isLoading = ref(false)
let searchTimeout = null

const store = useStore()
const activeIndex = computed(() => store.getters['header/activeIndex'])

const isWord = computed(() => activeIndex.value === 0)
const isKanji = computed(() => activeIndex.value === 1)
const isGrammar = computed(() => activeIndex.value === 2)
const isSentence = computed(() => activeIndex.value === 3)

// Map activeIndex to API search type
const getSearchType = () => {
  switch (activeIndex.value) {
    case 0: return 'vocabulary'  // Từ Vựng
    case 1: return 'kanji'       // Hán Tự
    case 2: return 'grammar'     // Ngữ Pháp
    case 3: return 'sentence'    // Mẫu câu
    default: return 'vocabulary'
  }
}

const handleClickOutside = (event) => {
  if (searchBarRef.value && searchBarRef.value.contains(event.target)) {
    return
  }
  
  showSuggestions.value = false
  searchQuery.value = ''
  emit('search', '')
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }
})

const getMeaning = (item) => {
  return item.meaning || ''
}

const performSearch = async (query) => {
  if (!query.trim()) {
    searchResults.value = []
    showSuggestions.value = false
    return
  }

  try {
    isLoading.value = true
    const searchType = getSearchType()
    
    try {
      // Try to use real API first
      const response = await api.search.search(query, searchType)
      const results = Array.isArray(response) ? response : (response.content || response.data || [])
      searchResults.value = results.slice(0, 10)
    } catch (error) {
      console.warn('API not available, using mock data:', error)
      
      searchResults.value = results.filter(item => 
        item.word.toLowerCase().includes(query.toLowerCase()) ||
        item.reading.toLowerCase().includes(query.toLowerCase()) ||
        item.meaning.toLowerCase().includes(query.toLowerCase())
      ).slice(0, 10)
    }
    
    showSuggestions.value = true
    
  } catch (error) {
    console.error('Search error:', error)
    searchResults.value = []
    showSuggestions.value = false
  } finally {
    isLoading.value = false
  }
}

const onSearch = () => {
  // Clear previous timeout
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }
  
  // Debounce search to avoid too many API calls
  searchTimeout = setTimeout(() => {
    performSearch(searchQuery.value)
  }, 300)
  
  emit('search', searchQuery.value)
}

const clearSearch = () => {
  searchQuery.value = ''
  searchResults.value = []
  showSuggestions.value = false
}

const selectSuggestion = (item) => {
  try {
    // Set search query to the word from API response
    searchQuery.value = item.word || ''
    
    showSuggestions.value = false
    emit('search', searchQuery.value)
    
    // Navigate to detail page based on item type and correct ID field
    navigateToDetail(item)
  } catch (error) {
    console.error('Error in selectSuggestion:', error)
  }
}

const navigateToDetail = (item) => {
  console.log('Navigating to detail for item:', item) // Debug log
  
  try {
    let itemId;
    let routePath;
    
    // Get correct ID field based on item type
    switch (item.type) {
      case 'vocabulary':
        itemId = item.vocalbId || item.id || item.wordId
        routePath = '/word'
        break
      case 'kanji':
        itemId = item.kanjiId || item.id || item.characterId
        routePath = '/kanji'
        break
      case 'grammar':
        itemId = item.grammarId || item.id || item.structureId
        routePath = '/grammar'
        break
      case 'sentence':
        itemId = item.id || item.sentenceId
        routePath = '/sentence'
        break
      default:
        // Fallback to activeIndex mapping if type is not available
        if (isWord.value) {
          itemId = item.vocalbId || item.id || item.wordId || 'vocab-1'
          routePath = '/word'
        } else if (isKanji.value) {
          itemId = item.kanjiId || item.id || item.characterId || 'kanji-1'
          routePath = '/kanji'
        } else if (isGrammar.value) {
          itemId = item.grammarId || item.id || item.structureId || 'grammar-1'
          routePath = '/grammar'
        } else if (isSentence.value) {
          itemId = item.id || item.sentenceId || 'sentence-1'
          routePath = '/sentence'
        }
        break
    }
    
    // If still no itemId, generate one based on type and word
    if (!itemId) {
      const searchType = getSearchType()
      switch (searchType) {
        case 'vocabulary':
          itemId = 'vocab-1'
          routePath = '/word'
          break
        case 'kanji':
          itemId = 'kanji-1'
          routePath = '/kanji'
          break
        case 'grammar':
          itemId = 'grammar-1'
          routePath = '/grammar'
          break
        case 'sentence':
          itemId = 'sentence-1'
          routePath = '/sentence'
          break
        default:
          itemId = 'vocab-1'
          routePath = '/word'
      }
    }
    
    console.log('Navigation details:', { itemId, routePath, item }) // Debug log
    
    if (itemId && routePath) {
      const fullPath = `${routePath}/${itemId}`
      console.log('Navigating to:', fullPath) // Debug log
      router.push(fullPath)
    } else {
      console.error('Cannot navigate: missing itemId or routePath', { itemId, routePath, item })
    }
  } catch (error) {
    console.error('Error in navigateToDetail:', error)
  }
}
</script>

<style lang="scss" scoped>
@use '@/components/layout/searchbar/Searchbar.scss';
</style>