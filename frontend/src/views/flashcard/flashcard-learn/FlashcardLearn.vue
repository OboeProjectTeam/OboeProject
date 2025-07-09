<template>
  <div class="flashcard-learn" :class="{ 'is-fullscreen': isFullscreen }">
    <div class="deck-header">
      <div>
        <h2 class="deck-title">
          {{ deckTitle }}
        </h2>
        <p class="description-text">mô tả ở đây</p>
      </div>
      <div class="creator-info">
        <div class="creator-card">
          <div class="creator-avatar">
            <img :src="creatorInfo.avatar" :alt="creatorInfo.name" />
          </div>
          <div class="creator-details">
            <h3 class="creator-name">{{ creatorInfo.name }}</h3>
            <p class="creator-date">Đã tạo {{ creatorInfo.createdDate }}</p>

          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <!-- Menu bên trái -->
      <div class="side-menu">
        <div class="menu-item" :class="{ active: activeMode === 'flashcard' }" @click="setMode('flashcard')">
          <i class="fas fa-sticky-note"></i>
          <span>Thẻ ghi nhớ</span>
        </div>
        <div class="menu-item" :class="{ active: activeMode === 'test' }" @click="openTestOptions">
          <i class="fas fa-tasks"></i>
          <span>Kiểm tra</span>
        </div>
        <div class="menu-item" :class="{ active: activeMode === 'match' }" @click="setMode('match')">
          <i class="fas fa-puzzle-piece"></i>
          <span>Ghép thẻ</span>
        </div>
      </div>

      <!-- Card ở giữa -->
      <div class="card-section">
        <TheCard 
          ref="cardRef" 
          :slides="slides" 
    
          :pagination="{
            type: 'fraction',
            clickable: true,
            formatFractionCurrent: (number) => number,
            formatFractionTotal: (number) => number
          }" 
          :canFlip="true" 
          :speed="300" 
          :keyboard="{
            enabled: true,
            onlyInViewport: true
          }" 
          :class="{ 'fullscreen-card': isFullscreen }" 
          @swiper="onSwiper" 
          @card-flipped="onCardFlip"
          @slideChange="onSlideChange" 
        />
      </div>

      <!-- Control bar bên phải -->
      <div class="control-menu">
        <button class="control-btn" :class="{ 'playing': isAutoPlaying }" @click="toggleAutoplay">
          <i :class="isAutoPlaying ? 'fas fa-pause' : 'fas fa-play'"></i>
          <span>{{ isAutoPlaying ? 'Tạm dừng' : 'Phát' }}</span>
        </button>
        <button class="control-btn" :class="{ 'disabled': trackProgress }" @click="!trackProgress && shuffleCards">
          <i class="fas fa-random"></i>
          <span>Trộn thẻ</span>
        </button>
        <button class="control-btn" @click="openSettings">
          <i class="fas fa-cog"></i>
          <span>Cài đặt</span>
        </button>
        <button class="control-btn" @click="toggleFullscreen">
          <i class="fas fa-expand"></i>
          <span>Toàn màn hình</span>
        </button>
      </div>
    </div>
    <!-- Nút theo dõi tiến độ -->
    <div v-if="trackProgress" class="progress-buttons-container">
      <div class="progress-buttons">
        <button class="progress-btn learning"
          :class="{ active: slides[currentSlideIndex]?.status === 'learning', pressed: slides[currentSlideIndex]?.status === 'learning' }"
          @click="updateCardStatus('learning')">
          <i class="fas fa-minus"></i>
          <span>Đang học</span>
          <span class="count">({{ learningStats.learning }})</span>
        </button>
        <button class="progress-btn known"
          :class="{ active: slides[currentSlideIndex]?.status === 'known', pressed: slides[currentSlideIndex]?.status === 'known' }"
          @click="updateCardStatus('known')">
          <i class="fas fa-plus"></i>
          <span>Đã biết</span>
          <span class="count">({{ learningStats.known }})</span>
        </button>
      </div>
    </div>
    <!-- Creator Info Section -->
    <div class="description-section">

      <!-- List Items Section -->
      <div class="list-items-section">
        <div class="list-header">
          <h3>Thuật ngữ trong học phần này</h3>
          <button class="add-term-btn" @click="navigateToTermCreation">
            <i class="fas fa-plus"></i>
            Thêm hoặc xóa thuật ngữ
          </button>
        </div>

        <!-- Learning Terms -->
        <div class="terms-list">
          <h4 class="list-title">
            Đang học
            <span class="count">({{ learningStats.learning }})</span>
          </h4>
          <TransitionGroup name="list" tag="div" class="terms-container">
            <div v-for="item in displayLearningItems" :key="item.id" class="term-item" @click="editTerm(item)">
              <div class="term-content">
                <div class="term">{{ getItemContent(item) }}</div>
                <div class="definition">{{ getItemDefinition(item) }}</div>
              </div>
              <div class="term-actions">
                <!-- <button class="edit-btn" @click.stop="editTerm(item)">
                  <i class="fas fa-pencil-alt"></i>
                </button> -->
                <button class="delete-btn" @click.stop="deleteTerm(item)">
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </div>
          </TransitionGroup>
        </div>

        <!-- Known Terms -->
        <div v-if="trackProgress" class="terms-list">
          <h4 class="list-title">
            Đã biết
            <span class="count">({{ learningStats.known }})</span>
          </h4>
          <TransitionGroup name="list" tag="div" class="terms-container">
            <div v-for="item in displayKnownItems" :key="item.id" class="term-item" @click="editTerm(item)">
              <div class="term-content">
                <div class="term">{{ getItemContent(item) }}</div>
                <div class="definition">{{ getItemDefinition(item) }}</div>
              </div>
              <div class="term-actions">
                <button class="edit-btn" @click.stop="editTerm(item)">
                  <i class="fas fa-pencil-alt"></i>
                </button>
                <button class="delete-btn" @click.stop="deleteTerm(item)">
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </div>
          </TransitionGroup>
        </div>
      </div>
    </div>
    <!-- Animation hiển thị trạng thái -->
    <transition name="status-fade">
      <div v-if="showStatusAnimation" class="status-animation" :class="currentStatus">
        <i class="fas" :class="currentStatus === 'known' ? 'fa-check' : 'fa-clock'"></i>
        <span>{{ currentStatus === 'known' ? 'Đã biết' : 'Đang học' }}</span>
      </div>
    </transition>

    <!-- Settings Modal -->
    <div v-if="showSettings" class="settings-modal">
      <div class="modal-content">
        <h3>Cài đặt</h3>

        <div class="settings-body">
          <div class="setting-item speed-control">
            <label>Tốc độ tự động chuyển</label>
            <div class="speed-buttons">
              <button @click="tempSettings.autoplaySpeed = Math.max(1, tempSettings.autoplaySpeed - 1)"
                class="speed-btn">
                <i class="fas fa-minus"></i>
              </button>
              <span class="speed-value">{{ tempSettings.autoplaySpeed }}s</span>
              <button @click="tempSettings.autoplaySpeed = Math.min(20, tempSettings.autoplaySpeed + 1)"
                class="speed-btn">
                <i class="fas fa-plus"></i>
              </button>
            </div>
          </div>

          <div class="settings-group">
            <div class="setting-item toggle">
              <span class="setting-label">Theo dõi tiến độ</span>
              <label class="toggle-switch">
                <input type="checkbox" v-model="tempSettings.trackProgress" />
                <span class="toggle-slider"></span>
              </label>
            </div>

            <div class="setting-item toggle">
              <span class="setting-label">Đảo mặt thẻ</span>
              <label class="toggle-switch">
                <input type="checkbox" v-model="tempSettings.reverseCards" />
                <span class="toggle-slider"></span>
              </label>
            </div>
          </div>

          <div class="settings-actions">
            <button class="action-btn shortcuts-btn" @click="showShortcuts = true">
              <i class="fas fa-keyboard"></i>
              <span>Phím tắt</span>
            </button>

            <button class="action-btn reset-btn" @click="resetCards">
              <i class="fas fa-redo"></i>
              <span>Khởi động lại</span>
            </button>
          </div>
        </div>

        <div class="modal-footer">
          <button class="cancel-btn" @click="cancelSettings">Hủy</button>
          <button class="confirm-btn" @click="applySettings">Xác nhận</button>
        </div>
      </div>

      <!-- Shortcuts Modal -->
      <div v-if="showShortcuts" class="shortcuts-modal">
        <div class="shortcuts-content">
          <h4>Phím tắt</h4>
          <div class="shortcut-list">
            <div class="shortcut-item">
              <span class="key">←</span>
              <span>Thẻ trước</span>
            </div>
            <div class="shortcut-item">
              <span class="key">→</span>
              <span>Thẻ sau</span>
            </div>
            <div class="shortcut-item">
              <span class="key">↑</span>
              <span>Lật thẻ</span>
            </div>
            <div v-if="trackProgress" class="shortcut-item">
              <span class="key">-</span>
              <span>Đánh dấu đang học</span>
            </div>
            <div v-if="trackProgress" class="shortcut-item">
              <span class="key">+</span>
              <span>Đánh dấu đã biết</span>
            </div>
          </div>
          <button class="close-shortcuts-btn" @click="showShortcuts = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
      </div>
    </div>

    <!-- Kết quả -->
    <div v-if="showResults" class="results-modal">
      <div class="modal-overlay"></div>
      <div class="results-content">
        <div class="results-header">
          <img :src="ImagePaths.learn.celebration" alt="Celebration" class="celebration-image" />                 
          <h2>{{ learningStats.known === slides.length ?
            'Chà,Bạn nắm bài thật chắc! Bạn đã sắp xếp tất cả các thẻ.' :
            'Bạn đang làm rất tốt! Hãy tiếp tục để tăng cường tự tin' }}</h2>
        </div>

        <div class="progress-section">
          <h3>Tiến độ của </h3>
          <div class="progress-items">
            <div class="progress-item learning">
              <div class="label">Đang học</div>
              <div class="count">{{ learningStats.learning }}</div>
            </div>
            <div class="progress-item known">
              <div class="label">Đã biết</div>
              <div class="count">{{ learningStats.known }}</div>
            </div>
          </div>
        </div>

        <div class="next-steps">
          <h3>Bước tiếp theo</h3>
          <button class="practice-btn">
            <i class="fas fa-sync-alt"></i>
            Ôn luyện với các câu hỏi
          </button>
          <button v-if="learningStats.known !== slides.length" class="review-btn" @click="reviewUnknownCards">
            <i class="fas fa-graduation-cap"></i>
            Học lại {{ learningStats.learning }} thẻ chưa thuộc
          </button>
          <button class="reset-btn" @click="handleReset">
            <i class="fas fa-redo"></i>
            Đặt lại Thẻ ghi nhớ
          </button>
        </div>
      </div>
    </div>

    <!-- Test Options Modal -->
    <div v-if="showTestOptions" class="test-options-modal">
      <div class="modal-overlay" @click="closeTestOptions"></div>
      <div class="test-options-content">
        <h3>Chọn loại bài kiểm tra</h3>
        <div class="test-options">
          <div class="test-option" :class="{ active: selectedTestType === 'multiple-choice' }"
            @click="selectedTestType = 'multiple-choice'">
            <div class="option-icon">
              <i class="fas fa-list-ul"></i>
            </div>
            <div class="option-details">
              <h4>Trắc nghiệm</h4>
              <p>Chọn đáp án đúng từ các lựa chọn</p>
            </div>
          </div>

          <div class="test-option" :class="{ active: selectedTestType === 'written' }"
            @click="selectedTestType = 'written'">
            <div class="option-icon">
              <i class="fas fa-pen"></i>
            </div>
            <div class="option-details">
              <h4>Tự luận</h4>
              <p>Viết câu trả lời của </p>
            </div>
          </div>

          <div class="test-option" :class="{ active: selectedTestType === 'true-false' }"
            @click="selectedTestType = 'true-false'">
            <div class="option-icon">
              <i class="fas fa-check-circle"></i>
            </div>
            <div class="option-details">
              <h4>Đúng/Sai</h4>
              <p>Xác định câu đúng hay sai</p>
            </div>
          </div>
        </div>

        <div class="test-options-footer">
          <button class="cancel-btn" @click="closeTestOptions">Hủy</button>
          <button class="start-test-btn" :disabled="!selectedTestType" @click="startTest">
            <i class="fas fa-play"></i>
            Bắt đầu kiểm tra
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ImagePaths } from '@/assets/img/imagePaths';
import { ref, computed, onMounted, onUnmounted, watch, nextTick, reactive } from 'vue';
import { useStore } from 'vuex';
import { useRoute, useRouter } from 'vue-router';
import TheCard from '@/components/layout/card/TheCard.vue';
import { TransitionGroup } from 'vue';

const store = useStore();
const route = useRoute();
const router = useRouter();
const cardRef = ref(null);
const swiperInstance = ref(null);
const activeMode = ref('flashcard');
const isAutoPlaying = ref(false);
const showSettings = ref(false);
const autoplaySpeed = ref(3);
const isFullscreen = ref(false);
const trackProgress = ref(false);
const reverseCards = ref(false);
const showShortcuts = ref(false);
const autoplayInterval = ref(null);
const showTestOptions = ref(false);
const selectedTestType = ref('');

// Lưu trữ tạm thời các giá trị cài đặt
const tempSettings = reactive({
autoplaySpeed: 3,
trackProgress: false,
reverseCards: false
});

// Theo dõi tiến độ học
const progress = ref({
total: 0,
reviewed: 0,
correct: 0
});

// Thêm state cho animation
const showStatusAnimation = ref(false);
const currentStatus = ref('');
const currentSlideIndex = ref(0);

// Thêm state cho kết quả
const showResults = ref(false);
const learningStats = reactive({
known: 0,
learning: 0,
remaining: 0
});

// Add these new refs for creator info
const isCurrentUserCreator = ref(false); // Will be true if current user is creator
const isFollowing = ref(false);
const creatorInfo = ref({
avatar: ImagePaths.avatar.default, // This should come from your data
name: 'hoangdul999', // This should come from your data
createdDate: '3 ngày trước', // This should come from your data
});

// Computed title based on source
const deckTitle = computed(() => {
const fromLibrary = route.query.source === 'library';
const setTitle = route.query.title;
return fromLibrary ? setTitle : 'Kho Thẻ Tạm Thời';
});


// Method to handle mode changes
const setMode = async (mode) => {
activeMode.value = mode;
if (mode === 'match') {
  console.log('Starting Match game...');
  try {
    // 1. Save current state of FlashcardLearn to localStorage
    const learnStateToSave = {
      allItems: JSON.parse(JSON.stringify(allItems.value)), // Deep clone
      learningStats: JSON.parse(JSON.stringify(learningStats)), // Deep clone
      activeMode: 'flashcard', // Default to flashcard mode when returning
      currentSlideIndex: swiperInstance.value?.activeIndex || 0,
      autoplaySpeed: autoplaySpeed.value,
      trackProgress: trackProgress.value,
      reverseCards: reverseCards.value,
      isAutoPlaying: isAutoPlaying.value,
    };
    localStorage.setItem('flashcardLearnStateBeforeMatch', JSON.stringify(learnStateToSave));
    console.log('Saved FlashcardLearn state to localStorage for Match game:', learnStateToSave);

    // 2. Save current flashcards to store for the match game
    // Ensure currentFlashcards are up-to-date and have IDs
    const currentFlashcards = allItems.value.map(item => ({
      id: item.id, // Ensure ID is passed
      front: item.content || '',
      back: item.backcontent || '',
      content: item.content || '', // Duplicate for potential different uses in components
      backcontent: item.backcontent || '',
      meaning: item.backcontent || '',
      type: item.type || 'word',
      status: item.status || 'learning'
    }));

    console.log('Saving flashcards to store for Match game:', currentFlashcards);
    await store.dispatch('flashcard/setLearningItems', currentFlashcards);
    console.log('Successfully saved to store, now navigating to Match game...');

    // 3. Navigate
    await router.push({
      name: 'FlashcardMatch',
      query: { // Pass similar queries as test, if needed in the future
        deckId: route.query.deckId || '',
        source: route.query.source || ''
      }
    });
    console.log('Navigation to Match game successful');

  } catch (err) {
    console.error('Error in starting Match game:', err);
  }
}
};

const openTestOptions = () => {
showTestOptions.value = true;
selectedTestType.value = '';
};

const closeTestOptions = () => {
showTestOptions.value = false;
selectedTestType.value = '';
};

const startTest = async () => {
console.log('Starting test with type:', selectedTestType.value);
if (!selectedTestType.value) {
  console.error('No test type selected');
  return;
}

try {
  // 1. Save current state of FlashcardLearn to localStorage
  const learnStateToSave = {
    allItems: JSON.parse(JSON.stringify(allItems.value)), // Deep clone
    learningStats: JSON.parse(JSON.stringify(learningStats)), // Deep clone
    activeMode: activeMode.value,
    currentSlideIndex: swiperInstance.value?.activeIndex || 0,
    autoplaySpeed: autoplaySpeed.value,
    trackProgress: trackProgress.value,
    reverseCards: reverseCards.value,
    isAutoPlaying: isAutoPlaying.value,
    // Add any other relevant states from tempSettings or other refs if needed
  };
  localStorage.setItem('flashcardLearnStateBeforeTest', JSON.stringify(learnStateToSave));
  console.log('Saved FlashcardLearn state to localStorage:', learnStateToSave);

  // 2. Save current flashcards to store for the test
  const currentFlashcards = allItems.value.map(item => ({
    id: item.id, // Ensure ID is passed for correct answer matching
    front: item.content || '',
    back: item.backcontent || '',
    content: item.content || '',
    backcontent: item.backcontent || '',
    meaning: item.backcontent || '', // Assuming backcontent is meaning for 'word' type
    type: item.type || 'word', // Pass item type if available
    status: item.status || 'learning'
  }));

  console.log('Saving flashcards to store for test:', currentFlashcards);
  await store.dispatch('flashcard/setLearningItems', currentFlashcards);
  console.log('Successfully saved to store, now navigating...');

  // 3. Navigate
  await router.push({
    path: '/flashcard/test',
    query: {
      type: selectedTestType.value,
      deckId: route.query.deckId || '',
      source: route.query.source || ''
    }
  });

  console.log('Navigation successful');
  closeTestOptions();
} catch (err) {
  console.error('Error in startTest:', err);
}
};

// Xử lý sự kiện thay đổi kích thước màn hình
const handleResize = () => {
if (typeof window !== 'undefined') {
  const width = window.innerWidth;
  const height = window.innerHeight;
  if (swiperInstance.value) {
    swiperInstance.value.update();
  }
}
};

// Xử lý sự kiện thay đổi trạng thái fullscreen
const handleFullscreenChange = () => {
isFullscreen.value = document?.fullscreenElement !== null;
if (swiperInstance.value) {
  swiperInstance.value.update();
}
};

// Xử lý phím tắt
const handleKeydown = (e) => {
console.log('handleKeydown called with:', e.code);

if (showSettings.value) {
  console.log('Settings modal is open, ignoring keyboard shortcuts');
  return;
}

// Xử lý các phím điều hướng (cần swiper)
if (['ArrowLeft', 'ArrowRight', 'ArrowUp'].includes(e.code)) {
  const swiper = cardRef.value?.swiper;
  if (!swiper) {
    console.log('No swiper instance found for navigation');
    return;
  }

  switch (e.code) {
    case 'ArrowLeft':
      console.log('Previous slide');
      swiper.slidePrev();
      break;
    case 'ArrowRight':
      console.log('Next slide');
      swiper.slideNext();
      break;
    case 'ArrowUp':
      console.log('Flip card');
      if (cardRef.value?.flipCard) {
        cardRef.value.flipCard(swiper.activeIndex);
      }
      break;
  }
  return;
}

// Xử lý phím + và - (không cần swiper)
if (trackProgress.value) {
  console.log('Processing progress key:', {
    code: e.code,
    trackProgress: trackProgress.value
  });

  // Thêm hiệu ứng nhấn nút
  const button = e.code.includes('Minus') || e.code.includes('Subtract')
    ? document.querySelector('.progress-btn.learning')
    : document.querySelector('.progress-btn.known');

  if (button) {
    button.classList.add('pressed');
    setTimeout(() => button.classList.remove('pressed'), 200);
  }

  switch (e.code) {
    case 'Minus':
    case 'NumpadSubtract':
      console.log('Mark as learning');
      updateCardStatus('learning');
      break;
    case 'Equal':
    case 'NumpadAdd':
      console.log('Mark as known');
      updateCardStatus('known');
      break;
  }
} else {
  console.log('Track progress is disabled');
}
};

onMounted(() => {
// Attempt to restore state if returning from test OR match
const savedLearnStateFromTestString = localStorage.getItem('flashcardLearnStateBeforeTest');
const savedLearnStateFromMatchString = localStorage.getItem('flashcardLearnStateBeforeMatch');

let savedLearnStateString = null;
let fromKey = '';

if (savedLearnStateFromTestString) {
  savedLearnStateString = savedLearnStateFromTestString;
  fromKey = 'flashcardLearnStateBeforeTest';
} else if (savedLearnStateFromMatchString) {
  savedLearnStateString = savedLearnStateFromMatchString;
  fromKey = 'flashcardLearnStateBeforeMatch';
}

if (savedLearnStateString) {
  try {
    const savedState = JSON.parse(savedLearnStateString);
    console.log(`Restoring FlashcardLearn state from localStorage (key: ${fromKey}):`, savedState);

    // Restore allItems and dependent states
    allItems.value = addIdsToItems(savedState.allItems || []); // Ensure IDs are re-added if not saved or structure changed

    // Restore learningStats
    if (savedState.learningStats) {
      learningStats.known = savedState.learningStats.known || 0;
      learningStats.learning = savedState.learningStats.learning || 0;
      learningStats.remaining = savedState.learningStats.remaining || 0;
    }
    updateCounts(); // Recalculate based on restored allItems and their statuses

    // Restore settings
    activeMode.value = savedState.activeMode || 'flashcard';
    autoplaySpeed.value = savedState.autoplaySpeed || 3;
    trackProgress.value = savedState.trackProgress || false;
    reverseCards.value = savedState.reverseCards || false;
    isAutoPlaying.value = savedState.isAutoPlaying || false;

    // Update tempSettings to reflect restored main settings
    tempSettings.autoplaySpeed = autoplaySpeed.value;
    tempSettings.trackProgress = trackProgress.value;
    tempSettings.reverseCards = reverseCards.value;

    // Restore swiper position and autoplay
    // slides.value should update automatically via watch(allItems, ...)
    // Wait for slides to update and swiper to be ready
    nextTick(() => {
      if (swiperInstance.value) {
        swiperInstance.value.slideTo(savedState.currentSlideIndex || 0, 0); // No animation
        swiperInstance.value.update(); // Ensure swiper reflects changes
        if (isAutoPlaying.value) {
          startAutoplay(); // Restart autoplay if it was active
        }
      }
    });

  } catch (error) {
    console.error('Error restoring FlashcardLearn state:', error);
  } finally {
    if (fromKey) {
      localStorage.removeItem(fromKey); // Clean up the used key
    }
  }
} else {
  // Normal mount: load items from store or default initialization
  const items = store.getters['flashcard/getLearningItems'];
  console.log('Initial items from store (no restore state found):', items);
  allItems.value = addIdsToItems(items);
  updateCounts();
}

// Initialize slides (this will run after potential state restoration or initial load)
// The watch on allItems should handle updating slides.value correctly.
// If not, explicit call to update slides might be needed here or ensure watch on allItems is robust.

// Add event listeners
if (typeof window !== 'undefined') {
  document.addEventListener('keydown', handleKeydown);
  document.addEventListener('fullscreenchange', handleFullscreenChange);
  window.addEventListener('resize', handleResize);
}
});

onUnmounted(() => {
console.log('Component unmounting, cleaning up...');
if (autoplayInterval.value) {
  console.log('Clearing existing interval');
  clearInterval(autoplayInterval.value);
}
if (typeof window !== 'undefined') {
  console.log('Removing event listeners...');
  document.removeEventListener('keydown', handleKeydown);
  document.removeEventListener('fullscreenchange', handleFullscreenChange);
  window.removeEventListener('resize', handleResize);
}
});

// Lấy danh sách items từ store và chuyển đổi thành format slides
const slides = ref([]);
const allItems = ref([]);

// Computed properties for filtered and processed items
const displayLearningItems = computed(() => {
return allItems.value.filter(item => !item.status || item.status === 'learning');
});

const displayKnownItems = computed(() => {
return allItems.value.filter(item => item.status === 'known');
});

// Methods
const onSwiper = (swiper) => {
console.log('Swiper instance created');
swiperInstance.value = swiper;
};

// Phương thức để tự động chuyển slide
const startAutoplay = () => {
console.log('Starting autoplay...');
const swiper = swiperInstance.value;
if (!swiper) {
  console.log('No swiper instance found');
  return;
}

// Clear existing interval if any
if (autoplayInterval.value) {
  console.log('Clearing existing interval');
  clearInterval(autoplayInterval.value);
}

// Create new interval
console.log('Creating new interval with speed:', autoplaySpeed.value);
autoplayInterval.value = setInterval(() => {
  console.log('Auto advancing slide...');
  if (swiper.isEnd) {
    console.log('Reached end, going to first slide');
    swiper.slideTo(0);
  } else {
    console.log('Moving to next slide');
    swiper.slideNext();
  }
}, autoplaySpeed.value * 1000);
};
// Dừng autoplay
const stopAutoplay = () => {
console.log('Stopping autoplay...');
if (autoplayInterval.value) {
  clearInterval(autoplayInterval.value);
  autoplayInterval.value = null;
}
};

// Tạm dừng autoplay khi nhấn nút "Phát"
const toggleAutoplay = () => {
console.log('Toggle autoplay, current state:', isAutoPlaying.value);
const swiper = swiperInstance.value;
if (!swiper) {
  console.log('No swiper instance found');
  return;
}

if (isAutoPlaying.value) {
  stopAutoplay();
  isAutoPlaying.value = false;
} else {
  startAutoplay();
  isAutoPlaying.value = true;
}
console.log('New autoplay state:', isAutoPlaying.value);
};




// Watch for autoplaySpeed changes
watch(autoplaySpeed, (newSpeed) => {
console.log('Autoplay speed changed to:', newSpeed);
if (isAutoPlaying.value) {
  console.log('Restarting autoplay with new speed');
  startAutoplay();
}
});

const shuffleCards = () => {
const currentItems = store.getters['flashcard/getLearningItems'];
const shuffledItems = [...currentItems].sort(() => Math.random() - 0.5);
store.commit('flashcard/setLearningItems', shuffledItems);

nextTick(() => {
  const swiper = cardRef.value?.swiper;
  if (swiper) {
    swiper.slideTo(0, 0);
    swiper.update();
  }
});
};

// Hàm mở modal cài đặt
const openSettings = () => {
// Dừng autoplay khi vào cài đặt
if (isAutoPlaying.value) {
  stopAutoplay();
  isAutoPlaying.value = false; // Đảm bảo nút "Phát" chuyển thành "Tạm dừng"
}
// Sao chép giá trị hiện tại vào tempSettings
tempSettings.autoplaySpeed = autoplaySpeed.value;
tempSettings.trackProgress = trackProgress.value;
tempSettings.reverseCards = reverseCards.value;
showSettings.value = true;
};

// Hàm hủy thay đổi cài đặt
const cancelSettings = () => {
showSettings.value = false;
showShortcuts.value = false;
};

// Hàm áp dụng cài đặt
const applySettings = () => {
// Áp dụng các giá trị từ tempSettings
autoplaySpeed.value = tempSettings.autoplaySpeed;

// Nếu bật "Theo dõi tiến độ", hãy dừng chế độ tự động phát
if (tempSettings.trackProgress && !trackProgress.value) {
  if (isAutoPlaying.value) {
    stopAutoplay();
    isAutoPlaying.value = false; // Đảm bảo autoplay dừng khi chọn "Theo dõi tiến độ"
  }
}

trackProgress.value = tempSettings.trackProgress;

// Xử lý đảo mặt thẻ nếu có thay đổi
if (reverseCards.value !== tempSettings.reverseCards) {
  reverseCards.value = tempSettings.reverseCards;
  if (swiperInstance.value) {
    const currentSlides = [...slides.value];
    currentSlides.forEach(slide => {
      const temp = {
        content: slide.content,
        description: slide.description,
        backcontent: slide.backcontent,
        backdescription: slide.backdescription
      };
      slide.content = temp.backcontent;
      slide.description = temp.backdescription;
      slide.backcontent = temp.content;
      slide.backdescription = temp.description;
    });

    nextTick(() => {
      swiperInstance.value.update();
    });
  }
}

// Cập nhật autoplay nếu đang phát
if (isAutoPlaying.value && swiperInstance.value) {
  swiperInstance.value.autoplay.stop();
  swiperInstance.value.params.autoplay.delay = autoplaySpeed.value * 1000;
  swiperInstance.value.autoplay.start();
}

showSettings.value = false;
showShortcuts.value = false;
};


const toggleFullscreen = () => {
if (!document.fullscreenElement) {
  document.documentElement.requestFullscreen();
} else if (document.exitFullscreen) {
  document.exitFullscreen();
}
};

const onCardFlip = (index) => {
console.log('Card flipped at index:', index);
console.log('Current slide:', slides.value[index]);
if (trackProgress.value) {
  progress.value.reviewed++;
}
};

const onSlideChange = () => {
const swiper = swiperInstance.value;
if (swiper?.pagination) {
  swiper.pagination.render();
  swiper.pagination.update();
}
currentSlideIndex.value = swiper?.activeIndex || 0;
};

// Reset functionality
const resetCards = () => {
console.log('=== Resetting Cards ===');
console.log('Before reset:', {
  allItems: allItems.value,
  learning: displayLearningItems.value.length,
  known: displayKnownItems.value.length
});

// Reset status của tất cả các items về 'learning'
const resetItems = allItems.value.map(item => ({
  ...item,
  status: 'learning'
}));

// Cập nhật store và local state
store.commit('flashcard/setLearningItems', resetItems);
allItems.value = resetItems;

// Reset các số liệu thống kê
learningStats.known = 0;
learningStats.learning = resetItems.length;
learningStats.remaining = resetItems.length;

// Cập nhật counts
updateCounts();

// Đóng modal kết quả
showResults.value = false;

// Quay về thẻ đầu tiên
nextTick(() => {
  if (swiperInstance.value) {
    swiperInstance.value.slideTo(0);
  }
});

console.log('After reset:', {
  allItems: resetItems,
  learning: displayLearningItems.value.length,
  known: displayKnownItems.value.length,
  stats: learningStats
});
};

const handleReset = () => {
resetCards();
};

// Thêm hàm reviewUnknownCards
const reviewUnknownCards = () => {
console.log('=== Reviewing Unknown Cards ===');

// Lọc ra các thẻ chưa thuộc
const unknownCards = allItems.value.filter(item => item.status !== 'known');

// Reset trạng thái của các thẻ chưa thuộc về 'learning'
const updatedItems = allItems.value.map(item => {
  if (item.status !== 'known') {
    return { ...item, status: 'learning' };
  }
  return item;
});

// Cập nhật store và local state
store.commit('flashcard/setLearningItems', updatedItems);
allItems.value = updatedItems;

// Cập nhật counts
updateCounts();

// Đóng modal kết quả
showResults.value = false;

// Quay về thẻ đầu tiên
nextTick(() => {
  if (swiperInstance.value) {
    swiperInstance.value.slideTo(0);
  }
});

console.log('After review setup:', {
  unknownCount: unknownCards.length,
  learning: displayLearningItems.value.length,
  known: displayKnownItems.value.length,
  stats: learningStats
});
};

// Watch cho trackProgress để reset trạng thái khi tắt theo dõi
watch(trackProgress, (newValue) => {
if (!newValue) {
  // Reset các trạng thái khi tắt theo dõi tiến độ
  slides.value.forEach(slide => {
    slide.status = null;
  });
  learningStats.known = 0;
  learningStats.learning = 0;
  learningStats.remaining = slides.value.length;
  showStatusAnimation.value = false;
  showResults.value = false;
}
});

// Update counts
const updateCounts = () => {
learningStats.learning = displayLearningItems.value.length;
learningStats.known = displayKnownItems.value.length;
learningStats.remaining = allItems.value.length - learningStats.known;
};

// Helpers for getting item content
const getItemContent = (item) => {
return item.content || item.kanji || '';
};

const getItemDefinition = (item) => {
return item.backcontent || item.meaning || '';
};

// Add unique IDs to items
const addIdsToItems = (items) => {
return items.map((item, index) => ({
  ...item,
  id: `item-${index}-${item.content || item.kanji || Date.now()}`
}));
};

// The single updateCardStatus function
const updateCardStatus = (status) => {
if (!trackProgress.value) {
  console.log('Track progress is disabled');
  return;
}

const currentIndex = swiperInstance.value?.activeIndex || 0;
console.log('Updating card status:', { currentIndex, status });

if (allItems.value[currentIndex]) {
  // Update the item's status
  allItems.value = allItems.value.map((item, index) => {
    if (index === currentIndex) {
      return { ...item, status };
    }
    return item;
  });

  // Update store
  store.commit('flashcard/setLearningItems', allItems.value);

  // Update counts
  updateCounts();

  console.log('After update:', {
    allItems: allItems.value,
    learning: displayLearningItems.value,
    known: displayKnownItems.value,
    learningCount: learningStats.learning,
    knownCount: learningStats.known
  });

  // Animation and UI updates
  currentStatus.value = status;
  showStatusAnimation.value = true;
  setTimeout(() => {
    showStatusAnimation.value = false;
  }, 1000);

  // Check if last slide
  const isLastSlide = currentIndex === allItems.value.length - 1;
  if (isLastSlide) {
    showResults.value = true;
  } else {
    swiperInstance.value?.slideNext();
  }
}
};

// Watch for store changes
watch(() => store.getters['flashcard/getLearningItems'], (newItems) => {
console.log('Store items updated:', newItems);
allItems.value = addIdsToItems(newItems);
updateCounts();
}, { deep: true });

// Methods for term management
const addNewTerm = () => {
// TODO: Implement add new term functionality
console.log('Add new term');
};

const editTerm = (slide, index) => {
// TODO: Implement edit term functionality
console.log('Edit term:', slide, index);
};

const deleteTerm = (index) => {
// TODO: Implement delete term functionality
console.log('Delete term at index:', index);
};

// Watch để cập nhật UI khi slides thay đổi
watch(slides, () => {
nextTick(() => {
  // Force re-render của các list
  const container = document.querySelector('.terms-container');
  if (container) {
    container.style.opacity = '0.99';
    setTimeout(() => {
      // Re-check container existence, as component might have unmounted
      const currentContainer = document.querySelector('.terms-container');
      if (currentContainer) {
        currentContainer.style.opacity = '1';
      }
    }, 0);
  }
});
}, { deep: true });

// Watch để debug các thay đổi
watch([displayLearningItems, displayKnownItems], ([newLearning, newKnown], [oldLearning, oldKnown]) => {
console.log('=== Lists Update Debug ===');
console.log('Learning list changed:', {
  count: newLearning.length,
  oldCount: oldLearning?.length,
  items: newLearning.map(item => ({
    content: item.content,
    status: item.status
  }))
});

console.log('Known list changed:', {
  count: newKnown.length,
  oldCount: oldKnown?.length,
  items: newKnown.map(item => ({
    content: item.content,
    status: item.status
  }))
});

console.log('Total items:', newLearning.length + newKnown.length);
console.log('=== End Debug ===');
}, { deep: true });

// Navigation function
const navigateToTermCreation = () => {
// Lưu trạng thái hiện tại vào store hoặc localStorage
const currentState = {
  items: allItems.value,
  learningStats: {
    known: learningStats.known,
    learning: learningStats.learning,
    remaining: learningStats.remaining
  },
  settings: {
    isAutoPlaying: isAutoPlaying.value,
    autoplaySpeed: autoplaySpeed.value,
    trackProgress: trackProgress.value,
    reverseCards: reverseCards.value,
    activeMode: activeMode.value,
    currentSlideIndex: swiperInstance.value?.activeIndex || 0
  },
  fromLearningPage: true
};

// Dừng autoplay nếu đang chạy
if (isAutoPlaying.value) {
  stopAutoplay();
}

// Lưu state vào localStorage
localStorage.setItem('flashcardLearnState', JSON.stringify(currentState));

// Chuyển hướng đến trang tạo thuật ngữ với query params
router.push({
  name: 'CreateFlashcard',
  query: {
    fromLearn: 'true',
    deckId: route.query.deckId || '',
    source: route.query.source || ''
  }
});
};

// Watch for route changes to update data when returning from edit page
watch(
() => route.query,
() => {
  // Kiểm tra nếu có dữ liệu mới từ store
  const storeItems = store.getters['flashcard/getLearningItems'];
  if (storeItems && storeItems.length > 0) {
    console.log('Updating items from store:', storeItems);
    allItems.value = addIdsToItems(storeItems);
    updateCounts();
  }
},
{ immediate: true, deep: true }
);

// Thêm hàm để cập nhật slides khi allItems thay đổi
watch(allItems, (newItems) => {
if (newItems.length > 0) {
  console.log('Updating slides from allItems:', newItems);
  slides.value = newItems.map(item => {
    let frontContent, backContent, description, backDescription;
    let title = 'Từ vựng';

    switch (item.type) {
      case 'kanji':
        title = 'Hán tự';
        frontContent = item.kanji || '';
        description = '';
        backContent = item.kanjiname || '';
        backDescription = item.kunyomi || '';
        break;

      case 'grammar':
        title = 'Ngữ pháp';
        frontContent = item.kana || '';
        description = item.romaji || '';
        backContent = item.meaning || '';
        backDescription = '';
        break;

      case 'sentence':
        title = 'Mẫu câu';
        frontContent = item.sentence || '';
        description = '';
        backContent = item.translation || '';
        backDescription = '';
        break;

      case 'word':
      default:
        frontContent = item.front || item.kanji || '';
        description = item.kana || '';
        backContent = item.back || item.meaning || '';
        backDescription = '';
    }

    return {
      id: item.id,
      title,
      content: frontContent,
      description: description,
      backcontent: backContent,
      backdescription: backDescription,
      bgColor: '#ffffff',
      progressColor: '$primary-color',
      status: item.status || 'learning'
    };
  });

  // Cập nhật swiper nếu cần
  nextTick(() => {
    if (swiperInstance.value) {
      swiperInstance.value.update();
    }
  });
}
}, { deep: true });

// Sửa lại hàm saveFlashcard trong CreateFlashcard để cập nhật store
const saveFlashcard = async () => {
if (!validateForm()) {
  return;
}

try {
  const flashcardData = {
    title: title.value.trim(),
    description: description.value.trim(),
    cards: cards.value.filter(card => card.front.trim() && card.back.trim()).map(card => ({
      ...card,
      type: 'word',
      status: 'learning'
    })),
    cardCount: cards.value.filter(card => card.front.trim() && card.back.trim()).length
  };

  // Cập nhật store với các thẻ mới
  await store.dispatch('flashcard/setLearningItems', flashcardData.cards);

  // Clean up storage
  localStorage.removeItem(STORAGE_KEY);
  localStorage.removeItem('flashcardLearnState');

  // Navigate based on source
  if (fromLearningPage.value) {
    goBackToLearning();
  } else {
    router.push('/library');
  }
} catch (error) {
  console.error('Error saving flashcard:', error);
}
};
</script>

<style lang="scss" scoped>
@use '@/views/flashcard/flashcard-learn/FlashcardLearn.scss';
</style>