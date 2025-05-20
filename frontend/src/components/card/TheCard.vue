<template>
  <Swiper ref="swiperRef" class="swiper" :modules="[Autoplay, Pagination, Navigation, EffectCards, Keyboard]"
    effect="cards" grabCursor :slides-per-view="1" :space-between="30" :autoplay="autoplay" :pagination="pagination"
    :navigation="{ nextEl: '.slider-button-next', prevEl: '.slider-button-prev' }" :keyboard="{ enabled: true }"
    :mousewheel="true" @autoplayTimeLeft="onAutoplayTimeLeft" @swiper="onSwiper" @slideChange="handleSlideChange"
    :style="{
      width: width + 'px',
      height: height + 'px',
    }">
    <SwiperSlide v-for="(slide, index) in slides" :key="index" :data-bg-color="slide.bgColor"
      :data-progress-color="slide.progressColor">
      <div class="card slide-item"
        :class="{ 'is-flipped': flippedIndex === index, 'animation-started': animationStartedList[index] }"
        @click="flipCard(index)">
        <div class="card-content flex" v-if="showBackIndex !== index">
          <h2 class="card-title">{{ slide.title }}</h2>
          <div class="slide-content card-description flex">
            <slot name="content" :slide="slide">
              <template v-if="isImage(slide.content)">
                <img :src="slide.content" alt="Slide Image" class="slide-image" />
              </template>
              <template v-else>
                <p class="main-content-text webkit-box webkit-line-2">{{ slide.content }}</p>
              </template>
            </slot>

          </div>
          <p class="card-description webkit-box webkit-line-3">{{ slide.description }}</p>
          <div class="flex see-more">
            <button class="cta-button" @click.stop="onSeeMore(slide)">Xem thêm</button>
          </div>
        </div>
        <div class="card-content flex" v-else>
          <h2 class="card-title fade-in-text">mặt sau</h2>
          <div class="slide-content card-description flex">
            <slot name="backcontent" :slide="slide">
              <template v-if="isImage(slide.backcontent)">
                <img :src="slide.backcontent" alt="Slide Image" class="slide-image" />
              </template>
              <template v-else>
                <p class="main-content-text webkit-box webkit-line-2">{{ slide.backcontent }}</p>
              </template>
            </slot>
          </div>
          <p class="card-description webkit-box webkit-line-3">{{ slide.backdescription }}</p>
          <div class="flex see-more">
            <button class="cta-button" @click.stop="onSeeMore(slide)">Xem thêm</button>
          </div>

        </div>
      </div>
    </SwiperSlide>

    <div class="slider-button-prev slider-button"></div>
    <div class="slider-button-next slider-button"></div>
  </Swiper>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Autoplay, Navigation, Pagination, EffectCards, Keyboard } from 'swiper/modules'
import { nextTick } from 'vue';

import 'swiper/css'
import 'swiper/css/effect-cards'
import 'swiper/css/pagination'
import 'swiper/css/navigation'

const { slides, width, height, pagination, autoplay, canFlip } = defineProps({
  slides: {
    type: Array,
    required: true
  },
  width: {
    type: Number,
    default: 600
  },
  height: {
    type: Number,
    default: 400
  },
  pagination: {
    type: [Object, Boolean],
    default: () => ({ type: 'fraction' })
  },
  autoplay: {
    type: [Object, Boolean],
    default: false
  },
  canFlip: {
    type: Boolean,
    default: true
  }
});

const swiperRef = ref(null);
const flippedIndex = ref(null);
const showBackIndex = ref(null);
const animationStartedList = ref([]);
const activeIndex = ref(0);
const swiperInstance = ref(null);


// Khi Swiper khởi tạo xong thì gọi hàm này để lưu lại instance
function onSwiper(swiper) {
  swiperInstance.value = swiper;
}


// Xử lý khi bấm nút "Xem thêm"
function onSeeMore(slide) {
  console.log("Clicked Xem thêm:", slide);
  // ví dụ: mở modal, hoặc navigate đến route chi tiết
}

// Kiểm tra một nội dung có phải hình ảnh không
function isImage(content) {
  if (!content) return false;
  return /\.(jpg|jpeg|png|webp|gif|bmp|svg)$/i.test(content);
}


// Hàm lật thẻ (flip) slide
function flipCard(index) {
  if (!canFlip) return; // Nếu không cho lật, thoát luôn
  if (!animationStartedList.value[index]) {
    animationStartedList.value[index] = true;
  } if (flippedIndex.value === index) {
    // Đang ở mặt sau => lật về mặt trước
    flippedIndex.value = null;
    setTimeout(() => {
      showBackIndex.value = null; // Delay 600ms mới ẩn mặt sau
    }, 600);
  } else {
    // Lật sang mặt sau
    flippedIndex.value = index;
    setTimeout(() => {
      showBackIndex.value = index;
    }, 600);
  }
}


// Xử lý khi chuyển slide (slideChange event)
function handleSlideChange() {
  console.log("swiper", swiperInstance.value)
  if (swiperInstance.value) {
    activeIndex.value = swiperInstance.value.activeIndex
    console.log("activeIndex", activeIndex.value)
  }
  flippedIndex.value = null
  showBackIndex.value = null
}



// Xử lý khi autoplay đang đếm ngược thời gian
function onAutoplayTimeLeft(swiper, time, progress) {
  document.querySelectorAll('.autoplay-progress svg')?.forEach(svg => {
    svg.style.setProperty('--progress', 1 - progress)
  })
  document.querySelectorAll('.autoplay-progress-bar')?.forEach(bar => {
    bar.style.setProperty('--progress', `${(1 - progress) * 100}%`)
  })
}

// Bắt phím toàn cục (global keydown) để lật thẻ bằng phím Up Arrow
function handleGlobalKeyDown(e) {
  if (e.code === 'ArrowUp') {
    e.preventDefault()
    flipCard(activeIndex.value)
  }
}
onMounted(() => {
  document.body.addEventListener('keydown', handleGlobalKeyDown)
})
onUnmounted(() => {
  document.body.removeEventListener('keydown', handleGlobalKeyDown)
})
</script>


<style scoped>
@import "./TheCardCss.scss";
</style>
