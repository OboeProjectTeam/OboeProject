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
@use '@/assets/css/index.scss' as *;
@use 'sass:color';

.forum-container {
    width: 100%;
    margin: 0 auto;
    font-family: $font-family-regular;
}

// --- Forum Header ---
.forum-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 0;
    border-bottom: 1px solid #e9ecef;
    margin-bottom: 20px;

    .header-content h1 {
        font-family: $font-family-bold;
        font-size: 2.8rem;
        color: #212529;
        margin: 0;
    }
    .header-content p {
        font-size: 1.1rem;
        color: #6c757d;
        margin-top: 5px;
    }
}

.create-post-btn {
    padding: 12px 22px;
    font-size: 1rem;
    font-weight: 700;
    border-radius: 8px;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    background-color: $primary-color;
    color: white;
    border: none;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
        background-color: color.adjust($primary-color, $lightness: -5%);
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(0,0,0,0.1);
    }
}

// --- List Header ---
.list-header {
    display: flex;
    align-items: center;
    padding: 10px 20px;
    margin-bottom: 15px;
    font-size: 0.9rem;
    color: #868e96;
    font-weight: 600;
    background-color: #f8f9fa;
    border-radius: 10px;

    .header-main {
        flex-grow: 1;
        display: flex;
        align-items: center;
        gap: 15px;
    }

    .custom-select-wrapper {
        position: relative;
        min-width: 180px;

        &::after {
            content: '\f078';
            font-family: 'Font Awesome 5 Free';
            font-weight: 900;
            position: absolute;
            top: 50%;
            right: 12px;
            transform: translateY(-50%);
            pointer-events: none;
            color: #868e96;
            font-size: 0.75rem;
        }

        select {
            width: 100%;
            padding: 8px 30px 8px 12px;
            border-radius: 6px;
            border: 1px solid #dee2e6;
            font-size: 0.9rem;
            font-family: inherit;
            font-weight: 500;
            transition: all 0.2s ease;
            background-color: white;
            appearance: none;
            -webkit-appearance: none;
            -moz-appearance: none;
            cursor: pointer;
            color: #495057;

            &:focus {
                outline: none;
                border-color: $primary-color;
                box-shadow: 0 0 0 3px color.adjust($primary-color, $alpha: -0.7);
            }
        }
    }

    .clear-filter-btn {
        padding: 8px 12px;
        font-size: 0.9rem;
        border-radius: 6px;
        border: none;
        cursor: pointer;
        transition: all 0.2s ease;
        background-color: #e9ecef;
        color: #495057;
        
        &:hover {
            background-color: #dee2e6;
        }
    }

    .header-stats {
        display: flex;
        gap: 25px;
        width: 180px;
        justify-content: center;
    }
    .header-activity {
        width: 200px;
        text-align: left;
    }
    .sort-btn {
        background: none;
        border: none;
        color: inherit;
        font-family: inherit;
        font-weight: inherit;
        font-size: inherit;
        cursor: pointer;
        padding: 5px;
        border-radius: 4px;
        transition: background-color 0.2s, color 0.2s;
        &:hover {
            color: #343a40;
        }
        i {
            margin-left: 5px;
        }
    }
}

// --- Post List ---
.post-list {
    background-color: white;
    border-radius: 12px;
    box-shadow: 0 4px 25px -10px rgba(0,0,0,0.08);
    border: 1px solid #e9ecef;
    overflow: hidden;
    margin-bottom: 30px;
}

.post-item {
    display: flex;
    align-items: center;
    padding: 20px;
    transition: background-color 0.2s ease;
    cursor: pointer;

    &:not(:last-child) {
        border-bottom: 1px solid #f1f3f5;
    }

    &:hover {
        background-color: #f8f9fa;
    }

    .post-avatar {
        flex-shrink: 0;
        margin-right: 15px;
        img {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            object-fit: cover;
        }
    }

    .post-content {
        flex-grow: 1;
        .post-category-tag {
            display: inline-block;
            padding: 3px 10px;
            font-size: 0.75rem;
            font-weight: 700;
            border-radius: 20px;
            color: white;
            margin-bottom: 8px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .post-title {
            font-size: 1.1rem;
            margin: 0 0 5px 0;
            text-decoration: none;
            color: #343a40;
            &:hover {
                color: $primary-color;
            }
        }
        .post-meta {
            font-size: 0.9rem;
            color: #868e96;
            margin: 0;
            .author-name {
                color: #495057;
                font-weight: 600;
                text-decoration: none;
                &:hover {
                    text-decoration: underline;
                }
            }
            .post-time::before {
                content: "•";
                margin: 0 8px;
            }
        }
    }

    .post-stats {
        display: flex;
        gap: 25px;
        text-align: center;
        width: 180px;
        flex-shrink: 0;
        justify-content: center;
        .stat-item {
            width: 50px; /* Fixed width for alignment */
            strong {
                display: block;
                font-size: 1.2rem;
                font-weight: 700;
                color: #495057;
            }
        }
    }

    .post-last-reply {
        display: flex;
        align-items: center;
        width: 200px;
        flex-shrink: 0;
        .last-reply-avatar {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            margin-right: 12px;
        }
        .last-reply-info {
            line-height: 1.4;
            .author-name {
                display: block;
                font-weight: 600;
                color: #495057;
                text-decoration: none;
                &:hover {
                    text-decoration: underline;
                }
            }
            .post-time {
                font-size: 0.85rem;
                color: #868e96;
            }
        }
    }
}

// --- Pagination ---
.pagination-container {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
}

.pagination-btn {
    border: 1px solid #ced4da;
    background-color: white;
    color: #495057;
    border-radius: 6px;
    min-width: 40px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover:not(:disabled) {
        border-color: $primary-color;
        color: $primary-color;
    }

    &.active {
        background-color: $primary-color;
        color: white;
        border-color: $primary-color;
    }

    &:disabled {
        background-color: #f8f9fa;
        color: #adb5bd;
        cursor: not-allowed;
    }
}

.suggest-btn {
    padding: 8px 18px;
    margin-right: 12px;
    border: none;
    border-radius: 6px;
    background: $primary-color;
    color: #fff;
    font-weight: 700;
    font-size: 1rem;
    cursor: pointer;
    transition: background 0.2s;
    &:hover {
        background: $hover-btn-color;
    }
}
</style>