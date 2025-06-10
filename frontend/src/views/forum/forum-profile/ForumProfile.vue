<template>
  <div class="forum-profile-container">
    <div class="breadcrumb">
      <router-link to="/forum" class="breadcrumb-link">Diễn đàn</router-link>
      <template v-if="fromPostId && fromPostTitle">
        <span class="breadcrumb-separator">></span>
        <router-link :to="`/forum/post/${fromPostId}`" class="breadcrumb-link">Chi tiết bài viết</router-link>
      </template>
      <span class="breadcrumb-separator">></span>
      <span class="breadcrumb-current">Hồ sơ</span>
    </div>
    <ProfileDetail v-if="user" :user="user"/> 
    <div v-else class="loading">
      Đang tải hồ sơ...
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import ProfileDetail from '@/components/layout/forum/profile/ProfileDetail.vue';

const route = useRoute();
const user = ref(null);
const username = computed(() => route.params.username);

// Lấy thông tin bài viết trước đó từ query params
const fromPostId = computed(() => route.query.fromPostId);
const fromPostTitle = computed(() => route.query.fromPostTitle);

// This is the mock data. In a real application, you would fetch this from your API.
// Note: I'm including the full user data here for demonstration purposes.
const mockUsers = {
    'sarah-w': {
      username: 'sarah-w',
      fullName: 'Sarah Wilson',
      title: 'Product Designer',
      avatar: 'https://i.pravatar.cc/150?u=sarah-w',
      bio: 'Chuyên gia thiết kế tập trung vào trải nghiệm người dùng và các hệ thống thiết kế. Yêu thích việc tạo ra các sản phẩm đẹp và dễ sử dụng.',
      location: 'TP. Hồ Chí Minh',
      website: 'thietkesarah.com',
      websiteUrl: 'https://thietkesarah.com',
      stats: {
        posted: '1.2k',
        joined: '15 tháng 5, 2022',
        read: '10 giờ',
        solutions: 42,
        topics: 120,
        likes: '2.5k'
      },
      activities: [
        {
          type: 'post',
          id: 'post-1',
          title: 'Thảo luận về các phương pháp học Kanji hiệu quả',
          timestamp: '2 ngày trước',
          topic: 'Học Kanji',
          url: '/forum/post/1'
        },
        {
          type: 'reply',
          id: 'reply-1',
          postTitle: 'Cách phân biệt trợ từ は và が?',
          content_snippet: 'Một cách dễ hiểu là は nhấn mạnh vào hành động/mô tả phía sau, trong khi が nhấn mạnh vào chủ thể thực hiện...',
          timestamp: '5 ngày trước',
          url: '/forum/post/2#comment-123'
        },
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
          id: 'reply-2',
          postTitle: 'Mẫu câu giao tiếp trong nhà hàng',
          content_snippet: ' có thể thêm vào danh sách mẫu "すみません、注文お願いします" (Xin lỗi, cho tôi gọi món) nhé. Rất phổ biến!',
          timestamp: '2 tuần trước',
          url: '/forum/post/4#comment-456'
        },
         {
          type: 'reply',
          id: 'reply-3',
          postTitle: 'App học tiếng Nhật nào tốt nhất cho người mới bắt đầu?',
          content_snippet: 'Mình thấy Duolingo khá ổn cho việc làm quen mặt chữ và từ vựng cơ bản, nhưng để chuyên sâu hơn thì nên kết hợp với sách...',
          timestamp: '3 tuần trước',
          url: '/forum/post/5#comment-789'
        },
        {
          type: 'answer',
          id: 'answer-2',
          postTitle: 'Nên bắt đầu học tiếng Nhật từ đâu?',
          content_snippet: 'Theo kinh nghiệm của mình,  nên bắt đầu với bảng chữ cái Hiragana và Katakana trước tiên. Sau đó học các mẫu câu chào hỏi cơ bản.',
          timestamp: '1 tháng trước',
          url: '/forum/post/8#answer-2'
        },
        {
            type: 'material',
            id: 'material-2',
            title: 'Tổng hợp ngữ pháp N5',
            topic: 'Tài liệu JLPT',
            timestamp: '2 tháng trước',
            url: '/forum/material/2'
        },
        {
            type: 'post',
            id: 'post-5',
            title: 'Hỏi về cách dùng thể sai khiến',
            timestamp: '3 tháng trước',
            topic: 'Ngữ pháp',
            url: '/forum/post/9'
        },
        {
            type: 'answer',
            id: 'answer-6',
            postTitle: 'Nên bắt đầu học tiếng Nhật từ đâu?',
            content_snippet: 'Bắt đầu với bảng chữ cái là chuẩn rồi .',
            timestamp: '4 tháng trước',
            url: '/forum/post/8#answer-6'
        },
        {
            type: 'material',
            id: 'material-3',
            title: 'Bài tập ngữ pháp N4',
            topic: 'Tài liệu JLPT',
            timestamp: '4 tháng trước',
            url: '/forum/material/3'
        },
        {
            type: 'post',
            id: 'post-12',
            title: 'Cách nói "Cảm ơn" trong các tình huống khác nhau',
            timestamp: '5 tháng trước',
            topic: 'Giao tiếp',
            url: '/forum/post/12'
        }
      ]
    },
    'Mai An': {
      username: 'Mai An',
      fullName: 'Mai Thị An',
      title: 'Chuyên gia Ngôn ngữ Nhật',
      avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026704d',
      bio: 'Chỉ là một cô gái đang cố gắng tìm hiểu về Kanji. Chia sẻ hành trình của mình và học hỏi từ cộng đồng.',
      location: 'Hà Nội, Việt Nam',
      website: 'my-japanese-journey.com',
      websiteUrl: 'https://oboe.com',
      stats: {
        posted: '258',
        joined: '12 tháng 1, 2023',
        read: '152 giờ',
        solutions: 89,
        topics: 34,
        likes: '1.8k'
      },
      activities: [
        {
          type: 'post',
          id: 'post-1',
          title: 'Thảo luận về các phương pháp học Kanji hiệu quả',
          timestamp: '2 ngày trước',
          topic: 'Học Kanji',
          url: '/forum/post/1'
        },
        {
          type: 'reply',
          id: 'reply-1',
          postTitle: 'Cách phân biệt trợ từ は và が?',
          content_snippet: 'Một cách dễ hiểu là は nhấn mạnh vào hành động/mô tả phía sau, trong khi が nhấn mạnh vào chủ thể thực hiện...',
          timestamp: '5 ngày trước',
          url: '/forum/post/2#comment-123'
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
          topic: 'Tài liệu JLPT',
          timestamp: '2 tháng trước',
          url: '/forum/material/1'
        },
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
          id: 'reply-2',
          postTitle: 'Mẫu câu giao tiếp trong nhà hàng',
          content_snippet: ' có thể thêm vào danh sách mẫu "すみません、注文お願いします" (Xin lỗi, cho tôi gọi món) nhé. Rất phổ biến!',
          timestamp: '2 tuần trước',
          url: '/forum/post/4#comment-456'
        },
         {
          type: 'reply',
          id: 'reply-3',
          postTitle: 'App học tiếng Nhật nào tốt nhất cho người mới bắt đầu?',
          content_snippet: 'Mình thấy Duolingo khá ổn cho việc làm quen mặt chữ và từ vựng cơ bản, nhưng để chuyên sâu hơn thì nên kết hợp với sách...',
          timestamp: '3 tuần trước',
          url: '/forum/post/5#comment-789'
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
    },
    // Add other mock users if needed, matching the usernames from your data
  };


onMounted(() => {
  // Simulate API call to fetch user data
  setTimeout(() => {
    user.value = mockUsers[username.value] || null;
  }, 500);
});
</script>
<style lang="scss" scoped>
@use '@/views/forum/forum-profile/ForumProfile.scss';
</style>