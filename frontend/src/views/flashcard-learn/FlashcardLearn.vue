<template>
  <div class="flashcard-learn">
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
          :width="700" 
          :height="400" 
          :pagination="{ 
            type: 'fraction',
            clickable: true
          }" 
          :canFlip="true"
          :autoplay="{
            delay: 5000,
            disableOnInteraction: false,
            pauseOnMouseEnter: true
          }"
          @autoplay-start="() => isAutoPlaying = true"
          @autoplay-stop="() => isAutoPlaying = false"
          @card-flipped="onCardFlip"
        />
      </div>

      <!-- Control bar bên phải -->
      <div class="control-menu">
        <button class="control-btn" :class="{ 'playing': isAutoPlaying }" @click="toggleAutoplay">
          <i :class="isAutoPlaying ? 'fas fa-pause' : 'fas fa-play'"></i>
          <span>{{ isAutoPlaying ? 'Tạm dừng' : 'Phát' }}</span>
        </button>
        <button class="control-btn" @click="shuffleCards">
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

    <!-- Settings Modal -->
    <div v-if="showSettings" class="settings-modal">
      <div class="modal-content">
        <h3>Cài đặt</h3>
        <div class="setting-item">
          <label>Tốc độ tự động chuyển (giây)</label>
          <input type="number" v-model="autoplaySpeed" min="1" max="10" />
        </div>
        <div class="setting-item">
          <label>Hiệu ứng chuyển thẻ</label>
          <select v-model="transitionEffect">
            <option value="slide">Trượt</option>
            <option value="fade">Mờ dần</option>
            <option value="flip">Lật</option>
          </select>
        </div>
        <button class="close-btn" @click="showSettings = false">
          <i class="fas fa-times"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import TheCard from '@/components/layout/card/TheCard.vue';

const store = useStore();
const route = useRoute();
const cardRef = ref(null);
const activeMode = ref('flashcard');
const isAutoPlaying = ref(false);
const showSettings = ref(false);
const autoplaySpeed = ref(3);
const transitionEffect = ref('slide');

// Computed title based on source
const deckTitle = computed(() => {
  const fromLibrary = route.query.source === 'library';
  const setTitle = route.query.title;
  return fromLibrary ? setTitle : 'Kho Thẻ Tạm Thời';
});

// Method to handle mode changes
const setMode = (mode) => {
  activeMode.value = mode;
  // Additional logic for mode changes can be added here
};

// Debug: Log khi component được mount
onMounted(() => {
  const items = store.getters['flashcard/getLearningItems'];
  console.log('FlashcardLearn mounted, items:', items);
});

// Lấy danh sách items từ store và chuyển đổi thành format slides
const slides = computed(() => {
  const learningItems = store.getters['flashcard/getLearningItems'];
  console.log('Learning items in slides computed:', learningItems);
  
  return learningItems.map(item => {
    // Xử lý đặc biệt cho flashcard type
    if (item.type === 'flashcard') {
      return {
        title: 'Thẻ ghi nhớ',
        content: item.kanji, // Mặt trước (front)
        description: '', // Có thể thêm mô tả nếu cần
        backcontent: item.meaning, // Mặt sau (back)
        backdescription: '', // Có thể thêm mô tả phụ nếu cần
        bgColor: '#ffffff',
        progressColor: '#E94560'
      };
    }

    // Giữ nguyên logic cũ cho các loại khác
    let mainText = '';
    let subText = '';
    let backText = '';
    let backSubText = '';

    switch (item.type) {
      case 'word':
        mainText = item.kanji || item.kana;
        subText = item.kana;
        backText = item.meaning;
        break;
      case 'kanji':
        mainText = item.kanji;
        subText = item.kunyomi;
        backText = item.kanjiname;
        backSubText = '';
        break;
      case 'grammar':
        mainText = item.kana || item.pattern;
        subText = item.pattern || '';
        backText = item.meaning;
        backSubText = item.note || item.explanation || '';
        break;
      case 'sentence':
        mainText = item.japanese || item.sentence;
        subText = '';
        backText = item.meaning || item.translation;
        break;
    }

    return {
      title: item.type === 'word' ? 'Từ vựng' :
             item.type === 'kanji' ? 'Hán tự' :
             item.type === 'grammar' ? 'Ngữ pháp' : 'Mẫu câu',
      content: mainText,
      description: subText,
      backcontent: backText,
      backdescription: backSubText,
      bgColor: '#ffffff',
      progressColor: '#E94560'
    };
  });
});

const totalCards = computed(() => slides.value.length);

// Methods
const previousCard = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--;
    cardRef.value?.swiper?.slidePrev();
  }
};

const nextCard = () => {
  if (currentIndex.value < totalCards.value - 1) {
    currentIndex.value++;
    cardRef.value?.swiper?.slideNext();
  }
};

const toggleAutoplay = () => {
  isAutoPlaying.value = !isAutoPlaying.value;
  if (isAutoPlaying.value) {
    cardRef.value?.swiper?.autoplay?.start();
  } else {
    cardRef.value?.swiper?.autoplay?.stop();
  }
};

const shuffleCards = () => {
  const shuffledItems = [...slides.value].sort(() => Math.random() - 0.5);
  store.dispatch('flashcard/setLearningItems', shuffledItems);
  currentIndex.value = 0;
};

const openSettings = () => {
  showSettings.value = true;
};

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen();
  } else {
    document.exitFullscreen();
  }
};

const onCardFlip = () => {
  // Handle card flip event if needed
};

// Cleanup
onUnmounted(() => {
  if (cardRef.value?.swiper?.autoplay) {
    cardRef.value.swiper.autoplay.stop();
  }
});
</script>

<style lang="scss" scoped>
.flashcard-learn {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: white;
  padding: 0 20px;
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
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;

  .modal-content {
    background: white;
    padding: 24px;
    border-radius: 12px;
    width: 400px;
    position: relative;

    h3 {
      margin: 0 0 20px 0;
      color: #333;
    }

    .setting-item {
      margin-bottom: 16px;

      label {
        display: block;
        margin-bottom: 8px;
        color: #666;
      }

      input, select {
        width: 100%;
        padding: 8px;
        border: 1px solid #ddd;
        border-radius: 4px;
        
        &:focus {
          outline: none;
          border-color: #e94560;
        }
      }
    }

    .close-btn {
      position: absolute;
      top: 16px;
      right: 16px;
      background: none;
      border: none;
      color: #666;
      cursor: pointer;
      
      &:hover {
        color: #333;
      }
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

    // Cập nhật style cho trạng thái đang phát
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
  }
}
</style>
  