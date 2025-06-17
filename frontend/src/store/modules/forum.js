// forum.js
const defaultPost = {
  id: null,
  title: '',
  content: '',
  category: '',
  tags: [],
  timestamp: '',
  author: {
    id: '',
    username: '',
    fullName: '',
    title: '',
    avatar: '',
    bio: '',
    website: '',
    websiteUrl: '#',
    location: '',
    stats: { 
      posted: '', 
      joined: '', 
      read: '', 
      solutions: 0 
    }
  },
  stats: {
    replies: 0,
    views: 0
  }
};

const state = {
  posts: [],
  currentPost: defaultPost
};

const mutations = {
  SET_POSTS(state, posts) {
    state.posts = posts;
  },
  ADD_POST(state, post) {
    state.posts.unshift(post);
    state.currentPost = post;
  },
  SET_CURRENT_POST(state, post) {
    state.currentPost = post || defaultPost;
  }
};

const actions = {
  createPost({ commit }, post) {
    // In a real app, this would be an API call
    commit('ADD_POST', post);
    return post;
  },
  
  getPost({ commit, state }, postId) {
    // First check if it's the current post
    if (state.currentPost && state.currentPost.id === postId) {
      return state.currentPost;
    }
    
    // Then check in posts array
    const post = state.posts.find(p => p.id === postId);
    if (post) {
      commit('SET_CURRENT_POST', post);
      return post;
    }
    
    // If not found, return demo post
    const demoPost = {
      id: "1750066910573",
      title: "Thảo luận về cách học Kanji hiệu quả cho người mới bắt đầu",
      content: `<p>Chào mọi người,</p>
                <p>Mình là người mới bắt đầu học tiếng Nhật và đang gặp khá nhiều khó khăn với Kanji. Dù đã thử qua nhiều phương pháp như viết tay, dùng flashcard, học theo bộ thủ nhưng vẫn cảm thấy rất rối và khó nhớ.</p>
                <p>Mọi người có kinh nghiệm hay phương pháp nào học Kanji hiệu quả, đặc biệt là cho người mới, thì chia sẻ cho mình và mọi người cùng biết với ạ. Ví dụ như:</p>
                <ul>
                  <li>Nên bắt đầu từ những bộ Kanji nào?</li>
                  <li>Có app hay website nào hỗ trợ học tốt không?</li>
                  <li>Cách để ôn tập và tránh quên Kanji đã học?</li>
                </ul>
                <p>Cảm ơn mọi người rất nhiều!</p>`,
      category: "kanji",
      tags: ["kanji", "người mới bắt đầu", "học tập"],
      timestamp: "2024-03-16T10:00:00.000Z",
      author: {
        id: "demo-user-456",
        username: "Mai An",
        fullName: "Mai Thị An",
        title: "Người học tiếng Nhật",
        avatar: "https://i.pravatar.cc/150?u=a042581f4e29026704d",
        bio: "Chỉ là một cô gái đang cố gắng tìm hiểu về Kanji. Chia sẻ hành trình của mình và học hỏi từ cộng đồng.",
        website: "my-japanese-journey.com",
        websiteUrl: "#",
        location: "Hà Nội, Việt Nam",
        stats: {
          posted: "2 giờ trước",
          joined: "08/12/2022",
          read: "1 ngày",
          solutions: 2
        }
      },
      stats: {
        replies: 15,
        views: 45
      }
    };
    
    commit('SET_CURRENT_POST', demoPost);
    return demoPost;
  },

  updatePost({ commit, state }, { id, ...updates }) {
    const postIndex = state.posts.findIndex(p => p.id === id);
    if (postIndex !== -1) {
      const updatedPost = { ...state.posts[postIndex], ...updates };
      const updatedPosts = [...state.posts];
      updatedPosts[postIndex] = updatedPost;
      commit('SET_POSTS', updatedPosts);
      if (state.currentPost && state.currentPost.id === id) {
        commit('SET_CURRENT_POST', updatedPost);
      }
    }
  },

  deletePost({ commit, state }, postId) {
    commit('SET_POSTS', state.posts.filter(p => p.id !== postId));
    if (state.currentPost && state.currentPost.id === postId) {
      commit('SET_CURRENT_POST', null);
    }
  }
};

const getters = {
  getCurrentPost: state => state.currentPost || defaultPost
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}; 