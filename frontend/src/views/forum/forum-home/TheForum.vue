<template>
    <div class="forum-container">
        <!-- Header -->
        <div class="forum-header">
            <div class="header-content">
                <h1>Diễn đàn Oboe</h1>
                <div class="header-actions flex-jsb">
                    <div class="search-container">
                        <input 
                            type="text" 
                            v-model="searchQuery" 
                            placeholder="Tìm kiếm bài viết..." 
                            class="search-input"
                            @input="handleSearch"
                        >
                    </div>
                    <button class="btn btn-primary create-post-btn" @click="goToCreatePost">
                        <i class="fas fa-edit"></i> Tạo bài viết mới
                    </button>
                </div>
            </div>
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
            </div>

            <!-- Post List -->
            <div class="post-list">
                <!-- Loading State -->
                <div v-if="loading" class="loading-container">
                    <div class="loading-spinner">
                        <i class="fas fa-spinner fa-spin fa-2x"></i>
                        <p>Đang tải bài viết...</p>
                    </div>
                </div>
                
                <!-- Error State -->
                <div v-else-if="error" class="error-container">
                    <div class="error-message">
                        <i class="fas fa-exclamation-triangle fa-2x"></i>
                        <p>{{ error }}</p>
                        <button class="btn btn-primary" @click="fetchBlogs(currentPage - 1, postsPerPage, searchQuery)">
                            Thử lại
                        </button>
                    </div>
                </div>
                
                <!-- Empty State -->
                <div v-else-if="posts.length === 0" class="empty-container">
                    <div class="empty-message">
                        <i class="fas fa-comments fa-2x"></i>
                        <p>Chưa có bài viết nào</p>
                    </div>
                </div>
                
                <!-- Posts -->
                <div v-else v-for="post in paginatedPosts" :key="post.id" class="post-item" @click="goToPostDetail(post.id)">
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
                </div>
            </div>

            <!-- Pagination Controls -->
            <div class="pagination-container" v-if="totalPages > 1 && !loading">
                <button class="pagination-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">
                    <i class="fas fa-chevron-left"></i>
                </button>
                
                <!-- Show page numbers (limited to prevent too many buttons) -->
                <template v-if="totalPages <= 7">
                    <button v-for="page in totalPages" :key="page" 
                            class="pagination-btn" 
                            :class="{ active: page === currentPage }"
                            @click="changePage(page)">
                        {{ page }}
                    </button>
                </template>
                <template v-else>
                    <!-- Show first page -->
                    <button class="pagination-btn" 
                            :class="{ active: 1 === currentPage }"
                            @click="changePage(1)">
                        1
                    </button>
                    
                    <!-- Show ellipsis if needed -->
                    <span v-if="currentPage > 4" class="pagination-ellipsis">...</span>
                    
                    <!-- Show pages around current page -->
                    <button v-for="page in getVisiblePages()" :key="page" 
                            class="pagination-btn" 
                            :class="{ active: page === currentPage }"
                            @click="changePage(page)">
                        {{ page }}
                    </button>
                    
                    <!-- Show ellipsis if needed -->
                    <span v-if="currentPage < totalPages - 3" class="pagination-ellipsis">...</span>
                    
                    <!-- Show last page -->
                    <button v-if="totalPages > 1" class="pagination-btn" 
                            :class="{ active: totalPages === currentPage }"
                            @click="changePage(totalPages)">
                        {{ totalPages }}
                    </button>
                </template>
                
                <button class="pagination-btn" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">
                    <i class="fas fa-chevron-right"></i>
                </button>
                
                <!-- Show page info -->
                <div class="page-info">
                    Trang {{ currentPage }} / {{ totalPages }} ({{ totalElements }} bài viết)
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import blogApi from '@/api/modules/blogApi';

const router = useRouter();

// --- STATE MANAGEMENT ---
const currentPage = ref(1);
const postsPerPage = ref(10); // Sync with API pageSize
const sortKey = ref('replies'); // 'replies', 'views'
const sortOrder = ref('desc'); // 'asc', 'desc'
const selectedCategory = ref('all');
const selectedTag = ref('all');
const searchQuery = ref('');

// API state
const posts = ref([]);
const loading = ref(false);
const error = ref(null);
const totalPages = ref(0);
const totalElements = ref(0);

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

// --- API FUNCTIONS ---
const fetchBlogs = async (page = 0, size = 10, searchTitle = '') => {
    try {
        loading.value = true;
        error.value = null;
        
        console.log('Fetching blogs...', { page, size, searchTitle });
        
        let response;
        if (searchTitle && searchTitle.trim()) {
            // Use search API
            response = await blogApi.search(searchTitle.trim());
            // Search API may not support pagination, so we handle it differently
            if (Array.isArray(response)) {
                // If search returns array directly
                const startIndex = page * size;
                const endIndex = startIndex + size;
                const paginatedResults = response.slice(startIndex, endIndex);
                
                response = {
                    blogs: paginatedResults,
                    totalPages: Math.ceil(response.length / size),
                    totalElements: response.length,
                    currentPage: page
                };
            }
        } else {
            // Use getAll API
            response = await blogApi.getAll({ page, size });
        }
        
        console.log('Blogs API response:', response);
        
        // Map API response to component format
        const blogs = response.blogs || response; // Handle different response formats
        const mappedPosts = (Array.isArray(blogs) ? blogs : []).map(blog => ({
            id: blog.id,
            title: blog.title,
            content: blog.content,
            author: {
                name: blog.author,
                avatar: `https://ui-avatars.com/api/?name=${encodeURIComponent(blog.author)}`
            },
            time: new Date(blog.createdAt),
            stats: {
                replies: blog.commentCount || 0,
                views: Math.floor(Math.random() * 10000) + 100 // Random views since API doesn't provide
            },
            category: mapTopicToCategory(blog.topics),
            tags: blog.tags ? blog.tags.split(',').map(tag => tag.trim()) : []
        }));
        
        posts.value = mappedPosts;
        totalPages.value = response.totalPages || Math.ceil((response.totalElements || mappedPosts.length) / size);
        totalElements.value = response.totalElements || mappedPosts.length;
        
    } catch (err) {
        console.error('Error fetching blogs:', err);
        error.value = err.message || 'Không thể tải danh sách bài viết';
        posts.value = [];
        totalPages.value = 0;
        totalElements.value = 0;
    } finally {
        loading.value = false;
    }
};

// Map topics to category IDs
const mapTopicToCategory = (topics) => {
    if (!topics) return 'other';
    
    const topicsLower = topics.toLowerCase();
    if (topicsLower.includes('kanji')) return 'kanji';
    if (topicsLower.includes('jlpt') || topicsLower.includes('n1') || topicsLower.includes('n2') || topicsLower.includes('n3') || topicsLower.includes('n4') || topicsLower.includes('n5')) return 'jlpt';
    if (topicsLower.includes('ngữ pháp') || topicsLower.includes('grammar')) return 'grammar';
    if (topicsLower.includes('từ vựng') || topicsLower.includes('vocabulary')) return 'word';
    if (topicsLower.includes('giao tiếp') || topicsLower.includes('communication')) return 'communication';
    if (topicsLower.includes('nhật bản') || topicsLower.includes('tokyo') || topicsLower.includes('sống')) return 'life-in-japan';
    
    return 'other';
};

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
    
    // Apply search filter
    if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        result = result.filter(post => 
            post.title.toLowerCase().includes(query)
        );
    }
    
    // Apply category filter
    if (selectedCategory.value !== 'all') {
        result = result.filter(post => post.category === selectedCategory.value);
    }
    
    // Apply tag filter
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
            default:
                valA = a.stats.views;
                valB = b.stats.views;
                break;
        }

        if (valA < valB) return sortOrder.value === 'asc' ? -1 : 1;
        if (valA > valB) return sortOrder.value === 'asc' ? 1 : -1;
        return 0;
    });
});

// Use server-side pagination, so display all posts from current page
const paginatedPosts = computed(() => {
    return sortedPosts.value;
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

const changePage = async (page) => {
    if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page;
        // API uses 0-based page index
        await fetchBlogs(page - 1, postsPerPage.value, searchQuery.value);
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

const handleSearch = async () => {
    currentPage.value = 1; // Reset to first page when searching
    await fetchBlogs(0, postsPerPage.value, searchQuery.value); // Reload data with search
};

// Get visible page numbers for pagination
const getVisiblePages = () => {
    const pages = [];
    const start = Math.max(2, currentPage.value - 2);
    const end = Math.min(totalPages.value - 1, currentPage.value + 2);
    
    for (let i = start; i <= end; i++) {
        pages.push(i);
    }
    return pages;
};

// Initialize data when component mounts
onMounted(() => {
    fetchBlogs(0, postsPerPage.value, searchQuery.value);
});
</script>

<style lang="scss" scoped>
@use '@/views/forum/forum-home/TheForum.scss';

// Additional styles for loading, error, and empty states
.loading-container, .error-container, .empty-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 300px;
    
    .loading-spinner, .error-message, .empty-message {
        text-align: center;
        
        i {
            color: #6c757d;
            margin-bottom: 16px;
        }
        
        p {
            font-size: 16px;
            color: #6c757d;
            margin-bottom: 16px;
        }
    }
    
    .error-message i {
        color: #dc3545;
    }
}

.pagination-container {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    margin-top: 24px;
    flex-wrap: wrap;
    
    .page-info {
        margin-left: 16px;
        font-size: 14px;
        color: #6c757d;
    }
    
    .pagination-ellipsis {
        padding: 8px 4px;
        color: #6c757d;
    }
}

.pagination-btn {
    padding: 8px 12px;
    border: 1px solid #dee2e6;
    background: white;
    color: #495057;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s;
    
    &:hover:not(:disabled) {
        background: #e9ecef;
        border-color: #adb5bd;
    }
    
    &.active {
        background: #007bff;
        border-color: #007bff;
        color: white;
    }
    
    &:disabled {
        background: #f8f9fa;
        border-color: #dee2e6;
        color: #6c757d;
        cursor: not-allowed;
    }
}
</style>