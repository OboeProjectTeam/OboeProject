<template>
  <div class="search-results-page">
    <div class="container mx-auto py-8 px-4">
      <h1 class="text-3xl font-bold mb-6">Kết quả cho "{{ query }}"</h1>

      <!-- Tabs -->
      <div class="tabs-container mb-8">
        <ul class="tabs-list">
          <li v-for="tab in tabs" :key="tab.key" 
              :class="['tab-item', { 'active': activeTab === tab.key }]"
              @click="activeTab = tab.key">
            {{ tab.name }}
          </li>
        </ul>
      </div>

      <!-- All Results Tab -->
      <div v-if="activeTab === 'all'" class="search-content">
        <!-- Users Section -->
        <section class="mt-16" v-if="users.length > 0">
          <div class="section-header">
            <h2 class="text-2xl font-semibold">Người dùng</h2>
            <a href="#" class="view-all" @click.prevent="activeTab = 'users'">Xem tất cả</a>
          </div>
          <div class="single-row">
            <div v-for="user in users.slice(0, 6)" :key="user.id" class="user-card">
              <img :src="user.avatar" :alt="user.name" class="avatar"/>
              <div class="user-info">
                <h3 class="user-name">{{ user.name }}</h3>
                <div class="user-stats">
                  <span>{{ user.sets }} học phần</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Study Sets Section -->
        <section class="mt-16" v-if="studySets.length > 0">
          <div class="section-header">
            <h2 class="text-2xl font-semibold">Học liệu</h2>
            <a href="#" class="view-all" @click.prevent="activeTab = 'sets'">Xem tất cả</a>
          </div>
          <div class="single-row">
            <div v-for="set in studySets.slice(0, 5)" :key="set.id" class="set-card">
              <div class="set-card-body">
                <h3 class="set-title">{{ set.title }}</h3>
                <p class="set-term-count">{{ set.termCount }} thuật ngữ</p>
                <div class="author-info">
                  <img :src="set.author.avatar" :alt="set.author.name" class="author-avatar"/>
                  <span>{{ set.author.name }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Questions Section -->
        <section class="mt-16" v-if="questions.length > 0">
          <div class="section-header">
            <h2 class="text-2xl font-semibold">Bài kiểm tra</h2>
            <a href="#" class="view-all" @click.prevent="activeTab = 'questions'">Xem tất cả</a>
          </div>
          <div class="single-row">
            <div v-for="question in questions.slice(0, 5)" :key="question.id" class="question-card">
              <div class="question-header">
                <div class="author-info">
                  <img :src="question.author.avatar" :alt="question.author.name" class="author-avatar"/>
                  <span>{{ question.author.name }}</span>
                </div>
                <span class="answer-count">{{ question.answerCount }} câu hỏi</span>
              </div>
              <h3 class="question-title">{{ question.title }}</h3>
            </div>
          </div>
        </section>
      </div>

      <!-- Study Sets Tab -->
      <div v-else-if="activeTab === 'sets'" class="study-sets-content">
        <div class="study-sets-container">
          <div class="study-sets-list">
            <div class="list-header">
              <h2>Học liệu</h2>
              <div class="results-count">{{ studySets.length }} kết quả</div>
            </div>
            <div class="sets-grid">
              <div v-for="set in paginatedList" :key="set.id" 
                   @click="selectStudySet(set)"
                   :class="['study-set-card', { 'active': selectedSet?.id === set.id }]">
                <h3>{{ set.title }}</h3>
                <p class="term-count">{{ set.termCount }} thuật ngữ</p>
                <div class="author-info">
                  <img :src="set.author.avatar" :alt="set.author.name" class="author-avatar"/>
                  <span>{{ set.author.name }}</span>
                </div>
              </div>
            </div>
            <div class="pagination">
              <button :disabled="currentPage === 1" @click="currentPage--">
                <i class="fas fa-chevron-left"></i> Trước
              </button>
              <span class="page-number">{{ currentPage }}/{{ totalPages }}</span>
              <button :disabled="currentPage === totalPages" @click="currentPage++">
                Sau <i class="fas fa-chevron-right"></i>
              </button>
            </div>
          </div>
          <div class="study-set-preview" v-if="selectedSet">
            <div class="preview-header">
              <div class="header-top">
                <h1>{{ selectedSet.title }}</h1>
                <button class="learn-btn">
                  <i class="fas fa-graduation-cap"></i>Học
                </button>
              </div>
            </div>

            <!-- Terms Preview -->
            <div class="terms-preview">
              <div class="terms-header">
                <div>Mặt trước</div>
                <div>Mặt sau</div>
              </div>
              <div class="terms-list">
                <div v-for="(term, index) in selectedSet.terms" :key="index"
                     class="term-row">
                  <div class="front">{{ term.front }}</div>
                  <div class="back">{{ term.back }}</div>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-preview">
            <div class="empty-content">
              <i class="fas fa-book-open"></i>
              <p>Chọn một học liệu để xem trước</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Questions Tab -->
      <div v-else-if="activeTab === 'questions'" class="paged-grid-content">
        <div class="grid-container">
          <div v-for="question in paginatedList" :key="question.id" class="question-card-grid">
            <div class="question-header">
              <div class="author-info">
                <img :src="question.author.avatar" :alt="question.author.name" class="author-avatar"/>
                <span>{{ question.author.name }}</span>
              </div>
              <span class="answer-count">{{ question.answerCount }} câu hỏi</span>
            </div>
            <h3 class="question-title">{{ question.title }}</h3>
          </div>
        </div>
        <div class="pagination">
          <button :disabled="currentPage === 1" @click="currentPage--">
            <i class="fas fa-chevron-left"></i> Trước
          </button>
          <span class="page-number">{{ currentPage }}/{{ totalPages }}</span>
          <button :disabled="currentPage === totalPages" @click="currentPage++">
            Sau <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>

      <!-- Users Tab -->
      <div v-else-if="activeTab === 'users'" class="paged-grid-content">
        <div class="grid-container">
          <div v-for="user in paginatedList" :key="user.id" class="user-card-grid" @click="goToUserProfile(user)">
            <img :src="user.avatar" :alt="user.name" class="avatar"/>
            <div class="user-info">
              <h3 class="user-name">{{ user.name }}</h3>
              <div class="user-stats">
                <span>{{ user.sets }} học phần</span>
              </div>
            </div>
          </div>
        </div>
        <div class="pagination">
          <button :disabled="currentPage === 1" @click="currentPage--">
            <i class="fas fa-chevron-left"></i> Trước
          </button>
          <span class="page-number">{{ currentPage }}/{{ totalPages }}</span>
          <button :disabled="currentPage === totalPages" @click="currentPage++">
            Sau <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>

      <div v-if="!users.length && !studySets.length && !questions.length" class="text-center text-gray-500 mt-12">
        <p>Không tìm thấy kết quả nào cho "{{ query }}".</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SearchResults',
  props: {
    query: {
      type: String,
      default: 'Kanji N5',
    },
  },
  data() {
    return {
      activeTab: 'all',
      currentPage: 1,
      itemsPerPageConfig: {
        sets: 5,
        questions: 12, // 4 columns * 3 rows
        users: 12,     // 4 columns * 3 rows
      },
      selectedSet: null,
      tabs: [
        { key: 'all', name: 'Tất cả kết quả' },
        { key: 'sets', name: 'Học liệu' },
        { key: 'questions', name: 'Bài kiểm tra' },
        { key: 'users', name: 'Người dùng' },
      ],
      users: [
        { id: 1, name: 'kanji', avatar: 'https://cdn-icons-png.flaticon.com/512/1998/1998627.png', sets: 0 },
        { id: 2, name: 'kanjiN5', avatar: 'https://cdn-icons-png.flaticon.com/512/7046/7046215.png', sets: 6 },
        { id: 3, name: 'user_3', avatar: 'https://i.pravatar.cc/80?u=user3', sets: 12 },
        { id: 4, name: 'user_4', avatar: 'https://i.pravatar.cc/80?u=user4', sets: 5 },
        { id: 5, name: 'user_5', avatar: 'https://i.pravatar.cc/80?u=user5', sets: 8 },
        { id: 6, name: 'user_6', avatar: 'https://i.pravatar.cc/80?u=user6', sets: 2 },
        { id: 7, name: 'user_7', avatar: 'https://i.pravatar.cc/80?u=user7', sets: 23 },
        { id: 8, name: 'user_8', avatar: 'https://i.pravatar.cc/80?u=user8', sets: 1 },
        { id: 9, name: 'user_9', avatar: 'https://i.pravatar.cc/80?u=user9', sets: 15 },
        { id: 10, name: 'user_10', avatar: 'https://i.pravatar.cc/80?u=user10', sets: 9 },
        { id: 11, name: 'user_11', avatar: 'https://i.pravatar.cc/80?u=user11', sets: 11 },
        { id: 12, name: 'user_12', avatar: 'https://i.pravatar.cc/80?u=user12', sets: 4 },
        { id: 13, name: 'user_13', avatar: 'https://i.pravatar.cc/80?u=user13', sets: 7 },
      ],
      studySets: [
        {
          id: 101,
          title: 'Kanji N5 - Các chữ cơ bản',
          termCount: 528,
          author: { name: 'duong_nguyenvan', avatar: 'https://i.pravatar.cc/32?u=duong' },
          terms: [
            { front: '人', back: 'ひと - người' },
            { front: '日', back: 'ひ - mặt trời, ngày' },
            { front: '月', back: 'つき - mặt trăng, tháng' },
            { front: '木', back: 'き - cây' },
            { front: '山', back: 'やま - núi' },
          ]
        },
        {
          id: 102,
          title: 'Kanji N5 - Số đếm',
          termCount: 255,
          author: { name: 'hoang thuy linh', avatar: 'https://i.pravatar.cc/32?u=quizlette' },
          terms: [
            { front: '水', back: 'みず - nước' },
            { front: '火', back: 'ひ - lửa' },
            { front: '土', back: 'つち - đất' },
            { front: '金', back: 'かね - vàng, tiền' },
            { front: '雨', back: 'あめ - mưa' },
          ]
        },
        {
          id: 103,
          title: 'Kanji N5 - Động từ thường gặp',
          termCount: 131,
          author: { name: 'Nguyễn Văn Bách', avatar: 'https://i.pravatar.cc/32?u=quizlette2' },
          terms: [
            { front: '一', back: 'いち - một' },
            { front: '二', back: 'に - hai' },
            { front: '三', back: 'さん - ba' },
            { front: '四', back: 'よん/し - bốn' },
            { front: '五', back: 'ご - năm' },
            { front: '一', back: 'いち - một' },
            { front: '二', back: 'に - hai' },
            { front: '三', back: 'さん - ba' },
            { front: '四', back: 'よん/し - bốn' },
            { front: '五', back: 'ご - năm' }, { front: '一', back: 'いち - một' },
            { front: '二', back: 'に - hai' },
            { front: '三', back: 'さん - ba' },
            { front: '四', back: 'よん/し - bốn' },
            { front: '五', back: 'ご - năm' }, { front: '一', back: 'いち - một' },
            { front: '二', back: 'に - hai' },
            { front: '三', back: 'さん - ba' },
            { front: '四', back: 'よん/し - bốn' },
            { front: '五', back: 'ご - năm' }, { front: '一', back: 'いち - một' },
            { front: '二', back: 'に - hai' },
            { front: '三', back: 'さん - ba' },
            { front: '四', back: 'よん/し - bốn' },
            { front: '五', back: 'ご - năm' }, { front: '一', back: 'いち - một' },
            { front: '二', back: 'に - hai' },
            { front: '三', back: 'さん - ba' },
            { front: '四', back: 'よん/し - bốn' },
            { front: '五', back: 'ご - năm' },
          ]
        },
        {
          id: 104,
          title: 'Kanji N5 - Tính từ cơ bản',
          termCount: 180,
          author: { name: 'Mai Anh', avatar: 'https://i.pravatar.cc/32?u=maianh' },
          terms: [
            { front: '大', back: 'おお - lớn' },
            { front: '小', back: 'ちい - nhỏ' },
            { front: '新', back: 'あたら - mới' },
            { front: '古', back: 'ふる - cũ' },
            { front: '高', back: 'たか - cao' },
          ]
        },
        {
          id: 105,
          title: 'Kanji N5 - Tính từ cơ bản',
          termCount: 180,
          author: { name: 'Mai Anh', avatar: 'https://i.pravatar.cc/32?u=maianh' },
          terms: [
            { front: '大', back: 'おお - lớn' },
            { front: '小', back: 'ちい - nhỏ' },
            { front: '新', back: 'あたら - mới' },
            { front: '古', back: 'ふる - cũ' },
            { front: '高', back: 'たか - cao' },
          ]
        },
        {
          id: 106,
          title: 'Kanji N5 - Tính từ cơ bản',
          termCount: 180,
          author: { name: 'Mai Anh', avatar: 'https://i.pravatar.cc/32?u=maianh' },
          terms: [
            { front: '大', back: 'おお - lớn' },
            { front: '小', back: 'ちい - nhỏ' },
            { front: '新', back: 'あたら - mới' },
            { front: '古', back: 'ふる - cũ' },
            { front: '高', back: 'たか - cao' },
          ]
        },
        {
          id: 107,
          title: 'Kanji N5 - Tính từ cơ bản',
          termCount: 180,
          author: { name: 'Mai Anh', avatar: 'https://i.pravatar.cc/32?u=maianh' },
          terms: [
            { front: '大', back: 'おお - lớn' },
            { front: '小', back: 'ちい - nhỏ' },
            { front: '新', back: 'あたら - mới' },
            { front: '古', back: 'ふる - cũ' },
            { front: '高', back: 'たか - cao' },
          ]
        },
        {
          id: 108,
          title: 'Kanji N5 - Màu sắc',
          termCount: 90,
          author: { name: 'Đức Anh', avatar: 'https://i.pravatar.cc/32?u=ducanh' },
          terms: [
            { front: '白', back: 'しろ - trắng' },
            { front: '黒', back: 'くろ - đen' },
            { front: '赤', back: 'あか - đỏ' },
            { front: '青', back: 'あお - xanh' },
            { front: '黄', back: 'き - vàng' },
          ]
        },
      ],
      questions: [
        { id: 301, title: 'Làm sao để phân biệt を và が?', author: { name: 'minh_an', avatar: 'https://i.pravatar.cc/32?u=minhan' }, answerCount: 5 },
        { id: 302, title: 'Kinh nghiệm thi đậu N1 trong 6 tháng?', author: { name: 'long_hoang', avatar: 'https://i.pravatar.cc/32?u=longhoang' }, answerCount: 22 },
        { id: 303, title: 'Tài liệu tự học tiếng Nhật từ con số 0?', author: { name: 'bich_phuong', avatar: 'https://i.pravatar.cc/32?u=bichphuong' }, answerCount: 18 },
        { id: 304, title: 'Cách dùng trợ từ は và も?', author: { name: 'thanh_tam', avatar: 'https://i.pravatar.cc/32?u=thanhtam' }, answerCount: 11 },
        { id: 305, title: 'Mẹo nhớ Kanji hiệu quả?', author: { name: 'gia_huy', avatar: 'https://i.pravatar.cc/32?u=giahuy' }, answerCount: 35 },
        { id: 306, title: 'Nên học Mina no Nihongo hay Genki?', author: { name: 'hoai_an', avatar: 'https://i.pravatar.cc/32?u=hoaian' }, answerCount: 9 },
        { id: 307, title: 'Phân biệt thể sai khiến và bị động sai khiến?', author: { name: 'duc_anh', avatar: 'https://i.pravatar.cc/32?u=ducanh2' }, answerCount: 15 },
        { id: 308, title: 'JLPT N3 cần bao nhiêu từ vựng?', author: { name: 'mai_phuong', avatar: 'https://i.pravatar.cc/32?u=maiphuong' }, answerCount: 7 },
        { id: 309, title: 'Lộ trình học từ N5 đến N3 mất bao lâu?', author: { name: 'quoc_viet', avatar: 'https://i.pravatar.cc/32?u=quocviet' }, answerCount: 25 },
        { id: 310, title: 'Có nên đi du học Nhật Bản không?', author: { name: 'thuy_linh', avatar: 'https://i.pravatar.cc/32?u=thuylinh' }, answerCount: 42 },
        { id: 311, title: 'Sách luyện đọc hiểu N2 nào hay?', author: { name: 'bao_ngoc', avatar: 'https://i.pravatar.cc/32?u=baongoc' }, answerCount: 13 },
        { id: 312, title: 'Kính ngữ trong tiếng Nhật có khó không?', author: { name: 'trung_kien', avatar: 'https://i.pravatar.cc/32?u=trungkien' }, answerCount: 31 },
        { id: 313, title: 'App học tiếng Nhật miễn phí tốt nhất?', author: { name: 'lan_anh', avatar: 'https://i.pravatar.cc/32?u=lananh' }, answerCount: 50 },
      ]
    };
  },
  computed: {
    currentList() {
      switch (this.activeTab) {
        case 'sets': return this.studySets;
        case 'questions': return this.questions;
        case 'users': return this.users;
        default: return [];
      }
    },
    currentItemsPerPage() {
      return this.itemsPerPageConfig[this.activeTab] || 1;
    },
    totalPages() {
      if (!this.currentList.length) return 1;
      return Math.ceil(this.currentList.length / this.currentItemsPerPage);
    },
    paginatedList() {
      if (!this.currentList.length) return [];
      const start = (this.currentPage - 1) * this.currentItemsPerPage;
      const end = start + this.currentItemsPerPage;
      return this.currentList.slice(start, end);
    }
  },
  methods: {
    selectStudySet(set) {
      this.selectedSet = set;
    },
    goToUserProfile(user) {
      this.$router.push({ name: 'ProfileDetail', params: { username: user.name } });
    }
  },
  watch: {
    activeTab: {
      immediate: true,
      handler(newTab) {
        this.currentPage = 1;
        if (newTab === 'sets' && this.studySets.length > 0) {
          this.selectStudySet(this.paginatedList[0]);
        } else {
          this.selectedSet = null;
        }
      }
    },
    paginatedList: {
      deep: true,
      handler(newList) {
        if (this.activeTab === 'sets' && newList.length > 0 && !this.selectedSet) {
           this.selectStudySet(newList[0]);
        }
      }
    }
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/home/search-results/SearchResults.scss';
</style> 