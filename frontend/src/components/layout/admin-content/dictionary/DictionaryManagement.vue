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
      </button>
    </div>

    <!-- Search and Create Button -->
    <div class="search-filters">
      <div class="search-group">
        <i class="fas fa-search search-icon"></i>
        <input 
          v-model="searchQuery"
          type="text" 
          :placeholder="getSearchPlaceholder()"
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
        <!-- Search Results Info -->
        <div v-if="searchQuery && searchQuery.trim()" class="search-results-info">
          <p>Tìm thấy <strong>{{ filteredVocabularyTotal }}</strong> từ vựng cho từ khóa "<strong>{{ searchQuery }}</strong>"</p>
          <p v-if="getSearchTotalPages() > 1" class="pagination-info">
            Hiển thị {{ searchCurrentPage * searchPageSize + 1 }} - {{ Math.min((searchCurrentPage + 1) * searchPageSize, filteredVocabularyTotal) }} trong tổng số {{ filteredVocabularyTotal }} kết quả
          </p>
        </div>
        
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
              </div>
            </div>
          </div>
          
          <!-- Search Pagination -->
          <div v-if="searchQuery && searchQuery.trim() && getSearchTotalPages() > 1" class="pagination">
            <button 
              @click="prevSearchPage()" 
              :disabled="searchCurrentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ searchCurrentPage + 1 }} / {{ getSearchTotalPages() }}
              ({{ filteredVocabularyTotal }} kết quả)
            </span>
            
            <button 
              @click="nextSearchPage()" 
              :disabled="searchCurrentPage >= getSearchTotalPages() - 1"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
          
          <!-- Regular Pagination -->
          <div v-if="totalPages > 1 && (!searchQuery || !searchQuery.trim())" class="pagination">
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
          <!-- Search Results Info -->
          <div v-if="searchQuery && searchQuery.trim()" class="search-results-info">
            <p>Tìm thấy <strong>{{ filteredGrammarTotal }}</strong> ngữ pháp cho từ khóa "<strong>{{ searchQuery }}</strong>"</p>
            <p v-if="getSearchTotalPages() > 1" class="pagination-info">
              Hiển thị {{ searchCurrentPage * searchPageSize + 1 }} - {{ Math.min((searchCurrentPage + 1) * searchPageSize, filteredGrammarTotal) }} trong tổng số {{ filteredGrammarTotal }} kết quả
            </p>
          </div>
          
          <div class="items-grid">
            <div 
              v-for="item in filteredGrammar" 
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
              </div>
            </div>
          </div>
          
          <!-- Search Pagination -->
          <div v-if="searchQuery && searchQuery.trim() && getSearchTotalPages() > 1" class="pagination">
            <button 
              @click="prevSearchPage()" 
              :disabled="searchCurrentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ searchCurrentPage + 1 }} / {{ getSearchTotalPages() }}
              ({{ filteredGrammarTotal }} kết quả)
            </span>
            
            <button 
              @click="nextSearchPage()" 
              :disabled="searchCurrentPage >= getSearchTotalPages() - 1"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
          
          <!-- Regular Pagination -->
          <div v-if="totalPages > 1 && (!searchQuery || !searchQuery.trim())" class="pagination">
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
          <!-- Search Results Info -->
          <div v-if="searchQuery && searchQuery.trim()" class="search-results-info">
            <p>Tìm thấy <strong>{{ filteredKanjiTotal }}</strong> hán tự cho từ khóa "<strong>{{ searchQuery }}</strong>"</p>
            <p v-if="getSearchTotalPages() > 1" class="pagination-info">
              Hiển thị {{ searchCurrentPage * searchPageSize + 1 }} - {{ Math.min((searchCurrentPage + 1) * searchPageSize, filteredKanjiTotal) }} trong tổng số {{ filteredKanjiTotal }} kết quả
            </p>
          </div>
          
          <div class="items-grid">
            <div 
              v-for="item in filteredKanji" 
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
              </div>
            </div>
          </div>
          
          <!-- Search Pagination -->
          <div v-if="searchQuery && searchQuery.trim() && getSearchTotalPages() > 1" class="pagination">
            <button 
              @click="prevSearchPage()" 
              :disabled="searchCurrentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ searchCurrentPage + 1 }} / {{ getSearchTotalPages() }}
              ({{ filteredKanjiTotal }} kết quả)
            </span>
            
            <button 
              @click="nextSearchPage()" 
              :disabled="searchCurrentPage >= getSearchTotalPages() - 1"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
          
          <!-- Regular Pagination -->
          <div v-if="totalPages > 1 && (!searchQuery || !searchQuery.trim())" class="pagination">
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
          <!-- Search Results Info -->
          <div v-if="searchQuery && searchQuery.trim()" class="search-results-info">
            <p>Tìm thấy <strong>{{ filteredSentencesTotal }}</strong> mẫu câu cho từ khóa "<strong>{{ searchQuery }}</strong>"</p>
            <p v-if="getSearchTotalPages() > 1" class="pagination-info">
              Hiển thị {{ searchCurrentPage * searchPageSize + 1 }} - {{ Math.min((searchCurrentPage + 1) * searchPageSize, filteredSentencesTotal) }} trong tổng số {{ filteredSentencesTotal }} kết quả
            </p>
          </div>
          
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
              </div>
            </div>
          </div>
          
          <!-- Search Pagination -->
          <div v-if="searchQuery && searchQuery.trim() && getSearchTotalPages() > 1" class="pagination">
            <button 
              @click="prevSearchPage()" 
              :disabled="searchCurrentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ searchCurrentPage + 1 }} / {{ getSearchTotalPages() }}
              ({{ filteredSentencesTotal }} kết quả)
            </span>
            
            <button 
              @click="nextSearchPage()" 
              :disabled="searchCurrentPage >= getSearchTotalPages() - 1"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
          
          <!-- Regular Pagination -->
          <div v-if="totalPages > 1 && (!searchQuery || !searchQuery.trim())" class="pagination">
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
          <!-- Error Message in Modal -->
          <div v-if="error" class="modal-error">
            <i class="fas fa-exclamation-triangle"></i>
            <span>{{ error }}</span>
          </div>
          
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
            
            <div class="form-row">
              <div class="form-field">
                <label for="kanjiId">Liên kết Kanji (tùy chọn)</label>
                <select id="kanjiId" v-model="editingItem.kanjiId">
                  <option value="">Chọn kanji liên quan</option>
                  <option 
                    v-for="kanji in availableKanjis" 
                    :key="kanji.id" 
                    :value="kanji.id"
                  >
                    {{ kanji.character }} - {{ kanji.meaning }}
                  </option>
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

    <!-- Delete Confirmation Popup -->
    <ThePopup
      v-if="showDeleteConfirm"
      title="Xác nhận xóa"
      :message="deleteConfirmMessage"
      confirm-text="Xóa"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import ThePopup from '@/components/common/popup/ThePopup.vue'
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
const availableKanjis = ref([]) // Danh sách kanji để chọn khi tạo từ vựng
const loading = ref(false)
const error = ref('')
const successMessage = ref('')

// Delete confirmation popup
const showDeleteConfirm = ref(false)
const deleteConfirmMessage = ref('')
const deleteItemId = ref(null)

// All data for search (tất cả dữ liệu để tìm kiếm)
const allVocabularyData = ref([])
const allGrammarData = ref([])
const allKanjiData = ref([])
const allSentencesData = ref([])

// Pagination
const currentPage = ref(0) // API sử dụng 0-based indexing
const pageSize = ref(10)
const totalPages = ref(0)
const totalElements = ref(0)
const isLastPage = ref(false)

// Search pagination
const searchCurrentPage = ref(0)
const searchPageSize = ref(10)

// API Functions
const loadVocabulary = async (page = 0) => {
  try {
    loading.value = true
    error.value = ''
    const response = await vocabularyApi.getAll({ page, size: pageSize.value })
    vocabularyData.value = response.vocabularies.map(item => {
      return {
        id: item.vocalbId,
        word: item.words,
        reading: item.vietnamese_pronunciation || '', 
        meaning: item.meanning || item.meaning || item.vietnameseMeaning || '', // Thử nhiều trường khác nhau
        type: item.wordType,
        scriptType: item.scriptType,
        kanjiId: item.kanjiId,
        createdAt: new Date().toLocaleDateString('vi-VN')
      }
    })
    
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

    // Load all data functions for search
    const loadAllVocabulary = async () => {
      try {
        const response = await vocabularyApi.getAll({ page: 0, size: 1000 }) // Tải nhiều dữ liệu hơn
        allVocabularyData.value = response.vocabularies.map(item => ({
          id: item.vocalbId,
          word: item.words,
          reading: item.vietnamese_pronunciation || '', 
          meaning: item.meanning || item.meaning || item.vietnameseMeaning || '',
          type: item.wordType,
          scriptType: item.scriptType,
          kanjiId: item.kanjiId,
          createdAt: new Date().toLocaleDateString('vi-VN')
        }))
      } catch (err) {
        console.error('Error loading all vocabulary:', err)
      }
    }

    const loadAllGrammar = async () => {
      try {
        const response = await grammarApi.getAll({ page: 0, size: 1000 })
        allGrammarData.value = response.grammars.map(item => ({
          id: item.grammarId,
          structure: item.structure,
          explanation: item.explanation,
          example: item.example,
          grammarType: item.grammarType,
          vietnamesePronunciation: item.vietnamesePronunciation,
          readings: item.readings || [],
          createdAt: new Date().toISOString()
        }))
      } catch (err) {
        console.error('Error loading all grammar:', err)
      }
    }

    const loadAllKanji = async () => {
      try {
        const response = await kanjiApi.getAll({ page: 0, size: 1000 })
        allKanjiData.value = response.kanjis.map(item => ({
          id: item.kanjiId,
          character: item.characterName,
          meaning: item.meaning,
          strokes: item.strokes,
          vietnamesePronunciation: item.vietnamesePronunciation,
          createdAt: new Date().toLocaleDateString('vi-VN')
        }))
      } catch (err) {
        console.error('Error loading all kanji:', err)
      }
    }

    const loadAllSentences = async () => {
      try {
        const response = await sampleSentenceApi.getAll({ page: 0, size: 1000 })
        allSentencesData.value = response.content.map(item => ({
          id: item.id,
          japanese: item.japaneseText,
          vietnamese: item.vietnameseMeaning,
          createdAt: new Date().toLocaleDateString('vi-VN')
        }))
      } catch (err) {
        console.error('Error loading all sentences:', err)
      }
    }

    // Computed properties
    const filteredVocabulary = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        const filtered = allVocabularyData.value.filter(item => {
          // Tìm kiếm theo từ vựng
          const wordMatch = item.word && item.word.toLowerCase().includes(query)
          
          // Tìm kiếm theo nghĩa (hỗ trợ cả tiếng Việt có dấu)
          const meaningMatch = item.meaning && item.meaning.toLowerCase().includes(query)
          
          // Tìm kiếm theo cách đọc
          const readingMatch = item.reading && item.reading.toLowerCase().includes(query)
          
          // Tìm kiếm theo loại từ
          const typeMatch = item.type && item.type.toLowerCase().includes(query)
          
          return wordMatch || meaningMatch || readingMatch || typeMatch
        })
        
        // Phân trang cho kết quả tìm kiếm
        const startIndex = searchCurrentPage.value * searchPageSize.value
        const endIndex = startIndex + searchPageSize.value
        return filtered.slice(startIndex, endIndex)
      }
      
      return vocabularyData.value
    })

    const filteredVocabularyTotal = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        return allVocabularyData.value.filter(item => {
          const wordMatch = item.word && item.word.toLowerCase().includes(query)
          const meaningMatch = item.meaning && item.meaning.toLowerCase().includes(query)
          const readingMatch = item.reading && item.reading.toLowerCase().includes(query)
          const typeMatch = item.type && item.type.toLowerCase().includes(query)
          return wordMatch || meaningMatch || readingMatch || typeMatch
        }).length
      }
      return vocabularyData.value.length
    })

    const filteredGrammar = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        const filtered = allGrammarData.value.filter(item => {
          const structureMatch = item.structure && item.structure.toLowerCase().includes(query)
          const explanationMatch = item.explanation && item.explanation.toLowerCase().includes(query)
          const exampleMatch = item.example && item.example.toLowerCase().includes(query)
          const pronunciationMatch = item.vietnamesePronunciation && item.vietnamesePronunciation.toLowerCase().includes(query)
          const typeMatch = item.grammarType && item.grammarType.toLowerCase().includes(query)
          
          return structureMatch || explanationMatch || exampleMatch || pronunciationMatch || typeMatch
        })
        
        // Phân trang cho kết quả tìm kiếm
        const startIndex = searchCurrentPage.value * searchPageSize.value
        const endIndex = startIndex + searchPageSize.value
        return filtered.slice(startIndex, endIndex)
      }
      
      return grammarData.value
    })

    const filteredGrammarTotal = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        return allGrammarData.value.filter(item => {
          const structureMatch = item.structure && item.structure.toLowerCase().includes(query)
          const explanationMatch = item.explanation && item.explanation.toLowerCase().includes(query)
          const exampleMatch = item.example && item.example.toLowerCase().includes(query)
          const pronunciationMatch = item.vietnamesePronunciation && item.vietnamesePronunciation.toLowerCase().includes(query)
          const typeMatch = item.grammarType && item.grammarType.toLowerCase().includes(query)
          return structureMatch || explanationMatch || exampleMatch || pronunciationMatch || typeMatch
        }).length
      }
      return grammarData.value.length
    })

    const filteredKanji = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        const filtered = allKanjiData.value.filter(item => {
          const characterMatch = item.character && item.character.toLowerCase().includes(query)
          const meaningMatch = item.meaning && item.meaning.toLowerCase().includes(query)
          const pronunciationMatch = item.vietnamesePronunciation && item.vietnamesePronunciation.toLowerCase().includes(query)
          const strokesMatch = item.strokes && item.strokes.toString().includes(query)
          
          return characterMatch || meaningMatch || pronunciationMatch || strokesMatch
        })
        
        // Phân trang cho kết quả tìm kiếm
        const startIndex = searchCurrentPage.value * searchPageSize.value
        const endIndex = startIndex + searchPageSize.value
        return filtered.slice(startIndex, endIndex)
      }
      
      return kanjiData.value
    })

    const filteredKanjiTotal = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        return allKanjiData.value.filter(item => {
          const characterMatch = item.character && item.character.toLowerCase().includes(query)
          const meaningMatch = item.meaning && item.meaning.toLowerCase().includes(query)
          const pronunciationMatch = item.vietnamesePronunciation && item.vietnamesePronunciation.toLowerCase().includes(query)
          const strokesMatch = item.strokes && item.strokes.toString().includes(query)
          return characterMatch || meaningMatch || pronunciationMatch || strokesMatch
        }).length
      }
      return kanjiData.value.length
    })

    const filteredSentences = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        const filtered = allSentencesData.value.filter(item => {
          const japaneseMatch = item.japanese && item.japanese.toLowerCase().includes(query)
          const vietnameseMatch = item.vietnamese && item.vietnamese.toLowerCase().includes(query)
          
          return japaneseMatch || vietnameseMatch
        })
        
        // Phân trang cho kết quả tìm kiếm
        const startIndex = searchCurrentPage.value * searchPageSize.value
        const endIndex = startIndex + searchPageSize.value
        return filtered.slice(startIndex, endIndex)
      }
      
      return sentencesData.value
    })

    const filteredSentencesTotal = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        return allSentencesData.value.filter(item => {
          const japaneseMatch = item.japanese && item.japanese.toLowerCase().includes(query)
          const vietnameseMatch = item.vietnamese && item.vietnamese.toLowerCase().includes(query)
          return japaneseMatch || vietnameseMatch
        }).length
      }
      return sentencesData.value.length
    })


    // Methods
    const getTabCount = (tabKey) => {
      switch (tabKey) {
        case 'vocabulary': return filteredVocabularyTotal.value
        case 'grammar': return filteredGrammarTotal.value
        case 'kanji': return filteredKanjiTotal.value
        case 'sentences': return filteredSentencesTotal.value
        default: return 0
      }
    }

    const getSearchPlaceholder = () => {
      switch (activeTab.value) {
        case 'vocabulary': return 'Tìm kiếm theo từ, nghĩa, cách đọc hoặc loại từ...'
        case 'grammar': return 'Tìm kiếm theo cấu trúc, giải thích, ví dụ...'
        case 'kanji': return 'Tìm kiếm theo ký tự, nghĩa, phát âm...'
        case 'sentences': return 'Tìm kiếm theo câu tiếng Nhật hoặc tiếng Việt...'
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

    // Load available kanjis for vocabulary form
    const loadAvailableKanjis = async () => {
      try {
        const response = await kanjiApi.getAll({ page: 0, size: 100 }) // Lấy 100 kanji đầu tiên
        availableKanjis.value = response.kanjis.map(item => ({
          id: item.kanjiId,
          character: item.characterName,
          meaning: item.meaning
        }))
      } catch (err) {
        console.error('Error loading available kanjis:', err)
      }
    }

    // Methods
    const openCreateModal = async () => {
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
        // Vocabulary form - load available kanjis
        await loadAvailableKanjis()
        editingItem.value = {
          word: '',
          reading: '',
          meaning: '',
          type: '',
          scriptType: '',
          kanjiId: ''
        }
      }
      
      showModal.value = true
    }

    const editItem = async (item) => {
      modalMode.value = 'edit'
      editingItem.value = { ...item }
      
      // Load available kanjis if editing vocabulary
      if (activeTab.value === 'vocabulary') {
        await loadAvailableKanjis()
      }
      
      showModal.value = true
    }

    const deleteItem = (id) => {
      deleteItemId.value = id
      const itemType = getModalTitle().toLowerCase()
      deleteConfirmMessage.value = `Bạn có chắc chắn muốn xóa ${itemType} này không?\n\nHành động này không thể hoàn tác.`
      showDeleteConfirm.value = true
    }

    const confirmDelete = async () => {
      try {
        const id = deleteItemId.value
        let result
        
        if (activeTab.value === 'kanji') {
          result = await kanjiApi.delete(id)
          await loadKanji(currentPage.value)
          // Cập nhật allData nếu đã tải
          if (allKanjiData.value.length > 0) {
            await loadAllKanji()
          }
        } else if (activeTab.value === 'grammar') {
          result = await grammarApi.delete(id)
          await loadGrammar(currentPage.value)
          // Cập nhật allData nếu đã tải
          if (allGrammarData.value.length > 0) {
            await loadAllGrammar()
          }
        } else if (activeTab.value === 'sentences') {
          result = await sampleSentenceApi.delete(id)
          await loadSentences(currentPage.value)
          // Cập nhật allData nếu đã tải
          if (allSentencesData.value.length > 0) {
            await loadAllSentences()
          }
        } else {
          result = await vocabularyApi.delete(id)
          await loadVocabulary(currentPage.value)
          // Cập nhật allData nếu đã tải
          if (allVocabularyData.value.length > 0) {
            await loadAllVocabulary()
          }
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
      } finally {
        showDeleteConfirm.value = false
        deleteItemId.value = null
      }
    }

    const cancelDelete = () => {
      showDeleteConfirm.value = false
      deleteItemId.value = null
    }

    const closeModal = () => {
      showModal.value = false
      modalMode.value = 'create'
      editingItem.value = null
      error.value = '' // Clear any error messages
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
          // Cập nhật allData nếu đã tải
          if (allKanjiData.value.length > 0) {
            await loadAllKanji()
          }
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
          // Cập nhật allData nếu đã tải
          if (allGrammarData.value.length > 0) {
            await loadAllGrammar()
          }
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
          // Cập nhật allData nếu đã tải
          if (allSentencesData.value.length > 0) {
            await loadAllSentences()
          }
        } else {
          // Validate vocabulary data
          if (!item.word || !item.meaning) {
            error.value = 'Vui lòng nhập đầy đủ từ vựng và nghĩa'
            return
          }
          
          // Prepare vocabulary data for API
          const vocabularyData = {
            words: item.word.trim(),
            meaning: item.meaning.trim(),
            meanning: item.meaning.trim(), // Thử cả hai trường
            wordType: item.type || 'noun',
            scriptType: item.scriptType || 'kanji',
            kanjiId: item.kanjiId || null,
            vietnamese_pronunciation: item.reading ? item.reading.trim() : '',
            readings: []
          }
          
          if (modalMode.value === 'create') {
            const result = await vocabularyApi.create(vocabularyData)
            successMessage.value = 'Từ vựng đã được thêm thành công'
          } else {
            const result = await vocabularyApi.update(item.id, vocabularyData)
            successMessage.value = 'Từ vựng đã được cập nhật thành công'
          }
          await loadVocabulary(currentPage.value)
          
          // Cập nhật allData nếu đã tải
          if (allVocabularyData.value.length > 0) {
            await loadAllVocabulary()
          }
          
          // Auto hide success message after 3 seconds
          setTimeout(() => {
            successMessage.value = ''
          }, 3000)
        }
        
        closeModal()
      } catch (err) {
        error.value = `Không thể lưu ${getModalTitle()}`
        console.error(`Error saving ${activeTab.value}:`, err)
      }
    }

    // Search pagination functions
    const getSearchTotalPages = () => {
      let total = 0
      if (activeTab.value === 'vocabulary') {
        total = filteredVocabularyTotal.value
      } else if (activeTab.value === 'grammar') {
        total = filteredGrammarTotal.value
      } else if (activeTab.value === 'kanji') {
        total = filteredKanjiTotal.value
      } else if (activeTab.value === 'sentences') {
        total = filteredSentencesTotal.value
      }
      return Math.ceil(total / searchPageSize.value)
    }

    const goToSearchPage = (page) => {
      searchCurrentPage.value = page
    }

    const nextSearchPage = () => {
      if (searchCurrentPage.value < getSearchTotalPages() - 1) {
        searchCurrentPage.value++
      }
    }

    const prevSearchPage = () => {
      if (searchCurrentPage.value > 0) {
        searchCurrentPage.value--
      }
    }

    // Watch search query để tải tất cả dữ liệu khi cần
    watch(searchQuery, async (newQuery, oldQuery) => {
      // Reset search pagination khi thay đổi search query
      searchCurrentPage.value = 0
      
      // Khi bắt đầu tìm kiếm (từ rỗng sang có nội dung)
      if (newQuery && newQuery.trim() && (!oldQuery || !oldQuery.trim())) {
        if (activeTab.value === 'vocabulary' && allVocabularyData.value.length === 0) {
          await loadAllVocabulary()
        } else if (activeTab.value === 'grammar' && allGrammarData.value.length === 0) {
          await loadAllGrammar()
        } else if (activeTab.value === 'kanji' && allKanjiData.value.length === 0) {
          await loadAllKanji()
        } else if (activeTab.value === 'sentences' && allSentencesData.value.length === 0) {
          await loadAllSentences()
        }
      }
    })

    // Client-side search - không cần API search nữa vì đã có computed properties để filter

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