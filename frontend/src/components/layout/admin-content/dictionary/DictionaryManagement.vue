<template>
  <div class="dictionary-management">
    <div class="page-header">
      <h1>Quản lý từ điển</h1>
      <p>Quản lý từ vựng, ngữ pháp, hán từ và mẫu câu</p>
    </div>

    <!-- Tab Navigation -->
    <div class="tab-navigation">
      <button 
        v-for="tab in tabs" 
        :key="tab.key"
        @click="activeTab = tab.key"
        :class="['tab-btn', { active: activeTab === tab.key }]"
      >
        <i :class="tab.icon"></i>
        {{ tab.label }}
        <span class="count">{{ getTabCount(tab.key) }}</span>
      </button>
    </div>

    <!-- Search and Create Button -->
    <div class="search-filters">
      <div class="search-group">
        <i class="fas fa-search search-icon"></i>
        <input 
          v-model="searchQuery"
          type="text" 
          placeholder="Tìm kiếm từ vựng..."
          class="search-input"
        >
      </div>

      <button @click="openCreateModal" class="btn-create">
        <i class="fas fa-plus"></i>
        Thêm {{ getCreateButtonText() }}
      </button>
    </div>

    <!-- Content based on active tab -->
    <div class="content-area">
      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="error-state">
        <div class="error-icon">⚠️</div>
        <p>{{ error }}</p>
        <button @click="loadVocabulary()" class="retry-btn">Thử lại</button>
      </div>

      <!-- Success Message -->
      <div v-if="successMessage" class="success-state">
        <div class="success-icon">✅</div>
        <p>{{ successMessage }}</p>
      </div>

      <!-- Vocabulary Tab -->
      <div v-else-if="activeTab === 'vocabulary'" class="vocabulary-section">
        <div class="items-grid">
          <div 
            v-for="item in filteredVocabulary" 
            :key="item.id"
            class="item-card vocabulary-card"
          >
            <div class="card-header">
              <div class="word-info">
                <h3 class="word">{{ item.word }}</h3>
                <div class="reading-info" v-if="item.reading">
                  <strong>Cách đọc:</strong> <span class="reading">{{ item.reading }}</span>
                </div>
                <span class="level-badge" :class="item.level ? item.level.toLowerCase() : ''">{{ item.level }}</span>
              </div>
              <div class="actions">
                <button @click="editItem(item)" class="btn-edit" title="Chỉnh sửa">
                  <i class="fas fa-edit"></i>
                </button>
                <button @click="deleteItem(item.id)" class="btn-delete" title="Xóa">
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </div>
            <div class="card-content">
              <p class="meaning">{{ item.meaning }}</p>
              <div class="meta-info">
                <span class="type">{{ item.type }}</span>
                <span class="script-type">{{ item.scriptType }}</span>
                <span class="created-date">{{ formatDate(item.createdAt) }}</span>
              </div>
            </div>
          </div>
          
          <!-- Pagination -->
          <div v-if="totalPages > 1" class="pagination">
            <button 
              @click="loadVocabulary(currentPage - 1)" 
              :disabled="currentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ currentPage + 1 }} / {{ totalPages }}
              ({{ totalElements }} từ vựng)
            </span>
            
            <button 
              @click="loadVocabulary(currentPage + 1)" 
              :disabled="isLastPage"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Grammar Tab -->
      <div v-if="activeTab === 'grammar'" class="grammar-section">
        <!-- Loading state -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Đang tải dữ liệu ngữ pháp...</p>
        </div>
        
        <!-- Error state -->
        <div v-else-if="error" class="error-state">
          <i class="fas fa-exclamation-triangle error-icon"></i>
          <p>{{ error }}</p>
          <button @click="loadGrammar()" class="retry-btn">Thử lại</button>
        </div>
        
        <!-- Grammar data -->
        <div v-else>
          <div class="items-grid">
            <div 
              v-for="item in grammarData" 
              :key="item.id"
              class="item-card grammar-card"
            >
              <div class="card-header">
                <div class="grammar-info">
                  <h3 class="structure">{{ item.structure }}</h3>
                  <span class="grammar-type-badge" :class="item.grammarType">{{ getGrammarTypeName(item.grammarType) }}</span>
                </div>
                <div class="actions">
                  <button @click="editItem(item)" class="btn-edit" title="Chỉnh sửa">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button @click="deleteItem(item.id)" class="btn-delete" title="Xóa">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
              <div class="card-content">
                <p class="explanation">{{ item.explanation }}</p>
                <div class="pronunciation">
                  <strong>Phát âm:</strong> {{ item.vietnamesePronunciation }}
                </div>
                <div class="example">
                  <strong>Ví dụ:</strong> {{ item.example }}
                </div>
                <div class="meta-info">
                  <span class="created-date">{{ formatDate(item.createdAt) }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Pagination -->
          <div v-if="totalPages > 1" class="pagination">
            <button 
              @click="loadGrammar(currentPage - 1)" 
              :disabled="currentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ currentPage + 1 }} / {{ totalPages }}
              ({{ totalElements }} ngữ pháp)
            </span>
            
            <button 
              @click="loadGrammar(currentPage + 1)" 
              :disabled="isLastPage"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Kanji Tab -->
      <div v-if="activeTab === 'kanji'" class="kanji-section">
        <!-- Loading state -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Đang tải dữ liệu kanji...</p>
        </div>
        
        <!-- Error state -->
        <div v-else-if="error" class="error-state">
          <i class="fas fa-exclamation-triangle error-icon"></i>
          <p>{{ error }}</p>
          <button @click="loadKanji()" class="retry-btn">Thử lại</button>
        </div>
        
        <!-- Kanji data -->
        <div v-else>
          <div class="items-grid">
            <div 
              v-for="item in kanjiData" 
              :key="item.id"
              class="item-card kanji-card"
            >
              <div class="card-header">
                <div class="kanji-info">
                  <h3 class="character">{{ item.character }}</h3>
                </div>
                <div class="actions">
                  <button @click="editItem(item)" class="btn-edit" title="Chỉnh sửa">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button @click="deleteItem(item.id)" class="btn-delete" title="Xóa">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
              <div class="card-content">
                <div class="pronunciation">
                  <strong>Phát âm:</strong> {{ item.vietnamesePronunciation }}
                </div>
                <p class="meaning">{{ item.meaning }}</p>
                <div class="strokes">{{ item.strokes }} nét</div>
                <div class="meta-info">
                  <span class="created-date">{{ formatDate(item.createdAt) }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Pagination -->
          <div v-if="totalPages > 1" class="pagination">
            <button 
              @click="loadKanji(currentPage - 1)" 
              :disabled="currentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ currentPage + 1 }} / {{ totalPages }}
              ({{ totalElements }} kanji)
            </span>
            
            <button 
              @click="loadKanji(currentPage + 1)" 
              :disabled="isLastPage"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Sentences Tab -->
      <div v-if="activeTab === 'sentences'" class="sentences-section">
        <!-- Loading state -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Đang tải dữ liệu mẫu câu...</p>
        </div>
        
        <!-- Error state -->
        <div v-else-if="error" class="error-state">
          <i class="fas fa-exclamation-triangle error-icon"></i>
          <p>{{ error }}</p>
          <button @click="loadSentences()" class="retry-btn">Thử lại</button>
        </div>
        
        <!-- Sentences data -->
        <div v-else>
          <div class="items-grid">
            <div 
              v-for="item in filteredSentences" 
              :key="item.id"
              class="item-card sentence-card"
            >
              <div class="card-header">
                <div class="sentence-info">
                  <h3 class="japanese-text">{{ item.japanese }}</h3>
                </div>
                <div class="actions">
                  <button @click="editItem(item)" class="btn-edit" title="Chỉnh sửa">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button @click="deleteItem(item.id)" class="btn-delete" title="Xóa">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
              <div class="card-content">
                <p class="vietnamese-meaning">{{ item.vietnamese }}</p>
                <div class="meta-info">
                  <span class="created-date">{{ formatDate(item.createdAt) }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Pagination -->
          <div v-if="totalPages > 1" class="pagination">
            <button 
              @click="loadSentences(currentPage - 1)" 
              :disabled="currentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ currentPage + 1 }} / {{ totalPages }}
              ({{ totalElements }} mẫu câu)
            </span>
            
            <button 
              @click="loadSentences(currentPage + 1)" 
              :disabled="isLastPage"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>
      </div>
    </div>



    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ modalMode === 'edit' ? 'Chỉnh sửa' : 'Thêm mới' }} {{ getModalTitle() }}</h3>
          <button @click="closeModal" class="btn-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <!-- Vocabulary Form -->
          <div v-if="activeTab === 'vocabulary'" class="form-group">
            <div class="form-row">
              <div class="form-field">
                <label for="word">Từ vựng *</label>
                <input 
                  id="word"
                  v-model="editingItem.word" 
                  type="text" 
                  placeholder="Nhập từ vựng"
                  required
                />
              </div>
              <div class="form-field">
                <label for="reading">Cách đọc</label>
                <input 
                  id="reading"
                  v-model="editingItem.reading" 
                  type="text" 
                  placeholder="Nhập cách đọc"
                />
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="meaning">Nghĩa *</label>
                <textarea 
                  id="meaning"
                  v-model="editingItem.meaning" 
                  placeholder="Nhập nghĩa của từ"
                  rows="3"
                  required
                ></textarea>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="type">Loại từ</label>
                <select id="type" v-model="editingItem.type">
                  <option value="">Chọn loại từ</option>
                  <option value="noun">Danh từ</option>
                  <option value="verb">Động từ</option>
                  <option value="adjective">Tính từ</option>
                  <option value="adverb">Trạng từ</option>
                  <option value="particle">Trợ từ</option>
                  <option value="conjunction">Liên từ</option>
                  <option value="interjection">Thán từ</option>
                </select>
              </div>
              <div class="form-field">
                <label for="scriptType">Kiểu chữ</label>
                <select id="scriptType" v-model="editingItem.scriptType">
                  <option value="">Chọn kiểu chữ</option>
                  <option value="hiragana">Hiragana</option>
                  <option value="katakana">Katakana</option>
                  <option value="kanji">Kanji</option>
                  <option value="mixed">Hỗn hợp</option>
                </select>
              </div>
            </div>
          </div>
          
          <!-- Kanji Form -->
          <div v-else-if="activeTab === 'kanji'" class="form-group">
            <div class="form-row">
              <div class="form-field">
                <label for="character">Ký tự Kanji *</label>
                <input 
                  id="character"
                  v-model="editingItem.character" 
                  type="text" 
                  placeholder="Nhập ký tự kanji"
                  required
                />
              </div>
              <div class="form-field">
                <label for="strokes">Số nét</label>
                <input 
                  id="strokes"
                  v-model="editingItem.strokes" 
                  type="number" 
                  placeholder="Nhập số nét"
                  min="1"
                />
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="vietnamesePronunciation">Phát âm tiếng Việt</label>
                <input 
                  id="vietnamesePronunciation"
                  v-model="editingItem.vietnamesePronunciation" 
                  type="text" 
                  placeholder="Nhập phát âm tiếng Việt"
                />
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="meaning">Nghĩa *</label>
                <textarea 
                  id="meaning"
                  v-model="editingItem.meaning" 
                  placeholder="Nhập nghĩa của kanji"
                  rows="3"
                  required
                ></textarea>
              </div>
            </div>
          </div>
          
          <!-- Grammar Form -->
          <div v-else-if="activeTab === 'grammar'" class="form-group">
            <div class="form-row">
              <div class="form-field">
                <label for="structure">Cấu trúc ngữ pháp *</label>
                <input 
                  id="structure"
                  v-model="editingItem.structure" 
                  type="text" 
                  placeholder="Nhập cấu trúc ngữ pháp (VD: ~ています)"
                  required
                />
              </div>
              <div class="form-field">
                <label for="grammarType">Loại ngữ pháp</label>
                <select id="grammarType" v-model="editingItem.grammarType">
                  <option value="">Chọn loại ngữ pháp</option>
                  <option value="negative">Phủ định</option>
                  <option value="positive">Khẳng định</option>
                  <option value="progressive">Tiến hành</option>
                  <option value="request">Yêu cầu</option>
                  <option value="sequence">Trình tự</option>
                  <option value="condition">Điều kiện</option>
                  <option value="question">Nghi vấn</option>
                  <option value="contrast">Tương phản</option>
                </select>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="vietnamesePronunciation">Phát âm tiếng Việt</label>
                <input 
                  id="vietnamesePronunciation"
                  v-model="editingItem.vietnamesePronunciation" 
                  type="text" 
                  placeholder="Nhập phát âm tiếng Việt"
                />
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="explanation">Giải thích *</label>
                <textarea 
                  id="explanation"
                  v-model="editingItem.explanation" 
                  placeholder="Nhập giải thích ngữ pháp"
                  rows="3"
                  required
                ></textarea>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="example">Ví dụ *</label>
                <textarea 
                  id="example"
                  v-model="editingItem.example" 
                  placeholder="Nhập ví dụ sử dụng"
                  rows="2"
                  required
                ></textarea>
              </div>
            </div>
          </div>
          
          <!-- Sentences Form -->
          <div v-else-if="activeTab === 'sentences'" class="form-group">
            <div class="form-row">
              <div class="form-field">
                <label for="japanese">Câu tiếng Nhật *</label>
                <textarea 
                  id="japanese"
                  v-model="editingItem.japanese" 
                  placeholder="Nhập câu tiếng Nhật"
                  rows="3"
                  required
                ></textarea>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="vietnamese">Nghĩa tiếng Việt *</label>
                <textarea 
                  id="vietnamese"
                  v-model="editingItem.vietnamese" 
                  placeholder="Nhập nghĩa tiếng Việt"
                  rows="3"
                  required
                ></textarea>
              </div>
            </div>
          </div>
          
          <!-- Other tab forms can be added here -->
          <div v-else class="placeholder-text">
            Form cho {{ getModalTitle() }} sẽ được thêm vào sau...
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="closeModal" class="btn-cancel">Hủy</button>
          <button @click="saveItem" class="btn-save">
            {{ modalMode === 'edit' ? 'Cập nhật' : 'Thêm mới' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import vocabularyApi from '@/api/modules/vocabularyApi'
import kanjiApi from '@/api/modules/kanjiApi'
import grammarApi from '@/api/modules/grammarApi'
import sampleSentenceApi from '@/api/modules/sampleSentenceApi'

// State
const activeTab = ref('vocabulary')
const searchQuery = ref('')
const showModal = ref(false)
const modalMode = ref('create') // 'create' or 'edit'
const editingItem = ref(null)
const itemsPerPage = ref(10)

// Tabs configuration
const tabs = ref([
  { key: 'vocabulary', label: 'Từ vựng', icon: 'fas fa-book' },
  { key: 'grammar', label: 'Ngữ pháp', icon: 'fas fa-language' },
  { key: 'kanji', label: 'Hán từ', icon: 'fas fa-yin-yang' },
  { key: 'sentences', label: 'Mẫu câu', icon: 'fas fa-quote-left' }
])

// Data từ API
const vocabularyData = ref([])
const grammarData = ref([])
const kanjiData = ref([])
const sentencesData = ref([])
const loading = ref(false)
const error = ref('')
const successMessage = ref('')

// Pagination
const currentPage = ref(0) // API sử dụng 0-based indexing
const pageSize = ref(10)
const totalPages = ref(0)
const totalElements = ref(0)
const isLastPage = ref(false)

// API Functions
const loadVocabulary = async (page = 0) => {
  try {
    loading.value = true
    error.value = ''
    const response = await vocabularyApi.getAll({ page, size: pageSize.value })
    
    vocabularyData.value = response.vocabularies.map(item => ({
      id: item.vocalbId,
      word: item.words,
      reading: item.vietnamese_pronunciation || '', // Sử dụng vietnamese_pronunciation làm cách đọc
      meaning: item.meanning, // Sửa lỗi chính tả từ API (meanning -> meaning)
      type: item.wordType,
      scriptType: item.scriptType,
      kanjiId: item.kanjiId,
      createdAt: new Date().toLocaleDateString('vi-VN')
    }))
    
    currentPage.value = response.currentPage
    totalPages.value = response.totalPages
    totalElements.value = response.totalElements
    isLastPage.value = response.isLastPage
    
  } catch (err) {
    error.value = 'Không thể tải dữ liệu từ vựng'
    console.error('Error loading vocabulary:', err)
  } finally {
    loading.value = false
  }
}

    const loadKanji = async (page = 0) => {
      try {
        loading.value = true
        error.value = ''
        const response = await kanjiApi.getAll({ page, size: pageSize.value })
        
        kanjiData.value = response.kanjis.map(item => ({
          id: item.kanjiId,
          character: item.characterName,
          meaning: item.meaning,
          strokes: item.strokes,
          vietnamesePronunciation: item.vietnamesePronunciation,
          createdAt: new Date().toLocaleDateString('vi-VN')
        }))
        
        currentPage.value = response.currentPage
        totalPages.value = response.totalPages
        totalElements.value = response.totalElements
        isLastPage.value = response.isLastPage
        
      } catch (err) {
        error.value = 'Không thể tải dữ liệu kanji'
        console.error('Error loading kanji:', err)
      } finally {
        loading.value = false
      }
    }

    const loadGrammar = async (page = 0) => {
      try {
        loading.value = true
        error.value = ''
        const response = await grammarApi.getAll({ page, size: pageSize.value })
        
        grammarData.value = response.grammars.map(item => ({
          id: item.grammarId,
          structure: item.structure,
          explanation: item.explanation,
          example: item.example,
          grammarType: item.grammarType,
          vietnamesePronunciation: item.vietnamesePronunciation,
          readings: item.readings || [],
          createdAt: new Date().toISOString()
        }))
        
        currentPage.value = response.currentPage
        totalPages.value = response.totalPages
        totalElements.value = response.totalElements
        isLastPage.value = response.isLastPage
        
      } catch (err) {
        error.value = 'Không thể tải dữ liệu ngữ pháp'
        console.error('Error loading grammar:', err)
      } finally {
        loading.value = false
      }
    }

    const loadSentences = async (page = 0) => {
      try {
        loading.value = true
        error.value = ''
        const response = await sampleSentenceApi.getAll({ page, size: pageSize.value })
        
        sentencesData.value = response.content.map(item => ({
          id: item.id,
          japanese: item.japaneseText,
          vietnamese: item.vietnameseMeaning,
          createdAt: new Date().toLocaleDateString('vi-VN')
        }))
        
        currentPage.value = response.currentPage
        totalPages.value = response.totalPages
        totalElements.value = response.totalItems
        isLastPage.value = response.isLastPage
        
      } catch (err) {
        error.value = 'Không thể tải dữ liệu mẫu câu'
        console.error('Error loading sentences:', err)
      } finally {
        loading.value = false
      }
    }

    // Computed properties
    const filteredVocabulary = computed(() => {
      let result = vocabularyData.value
      if (searchQuery.value) {
        result = result.filter(item => 
          item.word.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          item.meaning.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          item.reading.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
      }
      return result
    })

    const filteredGrammar = computed(() => {
      let filtered = grammarData.value
      
      if (searchQuery.value) {
        filtered = filtered.filter(item => 
          item.structure.includes(searchQuery.value) ||
          item.explanation.includes(searchQuery.value) ||
          item.example.includes(searchQuery.value) ||
          item.vietnamesePronunciation.includes(searchQuery.value)
        )
      }
      
      return filtered
    })

    const filteredKanji = computed(() => {
      let filtered = kanjiData.value
      
      if (searchQuery.value) {
        filtered = filtered.filter(item => 
          item.character.includes(searchQuery.value) ||
          item.vietnamesePronunciation.includes(searchQuery.value) ||
          item.meaning.includes(searchQuery.value)
        )
      }
      
      return filtered
    })

    const filteredSentences = computed(() => {
      let filtered = sentencesData.value
      
      if (searchQuery.value) {
        filtered = filtered.filter(item => 
          item.japanese.includes(searchQuery.value) ||
          item.vietnamese.includes(searchQuery.value)
        )
      }
      
      return filtered
    })


    // Methods
    const getTabCount = (tabKey) => {
      switch (tabKey) {
        case 'vocabulary': return vocabularyData.value.length
        case 'grammar': return grammarData.value.length
        case 'kanji': return kanjiData.value.length
        case 'sentences': return sentencesData.value.length
        default: return 0
      }
    }

    const getSearchPlaceholder = () => {
      switch (activeTab.value) {
        case 'vocabulary': return 'Tìm kiếm từ vựng...'
        case 'grammar': return 'Tìm kiếm ngữ pháp...'
        case 'kanji': return 'Tìm kiếm hán từ...'
        case 'sentences': return 'Tìm kiếm mẫu câu...'
        default: return 'Tìm kiếm...'
      }
    }

    const getCreateButtonText = () => {
      switch (activeTab.value) {
        case 'vocabulary': return 'từ vựng'
        case 'grammar': return 'ngữ pháp'
        case 'kanji': return 'hán từ'
        case 'sentences': return 'mẫu câu'
        default: return 'mục'
      }
    }

    const getModalTitle = () => {
      switch (activeTab.value) {
        case 'vocabulary': return 'từ vựng'
        case 'grammar': return 'ngữ pháp'
        case 'kanji': return 'hán từ'
        case 'sentences': return 'mẫu câu'
        default: return 'mục'
      }
    }

    const getGrammarTypeName = (type) => {
      const types = {
        negative: 'Phủ định',
        positive: 'Khẳng định',
        progressive: 'Tiến hành',
        request: 'Yêu cầu',
        sequence: 'Trình tự',
        condition: 'Điều kiện',
        question: 'Nghi vấn',
        contrast: 'Tương phản'
      }
      return types[type] || type
    }

    // Utility functions
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    }

    // Methods
    const openCreateModal = () => {
      modalMode.value = 'create'
      
      if (activeTab.value === 'kanji') {
        editingItem.value = {
          character: '',
          meaning: '',
          strokes: '',
          vietnamesePronunciation: ''
        }
      } else if (activeTab.value === 'grammar') {
        editingItem.value = {
          structure: '',
          explanation: '',
          example: '',
          grammarType: '',
          vietnamesePronunciation: ''
        }
      } else if (activeTab.value === 'sentences') {
        editingItem.value = {
          japanese: '',
          vietnamese: ''
        }
      } else {
        editingItem.value = {
          word: '',
          reading: '',
          meaning: '',
          type: '',
          scriptType: ''
        }
      }
      
      showModal.value = true
    }

    const editItem = (item) => {
      modalMode.value = 'edit'
      editingItem.value = { ...item }
      showModal.value = true
    }

    const deleteItem = async (id) => {
      if (confirm('Bạn có chắc chắn muốn xóa mục này?')) {
        try {
          let result
          if (activeTab.value === 'kanji') {
            result = await kanjiApi.delete(id)
            await loadKanji(currentPage.value)
          } else if (activeTab.value === 'grammar') {
            result = await grammarApi.delete(id)
            await loadGrammar(currentPage.value)
          } else if (activeTab.value === 'sentences') {
            result = await sampleSentenceApi.delete(id)
            await loadSentences(currentPage.value)
          } else {
            result = await vocabularyApi.delete(id)
            await loadVocabulary(currentPage.value)
          }
          
          // Kiểm tra kết quả và hiển thị thông báo thành công
          if (result === 1 || result) {
            // Xóa error message nếu có và hiển thị success message
            error.value = ''
            successMessage.value = `${getModalTitle()} đã được xóa thành công`
            
            // Tự động ẩn success message sau 3 giây
            setTimeout(() => {
              successMessage.value = ''
            }, 3000)
          }
        } catch (err) {
          error.value = `Không thể xóa ${getModalTitle()}`
          console.error(`Error deleting ${activeTab.value}:`, err)
        }
      }
    }

    const closeModal = () => {
      showModal.value = false
      modalMode.value = 'create'
      editingItem.value = null
    }

    const saveItem = async () => {
      try {
        const item = editingItem.value
        if (!item) return
        
        if (activeTab.value === 'kanji') {
          // Prepare kanji data for API
          const kanjiData = {
            characterName: item.character,
            meaning: item.meaning,
            strokes: item.strokes,
            vietnamesePronunciation: item.vietnamesePronunciation
          }
          
          if (modalMode.value === 'create') {
            await kanjiApi.create(kanjiData)
          } else {
            await kanjiApi.update(item.id, kanjiData)
          }
          await loadKanji(currentPage.value)
        } else if (activeTab.value === 'grammar') {
          // Prepare grammar data for API
          const grammarData = {
            structure: item.structure,
            explanation: item.explanation,
            example: item.example,
            grammarType: item.grammarType,
            vietnamesePronunciation: item.vietnamesePronunciation
          }
          
          if (modalMode.value === 'create') {
            await grammarApi.create(grammarData)
          } else {
            await grammarApi.update(item.id, grammarData)
          }
          await loadGrammar(currentPage.value)
        } else if (activeTab.value === 'sentences') {
          // Prepare sentence data for API
          const sentenceData = {
            japaneseText: item.japanese,
            vietnameseMeaning: item.vietnamese
          }
          
          if (modalMode.value === 'create') {
            await sampleSentenceApi.create(sentenceData)
          } else {
            await sampleSentenceApi.update(item.id, sentenceData)
          }
          await loadSentences(currentPage.value)
        } else {
          // Prepare vocabulary data for API
          const vocabularyData = {
            words: item.word,
            meaning: item.meaning,
            wordType: item.type,
            scriptType: item.scriptType,
            kanjiId: item.kanjiId,
            vietnamese_pronunciation: item.reading || ''
          }
          
          if (modalMode.value === 'create') {
            await vocabularyApi.create(vocabularyData)
          } else {
            await vocabularyApi.update(item.id, vocabularyData)
          }
          await loadVocabulary(currentPage.value)
        }
        
        closeModal()
      } catch (err) {
        error.value = `Không thể lưu ${getModalTitle()}`
        console.error(`Error saving ${activeTab.value}:`, err)
      }
    }

    // Search functionality
    const searchVocabulary = async (keyword) => {
      if (!keyword.trim()) {
        await loadVocabulary(0)
        return
      }
      
      try {
        loading.value = true
        error.value = ''
        const response = await vocabularyApi.search(keyword)
        
        vocabularyData.value = response.vocabularies.map(item => ({
          id: item.vocalbId,
          word: item.words,
          reading: item.vietnamese_pronunciation || '', // Sử dụng vietnamese_pronunciation làm cách đọc
          meaning: item.meanning, // Sửa lỗi chính tả từ API (meanning -> meaning)
          type: item.wordType,
          scriptType: item.scriptType,
          kanjiId: item.kanjiId,
          createdAt: new Date().toLocaleDateString('vi-VN')
        }))
        
        // Reset pagination for search results
        currentPage.value = 0
        totalPages.value = 1
        totalElements.value = response.length
        isLastPage.value = true
        
      } catch (err) {
        error.value = 'Không thể tìm kiếm từ vựng'
        console.error('Error searching vocabulary:', err)
      } finally {
        loading.value = false
      }
    }

    const searchKanji = async (keyword) => {
      if (!keyword.trim()) {
        await loadKanji(0)
        return
      }
      
      try {
        loading.value = true
        error.value = ''
        const response = await kanjiApi.search(keyword)
        
        kanjiData.value = response.map(item => ({
          id: item.kanjiId,
          character: item.characterName,
          meaning: item.meaning,
          strokes: item.strokes,
          vietnamesePronunciation: item.vietnamesePronunciation,
          createdAt: item.createdAt || new Date().toISOString()
        }))
        
        // Reset pagination for search results
        currentPage.value = 0
        totalPages.value = 1
        totalElements.value = response.length
        isLastPage.value = true
        
      } catch (err) {
        error.value = 'Không thể tìm kiếm kanji'
        console.error('Error searching kanji:', err)
      } finally {
        loading.value = false
      }
    }

    const searchGrammar = async (keyword) => {
      if (!keyword.trim()) {
        await loadGrammar(0)
        return
      }
      
      try {
        loading.value = true
        error.value = ''
        const response = await grammarApi.search(keyword)
        
        grammarData.value = response.map(item => ({
          id: item.grammarId,
          structure: item.structure,
          explanation: item.explanation,
          example: item.example,
          grammarType: item.grammarType,
          vietnamesePronunciation: item.vietnamesePronunciation,
          readings: item.readings || [],
          createdAt: item.createdAt || new Date().toISOString()
        }))
        
        // Reset pagination for search results
        currentPage.value = 0
        totalPages.value = 1
        totalElements.value = response.length
        isLastPage.value = true
        
      } catch (err) {
        error.value = 'Không thể tìm kiếm ngữ pháp'
        console.error('Error searching grammar:', err)
      } finally {
        loading.value = false
      }
    }

    const searchSentences = async (keyword) => {
      if (!keyword.trim()) {
        await loadSentences(0)
        return
      }
      
      try {
        loading.value = true
        error.value = ''
        const response = await sampleSentenceApi.search(keyword)
        
        sentencesData.value = response.content.map(item => ({
          id: item.id,
          japanese: item.japaneseText,
          vietnamese: item.vietnameseMeaning,
          createdAt: new Date().toLocaleDateString('vi-VN')
        }))
        
        // Reset pagination for search results
        currentPage.value = 0
        totalPages.value = 1
        totalElements.value = response.content.length
        isLastPage.value = true
        
      } catch (err) {
        error.value = 'Không thể tìm kiếm mẫu câu'
        console.error('Error searching sentences:', err)
      } finally {
        loading.value = false
      }
    }

    // Debounced search
    let searchTimeout = null
    watch(searchQuery, (newQuery) => {
      if (searchTimeout) {
        clearTimeout(searchTimeout)
      }
      
      searchTimeout = setTimeout(() => {
        if (activeTab.value === 'vocabulary') {
          searchVocabulary(newQuery)
        } else if (activeTab.value === 'kanji') {
          searchKanji(newQuery)
        } else if (activeTab.value === 'grammar') {
          searchGrammar(newQuery)
        } else if (activeTab.value === 'sentences') {
          searchSentences(newQuery)
        }
      }, 500) // 500ms debounce
    })

    // Switch tab
    const switchTab = (tab) => {
      activeTab.value = tab
      currentPage.value = 0
      searchQuery.value = ''
      
      if (tab === 'vocabulary') {
        loadVocabulary()
      } else if (tab === 'kanji') {
        loadKanji()
      } else if (tab === 'grammar') {
        loadGrammar()
      } else if (tab === 'sentences') {
        loadSentences()
      }
    }

    // Watchers
    watch(activeTab, () => {
      currentPage.value = 0 // Reset to first page when changing tabs
      if (activeTab.value === 'vocabulary') {
        loadVocabulary(0)
      } else if (activeTab.value === 'kanji') {
        loadKanji(0)
      } else if (activeTab.value === 'grammar') {
        loadGrammar(0)
      } else if (activeTab.value === 'sentences') {
        loadSentences(0)
      }
    })

    // Load data on component mount
    onMounted(() => {
      loadVocabulary()
    })
</script>

<style src="./DictionaryManagement.scss" scoped></style>