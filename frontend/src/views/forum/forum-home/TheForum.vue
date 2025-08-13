<template>
    <div class="forum-container">
        <!-- Header -->
        <div class="forum-header">
            <div class="header-content">
                <h1>{{ t('forum.title') }}</h1>
                <div class="header-actions flex-jsb">
                    <div class="search-container">
                        <div class="search-input-wrapper">
                            <input 
                                type="text" 
                                v-model="searchQuery" 
                                :placeholder="t('forum.searchPlaceholder')" 
                                class="search-input"
                                @input="debouncedSearch"
                                @keyup.enter="handleSearch"
                            >
                            <div v-if="isSearching" class="search-loading">
                                <i class="fas fa-spinner fa-spin"></i>
                            </div>
                            <div v-else-if="searchQuery" class="search-clear" @click="clearSearch">
                                <i class="fas fa-times"></i>
                            </div>
                        </div>
                    </div>
                    <button class="btn-primary create-post-btn" @click="goToCreatePost">
                        <i class="fas fa-edit"></i> {{ t('forum.createNewPost') }}
                    </button>
                </div>
            </div>
        </div>

        <!-- Forum Body -->
        <div class="forum-body">
            <!-- List Header -->
            <div class="list-header">
                <div class="header-main">
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
                                <option value="all">{{ t('forum.allTags') }}</option>
                                <option v-for="tag in allTags.filter(t => t !== 'all')" :key="tag" :value="tag">{{ tag }}</option>
                            </select>
                        </div>
                    </div>
                    <button class="btn btn-secondary clear-filter-btn" @click="resetFilters" v-if="selectedCategory !== 'all' || selectedTag !== 'all'" :title="t('forum.clearFilters')">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <div class="header-stats">
                    <button class="sort-btn" @click="sortBy('replies')">
                        {{ t('forum.replies') }}
                        <i v-if="sortKey === 'replies'" :class="sortIconClass"></i>
                    </button>
                    <button class="sort-btn" @click="sortBy('views')">
                        {{ t('forum.views') }}
                        <i v-if="sortKey === 'views'" :class="sortIconClass"></i>
                    </button>
                </div>
            </div>

            <!-- Search Results Header -->
            <div v-if="searchQuery && !loading" class="search-results-header">
                <div class="search-info">
                    <i class="fas fa-search"></i>
                    <span>{{ t('forum.searchResultsFor') }}: "<strong>{{ searchQuery }}</strong>"</span>
                    <span class="result-count">({{ totalElements }} {{ t('forum.results') }})</span>
                </div>
                <button @click="clearSearch" class="btn btn-outline-secondary btn-sm">
                    <i class="fas fa-times"></i> {{ t('forum.clearSearch') }}
                </button>
            </div>

            <!-- Post List -->
            <div class="post-list">
                <!-- Loading State -->
                <div v-if="loading" class="loading-container">
                    <div class="loading-spinner">
                        <i class="fas fa-spinner fa-spin fa-2x"></i>
                        <p>{{ searchQuery ? t('forum.searching') : t('forum.loadingPosts') }}</p>
                    </div>
                </div>
                
                <!-- Error State -->
                <div v-else-if="error" class="error-container">
                    <div class="error-message">
                        <i class="fas fa-exclamation-triangle fa-2x"></i>
                        <p>{{ error }}</p>
                        <button class="btn btn-primary" @click="fetchBlogs(currentPage - 1, postsPerPage, searchQuery)">
                            {{ t('forum.tryAgain') }}
                        </button>
                    </div>
                </div>
                
                <!-- Empty State -->
                <div v-else-if="posts.length === 0" class="empty-container">
                    <div class="empty-message">
                        <i class="fas fa-2x" :class="searchQuery ? 'fa-search' : 'fa-comments'"></i>
                        <p>{{ searchQuery ? t('forum.noSearchResults', { query: searchQuery }) : t('forum.noPosts') }}</p>
                        <button v-if="searchQuery" @click="clearSearch" class="btn btn-secondary">
                            {{ t('forum.clearSearch') }}
                        </button>
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
                            {{ t('forum.by') }} <a class="author-name">{{ post.author.name }}</a>
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
                    {{ t('forum.pageInfo', { current: currentPage, total: totalPages, count: totalElements }) }}
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import blogApi from '@/api/modules/blogApi';

const { t } = useI18n();

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

// Search debounce
const searchTimeout = ref(null);
const isSearching = ref(false);

// --- DATA ---
const categories = computed(() => [
    { id: 'all', name: t('forum.categories.all'), color: '' },
    { id: 'word', name: t('forum.categories.word'), color: '#b90449' },
    { id: 'kanji', name: t('forum.categories.kanji'), color: '#b90449' },
    { id: 'grammar', name: t('forum.categories.grammar'), color: '#b90449' },
    { id: 'jlpt', name: t('forum.categories.jlpt'), color: '#b90449' },
    { id: 'communication', name: t('forum.categories.communication'), color: '#b90449' },
    { id: 'life-in-japan', name: t('forum.categories.lifeInJapan'), color: '#b90449' },
    { id: 'other', name: t('forum.categories.other'), color: '#b90449' }
]);

// --- API FUNCTIONS ---
const fetchBlogs = async (page = 0, size = 10, searchKeyword = '') => {
    try {
        loading.value = true;
        error.value = null;
        

        
        let response;
        let blogs;
        
        if (searchKeyword && searchKeyword.trim()) {
            // Use search API - returns array directly
            const searchResults = await blogApi.search(searchKeyword.trim());

            
            // Ensure searchResults is an array
            const resultsArray = Array.isArray(searchResults) ? searchResults : [];
            
            // Handle client-side pagination for search results
            const startIndex = page * size;
            const endIndex = startIndex + size;
            blogs = resultsArray.slice(startIndex, endIndex);
            
            // Create pagination info for search results
            response = {
                blogs: blogs,
                totalPages: Math.ceil(resultsArray.length / size),
                totalElements: resultsArray.length,
                currentPage: page
            };
        } else {
            // Use getAll API - returns paginated response
            response = await blogApi.getAll({ page, size });

            blogs = response.blogs || response.content || response || [];
            
            // Handle pagination info from getAll API
            if (!response.totalPages) {
                response = {
                    blogs: blogs,
                    totalPages: response.totalPages || 1,
                    totalElements: response.totalElements || blogs.length,
                    currentPage: page
                };
            }
        }
        

        const mappedPosts = (Array.isArray(blogs) ? blogs : []).map(blog => ({
            id: blog.id,
            title: blog.title,
            content: blog.content,
            author: {
                name: blog.author,
                avatar: blog.avatarUrl,
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
        totalPages.value = response.totalPages || 1;
        totalElements.value = response.totalElements || mappedPosts.length;
        
    } catch (err) {
        console.error('Error fetching blogs:', err);
        error.value = err.message || t('forum.errorLoadingPosts');
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
    
    // If searching, use server results directly (no client-side filtering)
    if (searchQuery.value && searchQuery.value.trim()) {
        return result;
    }
    
    // Apply category filter only when not searching
    if (selectedCategory.value !== 'all') {
        result = result.filter(post => post.category === selectedCategory.value);
    }
    
    // Apply tag filter only when not searching
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
    if (interval > 1) return t('forum.timeAgo.years', { count: Math.floor(interval) });
    interval = seconds / 2592000;
    if (interval > 1) return t('forum.timeAgo.months', { count: Math.floor(interval) });
    interval = seconds / 86400;
    if (interval > 1) return t('forum.timeAgo.days', { count: Math.floor(interval) });
    interval = seconds / 3600;
    if (interval > 1) return t('forum.timeAgo.hours', { count: Math.floor(interval) });
    interval = seconds / 60;
    if (interval > 1) return t('forum.timeAgo.minutes', { count: Math.floor(interval) });
    return t('forum.timeAgo.seconds');
}

// Debounced search function
const debouncedSearch = () => {
    // Clear previous timeout
    if (searchTimeout.value) {
        clearTimeout(searchTimeout.value);
    }
    
    // Set loading state for visual feedback
    isSearching.value = true;
    
    // Set new timeout
    searchTimeout.value = setTimeout(async () => {
        await handleSearch();
        isSearching.value = false;
    }, 500); // 500ms delay
};

const handleSearch = async () => {
    try {
        currentPage.value = 1; // Reset to first page when searching
        
        // Reset filters when searching to avoid confusion
        if (searchQuery.value && searchQuery.value.trim()) {
            selectedCategory.value = 'all';
            selectedTag.value = 'all';
        }
        
        await fetchBlogs(0, postsPerPage.value, searchQuery.value); // Reload data with search
    } catch (err) {
        console.error('Search error:', err);
    } finally {
        isSearching.value = false;
    }
};

// Clear search function
const clearSearch = async () => {
    searchQuery.value = '';
    currentPage.value = 1;
    // Clear search and reload normal data
    await fetchBlogs(0, postsPerPage.value, '');
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

// Cleanup on unmount
onUnmounted(() => {
    if (searchTimeout.value) {
        clearTimeout(searchTimeout.value);
    }
});
</script>

<style lang="scss" scoped>
@use '@/views/forum/forum-home/TheForum.scss';
</style>