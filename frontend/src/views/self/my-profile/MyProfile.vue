<template>
  <ProfileDetail 
    v-if="user" 
    :user="user" 
    :is-my-profile="true" 
    :active-tab="activeTab"
    @save-profile="handleProfileSave" 
  />
  <div v-else class="loading">
    Đang tải hồ sơ...
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import ProfileDetail from '@/components/layout/forum/profile/ProfileDetail.vue';

const store = useStore();
const route = useRoute();
const user = ref(null);

// Lấy người dùng đã đăng nhập từ Vuex store
const firebaseUser = computed(() => store.state.auth.user);

// Get active tab from route query
const activeTab = computed(() => route.query.tab || 'activities');

function handleProfileSave(updatedUser) {
  user.value = updatedUser;
  // Trong một ứng dụng thực tế,  sẽ gửi yêu cầu API ở đây để lưu thay đổi vào máy chủ.
  console.log('Profile saved!', updatedUser);
}

// Watch for new post flag in route query
watch(() => route.query.newPost, async (newPost) => {
  if (newPost === 'true') {
    // Refresh user data to include the new post
    const latestPost = await store.getters['forum/getLatestPost'];
    if (latestPost) {
      // Add the new post to the user's activities
      const newActivity = {
        type: 'post',
        id: `post-${latestPost.id}`,
        title: latestPost.title,
        timestamp: 'Vừa xong',
        topic: latestPost.category,
        url: `/forum/post/${latestPost.id}`
      };
      
      if (user.value && user.value.activities) {
        user.value.activities.unshift(newActivity);
      }
    }
  }
}, { immediate: true });

// Đây là dữ liệu chi tiết của hồ sơ. Trong ứng dụng thực tế,  sẽ lấy dữ liệu này
// từ cơ sở dữ liệu của riêng  bằng cách sử dụng UID của người dùng từ firebaseUser.
const profileDetailsMock = {
    title: 'Chuyên gia Ngôn ngữ Nhật',
    bio: 'Chỉ là một chàng trai đang cố gắng tìm hiểu về Kanji. Chia sẻ hành trình của mình và học hỏi từ cộng đồng.',
    location: 'Hà Nội, Việt Nam',
    website: 'my-japanese-journey.com',
    websiteUrl: 'https://oboe.com',
    dob: '05/08/1999',
    phone: '0775751954',
    hometown: 'Hải Dương',
    address: '123 Phố Huế, Q. Hai Bà Trưng, Hà Nội',
    stats: {
      posted: '258',
      joined: '12 tháng 1, 2023', // Sẽ được ghi đè nếu có thể
      read: '152 giờ',
      solutions: 89,
      topics: 34,
      likes: '1.8k'
    },
    activities: [
       {
        type: 'post',
        id: 'post-2',
        title: 'Tổng hợp tài liệu luyện thi JLPT N2 miễn phí',
        timestamp: '1 tuần trước',
        topic: 'Tài liệu JLPT',
        url: '/forum/post/3'
      },
       {
        type: 'reply',
        id: 'reply-3',
        postTitle: 'App học tiếng Nhật nào tốt nhất cho người mới bắt đầu?',
        content_snippet: 'Mình thấy khá ổn cho việc làm quen mặt chữ và từ vựng cơ bản, nhưng để chuyên sâu hơn thì nên kết hợp với sách...',
        timestamp: '3 tuần trước',
        url: '/forum/post/5#comment-789'
      },
      {
        type: 'answer',
        id: 'answer-1',
        postTitle: 'Làm thế nào để nhớ được nhiều từ vựng?',
        content_snippet: 'Mình thường dùng flashcard và lặp lại cách quãng (spaced repetition), thấy khá hiệu quả.  có thể thử app Anki xem sao.',
        timestamp: '1 tháng trước',
        url: '/forum/post/6#answer-1'
      },
      {
        type: 'material',
        id: 'material-1',
        title: 'Bộ flashcard 1000 từ vựng N3',
        topic: 'Thư viện',
        timestamp: '2 tháng trước',
        url: '/forum/material/1'
      },
      {
        type: 'post',
        id: 'post-3',
        title: 'Cùng thảo luận về phim Nhật hay',
        timestamp: '3 tháng trước',
        topic: 'Văn hóa Nhật Bản',
        url: '/forum/post/7'
      },
      {
        type: 'post',
        id: 'post-4',
        title: 'Kinh nghiệm du lịch Nhật Bản tự túc',
        timestamp: '3 tháng trước',
        topic: 'Du lịch',
        url: '/forum/post/8'
      },
      {
        type: 'reply',
        id: 'reply-4',
        postTitle: 'Cách phân biệt trợ từ は và が?',
        content_snippet: 'Cảm ơn , giải thích rất dễ hiểu!',
        timestamp: '4 tháng trước',
        url: '/forum/post/2#comment-124'
      },
      {
        type: 'answer',
        id: 'answer-5',
        postTitle: 'Học online hiệu quả?',
        content_snippet: 'Tự giác là quan trọng nhất nhé bro.',
        timestamp: '5 tháng trước',
        url: '/forum/post/9#answer-5'
      },
      {
        type: 'material',
        id: 'material-5',
        title: 'Giáo trình Minnano Nihongo',
        topic: 'Thư viện',
        timestamp: '5 tháng trước',
        url: '/forum/material/5'
      },
      {
        type: 'post',
        id: 'post-9',
        title: 'Review sách "Thiết kế cuộc đời đáng sống"',
        timestamp: '5 tháng trước',
        topic: 'Sách hay',
        url: '/forum/post/10'
      },
      {
        type: 'reply',
        id: 'reply-8',
        postTitle: 'Mẫu câu giao tiếp trong nhà hàng',
        content_snippet: 'Hay quá, cảm ơn !',
        timestamp: '6 tháng trước',
        url: '/forum/post/4#comment-459'
      },
      {
        type: 'post',
        id: 'post-11',
        title: 'Tìm  học chung N1',
        timestamp: '7 tháng trước',
        topic: 'Tìm  học',
        url: '/forum/post/11'
      }
    ]
};

// Theo dõi sự thay đổi của người dùng từ Vuex
watch(firebaseUser, (newFirebaseUser) => {
  if (newFirebaseUser) {
    // Khi có thông tin người dùng, kết hợp nó với dữ liệu chi tiết của hồ sơ
    user.value = {
      ...profileDetailsMock,
      // Ghi đè dữ liệu giả bằng dữ liệu thật từ Firebase
      username: newFirebaseUser.displayName || 'Người dùng',
      fullName: newFirebaseUser.displayName || 'Người dùng',
      avatar: newFirebaseUser.photoURL || `https://ui-avatars.com/api/?name=${newFirebaseUser.displayName || 'User'}`,
      email: newFirebaseUser.email,
    };
  } else {
    // Người dùng chưa đăng nhập hoặc đã đăng xuất
    user.value = null;
  }
}, { immediate: true }); // immediate: true để watcher chạy ngay khi component được tạo

</script>

<style lang="scss" scoped>
.loading {
  text-align: center;
  padding: 50px;
  font-size: 1.2rem;
}
</style>