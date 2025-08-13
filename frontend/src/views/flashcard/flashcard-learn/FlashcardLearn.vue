<template>
  <div class="flashcard-learn" :class="{ 'is-fullscreen': isFullscreen }">
    <!-- Breadcrumb -->
    <div class="breadcrumb">
      <!-- Dynamic breadcrumb based on source -->
      <template v-if="route.query.source === 'search'">
        <router-link to="/" class="breadcrumb-link">{{ t('common.home') }}</router-link>
        <span class="breadcrumb-separator">></span>
        <router-link :to="route.query.searchQuery ? `/search?q=${encodeURIComponent(route.query.searchQuery)}` : '/search'" class="breadcrumb-link">{{ t('common.search') }}</router-link>
        <span class="breadcrumb-separator">></span>
        <span class="breadcrumb-current">{{ t('common.studyMaterial') }}</span>
      </template>
      <template v-else-if="route.query.source === 'library'">
        <router-link to="/" class="breadcrumb-link">{{ t('common.home') }}</router-link>
        <span class="breadcrumb-separator">></span>
        <router-link to="/library" class="breadcrumb-link">{{ t('common.library') }}</router-link>
        <span class="breadcrumb-separator">></span>
        <span class="breadcrumb-current">{{ t('common.studyMaterial') }}</span>
      </template>
      <template v-else-if="route.query.source === 'profile'">
        <router-link to="/" class="breadcrumb-link">{{ t('common.home') }}</router-link>
        <span class="breadcrumb-separator">></span>
        <router-link :to="`/forum/u/${route.query.creatorName}`" class="breadcrumb-link">{{ t('common.profile') }}</router-link>
        <span class="breadcrumb-separator">></span>
        <span class="breadcrumb-current">{{ t('common.studyMaterial') }}</span>
      </template>
      <template v-else>
        <router-link to="/" class="breadcrumb-link">{{ t('common.home') }}</router-link>
        <span class="breadcrumb-separator">></span>
        <span class="breadcrumb-current">{{ t('common.studyMaterial') }}</span>
      </template>
    </div>

    <div class="deck-header">
      <div>
        <h2 class="deck-title">
          {{ deckTitle }}
        </h2>
        <p class="description-text">{{ deckDescription }}</p>
      </div>
    </div>

    <div class="main-content">
      <!-- Menu bên trái -->
      <div class="side-menu">
        <div class="menu-item" :class="{ active: activeMode === 'flashcard' }" @click="setMode('flashcard')">
          <i class="fas fa-sticky-note"></i>
          <span>{{ t('flashcard.flashcards') }}</span>
        </div>
        <div class="menu-item" :class="{ active: activeMode === 'test' }" @click="openTestOptions">
          <i class="fas fa-tasks"></i>
          <span>{{ t('flashcard.test') }}</span>
        </div>
        <div class="menu-item" :class="{ active: activeMode === 'match' }" @click="setMode('match')">
          <i class="fas fa-puzzle-piece"></i>
          <span>{{ t('flashcard.match') }}</span>
        </div>
      </div>

      <!-- Card ở giữa -->
      <div class="card-section">
        <TheCard ref="cardRef" :slides="slides" :pagination="{
          type: 'fraction',
          clickable: true,
          formatFractionCurrent: (number) => number,
          formatFractionTotal: (number) => number
        }" :canFlip="true" :speed="300" :keyboard="{
            enabled: true,
            onlyInViewport: true
          }" :class="{ 'fullscreen-card': isFullscreen }" @swiper="onSwiper" @card-flipped="onCardFlip"
          @slideChange="onSlideChange" @translate-request="handleTranslateRequest"
          :width="isFullscreen ? 900 : undefined" :height="isFullscreen ? 500 : undefined" />
      </div>

      <!-- Control bar bên phải -->
      <div class="control-menu">
        <button class="control-btn" :class="{ 'playing': isAutoPlaying }" @click="toggleAutoplay">
          <i :class="isAutoPlaying ? 'fas fa-pause' : 'fas fa-play'"></i>
          <span>{{ isAutoPlaying ? t('flashcard.pause') : t('flashcard.play') }}</span>
        </button>
        <button class="control-btn" :class="{ 'disabled': trackProgress }" @click="handleShuffleClick">
          <i class="fas fa-random"></i>
          <span>{{ t('flashcard.shuffleCards') }}</span>
        </button>
        <button class="control-btn" @click="openSettings">
          <i class="fas fa-cog"></i>
          <span>{{ t('flashcard.settings') }}</span>
        </button>
        <button class="control-btn" @click="toggleFullscreen">
          <i class="fas fa-expand"></i>
          <span>{{ t('flashcard.fullscreen') }}</span>
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
          <span>{{ t('flashcard.learning') }}</span>
          <span class="count">({{ learningStats.learning }})</span>
        </button>
        <button class="progress-btn known"
          :class="{ active: slides[currentSlideIndex]?.status === 'known', pressed: slides[currentSlideIndex]?.status === 'known' }"
          @click="updateCardStatus('known')">
          <i class="fas fa-plus"></i>
          <span>{{ t('flashcard.known') }}</span>
          <span class="count">({{ learningStats.known }})</span>
        </button>
      </div>
    </div>
    <!-- Creator Info Section -->
    <div class="description-section">

      <!-- List Items Section -->
      <div class="list-items-section">
        <div class="list-header">
          <h3>{{ t('flashcard.termsInThisSet') }}</h3>
          <button class="add-term-btn" @click="navigateToTermCreation">
            <i class="fas fa-plus"></i>
            {{ t('flashcard.addOrRemoveTerms') }}
          </button>
        </div>

        <!-- Learning Terms -->
        <div class="terms-list">
          <h4 class="list-title">
            {{ t('flashcard.learning') }}
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
            {{ t('flashcard.known') }}
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
        <span>{{ currentStatus === 'known' ? t('flashcard.known') : t('flashcard.learning') }}</span>
      </div>
    </transition>

    <!-- Settings Modal -->
    <div v-if="showSettings" class="settings-modal">
      <div class="modal-content">
        <h3>{{ t('flashcard.settings') }}</h3>

        <div class="settings-body">
          <div class="setting-item speed-control">
            <label>{{ t('flashcard.autoplaySpeed') }}</label>
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
              <span class="setting-label">{{ t('flashcard.trackProgress') }}</span>
              <label class="toggle-switch">
                <input type="checkbox" v-model="tempSettings.trackProgress" />
                <span class="toggle-slider"></span>
              </label>
            </div>

            <div class="setting-item toggle">
              <span class="setting-label">{{ t('flashcard.reverseCards') }}</span>
              <label class="toggle-switch">
                <input type="checkbox" v-model="tempSettings.reverseCards" />
                <span class="toggle-slider"></span>
              </label>
            </div>
          </div>

          <div class="settings-actions">
            <button class="action-btn shortcuts-btn" @click="showShortcuts = true">
              <i class="fas fa-keyboard"></i>
              <span>{{ t('flashcard.shortcuts') }}</span>
            </button>

            <button class="action-btn reset-btn" @click="resetCards">
              <i class="fas fa-redo"></i>
              <span>{{ t('flashcard.restart') }}</span>
            </button>
          </div>
        </div>

        <div class="modal-footer">
          <button class="cancel-btn" @click="cancelSettings">{{ t('flashcard.cancel') }}</button>
          <button class="confirm-btn" @click="applySettings">{{ t('flashcard.confirm') }}</button>
        </div>
      </div>

      <!-- Shortcuts Modal -->
      <div v-if="showShortcuts" class="shortcuts-modal">
        <div class="shortcuts-content">
          <h4>{{ t('flashcard.shortcuts') }}</h4>
          <div class="shortcut-list">
            <div class="shortcut-item">
              <span class="key">←</span>
              <span>{{ t('flashcard.previousCard') }}</span>
            </div>
            <div class="shortcut-item">
              <span class="key">→</span>
              <span>{{ t('flashcard.nextCard') }}</span>
            </div>
            <div class="shortcut-item">
              <span class="key">↑</span>
              <span>{{ t('flashcard.flipCard') }}</span>
            </div>
            <div v-if="trackProgress" class="shortcut-item">
              <span class="key">-</span>
              <span>{{ t('flashcard.markAsLearning') }}</span>
            </div>
            <div v-if="trackProgress" class="shortcut-item">
              <span class="key">+</span>
              <span>{{ t('flashcard.markAsKnown') }}</span>
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
            t('flashcard.congratulations') :
            t('flashcard.keepGoing') }}</h2>
        </div>

        <div class="progress-section">
          <h3>{{ t('flashcard.progressOf') }}</h3>
          <div class="progress-items">
            <div class="progress-item learning">
              <div class="label">{{ t('flashcard.learning') }}</div>
              <div class="count">{{ learningStats.learning }}</div>
            </div>
            <div class="progress-item known">
              <div class="label">{{ t('flashcard.known') }}</div>
              <div class="count">{{ learningStats.known }}</div>
            </div>
          </div>
        </div>

        <div class="next-steps">
          <h3>{{ t('flashcard.nextSteps') }}</h3>
          <button class="practice-btn" @click="startPracticeTest">
            <i class="fas fa-sync-alt"></i>
            {{ t('flashcard.practiceWithQuestions') }}
          </button>
          <button v-if="learningStats.known !== slides.length" class="review-btn" @click="reviewUnknownCards">
            <i class="fas fa-graduation-cap"></i>
            {{ t('flashcard.reviewUnknownCards', { count: learningStats.learning }) }}
          </button>
          <button class="reset-btn" @click="handleReset">
            <i class="fas fa-redo"></i>
            {{ t('flashcard.resetFlashcards') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Test Options Modal -->
    <div v-if="showTestOptions" class="test-options-modal">
      <div class="modal-overlay" @click="closeTestOptions"></div>
      <div class="test-options-content">
        <h3>{{ t('flashcard.chooseTestType') }}</h3>
        <div class="test-options">
          <div class="test-option" :class="{ active: selectedTestType === 'multiple-choice' }"
            @click="selectedTestType = 'multiple-choice'">
            <div class="option-icon">
              <i class="fas fa-list-ul"></i>
            </div>
            <div class="option-details">
              <h4>{{ t('flashcard.multipleChoice') }}</h4>
              <p>{{ t('flashcard.multipleChoiceDesc') }}</p>
            </div>
          </div>

          <div class="test-option" :class="{ active: selectedTestType === 'written' }"
            @click="selectedTestType = 'written'">
            <div class="option-icon">
              <i class="fas fa-pen"></i>
            </div>
            <div class="option-details">
              <h4>{{ t('flashcard.written') }}</h4>
              <p>{{ t('flashcard.writtenDesc') }}</p>
            </div>
          </div>

          <div class="test-option" :class="{ active: selectedTestType === 'true-false' }"
            @click="selectedTestType = 'true-false'">
            <div class="option-icon">
              <i class="fas fa-check-circle"></i>
            </div>
            <div class="option-details">
              <h4>{{ t('flashcard.trueFalse') }}</h4>
              <p>{{ t('flashcard.trueFalseDesc') }}</p>
            </div>
          </div>
        </div>

        <div class="test-options-footer">
          <button class="cancel-btn" @click="closeTestOptions">{{ t('flashcard.cancel') }}</button>
          <button class="start-test-btn" :disabled="!selectedTestType" @click="startTest">
            <i class="fas fa-play"></i>
            {{ t('flashcard.startTest') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Translation Popup -->
    <ThePopup v-if="showTranslationPopup" :title="t('flashcard.translationTitle')" :message="translationResult"
      :confirmText="t('flashcard.close')" :showCancel="false" :useHtml="true" @confirm="closeTranslationPopup"
      @cancel="closeTranslationPopup" />
  </div>
</template>

<script setup>
import { ImagePaths } from '@/assets/img/imagePaths';
import { ref, computed, watch, onMounted, onUnmounted, nextTick, reactive } from 'vue';
import { useStore } from 'vuex';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import TheCard from '@/components/layout/card/TheCard.vue';
import ThePopup from '@/components/common/popup/ThePopup.vue';
import api from '@/api';
import flashcardApi from '@/api/modules/flashcardApi';
import { TransitionGroup } from 'vue';

const store = useStore();
const route = useRoute();
const router = useRouter();
const { t } = useI18n();
const cardRef = ref(null);
const swiperInstance = ref(null);
const activeMode = ref('flashcard');
const isAutoPlaying = ref(false);
const showSettings = ref(false);
const autoplaySpeed = ref(3);
const isFullscreen = ref(false);
const showTranslationPopup = ref(false);
const translationResult = ref('');
const isTranslating = ref(false);
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
const isCurrentUserCreator = computed(() => {
  const currentUser = store.getters['auth/getCurrentUser'];
  const creatorName = route.query.creatorName;

  // Check if current user is the creator
  return currentUser && creatorName &&
    (currentUser.userName === creatorName ||
      currentUser.firstName === creatorName ||
      `${currentUser.firstName} ${currentUser.lastName}`.trim() === creatorName);
});
const isFollowing = ref(false);
const creatorInfo = computed(() => {
  const source = route.query.source;

  if (source && (source === 'library' || source === 'profile')) {
    // Use data from query params when coming from library or profile
    const creatorName = route.query.creatorName || t('flashcard.user');
    const displayName = isCurrentUserCreator.value ? t('flashcard.you') : creatorName;

    return {
      avatar: route.query.creatorAvatar || ImagePaths.avatar.default,
      name: displayName,
      createdDate: formatCreatedDate(route.query.createdAt)
    };
  } else {
    // Default fallback for other sources
    return {
      avatar: ImagePaths.avatar.default,
      name: t('flashcard.you'),
      createdDate: t('flashcard.justCreated')
    };
  }
});

// Computed title based on source
const deckTitle = computed(() => {
  const source = route.query.source;
  const setTitle = route.query.title;

  // Show title from query params for library, profile, and other sources
  if (source && setTitle) {
    return setTitle;
  }

  // Default fallback only for temporary/create mode
  return t('flashcard.temporaryDeck');
});

// Computed description based on source
const deckDescription = computed(() => {
  const source = route.query.source;
  const setDescription = route.query.description;

  // Show description from query params for library, profile, and other sources
  if (source && setDescription) {
    return setDescription;
  }

  // Default fallback only for temporary/create mode
  return t('flashcard.flashcardDescription');
});

// Helper function to format created date
const formatCreatedDate = (timestamp) => {
  if (!timestamp) return t('flashcard.justCreated');

  try {
    const date = new Date(timestamp);
    const now = new Date();
    const diffInMs = now - date;
    const diffInDays = Math.floor(diffInMs / (1000 * 60 * 60 * 24));
    const diffInHours = Math.floor(diffInMs / (1000 * 60 * 60));
    const diffInMinutes = Math.floor(diffInMs / (1000 * 60));

    if (diffInDays > 0) {
      return t('flashcard.daysAgo', { count: diffInDays });
    } else if (diffInHours > 0) {
      return t('flashcard.hoursAgo', { count: diffInHours });
    } else if (diffInMinutes > 0) {
      return t('flashcard.minutesAgo', { count: diffInMinutes });
    } else {
      return t('flashcard.justCreated');
    }
  } catch (error) {
    return t('flashcard.justCreated');
  }
};

// Function to load flashcard data from API
const loadFlashcardData = async (setId) => {
  // First check if we already have data in store (from MyLibrary or ProfileDetail)
  const existingItems = store.getters['flashcard/getLearningItems'];
  if (existingItems && existingItems.length > 0) {

    allItems.value = addIdsToItems(existingItems);
    updateCounts();
    return;
  }

  // If no setId provided and no store data, can't proceed
  if (!setId) {

    return;
  }

  try {
    const flashcardData = await api.flashcard.getById(setId);
    if (flashcardData?.cardItems && flashcardData.cardItems.length > 0) {
    }
    // Convert API response to items format - matching MyLibrary.vue format
    const items = flashcardData.cardItems?.map(item => ({
      id: item.cardItemId || item.id || Math.random().toString(36).substr(2, 9),
      type: 'word',
      kanji: item.word || '',
      kana: '',
      meaning: item.meaning || '',
      content: item.word || '',
      backcontent: item.meaning || '',
      front: item.word || '',
      back: item.meaning || '',
      status: 'learning'
    })) || [];
    if (items.length > 0) {
    }
    await store.dispatch('flashcard/setLearningItems', items);
    allItems.value = addIdsToItems(items);
    updateCounts();
    // Reset progress if tracking is enabled
    if (trackProgress.value) {
      learningStats.known = 0;
      learningStats.learning = items.length;
      learningStats.remaining = items.length;
    }
    // Reset swiper to first slide
    nextTick(() => {
      if (swiperInstance.value) {
        swiperInstance.value.slideTo(0);
        swiperInstance.value.update();
      }
    });
  } catch (error) {
    const existingItems = store.getters['flashcard/getLearningItems'];
    if (existingItems && existingItems.length > 0) {
      allItems.value = addIdsToItems(existingItems);
      updateCounts();
    }
  }
};
// Method to handle mode changes
const setMode = async (mode) => {
  activeMode.value = mode;
  if (mode === 'match') {
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
      
      // 2. Save current flashcards to store for the match game with proper mapping
      const currentFlashcards = allItems.value.map(item => {
        // Determine front and back content based on item structure
        let frontContent, backContent;
        
        // Check if item has slide structure (from slides.value)
        if (item.content !== undefined && item.backcontent !== undefined) {
          frontContent = item.content;
          backContent = item.backcontent;
        }
        // Check if item has flashcard structure (from original data)
        else if (item.front !== undefined && item.back !== undefined) {
          frontContent = item.front;
          backContent = item.back;
        }
        // Check for different item types
        else {
          switch (item.type) {
            case 'kanji':
              frontContent = item.kanji || '';
              backContent = item.kanjiname || '';
              break;
            case 'grammar':
              frontContent = item.kana || '';
              backContent = item.meaning || '';
              break;
            case 'sentence':
              frontContent = item.sentence || '';
              backContent = item.translation || '';
              break;
            case 'word':
            default:
              frontContent = item.kanji || item.front || '';
              backContent = item.meaning || item.back || '';
          }
        }

        return {
          id: item.id || Math.random().toString(36).substr(2, 9), // Ensure ID exists
          front: frontContent,
          back: backContent,
          content: frontContent, // For compatibility
          backcontent: backContent, // For compatibility
          meaning: backContent, // For compatibility
          type: item.type || 'word',
          status: item.status || 'learning',
          // Preserve original item data for reference
          originalItem: item
        };
      });

      await store.dispatch('flashcard/setLearningItems', currentFlashcards);
      
      // 3. Navigate with proper query parameters
      await router.push({
        name: 'FlashcardMatch',
        query: {
          deckId: route.query.deckId || route.query.id || '',
          source: route.query.source || 'library',
          title: deckTitle.value || route.query.title || '',
          description: deckDescription.value || route.query.description || '',
          creatorName: route.query.creatorName || '',
          creatorAvatar: route.query.creatorAvatar || '',
          createdAt: route.query.createdAt || '',
          setId: route.query.setId || route.query.id || ''
        }
      });

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
    
    // 2. Save current flashcards to store for the test with proper mapping
    const currentFlashcards = allItems.value.map(item => {
      // Determine front and back content based on item structure
      let frontContent, backContent;
      
      // Check if item has slide structure (from slides.value)
      if (item.content !== undefined && item.backcontent !== undefined) {
        frontContent = item.content;
        backContent = item.backcontent;
      }
      // Check if item has flashcard structure (from original data)
      else if (item.front !== undefined && item.back !== undefined) {
        frontContent = item.front;
        backContent = item.back;
      }
      // Check for different item types
      else {
        switch (item.type) {
          case 'kanji':
            frontContent = item.kanji || '';
            backContent = item.kanjiname || '';
            break;
          case 'grammar':
            frontContent = item.kana || '';
            backContent = item.meaning || '';
            break;
          case 'sentence':
            frontContent = item.sentence || '';
            backContent = item.translation || '';
            break;
          case 'word':
          default:
            frontContent = item.kanji || item.front || '';
            backContent = item.meaning || item.back || '';
        }
      }

      return {
        id: item.id || Math.random().toString(36).substr(2, 9), // Ensure ID exists
        front: frontContent,
        back: backContent,
        content: frontContent, // For compatibility
        backcontent: backContent, // For compatibility
        meaning: backContent, // For compatibility
        type: item.type || 'word',
        status: item.status || 'learning',
        // Preserve original item data for reference
        originalItem: item
      };
    });

    await store.dispatch('flashcard/setLearningItems', currentFlashcards);
    
    // 3. Navigate with all necessary query parameters
    await router.push({
      path: '/flashcard/test',
      query: {
        type: selectedTestType.value,
        deckId: route.query.deckId || route.query.id || '',
        source: route.query.source || 'library',
        title: deckTitle.value || route.query.title || '',
        description: deckDescription.value || route.query.description || '',
        creatorName: route.query.creatorName || '',
        creatorAvatar: route.query.creatorAvatar || '',
        createdAt: route.query.createdAt || '',
        setId: route.query.setId || route.query.id || ''
      }
    });
    closeTestOptions();
  } catch (err) {
    console.error('Error in startTest:', err);
  }
};

// Hàm bắt đầu luyện tập với câu hỏi trắc nghiệm
const startPracticeTest = async () => {
  resetCards();
  // Đợi một chút để đảm bảo swiper đã cập nhật xong
  await new Promise(resolve => setTimeout(resolve, 100));
  selectedTestType.value = 'multiple-choice';
  await startTest();
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


  if (showSettings.value) {

    return;
  }
  // Xử lý các phím điều hướng (cần swiper)
  if (['ArrowLeft', 'ArrowRight', 'ArrowUp'].includes(e.code)) {
    const swiper = cardRef.value?.swiper;
    if (!swiper) {

      return;
    }
    switch (e.code) {
      case 'ArrowLeft':

        swiper.slidePrev();
        break;
      case 'ArrowRight':

        swiper.slideNext();
        break;
      case 'ArrowUp':

        if (cardRef.value?.flipCard) {
          cardRef.value.flipCard(swiper.activeIndex);
        }
        break;
    }
    return;
  }

  // Xử lý phím + và - (không cần swiper)
  if (trackProgress.value) {
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

        updateCardStatus('learning');
        break;
      case 'Equal':
      case 'NumpadAdd':

        updateCardStatus('known');
        break;
    }
  } else {

  }
};

onMounted(async () => {
  // Attempt to restore state if returning from test, match, OR create flashcard
  const savedLearnStateFromTestString = localStorage.getItem('flashcardLearnStateBeforeTest');
  const savedLearnStateFromMatchString = localStorage.getItem('flashcardLearnStateBeforeMatch');
  const savedLearnStateFromCreateString = localStorage.getItem('flashcardLearnState');

  let savedLearnStateString = null;
  let fromKey = '';

  if (savedLearnStateFromTestString) {
    savedLearnStateString = savedLearnStateFromTestString;
    fromKey = 'flashcardLearnStateBeforeTest';
  } else if (savedLearnStateFromMatchString) {
    savedLearnStateString = savedLearnStateFromMatchString;
    fromKey = 'flashcardLearnStateBeforeMatch';
  } else if (savedLearnStateFromCreateString) {
    savedLearnStateString = savedLearnStateFromCreateString;
    fromKey = 'flashcardLearnState';
  }

  if (savedLearnStateString) {
    try {
      const savedState = JSON.parse(savedLearnStateString);


      // Restore allItems and dependent states
      // Handle different field names: 'allItems' from test/match, 'items' from create flashcard
      const itemsToRestore = savedState.allItems || savedState.items || [];
      allItems.value = addIdsToItems(itemsToRestore); // Ensure IDs are re-added if not saved or structure changed

      // Restore learningStats
      if (savedState.learningStats) {
        learningStats.known = savedState.learningStats.known || 0;
        learningStats.learning = savedState.learningStats.learning || 0;
        learningStats.remaining = savedState.learningStats.remaining || 0;
      }
      updateCounts(); // Recalculate based on restored allItems and their statuses

      // Restore settings
      // Handle different structure: direct fields from test/match, 'settings' object from create flashcard
      if (savedState.settings) {
        // From create flashcard
        activeMode.value = savedState.settings.activeMode || 'flashcard';
        autoplaySpeed.value = savedState.settings.autoplaySpeed || 3;
        trackProgress.value = savedState.settings.trackProgress || false;
        reverseCards.value = savedState.settings.reverseCards || false;
        isAutoPlaying.value = savedState.settings.isAutoPlaying || false;
      } else {
        // From test/match (direct fields)
        activeMode.value = savedState.activeMode || 'flashcard';
        autoplaySpeed.value = savedState.autoplaySpeed || 3;
        trackProgress.value = savedState.trackProgress || false;
        reverseCards.value = savedState.reverseCards || false;
        isAutoPlaying.value = savedState.isAutoPlaying || false;
      }

      // Update tempSettings to reflect restored main settings
      tempSettings.autoplaySpeed = autoplaySpeed.value;
      tempSettings.trackProgress = trackProgress.value;
      tempSettings.reverseCards = reverseCards.value;

      // Restore swiper position and autoplay
      // Đảm bảo slides được cập nhật ngay lập tức
      if (allItems.value.length > 0) {
        // Trigger slides update manually to ensure immediate update
        const event = new Event('allItemsUpdated');
        window.dispatchEvent(event);
      }
      
      // Wait for slides to update and swiper to be ready
      nextTick(() => {
        if (swiperInstance.value) {
          // Handle different structure for currentSlideIndex
          const slideIndex = savedState.settings?.currentSlideIndex || savedState.currentSlideIndex || 0;
          swiperInstance.value.slideTo(slideIndex, 0); // No animation
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
    // Normal mount: load items from API or store
    const setId = route.query.id || route.query.setId;
    await loadFlashcardData(setId);
    
    // Đảm bảo slides được cập nhật sau khi load dữ liệu
    if (allItems.value.length > 0) {
      // Trigger slides update manually to ensure immediate update
      const event = new Event('allItemsUpdated');
      window.dispatchEvent(event);
    }
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

  if (autoplayInterval.value) {

    clearInterval(autoplayInterval.value);
  }
  if (typeof window !== 'undefined') {

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

  swiperInstance.value = swiper;
};
// Phương thức để tự động chuyển slide
const startAutoplay = () => {
  const swiper = swiperInstance.value;
  if (!swiper) {
    return;
  }
  if (slides.value.length === 0) {
    return;
  }
  
  // Clear existing interval if any
  if (autoplayInterval.value) {
    clearInterval(autoplayInterval.value);
  }
  // Create new interval
  autoplayInterval.value = setInterval(() => {
    if (swiper.isEnd) {
      swiper.slideTo(0);
    } else {
      swiper.slideNext();
    }
  }, autoplaySpeed.value * 1000);
};
// Dừng autoplay
const stopAutoplay = () => {

  if (autoplayInterval.value) {
    clearInterval(autoplayInterval.value);
    autoplayInterval.value = null;
  }
};
// Tạm dừng autoplay khi nhấn nút "Phát"
const toggleAutoplay = () => {
  const swiper = swiperInstance.value;
  if (!swiper) {
    return;
  }

  if (isAutoPlaying.value) {
    stopAutoplay();
    isAutoPlaying.value = false;
  } else {
    startAutoplay();
    isAutoPlaying.value = true;
  }
};
// Watch for autoplaySpeed changes
watch(autoplaySpeed, (newSpeed) => {
  if (isAutoPlaying.value) {
    startAutoplay();
  }
});
// Handle shuffle button click
const handleShuffleClick = () => {
  if (!trackProgress.value) {
    shuffleCards();
  }
};
const shuffleCards = () => {
  // Shuffle the allItems array directly
  const shuffledItems = [...allItems.value].sort(() => Math.random() - 0.5);
  // Update both local state and store
  allItems.value = shuffledItems;
  store.commit('flashcard/setLearningItems', shuffledItems);
  nextTick(() => {
    // Use the correct swiper instance reference
    if (swiperInstance.value) {

      swiperInstance.value.slideTo(0, 0);
      swiperInstance.value.update();
    } else {

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

// Xử lý yêu cầu dịch từ TheCard
const handleTranslateRequest = async (text) => {
  if (!text || isTranslating.value) return;

  // Hiển thị popup ngay lập tức với loading state
  translationResult.value = `<div class="loading-container">
      <div class="loading-spinner"></div>
      <p>${t('flashcard.translating')}</p>
    </div>`;
  showTranslationPopup.value = true;
  isTranslating.value = true;

  try {
    const response = await api.ai.translateJapaneseToVietnamese(text);

    // Format nội dung dịch để hiển thị đẹp hơn
    const explanation = response.explanation || t('flashcard.cannotTranslate');
    translationResult.value = `
          <div class="original-text">
            <h4>📝 ${t('flashcard.originalText')}:</h4>
            <p class="japanese-text">${text}</p>
          </div>
          <div class="translation-result">
            <h4>🎯 ${t('flashcard.translateAndExplain')}:</h4>
            <div class="explanation-text">${explanation}</div>
          </div>
      `;
  } catch (error) {
    console.error('Lỗi khi dịch:', error);
    translationResult.value = `
        <div class="error-content">
          <div class="error-icon">❌</div>
          <p>${t('flashcard.translationError')}</p>
        </div>
      `;
  } finally {
    isTranslating.value = false;
  }
};

// Đóng popup dịch
const closeTranslationPopup = () => {
  showTranslationPopup.value = false;
  translationResult.value = '';
};
const onCardFlip = (index) => {
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
};

const handleReset = () => {
  resetCards();
};

// Thêm hàm reviewUnknownCards
const reviewUnknownCards = () => {
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
  // Handle different item types like in slides mapping
  switch (item.type) {
    case 'kanji':
      return item.kanji || item.content || '';
    case 'grammar':
      return item.kana || item.content || '';
    case 'sentence':
      return item.sentence || item.content || '';
    case 'word':
    default:
      return item.content || item.kanji || item.front || '';
  }
};
const getItemDefinition = (item) => {
  // Handle different item types like in slides mapping
  switch (item.type) {
    case 'kanji':
      return item.kanjiname || item.backcontent || item.meaning || '';
    case 'grammar':
      return item.meaning || item.backcontent || '';
    case 'sentence':
      return item.translation || item.backcontent || '';
    case 'word':
    default:
      return item.backcontent || item.meaning || item.back || '';
  }
};
// Add unique IDs to items
const addIdsToItems = (items) => {
  return items.map((item, index) => {
    // Đảm bảo mapping đúng các trường dữ liệu
    let mappedItem = { ...item };
    
    // Nếu item có cấu trúc slide (từ localStorage)
    if (item.content && item.backcontent) {
      mappedItem = {
        ...item,
        front: item.content,
        back: item.backcontent,
        // Giữ nguyên các trường khác nếu có
        kanji: item.content,
        meaning: item.backcontent,
        kana: item.description || '',
        romaji: item.description || '',
        sentence: item.content,
        translation: item.backcontent,
        kanjiname: item.backcontent,
        kunyomi: item.backdescription || ''
      };
    }
    
    // Nếu item có cấu trúc flashcard gốc
    if (item.front && item.back) {
      mappedItem = {
        ...item,
        content: item.front,
        backcontent: item.back,
        // Map theo type nếu có
        ...(item.type === 'kanji' && {
          kanji: item.front,
          kanjiname: item.back,
          kunyomi: item.kana || ''
        }),
        ...(item.type === 'grammar' && {
          kana: item.front,
          meaning: item.back,
          romaji: item.description || ''
        }),
        ...(item.type === 'sentence' && {
          sentence: item.front,
          translation: item.back
        }),
        ...(item.type === 'word' && {
          kanji: item.front,
          meaning: item.back,
          kana: item.kana || ''
        })
      };
    }
    
    // Đảm bảo có ID
    mappedItem.id = item.id || `item-${index}-${item.content || item.front || item.kanji || Date.now()}`;
    
    return mappedItem;
  });
};
// The single updateCardStatus function
const updateCardStatus = (status) => {
  if (!trackProgress.value) {

    return;
  }

  const currentIndex = swiperInstance.value?.activeIndex || 0;

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

  allItems.value = addIdsToItems(newItems);
  updateCounts();
}, { deep: true });

const editTerm = (slide, index) => {
  // TODO: Implement edit term functionality
};
const deleteTerm = async (itemToDelete) => {
  try {
    if (!itemToDelete) {
      console.error('Item không tồn tại');
      return;
    }

    // Tìm index của item cần xóa
    const itemIndex = allItems.value.findIndex(item => item.id === itemToDelete.id);
    if (itemIndex === -1) {
      console.error('Không tìm thấy item trong danh sách');
      return;
    }

    // Tạo danh sách items mới (loại bỏ item cần xóa)
    const updatedItems = allItems.value.filter(item => item.id !== itemToDelete.id);

    // Kiểm tra nguồn dữ liệu để quyết định có gọi API hay không
    const source = route.query.source;
    const setId = route.query.id || route.query.setId;

    // Nếu có setId và không phải từ flashcard-list thì gọi API
    if (setId && source !== 'flashcard-list') {
      // Chuẩn bị body cho API update
      const updateBody = {
        title: deckTitle.value,
        description: deckDescription.value,
        cardItems: updatedItems.map(item => ({
          word: item.content || item.front || item.kanji || '',
          meaning: item.backcontent || item.back || item.meaning || ''
        }))
      };
      // Gọi API update
      await flashcardApi.update(setId, updateBody);
    } else {
      console.log('Chỉ cập nhật local, không gọi API (source:', source, ', setId:', setId, ')');
    }

    // Cập nhật store và UI (luôn thực hiện)
    await store.dispatch('flashcard/setLearningItems', updatedItems);
    
    // Nếu từ flashcard-list, cũng cần xóa khỏi store flashcard items
    if (source === 'flashcard-list') {
      await store.dispatch('flashcard/removeItem', itemToDelete);
    }
    
    allItems.value = addIdsToItems(updatedItems);
    updateCounts();

    // Cập nhật learning stats nếu đang track progress
    if (trackProgress.value) {
      learningStats.learning = updatedItems.filter(item => item.status === 'learning').length;
      learningStats.known = updatedItems.filter(item => item.status === 'known').length;
      learningStats.remaining = learningStats.learning;
    }

    // Cập nhật swiper
    nextTick(() => {
      if (swiperInstance.value) {
        // Nếu xóa slide cuối cùng, chuyển về slide trước đó
        const newActiveIndex = Math.min(currentSlideIndex.value, updatedItems.length - 1);
        swiperInstance.value.slideTo(newActiveIndex);
        swiperInstance.value.update();
      }
    });
  } catch (error) {
    console.error('Lỗi khi xóa item:', error);
    console.error('Error details:', error.response?.data || error.message);
  }
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

// Navigation function
const navigateToTermCreation = () => {
  // Lưu trạng thái hiện tại vào store hoặc localStorage
  const currentState = {
    items: allItems.value,
    title: deckTitle.value,
    description: deckDescription.value,
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
  const navigationQuery = {
    fromLearn: 'true',
    deckId: route.query.deckId || route.query.id || '',
    source: route.query.source || 'library',
    title: deckTitle.value,
    description: deckDescription.value
  };
  router.push({
    name: 'CreateFlashcard',
    query: navigationQuery
  });
};
// Watch for route changes to update data when returning from edit page
watch(
  () => route.query,
  async (newQuery, oldQuery) => {
    // Check if setId changed (navigating between different flashcards)
    const newSetId = newQuery.id || newQuery.setId;
    const oldSetId = oldQuery?.id || oldQuery?.setId;
    if (newSetId && newSetId !== oldSetId) {

      await loadFlashcardData(newSetId);

      return;
    }

    // Check if returning from CreateFlashcard with updated data
    if (newQuery.updated === 'true' && oldQuery?.updated !== 'true') {


      // Clean the updated flag from query
      const cleanQuery = { ...newQuery };
      delete cleanQuery.updated;
      router.replace({ query: cleanQuery });

      // Refresh data from store
      const storeItems = store.getters['flashcard/getLearningItems'];
      if (storeItems && storeItems.length > 0) {

        allItems.value = addIdsToItems(storeItems);
        updateCounts();

        // Reset progress tracking if was enabled
        if (trackProgress.value) {
          learningStats.known = 0;
          learningStats.learning = allItems.value.length;
          learningStats.remaining = allItems.value.length;
        }

        // Update swiper
        nextTick(() => {
          if (swiperInstance.value) {
            swiperInstance.value.slideTo(0);
            swiperInstance.value.update();
          }
        });
      }
    } else {

      // Normal route change handling
      const storeItems = store.getters['flashcard/getLearningItems'];
      if (storeItems && storeItems.length > 0) {

        allItems.value = addIdsToItems(storeItems);
        updateCounts();
      }
    }

  },
  { immediate: true, deep: true }
);

// Thêm hàm để cập nhật slides khi allItems thay đổi
watch(allItems, (newItems) => {
  if (newItems.length > 0) {
    slides.value = newItems.map(item => {
      let frontContent, backContent, description, backDescription;
      let title = t('common.vocabulary');

      switch (item.type) {
        case 'kanji':
          title = t('common.kanji');
          frontContent = item.kanji || '';
          description = '';
          backContent = item.kanjiname || '';
          backDescription = item.kunyomi || '';
          break;

        case 'grammar':
          title = t('common.grammar');
          frontContent = item.kana || '';
          description = item.romaji || '';
          backContent = item.meaning || '';
          backDescription = '';
          break;

        case 'sentence':
          title = t('common.sentences');
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