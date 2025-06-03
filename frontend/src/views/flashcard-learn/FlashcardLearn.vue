<template>
  <div class="flashcard-learn" :class="{ 'is-fullscreen': isFullscreen }">
    <h2 class="deck-title">
      {{ deckTitle }}
    </h2>
    
    <div class="main-content">
      <!-- Menu bên trái -->
      <div class="side-menu">
        <div class="menu-item" :class="{ active: activeMode === 'flashcard' }" @click="setMode('flashcard')">
          <i class="fas fa-sticky-note"></i>
          <span>Thẻ ghi nhớ</span>
        </div>
        <div class="menu-item" :class="{ active: activeMode === 'test' }" @click="setMode('test')">
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
          :width="isFullscreen ? 900 : 700" 
          :height="isFullscreen ? 500 : 400" 
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
        <button 
          class="control-btn" 
          :class="{ 'disabled': trackProgress }"
          @click="!trackProgress && shuffleCards"
        >
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
        <button 
          class="progress-btn learning" 
          :class="{ active: slides[currentSlideIndex]?.status === 'learning', pressed: slides[currentSlideIndex]?.status === 'learning' }"
          @click="updateCardStatus('learning')"
        >
          <i class="fas fa-minus"></i>
          <span>Đang học</span>
          <span class="count">({{ learningStats.learning }})</span>
        </button>
        <button 
          class="progress-btn known" 
          :class="{ active: slides[currentSlideIndex]?.status === 'known', pressed: slides[currentSlideIndex]?.status === 'known' }"
          @click="updateCardStatus('known')"
        >
          <i class="fas fa-plus"></i>
          <span>Đã biết</span>
          <span class="count">({{ learningStats.known }})</span>
        </button>
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
              <button 
                @click="tempSettings.autoplaySpeed = Math.max(1, tempSettings.autoplaySpeed - 1)"
                class="speed-btn"
              >
                <i class="fas fa-minus"></i>
              </button>
              <span class="speed-value">{{ tempSettings.autoplaySpeed }}s</span>
              <button 
                @click="tempSettings.autoplaySpeed = Math.min(20, tempSettings.autoplaySpeed + 1)"
                class="speed-btn"
              >
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
          <img src="@/assets/img/celebration.jpg" alt="Celebration" class="celebration-image" />
          <h2>{{ learningStats.known === slides.length ? 
            'Chà, bạn nắm bài thật chắc! Bạn đã sắp xếp tất cả các thẻ.' : 
            'Bạn đang làm rất tốt! Hãy tiếp tục để tăng cường tự tin' }}</h2>
        </div>

        <div class="progress-section">
          <h3>Tiến độ của bạn</h3>
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick, reactive } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import TheCard from '@/components/layout/card/TheCard.vue';

const store = useStore();
const route = useRoute();
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

// Computed title based on source
const deckTitle = computed(() => {
  const fromLibrary = route.query.source === 'library';
  const setTitle = route.query.title;
  return fromLibrary ? setTitle : 'Kho Thẻ Tạm Thời';
});

// Method to handle mode changes
const setMode = (mode) => {
  activeMode.value = mode;
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

    switch(e.code) {
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

    switch(e.code) {
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
  const items = store.getters['flashcard/getLearningItems'];
  console.log('FlashcardLearn mounted, items:', items);
  
  // Thêm event listeners
  if (typeof window !== 'undefined') {
    console.log('Adding event listeners...');
    document.addEventListener('keydown', (e) => {
      console.log('Keydown event triggered:', e.code);
      handleKeydown(e);
    });
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
const slides = computed(() => {
  const learningItems = store.getters['flashcard/getLearningItems'];
  console.log('Raw learning items:', JSON.stringify(learningItems, null, 2));
  
  return learningItems.map(item => {
    console.log('Processing item type:', item.type);
    console.log('Item full structure:', item);
    
    // Xử lý nội dung mặt trước và mặt sau dựa vào loại
    let frontContent, backContent, description, backDescription;
    let title = 'Từ vựng';
    
    switch(item.type) {
      case 'kanji':
        // Hán tự - kanji ở mặt trước, reading ở description
        title = 'Hán tự';
        frontContent = item.kanji || '';
        description = '';
        backContent = item.kanjiname || '';
        backDescription = item.kunyomi || '';
        break;
        
      case 'grammar':
        // Ngữ pháp - kana ở description, meaning ở mặt sau
        title = 'Ngữ pháp';
        frontContent = item.kana || '';
        description = item.romaji || '';
        backContent = item.meaning || '';
        backDescription = '';
        break;
        
      case 'sentence':
        // Mẫu câu
        title = 'Mẫu câu';
        frontContent = item.sentence || '';
        description =  '';
        backContent =  item.translation || '';
        backDescription =  '';
        break;
        
      case 'word':
      default:
        // Từ vựng và mặc định
        frontContent = item.kanji || '';
        description = item.kana || '';
        backContent = item.meaning || '';
        backDescription = '';
    }

    // Thêm trạng thái học tập
    const slide = {
      title,
      content: frontContent,
      description: description,
      backcontent: backContent,
      backdescription: backDescription,
      bgColor: '#ffffff',
      progressColor: '#E94560',
      status: item.status || 'learning' // 'learning' hoặc 'known'
    };

    // Log kết quả xử lý cuối cùng
    console.log('Final processed slide:', {
      type: item.type,
      title: slide.title,
      front: slide.content,
      frontDesc: slide.description,
      back: slide.backcontent,
      backDesc: slide.backdescription
    });

    return slide;
  });
});

const totalCards = computed(() => slides.value.length);

// Methods
const onSwiper = (swiper) => {
  console.log('Swiper instance created');
  swiperInstance.value = swiper;
};

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

const stopAutoplay = () => {
  console.log('Stopping autoplay...');
  if (autoplayInterval.value) {
    clearInterval(autoplayInterval.value);
    autoplayInterval.value = null;
  }
};

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
        console.log('Before swap:', {...slide});
        slide.content = temp.backcontent;
        slide.description = temp.backdescription;
        slide.backcontent = temp.content;
        slide.backdescription = temp.description;
        console.log('After swap:', {...slide});
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

// Reset cards function
const resetCards = () => {
  // Reset trạng thái của tất cả các thẻ
  slides.value.forEach(slide => {
    slide.status = null; // Xóa trạng thái đã biết/đang học
  });

  // Reset các số liệu thống kê
  learningStats.known = 0;
  learningStats.learning = 0;
  learningStats.remaining = slides.value.length;

  // Reset progress
  progress.value = {
    total: slides.value.length,
    reviewed: 0,
    correct: 0
  };

  // Quay về thẻ đầu tiên
  nextTick(() => {
    if (swiperInstance.value) {
      swiperInstance.value.slideTo(0);
    }
  });

  // Đóng modal kết quả
  showResults.value = false;
};

const handleReset = () => {
  resetCards();
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

// Sửa lại hàm updateCardStatus để cập nhật số liệu ngay lập tức
const updateCardStatus = (status) => {
  if (!trackProgress.value) {
    console.log('Track progress is disabled');
    return;
  }
  
  const currentIndex = swiperInstance.value?.activeIndex || 0;
  console.log('Updating status:', {
    status,
    currentIndex,
    hasSwiper: !!swiperInstance.value
  });

  if (slides.value[currentIndex]) {
    // Lấy trạng thái cũ của thẻ để so sánh
    const oldStatus = slides.value[currentIndex].status;
    
    // Cập nhật trạng thái mới
    slides.value[currentIndex].status = status;
    
    // Cập nhật số liệu thống kê ngay lập tức
    if (oldStatus === 'known') {
      learningStats.known--;
    } else if (oldStatus === 'learning') {
      learningStats.learning--;
    }

    if (status === 'known') {
      learningStats.known++;
    } else if (status === 'learning') {
      learningStats.learning++;
    }
    
    // Hiển thị animation status ở góc
    currentStatus.value = status;
    showStatusAnimation.value = true;
    setTimeout(() => {
      showStatusAnimation.value = false;
    }, 1000);

    // Kiểm tra nếu là thẻ cuối cùng
    const isLastSlide = currentIndex === slides.value.length - 1;
    
    if (isLastSlide) {
      // Hiển thị kết quả
      showResults.value = true;
    } else {
      // Chuyển sang thẻ tiếp theo
      swiperInstance.value?.slideNext();
    }
  }
};

const reviewUnknownCards = () => {
  // Lọc ra các thẻ chưa thuộc (status không phải 'known')
  const unknownCards = slides.value.filter(slide => slide.status !== 'known');
  
  // Reset lại trạng thái của các thẻ chưa thuộc
  unknownCards.forEach(card => {
    card.status = 'learning';
  });

  // Ẩn kết quả
  showResults.value = false;

  // Quay lại thẻ đầu tiên
  nextTick(() => {
    if (swiperInstance.value) {
      swiperInstance.value.slideTo(0);
    }
  });
};

// Watch để cập nhật remaining khi known hoặc learning thay đổi
watch([() => learningStats.known, () => learningStats.learning], () => {
  learningStats.remaining = totalCards.value - learningStats.known - learningStats.learning;
});

// Khởi tạo giá trị ban đầu cho learningStats
onMounted(() => {
  learningStats.known = slides.value.filter(s => s.status === 'known').length;
  learningStats.learning = slides.value.filter(s => s.status === 'learning').length;
  learningStats.remaining = totalCards.value - learningStats.known - learningStats.learning;
});
</script>

<style lang="scss" scoped>
.flashcard-learn {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: white;
  padding: 0 20px;

  &.is-fullscreen {
    padding: 0;
    
    .deck-title,
    .side-menu,
    .control-menu,
    .settings-modal {
      display: none;
    }

    .main-content {
      padding: 0;
      margin: 0;
      gap: 0;
      min-height: calc(100vh - 140px);
    }

    .card-section {
      width: 100%;
      height: calc(100vh - 140px);
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 20px;
      margin-top: -130px;
    }

    .progress-buttons-container {
      position: fixed;
      bottom: 40px;
      left: 0;
      right: 0;
      padding: 0 40px;
      z-index: 100;

      .progress-buttons {
        background: rgba(255, 255, 255, 0.9);
        padding: 16px 24px;
        border-radius: 16px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        
        .progress-btn {
          font-size: 18px;
          padding: 16px 32px;

          .count {
            margin-left: 8px;
            font-size: 16px;
            opacity: 0.8;
          }

          i {
            font-size: 20px;
          }

          &.learning {
            .count {
              color: #666;
            }
          }

          &.known {
            .count {
              color: white;
            }
          }
        }
      }
    }
  }
}

.deck-title {
  font-size: 24px;
  color: #333;
  margin: 12px 0;
  text-align: center;
}

.main-content {
  display: flex;
  gap: 24px;
  justify-content: center;
  align-items: center;
  padding: 0 20px;
  margin-top: 20px;
}

.card-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.side-menu, .control-menu {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  width: 280px;
  height: 400px;
  justify-content: space-between;

  .menu-item, .control-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 12px;
    padding: 24px;
    height: calc(33.33% - 8px); // Giảm gap nên cần điều chỉnh lại height
    cursor: pointer;
    border-radius: 12px;
    transition: all 0.2s;
    background: #f8f9fa;
    border: none;

    i {
      font-size: 28px;
      color: #666;
    }

    span {
      font-size: 14px;
      font-weight: 500;
      color: #666;
    }

    &:hover {
      background: #f1f3f5;
      transform: translateY(-2px);
    }

    &:active {
      transform: translateY(0);
    }

    &.active {
      background: #e3f2fd;
      i, span {
        color: #1976d2;
      }
    }
  }
}

.control-menu .control-btn {
  height: calc(25% - 9px); // Điều chỉnh cho 4 nút
}

.settings-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  z-index: 9999;
  padding-top: 80px;

  .modal-content {
    width: 360px;
    background: white;
    border-radius: 16px;
    padding: 24px;
    position: relative;
    max-height: calc(100vh - 160px);
    overflow-y: auto;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background: #E94560;
      border-radius: 3px;
    }

    h3 {
      margin: 0 0 24px 0;
      font-size: 20px;
      color: #333;
      position: sticky;
      top: 0;
      background: white;
      padding-bottom: 12px;
      border-bottom: 1px solid #eee;
    }
  }
}

.side-menu {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  width: 280px;
  height: 400px;
  justify-content: space-between;

  .menu-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 16px;
    padding: 30px;
    height: calc(33.33% - 14px);
    cursor: pointer;
    border-radius: 12px;
    transition: all 0.2s;
    background: #f8f9fa;

    i {
      font-size: 32px;
      color: #666;
    }

    span {
      font-size: 16px;
      font-weight: 500;
      color: #666;
    }

    &:hover {
      background: #fff1f3; // Màu hover nhẹ
      i, span {
        color: #E94560;
      }
    }

    &.active {
      background: #ffe3e7; // Màu nền nhạt hơn
      i, span {
        color: #E94560; // Màu chữ đỏ
      }
    }
  }
}

.control-menu {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  width: 280px;
  height: 400px;
  justify-content: space-between;

  .control-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 12px;
    padding: 24px;
    height: calc(25% - 9px);
    cursor: pointer;
    border-radius: 12px;
    transition: all 0.2s;
    background: #f8f9fa;
    border: none;

    i {
      font-size: 28px;
      color: #666;
    }

    span {
      font-size: 14px;
      font-weight: 500;
      color: #666;
    }

    &:hover {
      background: #fff1f3;
      transform: translateY(-2px);
      
      i, span {
        color: #E94560;
      }
    }

    &:active {
      transform: translateY(0);
    }

    &.playing {
      background: #ffe3e7;
      
      i, span {
        color: #E94560;
      }

      &:hover {
        background: #fff1f3;
        transform: translateY(-2px);
      }
    }

    &.disabled {
      opacity: 0.5;
      cursor: not-allowed;
      pointer-events: none;

      &:hover {
        background: #f8f9fa;
        transform: none;
      }

      i, span {
        color: #999;
      }
    }
  }
}

:deep(.fullscreen-card) {
  .card-title {
    font-size: 2.8rem !important;
  }

  .main-content-text {
    font-size: 2.4rem !important;
    line-height: 1.4 !important;
  }

  .card-description {
    font-size: 1.8rem !important;
  }

  .swiper-pagination {
    font-size: 1.4rem !important;
  }

  .cta-button {
    font-size: 1.2rem !important;
    padding: 15px 30px !important;
  }
}

.setting-item.checkbox {
  display: flex;
  align-items: center;
  gap: 8px;

  label {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
  }

  input[type="checkbox"] {
    width: auto;
    margin: 0;
  }
}

.shortcuts-btn, .reset-btn {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;

  &:hover {
    border-color: #E94560;
    color: #E94560;
  }

  i {
    font-size: 14px;
  }
}

.reset-btn {
  background: #fff1f3;
  border-color: #E94560;
  color: #E94560;

  &:hover {
    background: #E94560;
    color: white;
  }
}

.shortcuts-modal {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  padding: 24px;
  border-radius: 12px;
  width: 300px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.2);

  h4 {
    margin: 0 0 16px 0;
    color: #333;
  }
}

.shortcut-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.shortcut-item {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #666;

  .key {
    background: #f8f9fa;
    padding: 4px 8px;
    border-radius: 4px;
    border: 1px solid #ddd;
    font-size: 12px;
    min-width: 24px;
    text-align: center;
  }
}

.close-shortcuts-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  padding: 4px;
  
  &:hover {
    color: #333;
  }
}

.cancel-btn, .confirm-btn {
  padding: 8px 24px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn {
  background: white;
  border: 1px solid #ddd;
  color: #666;

  &:hover {
    background: #f8f9fa;
    border-color: #E94560;
    color: #E94560;
  }
}

.confirm-btn {
  background: #E94560;
  border: 1px solid #E94560;
  color: white;

  &:hover {
    background: #d13651;
    border-color: #d13651;
  }
}

.number-input {
  position: relative;
  display: flex;
  align-items: center;

  input[type="number"] {
    width: 100%;
    padding: 8px;
    padding-right: 32px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
    
    &:focus {
      outline: none;
      border-color: #E94560;
    }

    /* Ẩn nút tăng giảm mặc định của trình duyệt */
    &::-webkit-inner-spin-button,
    &::-webkit-outer-spin-button {
      -webkit-appearance: none;
      margin: 0;
    }
    -moz-appearance: textfield;
  }

  .number-controls {
    position: absolute;
    right: 0;
    top: 0;
    bottom: 0;
    display: flex;
    flex-direction: column;
    border-left: 1px solid #ddd;

    .number-btn {
      flex: 1;
      border: none;
      background: none;
      padding: 0 8px;
      cursor: pointer;
      color: #666;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s;

      &:hover {
        color: #E94560;
        background: #fff1f3;
      }

      &:active {
        background: #ffe3e7;
      }

      i {
        font-size: 10px;
      }

      &:first-child {
        border-bottom: 1px solid #ddd;
      }
    }
  }
}

.settings-body {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.setting-item {
  &.speed-control {
    label {
      display: block;
      margin-bottom: 12px;
      color: #666;
      font-size: 14px;
    }
  }

  &.toggle {
    display: flex;
    align-items: center;
    justify-content: space-between;
    
    .setting-label {
      color: #666;
      font-size: 14px;
    }
  }
}

.speed-buttons {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 8px;

  .speed-btn {
    width: 36px;
    height: 36px;
    border: none;
    border-radius: 6px;
    background: white;
    color: #666;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);

    &:hover {
      color: #E94560;
      background: #fff1f3;
    }

    &:active {
      background: #ffe3e7;
      transform: translateY(1px);
    }
  }

  .speed-value {
    font-size: 16px;
    font-weight: 500;
    color: #333;
    min-width: 40px;
    text-align: center;
  }
}

.settings-group {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
}

.toggle-switch {
  position: relative;
  display: inline-block;
  width: 44px;
  height: 24px;

  input {
    opacity: 0;
    width: 0;
    height: 0;

    &:checked + .toggle-slider {
      background-color: #E94560;
      
      &:before {
        transform: translateX(20px);
      }
    }
  }

  .toggle-slider {
    position: absolute;
    cursor: pointer;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: #ddd;
    transition: .3s;
    border-radius: 24px;

    &:before {
      position: absolute;
      content: "";
      height: 18px;
      width: 18px;
      left: 3px;
      bottom: 3px;
      background-color: white;
      transition: .3s;
      border-radius: 50%;
    }
  }
}

.settings-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;

  .action-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 12px;
    border-radius: 8px;
    border: none;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s;

    i {
      font-size: 14px;
    }

    &.shortcuts-btn {
      background: #f8f9fa;
      color: #666;

      &:hover {
        background: #fff1f3;
        color: #E94560;
      }
    }

    &.reset-btn {
      background: #fff1f3;
      color: #E94560;

      &:hover {
        background: #E94560;
        color: white;
      }
    }
  }
}

.modal-footer {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 12px;

  button {
    padding: 10px 24px;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;

    &.cancel-btn {
      background: white;
      border: 1px solid #ddd;
      color: #666;

      &:hover {
        border-color: #E94560;
        color: #E94560;
      }
    }

    &.confirm-btn {
      background: #E94560;
      border: 1px solid #E94560;
      color: white;

      &:hover {
        background: #d13651;
      }
    }
  }
}

.progress-buttons-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 0 20px;
}

.progress-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;

  .progress-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    border-radius: 8px;
    border: none;
    cursor: pointer;
    transition: all 0.2s;
    font-weight: 500;

    i {
      font-size: 16px;
    }

    &.learning {
      background: #f8f9fa;
      color: #666;

      .count {
        color: inherit;
      }

      &:hover, &.active {
        background: #fff1f3;
        color: #E94560;
      }

      &.pressed {
        background: #ffe3e7;
        color: #E94560;
      }
    }

    &.known {
      background: #E94560;
      color: white;

      .count {
        color: inherit;
      }

      &:hover, &.active {
        background: #d13651;
      }

      &.pressed {
        background: #c62e47;
      }
    }
  }
}

.status-animation {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 20px 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 500;
  z-index: 1000;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);

  &.known {
    background: #E94560;
    color: white;
  }

  &.learning {
    background: #f8f9fa;
    color: #666;
  }

  i {
    font-size: 24px;
  }
}

.status-fade-enter-active,
.status-fade-leave-active {
  transition: all 0.3s ease;
}

.status-fade-enter-from,
.status-fade-leave-to {
  opacity: 0;
  transform: translate(-50%, -40%);
}

.status-fade-enter-to,
.status-fade-leave-from {
  opacity: 1;
  transform: translate(-50%, -50%);
}

.results-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;

  .modal-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
  }

  .results-content {
    position: relative;
    background: white;
    border-radius: 16px;
    padding: 24px;
    width: 480px;
    z-index: 1;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  }
}

.results-header {
  text-align: center;
  margin-bottom: 20px;

  .celebration-image {
    width: 120px;
    margin-bottom: 16px;
  }

  h2 {
    font-size: 20px;
    color: #333;
    line-height: 1.4;
    margin: 0;
    padding: 0 20px;
  }
}

.progress-section {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;

  h3 {
    font-size: 16px;
    color: #333;
    margin: 0 0 12px 0;
  }

  .progress-items {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .progress-item {
    background: white;
    border-radius: 8px;
    padding: 12px;
    text-align: center;

    .label {
      font-size: 13px;
      color: #666;
      margin-bottom: 4px;
    }

    .count {
      font-size: 20px;
      font-weight: 600;
    }

    &.known .count { 
      color: #E94560; 
    }
    
    &.learning .count { 
      color: #666;
    }
  }
}

.next-steps {
  h3 {
    font-size: 16px;
    color: #333;
    margin: 0 0 12px 0;
  }

  button {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    border: none;
    font-size: 14px;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    cursor: pointer;
    margin-bottom: 8px;
    transition: all 0.2s;

    &:last-child {
      margin-bottom: 0;
    }

    i {
      font-size: 16px;
    }

    &.practice-btn {
      background: #E94560;
      color: white;

      &:hover {
        background: #d13651;
      }
    }

    &.review-btn {
      background: #f8f9fa;
      color: #666;

      &:hover {
        background: #e9ecef;
      }
    }

    &.reset-btn {
      background: #f8f9fa;
      color: #666;

      &:hover {
        background: #e9ecef;
      }
    }
  }
}
</style>
  