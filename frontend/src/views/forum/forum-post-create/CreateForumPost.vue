<template>
  <div class="create-post-container">
    <!-- Loading state for edit mode -->
    <div v-if="isLoading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-3">{{ t('createForumPost.loadingPost') }}</p>
    </div>
    
    <div v-else class="create-post-card">
      <div class="card-header">
        <h1>{{ isEditMode ? t('createForumPost.editTitle') : t('createForumPost.title') }}</h1>
        <p>{{ isEditMode ? t('createForumPost.editSubtitle') : t('createForumPost.subtitle') }}</p>
      </div>
      <div class="card-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="post-title">{{ t('createForumPost.titleLabel') }}</label>
            <input type="text" id="post-title" v-model="postTitle" :placeholder="t('createForumPost.titlePlaceholder')">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label for="post-category">{{ t('createForumPost.categoryLabel') }}</label>
              <div class="custom-select-wrapper">
                <select id="post-category" v-model="selectedCategory">
                  <option value="" disabled>{{ t('createForumPost.categoryPlaceholder') }}</option>
                  <option v-for="category in categories" :key="category.id" :value="category.id">
                    {{ category.name }}
                  </option>
                </select>
              </div>
            </div>
            <div class="form-group">
              <label>{{ t('createForumPost.tagsLabel') }}</label>
              <div class="tags-input-container" ref="tagsContainerRef">
                <div class="tags-input-trigger" @click="isTagDropdownActive = !isTagDropdownActive" :class="{ 'is-active': isTagDropdownActive }">
                  <span v-if="selectedTags.length === 0" class="placeholder">{{ t('createForumPost.tagsPlaceholder') }}</span>
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
                      :placeholder="t('createForumPost.searchPlaceholder')"
                      @keydown.enter.prevent="addTagFromInput"
                      @keydown.backspace="removeLastTag"
                    />
                  </div>
                  <ul class="tags-suggestions">
                    <li v-for="tag in filteredTags" :key="tag" @mousedown.prevent="addTag(tag)">
                      {{ tag }}
                    </li>
                     <li v-if="canAddNewTag" class="add-new-tag" @mousedown.prevent="addTag(tagSearch)">
                      {{ t('createForumPost.addNewTag') }} <strong>"{{ tagSearch }}"</strong>
                    </li>
                    <li v-if="filteredTags.length === 0 && !canAddNewTag" class="no-results">
                      {{ t('createForumPost.noTagsFound') }}
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
          <div class="form-group">
            <label for="post-content">{{ t('createForumPost.contentLabel') }}</label>
            <textarea id="post-content" v-model="postContent" rows="12" :placeholder="t('createForumPost.contentPlaceholder')"></textarea>
          </div>
           <div class="form-actions">
            <button type="button" class="btn btn-secondary" @click="goBackToForum" :disabled="isSubmitting">{{ t('createForumPost.cancel') }}</button>
            <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
              <span v-if="isSubmitting">
                <i class="fas fa-spinner fa-spin"></i> {{ isEditMode ? t('createForumPost.updating') : t('createForumPost.posting') }}
              </span>
              <span v-else>
                <i class="fas fa-paper-plane"></i> {{ isEditMode ? t('createForumPost.update') : t('createForumPost.post') }}
              </span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useStore } from 'vuex';
import { useI18n } from 'vue-i18n';
import blogApi from '@/api/modules/blogApi';

const router = useRouter();
const route = useRoute();
const store = useStore();
const { t } = useI18n();

// --- STATE ---
const postTitle = ref('');
const postContent = ref('');
const selectedCategory = ref('');
const selectedTags = ref([]);
const tagSearch = ref('');
const isTagDropdownActive = ref(false);
const tagInputRef = ref(null);
const tagsContainerRef = ref(null);
const isSubmitting = ref(false);

// Edit mode detection
const isEditMode = computed(() => route.name === 'EditForumPost');
const editPostId = computed(() => route.params.id);
const isLoading = ref(false);

// Get current user from store
const currentUser = computed(() => store.state.auth.user);

// --- DATA ---
const categories = computed(() => [
  { id: 'word', name: t('createForumPost.categories.word') },
  { id: 'kanji', name: t('createForumPost.categories.kanji') },
  { id: 'grammar', name: t('createForumPost.categories.grammar') },
  { id: 'jlpt', name: t('createForumPost.categories.jlpt') },
  { id: 'communication', name: t('createForumPost.categories.communication') },
  { id: 'life-in-japan', name: t('createForumPost.categories.life-in-japan') },
  { id: 'other', name: t('createForumPost.categories.other') }
]);

const allTags = computed(() => [
    'kanji', 'jlpt', t('createForumPost.tags.grammar'), t('createForumPost.tags.communication'), 
    t('createForumPost.tags.selfStudy'), 'N2', 'N3', 'anime', 'review', 
    t('createForumPost.tags.listening'), t('createForumPost.tags.materials'), 
    t('createForumPost.tags.particles'), 'shadowing', t('createForumPost.tags.pronunciation'), 
    t('createForumPost.tags.mistakes'), t('createForumPost.tags.books'), 
    t('createForumPost.tags.beginner')
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

// Load existing post data if in edit mode
const loadPostData = async () => {
  if (!isEditMode.value || !editPostId.value) return;
  
  try {
    isLoading.value = true;
    const response = await blogApi.getById(editPostId.value);
    
    // Populate form with existing data
    postTitle.value = response.title || '';
    postContent.value = response.content || '';
    selectedCategory.value = response.topics || '';
    
    // Parse tags from comma-separated string
    if (response.tags) {
      selectedTags.value = response.tags.split(',').map(tag => tag.trim()).filter(tag => tag);
    }
    
  } catch (error) {
    console.error('Error loading post data:', error);
    store.dispatch('showMessage', {
      type: 'error',
      text: t('createForumPost.loadError')
    });
    // Redirect back to forum if can't load post
    router.push('/forum');
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', handleOutsideClick, true);
  // Load post data if in edit mode
  if (isEditMode.value) {
    loadPostData();
  }
});

onUnmounted(() => {
  document.removeEventListener('click', handleOutsideClick, true);
});

const handleSubmit = async (event) => {
  event.preventDefault(); // Prevent default form submission

  if (!postTitle.value || !postContent.value || !selectedCategory.value) {
    store.dispatch('showMessage', {
      type: 'error',
      text: t('createForumPost.fillAllFields')
    });
    return;
  }

  // Check if user is authenticated
  if (!currentUser.value) {
    store.dispatch('showMessage', {
      type: 'error',
      text: t('createForumPost.loginRequired')
    });
    router.push('/login');
    return;
  }

  try {
    isSubmitting.value = true;
    
    // Prepare BlogDTO according to backend format
    const blogDTO = {
      title: postTitle.value.trim(),
      content: postContent.value.trim(),
      topics: selectedCategory.value, // Map category to topics field
      tags: selectedTags.value.length > 0 ? selectedTags.value.join(', ') : '', // Convert array to comma-separated string
    };

    let response;
    let resultBlog;

    if (isEditMode.value) {
      // Update existing blog post
      response = await blogApi.update(editPostId.value, blogDTO);
      resultBlog = response; // Update API returns BlogDTO directly
      
      // Show success message
      store.dispatch('showMessage', {
        type: 'success',
        text: t('createForumPost.updateSuccess')
      });
    } else {
      // Create new blog post
      response = await blogApi.create(blogDTO);
      resultBlog = response.data; // Create API returns { message, data }
      
      // Show success message
      store.dispatch('showMessage', {
        type: 'success',
        text: response.message || t('createForumPost.createSuccess')
      });
    }

    // Navigate to the post detail page
    await router.push(`/forum/post/${resultBlog.id}`);

  } catch (error) {
    console.error(`Error ${isEditMode.value ? 'updating' : 'creating'} blog post:`, error);
    
    // Handle different error scenarios
    let errorMessage = isEditMode.value ? t('createForumPost.updateError') : t('createForumPost.createError');
    
    if (error.message) {
      errorMessage = error.message;
    }
    
    store.dispatch('showMessage', {
      type: 'error',
      text: errorMessage
    });
  } finally {
    isSubmitting.value = false;
  }
};

</script>

<style lang="scss" scoped>
@use '@/views/forum/forum-post-create/CreateForumPost.scss';
</style>