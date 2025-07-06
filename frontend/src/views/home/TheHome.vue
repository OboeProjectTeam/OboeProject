<template>
  <div class="home-page">
    <!-- Hero Section with Search -->
    <section class="hero-section">
      <div class="container">
        <h1>Chào mừng trở lại!</h1>
        <p>Hôm nay bạn muốn học gì?</p>
        <div class="main-search-bar">
          <input type="text" v-model="searchQuery" placeholder="Tìm kiếm học liệu từ cộng đồng..." @keyup.enter="performSearch" />
          <button @click="performSearch"><i class="fas fa-search"></i></button>
        </div>
      </div>
    </section>

    <!-- Recent Activity Section -->
    <section class="content-section" v-if="recentSets.length > 0">
      <div class="container">
        <div class="section-header">
          <h2>Truy cập gần đây</h2>
          <router-link to="/library" class="view-all-link">Xem tất cả</router-link>
        </div>
        <div class="horizontal-scroll">
          <div v-for="set in recentSets" :key="set.id" class="content-card">
            <h3>{{ set.title }}</h3>
            <p>{{ set.cardCount }} thuật ngữ</p>
            <button @click="startLearning(set)" class="learn-now-btn">Học ngay</button>
          </div>
        </div>
      </div>
    </section>

    <!-- Recommended Study Sets Section -->
    <section class="content-section">
      <div class="container">
        <div class="section-header">
          <h2>Học liệu đề xuất cho bạn</h2>
        </div>
        <div class="content-grid">
           <div v-for="set in recommendedSets" :key="set.id" class="content-card large">
             <div class="card-info">
                <h3>{{ set.title }}</h3>
                <p>{{ set.description }}</p>
                <div class="author-info">
                  <img :src="set.author.avatar" alt="author avatar" class="avatar">
                  <span>{{ set.author.name }}</span>
                </div>
             </div>
             <div class="card-actions">
                <button @click="startLearning(set)" class="learn-now-btn">Học ngay</button>
             </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Recommended Forum Topics Section -->
    <section class="content-section">
      <div class="container">
        <div class="section-header">
          <h2>Chủ đề nổi bật</h2>
          <router-link to="/forum" class="view-all-link">Xem tất cả</router-link>
        </div>
        <div class="forum-grid">
          <div v-for="topic in recommendedTopics" :key="topic.id" class="forum-topic-card">
            <h4>{{ topic.title }}</h4>
            <p>{{ topic.postCount }} bài viết</p>
            <router-link :to="`/forum/post/${topic.id}`" class="view-topic-btn">
              Xem chủ đề <i class="fas fa-arrow-right"></i>
            </router-link>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

const store = useStore();
const router = useRouter();

const searchQuery = ref('');

// --- Mock Data ---
// In a real app, this data would be fetched from your Vuex store or an API.
const recentSets = ref([
  { id: 1, title: 'Từ vựng N5', cardCount: 50 },
  { id: 2, title: 'Kanji cơ bản', cardCount: 100 },
  { id: 3, title: 'Ngữ pháp sơ cấp', cardCount: 45 },
]);

const recommendedSets = ref([
  { 
    id: 101, 
    title: 'Hội thoại giao tiếp nhà hàng', 
    description: 'Các mẫu câu và từ vựng thường dùng khi đi ăn nhà hàng tại Nhật.',
    author: { name: 'Oboe Sensei', avatar: 'https://i.pravatar.cc/32?u=sensei' }
  },
  { 
    id: 102, 
    title: 'Luyện thi JLPT N4', 
    description: 'Bộ học liệu tổng hợp Kanji, ngữ pháp và từ vựng cho kỳ thi N4.',
    author: { name: 'Mochi Chan', avatar: 'https://i.pravatar.cc/32?u=mochi' }
  },
]);

const recommendedTopics = ref([
  { id: 201, title: 'Kinh nghiệm sống ở Tokyo?', postCount: 15 },
  { id: 202, title: 'Cách học Kanji hiệu quả', postCount: 42 },
  { id: 203, title: 'Tìm bạn học chung N3-N2', postCount: 88 },
]);
// --- End Mock Data ---

const performSearch = () => {
  if (!searchQuery.value.trim()) return;
  router.push({ path: '/search', query: { q: searchQuery.value } });
};

const startLearning = (set) => {
  // This would dispatch an action to set the current learning deck
  console.log('Starting to learn set:', set.title);
  // Example navigation
  router.push('/flashcard/learn');
};

// You would typically fetch real data when the component is mounted
onMounted(() => {
  // Example: store.dispatch('fetchHomePageData');
});

</script>

<style lang="scss" scoped>
@use '@/views/home/TheHome.scss';
</style>
