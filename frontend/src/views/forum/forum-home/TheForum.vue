<template>
    <div class="forum-container">
        <!-- Header -->
        <div class="forum-header">
            <div class="header-content">
                <h1>Diễn đàn Oboe</h1>
                <p>Nơi chia sẻ, học hỏi và thảo luận về mọi khía cạnh của tiếng Nhật.</p>
            </div>
            <button class="btn btn-primary create-post-btn" @click="goToCreatePost">
                <i class="fas fa-edit"></i> Tạo bài viết mới
            </button>
        </div>

        <!-- Forum Body -->
        <div class="forum-body">
            <!-- List Header -->
            <div class="list-header">
                <div class="header-main">
                    <!-- Button Đề Xuất đứng đầu -->
                    <button class="suggest-btn" @click="sortBy('suggested')">
                        Đề Xuất
                    </button>
                    <!-- Dropdown Tất cả chuyên mục -->
                    <div class="control-group">
                        <div class="custom-select-wrapper">
                            <select id="category-filter" v-model="selectedCategory" @change="currentPage = 1">
                                <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
                            </select>
                        </div>
                    </div>
                    <!-- Dropdown Tất cả các thẻ -->
                    <div class="control-group">
                        <div class="custom-select-wrapper">
                            <select id="tag-filter" v-model="selectedTag" @change="currentPage = 1">
                                <option value="all">Tất cả các thẻ</option>
                                <option v-for="tag in allTags.filter(t => t !== 'all')" :key="tag" :value="tag">{{ tag }}</option>
                            </select>
                        </div>
                    </div>
                    <button class="btn btn-secondary clear-filter-btn" @click="resetFilters" v-if="selectedCategory !== 'all' || selectedTag !== 'all'" title="Xóa bộ lọc">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <div class="header-stats">
                    <button class="sort-btn" @click="sortBy('replies')">
                        Trả lời
                        <i v-if="sortKey === 'replies'" :class="sortIconClass"></i>
                    </button>
                    <button class="sort-btn" @click="sortBy('views')">
                        Lượt xem
                        <i v-if="sortKey === 'views'" :class="sortIconClass"></i>
                    </button>
                </div>
                <div class="header-activity">
                    <button class="sort-btn" @click="sortBy('activity')">
                        Hoạt động
                        <i v-if="sortKey === 'activity'" :class="sortIconClass"></i>
                    </button>
                </div>
            </div>

            <!-- Post List -->
            <div class="post-list">
                <div v-for="post in paginatedPosts" :key="post.id" class="post-item" @click="goToPostDetail(post.id)">
                    <div class="post-avatar">
                        <img :src="post.author.avatar" :alt="post.author.name">
                    </div>
                    <div class="post-content">
                        <span class="post-category-tag" 
                              v-if="post.category"
                              :style="{ backgroundColor: findCategoryDetails(post.category).color }">
                           {{ findCategoryDetails(post.category).name }}
                        </span>
                        <h3 class="post-title">{{ post.title }}</h3>
                        <p class="post-meta">
                            bởi <a href="#" class="author-name">{{ post.author.name }}</a>
                            <span class="post-time">{{ formatTimeAgo(post.time) }}</span>
                        </p>
                    </div>
                    <div class="post-stats">
                        <div class="stat-item">
                           {{ post.stats.replies }}
                        </div>
                        <div class="stat-item">
                           {{ post.stats.views.toLocaleString('vi-VN') }}
                        </div>
                    </div>
                    <div class="post-last-reply">
                        <img :src="post.lastReply.author.avatar" :alt="post.lastReply.author.name" class="last-reply-avatar">
                        <div class="last-reply-info">
                            <a href="#" class="author-name">{{ post.lastReply.author.name }}</a>
                            <span class="post-time">{{ formatTimeAgo(post.lastReply.time) }}</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Pagination Controls -->
            <div class="pagination-container" v-if="totalPages > 1">
                <button class="pagination-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">
                    <i class="fas fa-chevron-left"></i>
                </button>
                <button v-for="page in totalPages" :key="page" 
                        class="pagination-btn" 
                        :class="{ active: page === currentPage }"
                        @click="changePage(page)">
                    {{ page }}
                </button>
                <button class="pagination-btn" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">
                    <i class="fas fa-chevron-right"></i>
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// --- STATE MANAGEMENT ---
const currentPage = ref(1);
const postsPerPage = ref(5);
const sortKey = ref('activity'); // 'activity', 'replies', 'views'
const sortOrder = ref('desc'); // 'asc', 'desc'
const selectedCategory = ref('all');
const selectedTag = ref('all');

// --- DATA ---
const categories = ref([
    { id: 'all', name: 'Tất cả chuyên mục', color: '' },
    { id: 'word', name: 'Từ vựng', color: '#28a745' },
    { id: 'kanji', name: 'Học Kanji', color: '#17a2b8' },
    { id: 'grammar', name: 'Ngữ pháp', color: '#fd7e14' },
    { id: 'jlpt', name: 'Luyện thi JLPT', color: '#007bff' },
    { id: 'communication', name: 'Giao tiếp', color: '#6f42c1' },
    { id: 'life-in-japan', name: 'Cuộc sống tại Nhật', color: '#dc3545' },
    { id: 'other', name: 'Chủ đề khác', color: '#6c757d' }
]);

const now = new Date();
const posts = ref([
    { id: 1, title: 'Thảo luận về cách học Kanji hiệu quả cho người mới bắt đầu', author: { name: 'Mai An', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026704d' }, time: new Date(now.getTime() - 2 * 3600 * 1000), stats: { replies: 15, views: 2100 }, lastReply: { author: { name: 'Hùng Trần', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026705d' }, time: new Date(now.getTime() - 5 * 60 * 1000) }, category: 'kanji', tags: ['kanji', 'tự học', 'người mới bắt đầu'] },
    { id: 2, title: 'Kinh nghiệm thi JLPT N2 và tài liệu ôn tập', author: { name: 'Minh Tuấn', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026706d' }, time: new Date(now.getTime() - 8 * 3600 * 1000), stats: { replies: 32, views: 5800 }, lastReply: { author: { name: 'Lan Anh', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026707d' }, time: new Date(now.getTime() - 30 * 60 * 1000) }, category: 'jlpt', tags: ['jlpt', 'N2', 'tài liệu'] },
    { id: 3, title: 'Chia sẻ những bộ phim Anime hay để luyện nghe', author: { name: 'Ngọc Linh', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026708d' }, time: new Date(now.getTime() - 1 * 86400 * 1000), stats: { replies: 56, views: 12300 }, lastReply: { author: { name: 'Duy Mạnh', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026709d' }, time: new Date(now.getTime() - 1 * 3600 * 1000) }, category: 'communication', tags: ['giao tiếp', 'luyện nghe', 'anime'] },
    { id: 4, title: 'Tổng hợp ngữ pháp N3 thường gặp trong đề thi', author: { name: 'Thanh Hằng', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026710d' }, time: new Date(now.getTime() - 2 * 86400 * 1000), stats: { replies: 25, views: 8200 }, lastReply: { author: { name: 'Quốc Bảo', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026711d' }, time: new Date(now.getTime() - 3 * 3600 * 1000) }, category: 'grammar', tags: ['ngữ pháp', 'N3', 'jlpt'] },
    { id: 5, title: 'Cách phân biệt các trợ từ は, が, も?', author: { name: 'Bảo Châu', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026712d' }, time: new Date(now.getTime() - 2 * 86400 * 1000), stats: { replies: 18, views: 4500 }, lastReply: { author: { name: 'Gia Huy', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026713d' }, time: new Date(now.getTime() - 5 * 3600 * 1000) }, category: 'grammar', tags: ['ngữ pháp', 'trợ từ'] },
    { id: 6, title: 'Học giao tiếp qua Shadowing có thực sự hiệu quả?', author: { name: 'Khánh Vy', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026714d' }, time: new Date(now.getTime() - 3 * 86400 * 1000), stats: { replies: 41, views: 9100 }, lastReply: { author: { name: 'Mai An', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026704d' }, time: new Date(now.getTime() - 8 * 3600 * 1000) }, category: 'communication', tags: ['giao tiếp', 'shadowing'] },
    { id: 7, title: 'Những sai lầm người Việt thường mắc phải khi phát âm tiếng Nhật', author: { name: 'Gia Huy', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026713d' }, time: new Date(now.getTime() - 3 * 86400 * 1000), stats: { replies: 29, views: 7700 }, lastReply: { author: { name: 'Minh Tuấn', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026706d' }, time: new Date(now.getTime() - 1 * 86400 * 1000) }, category: 'communication', tags: ['phát âm', 'lỗi sai'] },
    { id: 8, title: 'Review sách "Minna no Nihongo" cho người mới bắt đầu', author: { name: 'Hùng Trần', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026705d' }, time: new Date(now.getTime() - 4 * 86400 * 1000), stats: { replies: 12, views: 3200 }, lastReply: { author: { name: 'Thanh Hằng', avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026710d' }, time: new Date(now.getTime() - 2 * 86400 * 1000) }, category: 'other', tags: ['sách', 'review', 'người mới bắt đầu'] }
]);

// --- COMPUTED PROPERTIES ---
const allTags = computed(() => {
    const tags = new Set();
    posts.value.forEach(post => {
        if(post.tags) {
            post.tags.forEach(tag => tags.add(tag));
        }
    });
    return ['all', ...Array.from(tags)];
});

const filteredPosts = computed(() => {
    let result = posts.value;
    if (selectedCategory.value !== 'all') {
        result = result.filter(post => post.category === selectedCategory.value);
    }
    if (selectedTag.value !== 'all') {
        result = result.filter(post => post.tags && post.tags.includes(selectedTag.value));
    }
    return result;
});

const sortedPosts = computed(() => {
    return [...filteredPosts.value].sort((a, b) => {
        let valA, valB;
        switch (sortKey.value) {
            case 'replies':
                valA = a.stats.replies;
                valB = b.stats.replies;
                break;
            case 'views':
                valA = a.stats.views;
                valB = b.stats.views;
                break;
            case 'activity':
            default:
                valA = a.lastReply.time;
                valB = b.lastReply.time;
                break;
        }

        if (valA < valB) return sortOrder.value === 'asc' ? -1 : 1;
        if (valA > valB) return sortOrder.value === 'asc' ? 1 : -1;
        return 0;
    });
});

const totalPages = computed(() => {
    return Math.ceil(sortedPosts.value.length / postsPerPage.value);
});

const paginatedPosts = computed(() => {
    const startIndex = (currentPage.value - 1) * postsPerPage.value;
    const endIndex = startIndex + postsPerPage.value;
    return sortedPosts.value.slice(startIndex, endIndex);
});

const sortIconClass = computed(() => {
    return sortOrder.value === 'asc' ? 'fas fa-sort-up' : 'fas fa-sort-down';
});

// --- METHODS ---
const goToCreatePost = () => {
    router.push('/forum/post/create');
};

const goToPostDetail = (postId) => {
    router.push(`/forum/post/${postId}`);
};

const changePage = (page) => {
    if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page;
    }
};

const sortBy = (key) => {
    if (sortKey.value === key) {
        sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
    } else {
        sortKey.value = key;
        sortOrder.value = 'desc';
    }
    currentPage.value = 1; // Reset to page 1 on new sort
};

const resetFilters = () => {
    selectedCategory.value = 'all';
    selectedTag.value = 'all';
    currentPage.value = 1;
};

const findCategoryDetails = (categoryId) => {
    return categories.value.find(c => c.id === categoryId) || {};
}

function formatTimeAgo(date) {
    const now = new Date();
    const seconds = Math.floor((now.getTime() - date.getTime()) / 1000);
    let interval = seconds / 31536000;
    if (interval > 1) return Math.floor(interval) + " năm trước";
    interval = seconds / 2592000;
    if (interval > 1) return Math.floor(interval) + " tháng trước";
    interval = seconds / 86400;
    if (interval > 1) return Math.floor(interval) + " ngày trước";
    interval = seconds / 3600;
    if (interval > 1) return Math.floor(interval) + " giờ trước";
    interval = seconds / 60;
    if (interval > 1) return Math.floor(interval) + " phút trước";
    return "Vài giây trước";
}
</script>

<style lang="scss" scoped>
@use '@/views/forum/forum-home/TheForum.scss';
</style>