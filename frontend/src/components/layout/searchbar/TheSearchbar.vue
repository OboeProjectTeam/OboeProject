<template>
  <div class="search-bar" ref="searchBarRef">
    <input
      type="text"
      v-model="searchQuery"
      @input="onSearch"
      @click="clearSearch"
      :placeholder="placeholder"
    />
    <ul v-if="filteredSuggestions.length && showSuggestions" class="suggestions">
      <li
        v-for="(item, index) in filteredSuggestions"
        :key="index"
        class="suggestion-item"
        @click.stop="selectSuggestion(item)"
      >
        <div v-if="isWord">
          <strong>{{ item.kanji }}</strong>
          <span class="kana">{{ item.kana }}</span>
        </div>
        <div v-else-if="isSentence">
          <span>{{ item.sentence }}</span>
        </div>
        <div v-else-if="isKanji">
          <strong>{{ item.kanji }}</strong>
          <span class="kana">{{ item.kunyomi }}</span>
        </div>
        <div v-else-if="isGrammar">
          <strong>{{ item.kana }}</strong>
        </div>
        <div class="meaning">{{ getMeaning(item) }}</div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

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

const store = useStore()
const activeIndex = computed(() => store.getters['header/activeIndex'])

const wordList = computed(() => store.getters['search/wordList'] || [])
const sentenceList = computed(() => store.state.search.sentenceList || [])
const kanjiList = computed(() => store.state.search.KanjiList || [])
const grammarList = computed(() => store.state.search.grammar || [])

const isWord = computed(() => activeIndex.value === 0)
const isKanji = computed(() => activeIndex.value === 1)
const isGrammar = computed(() => activeIndex.value === 2)
const isSentence = computed(() => activeIndex.value === 3)

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
})

const getMeaning = (item) => {
  if (isWord.value || isGrammar.value) return item.meaning
  if (isSentence.value) return item.translation
  if (isKanji.value) return `${item.reading}`
  return ''
}

const filteredSuggestions = computed(() => {
  const query = searchQuery.value.trim().toLowerCase()
  if (!query) return []

  if (isWord.value) {
    return wordList.value.filter(
      (word) =>
        word.kana.includes(query) ||
        word.kanji.includes(query) ||
        word.romaji.toLowerCase().includes(query) ||
        word.meaning.toLowerCase().includes(query)
    )
  } else if (isSentence.value) {
    return sentenceList.value.filter(
      (sentence) =>
        sentence.sentence.includes(query) ||
        sentence.translation.toLowerCase().includes(query)
    )
  } else if (isKanji.value) {
    return kanjiList.value.filter(
      (kanji) =>
        kanji.kanji.includes(query) ||
        kanji.kunyomi.includes(query) ||
        kanji.reading.includes(query) ||
        kanji.kanjiname.toLowerCase().includes(query)
    )
  } else if (isGrammar.value) {
    return grammarList.value.filter(
      (grammar) =>
        grammar.kana.includes(query) ||
        grammar.romaji.toLowerCase().includes(query) ||
        grammar.meaning.toLowerCase().includes(query)
    )
  }
  return []
})

const onSearch = () => {
  showSuggestions.value = searchQuery.value.length > 0
  emit('search', searchQuery.value)
}

const clearSearch = () => {
  searchQuery.value = ''
  showSuggestions.value = false
}

const selectSuggestion = (item) => {
  searchQuery.value = isSentence.value ? item.sentence : item.kanji || item.kana || ''
  showSuggestions.value = false
  emit('search', searchQuery.value)
  if (isWord.value) {
    router.push(`/word/${item.id}`)
  } else if (isSentence.value) {
    router.push(`/sentence/${item.id}`)
  } else if (isKanji.value) {
    router.push(`/kanji/${item.id}`)
  } else if (isGrammar.value) {
    router.push(`/grammar/${item.id}`)
  }
}
</script>

<style lang="scss" scoped>
@use '@/components/layout/searchbar/Searchbar.scss';
</style>