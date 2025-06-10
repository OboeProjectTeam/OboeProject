<template>
  <div class="post-detail-container" @click="activeUserCard = null">
    <!-- Breadcrumb -->
    <div class="breadcrumb">
      <a href="#" @click.prevent="goBackToForum">Diễn đàn</a>
      <i class="fas fa-chevron-right separator"></i>
      <span>Chi tiết bài viết</span>
    </div>

    <!-- Post Header -->
    <div class="post-header">
      <h1 class="post-title">Thảo luận về cách học Kanji hiệu quả cho người mới bắt đầu</h1>
      <div class="post-meta">
        <img 
          :src="originalPost.author.avatar" 
          :alt="originalPost.author.username" 
          class="author-avatar"
          @click.stop="toggleUserCard($event, originalPost.author)"
        >
        <div class="meta-info">
          <span class="author-name" @click.stop="toggleUserCard($event, originalPost.author)">{{ originalPost.author.username }}</span>
          <span class="post-time">Đăng 2 giờ trước</span>
        </div>
      </div>
    </div>

    <!-- Original Post Content -->
    <div class="post-content-card">
      <p>Chào mọi người,</p>
      <p>Mình là người mới bắt đầu học tiếng Nhật và đang gặp khá nhiều khó khăn với Kanji. Dù đã thử qua nhiều phương pháp như viết tay, dùng flashcard, học theo bộ thủ nhưng vẫn cảm thấy rất rối và khó nhớ.</p>
      <p>Mọi người có kinh nghiệm hay phương pháp nào học Kanji hiệu quả, đặc biệt là cho người mới, thì chia sẻ cho mình và mọi người cùng biết với ạ. Ví dụ như:</p>
      <ul>
        <li>Nên bắt đầu từ những bộ Kanji nào?</li>
        <li>Có app hay website nào hỗ trợ học tốt không?</li>
        <li>Cách để ôn tập và tránh quên Kanji đã học?</li>
      </ul>
      <p>Cảm ơn mọi người rất nhiều!</p>
    </div>

    <!-- Replies Section -->
    <div class="replies-section">
      <h2 class="replies-header">15 Trả lời</h2>
      
      <div class="reply-list">
        <!-- Reply Thread -->
        <div v-for="reply in replies" :key="reply.id" class="reply-thread">
          
          <!-- Main Reply -->
          <div class="reply-item">
            <img 
              :src="reply.author.avatar" 
              :alt="reply.author.username" 
              class="author-avatar"
              @click.stop="toggleUserCard($event, reply.author)"
            >
            <div class="reply-content-wrapper">
              <div class="reply-meta">
                <span class="author-name" @click.stop="toggleUserCard($event, reply.author)">{{ reply.author.username }}</span>
                <span class="post-time">{{ reply.time }}</span>
              </div>
              <div class="reply-content">
                <p>{{ reply.text }}</p>
              </div>
              <div class="reply-actions">
                <button @click="toggleReplyForm(reply.id)" class="btn-link">
                  <i class="fas fa-reply"></i> Trả lời
                </button>
              </div>
            </div>
          </div>

          <!-- Reply Form (for main reply) -->
          <transition name="slide-fade">
            <div v-if="replyingTo === reply.id" class="nested-reply-form">
              <div class="reply-form">
                <textarea class="reply-textarea" :placeholder="`Viết trả lời cho ${reply.author.username}...`"></textarea>
                <div class="reply-form-actions">
                  <button class="btn btn-primary submit-reply-btn">Gửi</button>
                  <button @click="replyingTo = null" class="btn btn-secondary cancel-reply-btn">Hủy</button>
                </div>
              </div>
            </div>
          </transition>

          <!-- Nested Replies -->
          <div class="nested-replies" v-if="reply.replies && reply.replies.length > 0">
            <div v-for="nestedReply in reply.replies" :key="nestedReply.id" class="reply-thread is-nested">
              <div class="reply-item">
                <img 
                  :src="nestedReply.author.avatar" 
                  :alt="nestedReply.author.username" 
                  class="author-avatar"
                  @click.stop="toggleUserCard($event, nestedReply.author)"
                >
                <div class="reply-content-wrapper">
                  <div class="reply-meta">
                    <span class="author-name" @click.stop="toggleUserCard($event, nestedReply.author)">{{ nestedReply.author.username }}</span>
                    <span class="post-time">{{ nestedReply.time }}</span>
                  </div>
                  <div class="reply-content">
                    <p>{{ nestedReply.text }}</p>
                  </div>
                  <div class="reply-actions">
                    <button @click="toggleReplyForm(nestedReply.id)" class="btn-link">
                      <i class="fas fa-reply"></i> Trả lời
                    </button>
                  </div>
                </div>
              </div>

              <!-- Reply Form (for nested reply) -->
              <transition name="slide-fade">
                <div v-if="replyingTo === nestedReply.id" class="nested-reply-form">
                  <div class="reply-form">
                    <textarea class="reply-textarea" :placeholder="`Viết trả lời cho ${nestedReply.author.username}...`"></textarea>
                    <div class="reply-form-actions">
                      <button class="btn btn-primary submit-reply-btn">Gửi</button>
                      <button @click="replyingTo = null" class="btn btn-secondary cancel-reply-btn">Hủy</button>
                    </div>
                  </div>
                </div>
              </transition>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Add a Reply -->
    <div class="add-reply-card">
       <h3 class="add-reply-header">Tham gia thảo luận</h3>
       <div class="reply-form">
        <textarea class="reply-textarea" placeholder="Viết bình luận của ..."></textarea>
        <button class="btn btn-primary submit-reply-btn">Gửi trả lời</button>
       </div>
    </div>

    <!-- User Profile Card -->
    <Teleport to="body">
      <UserProfileCard 
        ref="floatingEl"
        v-if="activeUserCard" 
        :user="activeUserCard" 
        :style="floatingStyles"
        :post-id="currentPostId"
        :post-title="postTitle"
      />
    </Teleport>

  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import UserProfileCard from '@/components/layout/forum/profilecard/UserProfileCard.vue';
import { useFloating, autoUpdate, offset } from '@floating-ui/vue';


const router = useRouter();
const replyingTo = ref(null);
const activeUserCard = ref(null);

// Floating UI
const referenceEl = ref(null);
const floatingEl = ref(null);
const { floatingStyles } = useFloating(referenceEl, floatingEl, {
  placement: 'bottom-start',
  whileElementsMounted: autoUpdate,
  middleware: [offset({ mainAxis: -10, crossAxis: 25 })]
});

const goBackToForum = () => {
  router.push('/forum');
};

const toggleReplyForm = (replyId) => {
  if (replyingTo.value === replyId) {
    replyingTo.value = null; // Close if already open for this reply
  } else {
    replyingTo.value = replyId; // Open for this reply
  }
};

const toggleUserCard = (event, user) => {
  if (activeUserCard.value && activeUserCard.value.username === user.username) {
    activeUserCard.value = null;
    referenceEl.value = null;
  } else {
    referenceEl.value = event.currentTarget;
    activeUserCard.value = user;
  }
};

// --- MOCK DATA ---
const currentPostId = ref(1); // Giả sử ID bài viết là 1
const postTitle = ref('Thảo luận về cách học Kanji hiệu quả cho người mới bắt đầu');

const originalPost = {
  author: { 
    username: 'Mai An', fullName: 'Mai Thị An', title: 'Người học tiếng Nhật',
    avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026704d',
    bio: 'Chỉ là một cô gái đang cố gắng tìm hiểu về Kanji. Chia sẻ hành trình của mình và học hỏi từ cộng đồng.',
    website: 'my-japanese-journey.com', websiteUrl: '#', location: 'Hà Nội, Việt Nam',
    stats: { posted: '4 giờ trước', joined: '08/12/2022', read: '1 ngày', solutions: 2 },
    postsInTopic: 2,
  },
  // ... other post data
};

const replies = ref([
  {
    id: 1,
    author: {
      username: 'Hùng Trần', fullName: 'Trần Minh Hùng', title: 'Bậc thầy Kanji',
      avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026705d',
      bio: 'Đã có N1 JLPT. Ở đây để giúp  với các câu hỏi về ngữ pháp và Kanji. Người tạo ra một số công cụ học tiếng Nhật mã nguồn mở thú vị.',
      website: 'github.com/hung-tran', websiteUrl: '#', location: 'Đà Nẵng, Việt Nam',
      stats: { posted: '2 giờ trước', joined: '15/01/2021', read: '22 ngày', solutions: 152 },
      postsInTopic: 2,
    },
    time: '1 giờ 45 phút trước',
    text: 'Mình thấy học theo sách "Look & Learn" khá hiệu quả, hình ảnh minh họa dễ nhớ lắm . Kết hợp với app Anki để ôn tập mỗi ngày nữa là ổn.',
    replies: [],
  },
  {
    id: 2,
    author: { 
      username: 'Lan Anh', fullName: 'Nguyễn Lan Anh', title: 'Chuyên gia ngữ pháp',
      avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026707d',
      bio: 'Tôi thích chia nhỏ các điểm ngữ pháp phức tạp thành các giải thích dễ hiểu.',
      website: 'grammar-is-fun.com', websiteUrl: '#', location: 'TP. Hồ Chí Minh, Việt Nam',
      stats: { posted: '1 giờ trước', joined: '02/03/2022', read: '5 ngày', solutions: 48 },
      postsInTopic: 1,
    },
    time: '1 giờ 20 phút trước',
    text: 'Đúng rồi, học theo bộ thủ là cách nhớ lâu nhất đó.  thử tìm hiểu về 214 bộ thủ cơ bản trước, sau đó ghép Kanji sẽ thấy logic hơn nhiều.',
    replies: [
      {
        id: 4,
        author: originalPost.author, // Re-using Mai An's profile
        time: '1 giờ 10 phút trước',
        text: 'Cảm ơn  Lan Anh, mình sẽ thử tìm hiểu xem sao. Mình cũng nghe nhiều người khen phương pháp này.',
      },
       {
        id: 5,
        author: {
          username: 'Hùng Trần', fullName: 'Trần Minh Hùng', title: 'Bậc thầy Kanji',
          avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026705d'
        }, // Re-using Hung Tran's profile
        time: '1 giờ 5 phút trước',
        text: 'Về app học bộ thủ thì mình highly recommend "Kanji Study", có cả trên iOS và Android. Rất chi tiết và có cả game để luyện tập.',
      }
    ]
  },
   {
    id: 3,
    author: {
      username: 'Duy Mạnh', fullName: 'Phạm Duy Mạnh', title: 'Người truyền cảm hứng',
      avatar: 'https://i.pravatar.cc/150?u=a042581f4e29026709d',
      bio: 'Chậm mà chắc sẽ chiến thắng. Mọi người hãy tiếp tục cố gắng nhé!',
      website: 'motivation.com', websiteUrl: '#', location: 'Hải Phòng, Việt Nam',
      stats: { posted: '30 phút trước', joined: '01/09/2022', read: '18 giờ', solutions: 12 },
      postsInTopic: 1,
    },
    time: '30 phút trước',
    text: 'Cứ bình tĩnh  ơi, ai mới học cũng vậy thôi. Quan trọng là kiên trì, mỗi ngày 5-10 chữ thôi cũng được, mưa dầm thấm lâu. Cố lên!',
    replies: [],
  }
]);
</script>

<style lang="scss" scoped>
@use '@/views/forum/forum-post-detail/ForumPostDetail.scss';
</style> 