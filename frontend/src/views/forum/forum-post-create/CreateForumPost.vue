<template>
  <div class="create-post-container">
    <div class="create-post-card">
      <div class="card-header">
        <h1>Tạo bài viết mới</h1>
        <p>Chia sẻ kiến thức và câu hỏi của  với cộng đồng</p>
      </div>
      <div class="card-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="post-title">Tiêu đề</label>
            <input type="text" id="post-title" v-model="postTitle" placeholder="Nhập tiêu đề hấp dẫn cho bài viết của ...">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label for="post-category">Chuyên mục</label>
              <div class="custom-select-wrapper">
                <select id="post-category" v-model="selectedCategory">
                  <option value="" disabled>-- Chọn một chuyên mục --</option>
                  <option v-for="category in categories" :key="category.id" :value="category.id">
                    {{ category.name }}
                  </option>
                </select>
              </div>
            </div>
            <div class="form-group">
              <label>Thẻ</label>
              <div class="tags-input-container" ref="tagsContainerRef">
                <div class="tags-input-trigger" @click="isTagDropdownActive = !isTagDropdownActive" :class="{ 'is-active': isTagDropdownActive }">
                  <span v-if="selectedTags.length === 0" class="placeholder">Thêm hoặc chọn thẻ...</span>
                  <div v-else class="selected-tags-pills">
                    <span v-for="(tag, index) in selectedTags" :key="index" class="tag-pill is-compact">
                      {{ tag }}
                      <i class="fas fa-times" @click.stop="removeTag(index)"></i>
                    </span>
                  </div>
                   <i class="fas fa-chevron-down trigger-icon"></i>
                </div>
                <div class="tags-dropdown" v-if="isTagDropdownActive">
                  <div class="tags-input">
                    <input
                      ref="tagInputRef"
                      type="text"
                      v-model="tagSearch"
                      placeholder="Tìm kiếm..."
                      @keydown.enter.prevent="addTagFromInput"
                      @keydown.backspace="removeLastTag"
                    />
                  </div>
                  <ul class="tags-suggestions">
                    <li v-for="tag in filteredTags" :key="tag" @mousedown.prevent="addTag(tag)">
                      {{ tag }}
                    </li>
                     <li v-if="canAddNewTag" class="add-new-tag" @mousedown.prevent="addTag(tagSearch)">
                      Thêm thẻ mới: <strong>"{{ tagSearch }}"</strong>
                    </li>
                    <li v-if="filteredTags.length === 0 && !canAddNewTag" class="no-results">
                      Không tìm thấy thẻ.
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
          <div class="form-group">
            <label for="post-content">Nội dung</label>
            <textarea id="post-content" v-model="postContent" rows="12" placeholder="Viết nội dung chi tiết ở đây.  có thể sử dụng markdown để định dạng."></textarea>
          </div>
           <div class="form-actions">
            <button type="button" class="btn btn-secondary" @click="goBackToForum">Hủy</button>
            <button type="submit" class="btn btn-primary">Đăng bài</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';

const router = useRouter();
const store = useStore();

// --- STATE ---
const postTitle = ref('');
const postContent = ref('');
const selectedCategory = ref('');
const selectedTags = ref([]);
const tagSearch = ref('');
const isTagDropdownActive = ref(false);
const tagInputRef = ref(null);
const tagsContainerRef = ref(null);

// Get current user from store
const currentUser = computed(() => store.state.auth.user);

// --- DATA ---
const categories = ref([
  { id: 'word', name: 'Từ vựng' },
  { id: 'kanji', name: 'Học Kanji' },
  { id: 'grammar', name: 'Ngữ pháp' },
  { id: 'jlpt', name: 'Luyện thi JLPT' },
  { id: 'communication', name: 'Giao tiếp' },
  { id: 'life-in-japan', name: 'Cuộc sống tại Nhật' },
  { id: 'other', name: 'Chủ đề khác' }
]);

const allTags = ref([
    'kanji', 'jlpt', 'ngữ pháp', 'giao tiếp', 'tự học', 'N2', 'N3', 
    'anime', 'review', 'luyện nghe', 'tài liệu', 'trợ từ', 'shadowing',
    'phát âm', 'lỗi sai', 'sách', 'người mới bắt đầu'
]);

// --- COMPUTED ---
const filteredTags = computed(() => {
  if (!tagSearch.value) {
    return allTags.value.filter(tag => !selectedTags.value.includes(tag));
  }
  const searchLower = tagSearch.value.toLowerCase();
  return allTags.value.filter(tag => 
    !selectedTags.value.includes(tag) && 
    tag.toLowerCase().includes(searchLower)
  );
});

const canAddNewTag = computed(() => {
  const search = tagSearch.value.trim();
  if (!search) return false;
  
  const inSelected = selectedTags.value.some(t => t.toLowerCase() === search.toLowerCase());
  const inAllTags = allTags.value.some(t => t.toLowerCase() === search.toLowerCase());

  return !inSelected && !inAllTags;
});

// --- METHODS ---
const goBackToForum = () => {
  router.push('/forum');
};

const addTag = (tag) => {
  const trimmedTag = tag.trim();
  if (trimmedTag && !selectedTags.value.includes(trimmedTag)) {
    selectedTags.value.push(trimmedTag);
  }
  tagSearch.value = '';
  tagInputRef.value?.focus();
};

const addTagFromInput = () => {
  if (canAddNewTag.value) {
    addTag(tagSearch.value);
  } else if (filteredTags.value.length > 0) {
    addTag(filteredTags.value[0]);
  }
};

const removeTag = (index) => {
  selectedTags.value.splice(index, 1);
};

const removeLastTag = () => {
  if (tagSearch.value === '' && selectedTags.value.length > 0) {
    removeTag(selectedTags.value.length - 1);
  }
};

const handleOutsideClick = (event) => {
  if (tagsContainerRef.value && !tagsContainerRef.value.contains(event.target)) {
    isTagDropdownActive.value = false;
  }
};

watch(isTagDropdownActive, (isActive) => {
  if (isActive) {
    nextTick(() => {
      tagInputRef.value?.focus();
    });
  }
});

onMounted(() => {
  document.addEventListener('click', handleOutsideClick, true);
});

onUnmounted(() => {
  document.removeEventListener('click', handleOutsideClick, true);
});

const handleSubmit = async (event) => {
  event.preventDefault(); // Prevent default form submission

  if (!postTitle.value || !postContent.value || !selectedCategory.value) {
    alert('Vui lòng điền đầy đủ thông tin bài viết');
    return;
  }

  const newPost = {
    id: Date.now().toString(),
    title: postTitle.value,
    content: postContent.value,
    category: selectedCategory.value,
    tags: selectedTags.value,
    timestamp: new Date().toISOString(),
    author: {
      id: 'current-user-123', // Sử dụng ID giả để kiểm tra quyền sở hữu bài viết
      username: 'Người dùng hiện tại',
      fullName: 'Người dùng hiện tại',
      title: 'Thành viên mới',
      avatar: 'https://i.pravatar.cc/150?u=current-user',
      bio: 'Chưa có thông tin giới thiệu.',
      website: '',
      websiteUrl: '#',
      location: 'Việt Nam',
      stats: { 
        posted: 'Vừa xong', 
        joined: new Date().toLocaleDateString('vi-VN'), 
        read: '1 giờ', 
        solutions: 0 
      }
    },
    stats: {
      replies: 0,
      views: 0
    }
  };

  try {
    // Add post to store
    await store.dispatch('forum/createPost', newPost);
    
    // Navigate to post detail page
    await router.push(`/forum/post/${newPost.id}`);
  } catch (error) {
    console.error('Error creating post:', error);
    alert('Có lỗi xảy ra khi tạo bài viết. Vui lòng thử lại sau.');
  }
};

</script>

<style lang="scss" scoped>
@use '@/views/forum/forum-post-create/CreateForumPost.scss';
</style> 