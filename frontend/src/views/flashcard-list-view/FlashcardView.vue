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

<style scoped>
.flashcard-container {
  padding-bottom: 40px;
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
}

.left-column {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.left-column h1 {
  font-size: 2rem;
  color: #333;
  margin: 0;
}

.right-column {
  display: flex;
  flex-direction: column;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 16px;
}

.header-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
}

.stat-item i {
  color: #E94560;
}

.categories {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.category-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.category-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.category-card.active {
  background: #E94560;
  color: white;
}

.category-icon {
  width: 40px;
  height: 40px;
  background: #f8f8f8;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  color: #E94560;
}

.category-card.active .category-icon {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.category-info h3 {
  margin: 0;
  font-size: 1rem;
}

.category-info .count {
  font-size: 0.875rem;
  opacity: 0.8;
}

.items-section {
  background: white;
  border-radius: 12px;
  padding: 47px;
  padding-bottom: 5px;
  max-height: calc(100vh - 200px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #333;
}

.actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.sort-controls {
  display: flex;
  gap: 8px;
}

.sort-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  color: #666;
  cursor: pointer;
  transition: all 0.2s ease;
}

.sort-btn:hover {
  border-color: #E94560;
  color: #E94560;
}

.sort-btn.active {
  background: #E94560;
  border-color: #E94560;
  color: white;
}

.action-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
}

.action-btn.study {
  background: #E94560;
  color: white;
}

.action-btn.study:hover {
  background: #d63d56;
  transform: translateY(-1px);
}

.action-btn.delete {
  background: #dc3545;
  color: white;
}

.action-btn.delete:hover {
  background: #c82333;
}

.action-btn.delete:disabled {
  background: #6c757d;
  cursor: not-allowed;
  opacity: 0.65;
}

.action-btn.cancel {
  background: #6c757d;
  color: white;
}

.action-btn.cancel:hover {
  background: #5a6268;
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
  overflow-y: auto;
  padding-right: 12px;
  flex: 1;
}

.item-card {
  background: #f8f8f8;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  width: 100%;
  box-sizing: border-box;
}

.item-content {
  flex: 1;
  min-width: 0;
  margin-right: 12px;
}

.item-main {
  font-size: 1.25rem;
  margin-bottom: 8px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.item-sub {
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.remove-btn {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.remove-btn:hover {
  background: #fee;
  color: #E94560;
}

.empty-state {
  text-align: center;
  padding: 48px 0;
  color: #666;
}

.empty-state i {
  font-size: 3rem;
  margin-bottom: 16px;
  color: #ddd;
}

.empty-state p {
  margin: 0;
  font-size: 1.1rem;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}

.page-btn {
  width: 36px;
  height: 36px;
  border: 1px solid #ddd;
  border-radius: 50%;
  background: white;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.page-btn:hover:not(:disabled) {
  border-color: #E94560;
  color: #E94560;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 0.9rem;
  color: #666;
}

.select-indicator {
  color: #E94560;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 8px;
}
</style> 