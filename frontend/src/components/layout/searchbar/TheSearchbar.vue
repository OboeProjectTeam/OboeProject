<template>
  <div class="search-bar" ref="searchBarRef">
    <input
      type="text"
      v-model="searchQuery"
      @input="onSearch"
      @click="clearSearch"
      :placeholder="computedPlaceholder"
    />
    <ul v-if="searchResults.length && showSuggestions" class="suggestions">
      <li v-if="isLoading" class="suggestion-item loading">
        <i class="fas fa-spinner fa-spin"></i>
        {{ t('common.searching') }}
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
import { useI18n } from 'vue-i18n'
import api from '@/api'

const { t } = useI18n()

const props = defineProps({
  placeholder: {
    type: String,
    default: ''
  }
})

const computedPlaceholder = computed(() => {
  return props.placeholder || t('search.placeholder')
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
    case 0: return 'vocabulary'  // Vocabulary
    case 1: return 'kanji'       // Kanji
    case 2: return 'grammar'     // Grammar
    case 3: return 'sentence'    // Sentences
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
    
    // Use real API
    const response = await api.search.search(query, searchType)
    // API trả về array của objects với structure: {id, type, word, reading, meaning}
    const results = Array.isArray(response) ? response : (response.content || response.data || [])
    
    searchResults.value = results.slice(0, 10)
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
  try {
    // API mới trả về cấu trúc chuẩn: {id, type, word, reading, meaning}
    const itemId = item.id
    if (!itemId) {
      return
    }
    
    // Map type to route path (theo router configuration)
    let routeType
    switch (item.type) {
      case 'vocabulary':
        routeType = 'word'  // Route: /word/:id
        break
      case 'kanji':
        routeType = 'kanji'  // Route: /kanji/:id
        break
      case 'grammar':
        routeType = 'grammar'  // Route: /grammar/:id
        break
      case 'sentence':
        routeType = 'sentence'  // Route: /sentence/:id
        break
      default:
        console.error('Unknown item type:', item.type)
        return
    }
    
    const fullPath = `/${routeType}/${itemId}`
    router.push(fullPath)
      .then(() => {
        console.log('Navigation successful to:', fullPath)
      })
      .catch((error) => {
        console.error('Navigation failed:', error)
      })
      
  } catch (error) {
    console.error('Error in navigateToDetail:', error)
  }
}
</script>

<style lang="scss" scoped>
@use '@/components/layout/searchbar/Searchbar.scss';
</style>