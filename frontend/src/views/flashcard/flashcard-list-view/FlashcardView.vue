<template>
  <div class="flashcard-container">
    <div class="left-column">
      <div class="page-header">
        <h1>Flashcard của tôi</h1>
        <div class="header-stats">
          <div class="stat-item">
            <i class="fas fa-book"></i>
            <span>{{ totalItems }} từ đã lưu</span>
          </div>
        </div>
      </div>

      <div class="categories">
        <div 
          v-for="tab in tabsWithCount" 
          :key="tab.type"
          class="category-card"
          :class="{ 'active': activeTab === tab.type }"
          @click="activeTab = tab.type"
        >
          <div class="category-icon">
            <i :class="getIconClass(tab.type)"></i>
          </div>
          <div class="category-info">
            <h3>{{ tab.label }}</h3>
            <span class="count">{{ tab.count }} từ</span>
          </div>
        </div>
      </div>
    </div>

    <div class="right-column">
      <div class="items-section">
        <div class="section-header">
          <h2>{{ activeTabLabel }}</h2>
          <div class="actions">
            <div class="sort-controls">
              <button 
                class="sort-btn"
                :class="{ 'active': sortBy === 'recent' }"
                @click="updateSort('recent')"
              >
                <i class="fas fa-clock"></i>
                Gần đây nhất
              </button>
              <button 
                class="sort-btn"
                :class="{ 'active': sortBy === 'alphabetical' }"
                @click="updateSort('alphabetical')"
              >
                <i class="fas fa-sort-alpha-down"></i>
                A-Z
              </button>
            </div>
            <div class="action-controls">
              <button class="action-btn study" v-if="hasAnyItems" @click="startLearning">
                <i class="fas fa-graduation-cap"></i>
                Học ngay
              </button>
            </div>
          </div>
        </div>

        <div v-if="hasItems" class="items-grid">
          <div 
            v-for="item in paginatedItems" 
            :key="item.id" 
            class="item-card"
          >
            <div class="item-content">
              <div class="item-main">{{ getMainText(item) }}</div>
              <div class="item-sub">{{ getSubText(item) }}</div>
            </div>
            <div class="item-actions">
              <button class="remove-btn" @click.stop="removeFromFlashcard(item)">
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <i class="fas fa-book-open"></i>
          <p>Chưa có {{ activeTabLabel.toLowerCase() }} nào được thêm vào flashcard</p>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="pagination">
          <button 
            class="page-btn"
            :disabled="currentPage === 1"
            @click="updatePage(currentPage - 1)"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          <button 
            class="page-btn"
            :disabled="currentPage === totalPages"
            @click="updatePage(currentPage + 1)"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()
const activeTab = ref('word')

const tabs = [
  { type: 'word', label: 'Từ vựng' },
  { type: 'kanji', label: 'Hán tự' },
  { type: 'grammar', label: 'Ngữ pháp' },
  { type: 'sentence', label: 'Mẫu câu' }
]

const getIconClass = (type) => {
  switch (type) {
    case 'word': return 'fas fa-book'
    case 'kanji': return 'fas fa-pen-fancy'
    case 'grammar': return 'fas fa-puzzle-piece'
    case 'sentence': return 'fas fa-comment'
    default: return 'fas fa-book'
  }
}

const tabsWithCount = computed(() => {
  return tabs.map(tab => ({
    ...tab,
    count: store.getters['flashcard/getItemsByType'](tab.type).length
  }))
})

const totalItems = computed(() => store.state.flashcard?.items?.length || 0)
const paginatedItems = computed(() => store.getters['flashcard/getPaginatedItems'](activeTab.value))
const hasItems = computed(() => paginatedItems.value.length > 0)
const currentPage = computed(() => store.state.flashcard.currentPage)
const totalPages = computed(() => store.getters['flashcard/getTotalPages'](activeTab.value))
const sortBy = computed(() => store.state.flashcard.sortBy)

const activeTabLabel = computed(() => {
  return tabs.find(tab => tab.type === activeTab.value)?.label || ''
})

const getMainText = (item) => {
  let text = '';
  switch (item.type) {
    case 'word': return item.kanji || item.kana
    case 'kanji': return item.kanji
    case 'grammar': return item.pattern
    case 'sentence': 
      text = item.japanese || item.sentence || '';
      return text.length > 20 ? text.substring(0, 20) + '...' : text
    default: return ''
  }
}

const getSubText = (item) => {
  let text = '';
  switch (item.type) {
    case 'word': return item.meaning
    case 'kanji': return item.meaning
    case 'grammar': return item.meaning
    case 'sentence':
      text = item.meaning || item.translation || '';
      return text.length > 25 ? text.substring(0, 25) + '...' : text
    default: return ''
  }
}

const removeFromFlashcard = (item) => {
  store.dispatch('flashcard/removeItem', item)
}

const updateSort = (sortType) => {
  store.dispatch('flashcard/updateSort', sortType)
}

const updatePage = (page) => {
  store.dispatch('flashcard/updatePage', page)
}

const hasAnyItems = computed(() => totalItems.value > 0)

const startLearning = () => {
  // Lưu danh sách items hiện tại vào store để FlashcardLearn có thể sử dụng
  store.dispatch('flashcard/setLearningItems', store.state.flashcard.items)
  // Chuyển hướng đến trang học
  router.push('/flashcard/learn')
}
</script>

<style lang="scss" scoped>
@use '@/views/flashcard/flashcard-list-view/FlashcardView.scss';
</style> 