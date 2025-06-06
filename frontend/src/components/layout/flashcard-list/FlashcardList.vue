<template>
  <div class="flashcard-container">
    <button 
      class="flashcard-btn" 
      @click="toggleList"
      :class="{ 'active': isOpen }"
    >
      <i class="fas fa-book"></i>
      <span v-if="totalItems > 0" class="item-count">{{ totalItems }}</span>
    </button>

    <!-- Dropdown List -->
    <div v-if="isOpen" class="flashcard-list">
      <div class="list-header">
        <h3>Danh sách Flashcard</h3>
        <button class="close-btn" @click="toggleList">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="list-tabs">
        <div 
          v-for="tab in tabsWithCount" 
          :key="tab.type"
          class="tab-wrapper"
        >
          <span v-if="tab.count > 0" class="tab-count">{{ tab.count }}</span>
          <button 
            class="tab-btn"
            :class="{ 'active': activeTab === tab.type }"
            @click="activeTab = tab.type"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>

      <div v-if="filteredItems.length > 0" class="items-list">
        <div v-for="item in filteredItems" :key="item.id" class="flashcard-item">
          <div class="item-content">
            <div class="item-main">{{ getMainText(item) }}</div>
            <div class="item-sub">{{ getSubText(item) }}</div>
          </div>
          <button class="remove-btn" @click="removeFromFlashcard(item)">
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>
      <div v-else class="empty-message">
        Chưa có item nào trong danh sách {{ getActiveTabLabel }}
      </div>

      <div class="list-footer">
        <router-link to="/flashcard" class="view-all-btn" @click="toggleList">
          <i class="fas fa-external-link-alt"></i>
          Đi tới trang Flashcard
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const isOpen = ref(false)
const activeTab = ref('word')

const tabs = [
  { type: 'word', label: 'Từ vựng' },
  { type: 'kanji', label: 'Hán tự' },
  { type: 'grammar', label: 'Ngữ pháp' },
  { type: 'sentence', label: 'Mẫu câu' }
]

const toggleList = () => {
  isOpen.value = !isOpen.value
}

// TODO: Replace with actual store getters
const flashcardItems = computed(() => store.state.flashcard?.items || [])

const filteredItems = computed(() => {
  return flashcardItems.value.filter(item => item.type === activeTab.value)
})

const getActiveTabLabel = computed(() => {
  return tabs.find(tab => tab.type === activeTab.value)?.label
})

const getMainText = (item) => {
  let text = '';
  switch (item.type) {
    case 'word':
      return item.kanji || item.kana
    case 'kanji':
      return item.kanji
    case 'grammar':
      return item.kana
    case 'sentence':
      text = item.sentence || item.japanese || '';
      return text.length > 20 ? text.substring(0, 20) + '...' : text
    default:
      return ''
  }
}

const getSubText = (item) => {
  let text = '';
  switch (item.type) {
    case 'word':
      return item.meaning
    case 'kanji':
      return item.reading
    case 'grammar':
      return item.meaning
    case 'sentence':
      text = item.translation || item.meaning || '';
      return text.length > 25 ? text.substring(0, 25) + '...' : text
    default:
      return ''
  }
}

const removeFromFlashcard = (item) => {
  store.dispatch('flashcard/removeItem', item)
}

const tabsWithCount = computed(() => {
  return tabs.map(tab => ({
    ...tab,
    count: store.getters['flashcard/getItemsByType'](tab.type).length
  }))
})

const totalItems = computed(() => flashcardItems.value.length)
</script>

<style lang="scss" scoped>
@use '@/components/layout/flashcard-list/FlashcardList.scss';
</style> 