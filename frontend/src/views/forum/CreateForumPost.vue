<template>
  <div class="create-post-container">
    <div class="create-post-card">
      <div class="card-header">
        <h1>Tạo bài viết mới</h1>
        <p>Chia sẻ kiến thức và câu hỏi của  với cộng đồng</p>
      </div>
      <div class="card-body">
        <form @submit.prevent>
          <div class="form-group">
            <label for="post-title">Tiêu đề</label>
            <input type="text" id="post-title" placeholder="Nhập tiêu đề hấp dẫn cho bài viết của ...">
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
            <textarea id="post-content" rows="12" placeholder="Viết nội dung chi tiết ở đây.  có thể sử dụng markdown để định dạng."></textarea>
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

const router = useRouter();

// --- STATE ---
const selectedCategory = ref('');
const selectedTags = ref([]);
const tagSearch = ref('');
const isTagDropdownActive = ref(false);
const tagInputRef = ref(null);
const tagsContainerRef = ref(null);

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

</script>

<style lang="scss" scoped>
@use '@/assets/css/index.scss' as *;
@use 'sass:color';

.create-post-container {
  max-width: 900px;
  margin: 0 auto;
  font-family: $font-family-regular;
}

.create-post-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 25px -10px rgba(0,0,0,0.08);
  border: 1px solid #e9ecef;
  overflow: hidden;
}

.card-header {
  padding: 25px 30px;
  border-bottom: 1px solid #e9ecef;
  background-color: #f8f9fa;
  h1 { font-size: 1.8rem; font-family: $font-family-bold; margin: 0 0 5px 0; }
  p { margin: 0; color: #6c757d; }
}

.card-body { padding: 30px; }

.form-row {
  display: flex;
  gap: 20px;
  .form-group { flex: 1; }
}

.form-group {
  margin-bottom: 25px;
  label {
    display: block;
    font-weight: 600;
    color: #343a40;
    margin-bottom: 8px;
  }
}

// --- Common input styles ---
input[type="text"], textarea, select, .tags-input-trigger {
  width: 100%;
  padding: 12px 15px;
  border-radius: 8px;
  border: 1px solid #ced4da;
  font-size: 1rem;
  font-family: inherit;
  transition: all 0.2s ease;
  background-color: white;

  &:focus, &.is-active {
    outline: none;
    border-color: $primary-color;
    box-shadow: 0 0 0 3px color.adjust($primary-color, $alpha: -0.7);
  }
}

textarea { resize: vertical; }

.custom-select-wrapper {
  position: relative;
  select {
    appearance: none;
    -webkit-appearance: none;
    -moz-appearance: none;
    cursor: pointer;
  }
  &::after {
    content: '\f078';
    font-family: 'Font Awesome 5 Free';
    font-weight: 900;
    position: absolute;
    top: 50%;
    right: 18px;
    transform: translateY(-50%);
    pointer-events: none;
    color: #868e96;
    font-size: 0.9rem;
  }
}

// --- Tags Input Component ---
.tags-input-container {
  position: relative;
}
.tags-input-trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  min-height: 48px;

  .placeholder { color: #adb5bd; }
  .trigger-icon {
    color: #868e96;
    transition: transform 0.2s ease;
  }
  &.is-active .trigger-icon {
    transform: rotate(180deg);
  }
}

.selected-tags-pills {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  padding-right: 10px;
}

.tag-pill.is-compact {
  background-color: #e9ecef;
  color: #495057;
  padding: 4px 8px;
  font-size: 0.85rem;
  border-radius: 4px;
  i {
    font-size: 0.75rem;
    margin-left: 6px;
    opacity: 0.7;
    &:hover { opacity: 1; }
  }
}

.tags-dropdown {
  position: absolute;
  top: calc(100% + 6px);
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  z-index: 10;
  box-shadow: 0 4px 15px -5px rgba(0,0,0,0.1);
}

.tags-input {
  padding: 10px 15px;
  border-bottom: 1px solid #e9ecef;
  input {
    width: 100%;
    border: none;
    outline: none;
    font-size: 1rem;
    padding: 2px 0;
    &::placeholder { color: #adb5bd; }
  }
}

.tags-suggestions {
  list-style: none;
  margin: 0;
  padding: 5px 0;
  max-height: 180px;
  overflow-y: auto;
  li {
    padding: 10px 15px;
    cursor: pointer;
    &:hover { background-color: #f1f3f5; }
    &.add-new-tag {
      color: $primary-color;
      font-weight: 500;
    }
    &.no-results {
      color: #868e96;
      font-style: italic;
      cursor: default;
      &:hover { background: none; }
    }
  }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 10px;
  
  .btn {
    padding: 10px 25px;
    font-size: 1rem;
    font-weight: 600;
    border-radius: 8px;
    border: none;
    cursor: pointer;
    transition: all 0.2s ease;
  }
  .btn-primary {
    background-color: $primary-color;
    color: white;
    &:hover { background-color: color.adjust($primary-color, $lightness: -5%); }
  }
  .btn-secondary {
    background-color: #e9ecef;
    color: #495057;
     &:hover { background-color: #dee2e6; }
  }
}
</style> 